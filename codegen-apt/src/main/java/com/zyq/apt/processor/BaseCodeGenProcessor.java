package com.zyq.apt.processor;

import com.squareup.javapoet.*;
import com.zyq.apt.annotation.*;
import com.zyq.apt.processor.impl.*;
import com.zyq.apt.context.CodeGenContext;
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
import java.lang.reflect.Type;
import java.math.BigDecimal;
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

    public BaseCodeGenProcessor () {
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
        String entityClassName = typeElement.getSimpleName().toString();
//        context.setProjectPath(config.projectPath());
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
        String tableComment = elementUtils.getDocComment(typeElement);
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
                    fieldComment.put(variableElement, StringUtils.cleanFieldComment(docComment));
                }

            }
        }
    }


    protected Type convertType(Class<?> type) {
        if (type.equals(long.class) || type.equals(Long.class)) {
            return Long.class;
        } else if (type.equals(short.class) || type.equals(Short.class)) {
            return Short.class;
        } else if (type.equals(int.class) || type.equals(Integer.class)) {
            return Integer.class;
        } else if (type.equals(float.class) || type.equals(Float.class)) {
            return Float.class;
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            return Double.class;
        } else if (type.equals(byte.class) || type.equals(Byte.class)) {
            return Byte.class;
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return Boolean.class;
        } else if (type.equals(String.class)) {
            return String.class;
        } else if (type.equals(BigDecimal.class)) {
            return BigDecimal.class;
//        } else if (type.equals(List.class)) {
//            return List.class;
        } else {
            return Object.class;
        }
    }

    protected Set<VariableElement> getFields(TypeElement typeElement, Predicate<VariableElement> predicate) {
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        List<VariableElement> variableElements = ElementFilter.fieldsIn(enclosedElements);
        return variableElements.stream().filter(predicate).collect(Collectors.toSet());

    }

    protected List<MethodSpec> createCommonMethod() {
        return methodCreator.dispatch(context.getMethodNames());
    }


    protected void genJavaFile(String projectPath, String pkName, TypeSpec spec, String sourcePath, boolean overrideSource) {

        JavaFile javaFile = JavaFile.builder(pkName, spec).build();
        String dir = projectPath.replace("\\",File.separator) + File.separator + sourcePath.replace("/", File.separator);

        File sourceFile = new File(dir);

        try {
            if (!overrideSource) {
                javaFile.writeTo(sourceFile);
            }

        } catch (IOException e) {
            ProcessingEnvHolder.getHolder().getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }


}
