package com.zyq.apt.processor;

import com.squareup.javapoet.*;
import com.zyq.apt.annotation.*;
import com.zyq.apt.constant.ConvertEnum;
import com.zyq.apt.processor.impl.*;
import com.zyq.apt.constant.CodeGenContext;
import com.zyq.apt.holder.ProcessingEnvHolder;
import com.zyq.apt.spi.CodeGenProcessor;
import com.zyq.common.utils.StringUtils;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author YiQing
 * @version 1.0
 */
public abstract class BaseCodeGenProcessor implements CodeGenProcessor {


    protected CodeGenContext context;
    protected MethodCreator methodCreator;

    public BaseCodeGenProcessor() {
    }


    @Override
    public void generate(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        initContext(typeElement);
        genClass(typeElement, roundEnvironment);
    }


    public abstract void genClass(TypeElement typeElement, RoundEnvironment roundEnvironment);


    private void initContext(TypeElement typeElement) {

        context = new CodeGenContext();

        Name className = typeElement.getSimpleName();
        context.setVoClassName(className + GenVoProcessor.SUFFIX);
        context.setDtoClassName(className + GenDtoProcessor.SUFFIX);
        context.setControllerClassName(className + GenControllerProcessor.SUFFIX);
        context.setServiceClassName(GenServiceProcessor.PREFIX + className + GenServiceProcessor.SUFFIX);
        context.setImplClassName(className + GenServiceImplProcessor.SUFFIX);


        GenConfig config = Optional.ofNullable(typeElement.getAnnotation(GenConfig.class))
                .orElseThrow(() -> {
                    ProcessingEnvHolder.getHolder().getMessager().printMessage(Diagnostic.Kind.ERROR, "annotation GenConfig has not Configure");
                    return new RuntimeException();
                });

        List<? extends Element> fieldsElements = typeElement.getEnclosedElements();
        List<VariableElement> variableElements = ElementFilter.fieldsIn(fieldsElements);
        Optional<VariableElement> keyField = variableElements.stream().filter(e -> e.getAnnotation(Key.class) != null).findFirst();


        Map<VariableElement, TypeName> voConvertMap = new HashMap<>();
        Map<VariableElement, TypeName> dtoConvertMap = new HashMap<>();

        variableElements.forEach(e -> {
            DtoItem dtoItem = e.getAnnotation(DtoItem.class);
            VoItem voItem = e.getAnnotation(VoItem.class);
            if (Objects.nonNull(dtoItem)) {
                ConvertEnum dtoType = dtoItem.converter();
                TypeName dtoTypeName = ConvertEnum.convertType(dtoType);
                if (dtoType.equals(ConvertEnum.DEFAULT)) {
                    dtoTypeName = TypeName.get(e.asType());
                }
                dtoConvertMap.put(e, dtoTypeName);
            }

            if (Objects.nonNull(voItem)) {
                ConvertEnum voType = voItem.converter();
                TypeName voTypeName = ConvertEnum.convertType(voType);
                if (voType.equals(ConvertEnum.DEFAULT)) {
                    voTypeName = TypeName.get(e.asType());
                }
                voConvertMap.put(e, voTypeName);
            }
        });

        context.setVoConvertMap(voConvertMap);
        context.setDtoConvertMap(dtoConvertMap);

        String key = "Id";
        if (keyField.isPresent()) {
            key = StringUtils.upper(keyField.get().getSimpleName().toString());
        }
        context.setKey(key);

        String entityClassName = typeElement.getSimpleName().toString();
        context.setMapperPackageName(config.mapperPkName());
        context.setMethodNames(config.methodName());
        context.setMapperClassName(entityClassName + "Mapper");
        context.setEntityPackageName(config.entityPkName());
        context.setEntityClassName(entityClassName);

        Optional.ofNullable(typeElement.getAnnotation(GenVo.class)).ifPresent(ann -> {
            context.setVoPackageName(ann.pkName());
            context.setExport(ann.export());
            context.setOverrideVo(ann.overrideSource());
        });
        Optional.ofNullable(typeElement.getAnnotation(GenDto.class)).ifPresent(ann -> {
            context.setDtoPackageName(ann.pkName());
            context.setOverrideDto(ann.overrideSource());
        });
        Optional.ofNullable(typeElement.getAnnotation(GenController.class)).ifPresent(ann -> {
            context.setControllerPackageName(ann.pkName());
            context.setOverrideController(ann.overrideSource());
        });
        Optional.ofNullable(typeElement.getAnnotation(GenService.class)).ifPresent(ann -> {
            context.setServicePackageName(ann.pkName());
            context.setOverrideService(ann.overrideSource());
        });
        Optional.ofNullable(typeElement.getAnnotation(GenServiceImpl.class)).ifPresent(ann -> {
            context.setImplPackageName(ann.pkName());
            context.setOverrideImpl(ann.overrideSource());
        });

        Elements elementUtils = ProcessingEnvHolder.getHolder().getElementUtils();
        // 保存表注释
        String tableComment = elementUtils.getDocComment(typeElement).replace("\n", "").trim();
        context.setTableComment(StringUtils.cleanTableComment(tableComment));
        // 保存字段注释
        Map<VariableElement, String> fieldComment = context.getFieldComment();
        List<? extends Element> elements = typeElement.getEnclosedElements();
        for (Element fieldElement : ElementFilter.fieldsIn(elements)) {

            // 字段类型的元素获取其注释
            if (fieldElement.getKind() == ElementKind.FIELD) {
                VariableElement variableElement = (VariableElement) fieldElement;
                String docComment = elementUtils.getDocComment(fieldElement);

                if (Objects.nonNull(docComment)) {
                    fieldComment.put(variableElement, docComment.trim());
                }

            }
        }
    }

    protected List<VariableElement> getFields(TypeElement typeElement, Predicate<VariableElement> predicate) {
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        List<VariableElement> variableElements = ElementFilter.fieldsIn(enclosedElements);
        return variableElements.stream().filter(predicate).distinct().collect(Collectors.toList());

    }

    protected List<MethodSpec> createCommonMethod() {
        return methodCreator.dispatch(context.getMethodNames());
    }


    protected void genJavaFile(String projectPath, String pkName, TypeSpec spec, String sourcePath, boolean overrideSource) {

        JavaFile javaFile = JavaFile.builder(pkName, spec).build();
        String dir = projectPath.replace("\\", File.separator) + File.separator + sourcePath.replace("/", File.separator);
        String filePath = dir + File.separator + pkName.replace(".", File.separator) + File.separator + spec.name + ".java";

        File sourceFile = new File(dir);
        File file = new File(filePath);

        try {
            if (file.exists() && !overrideSource) {
                return;
            }
            javaFile.writeTo(sourceFile);
        } catch (IOException e) {
            ProcessingEnvHolder.getHolder().getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }


}
