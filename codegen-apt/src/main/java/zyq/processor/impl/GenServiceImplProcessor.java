package zyq.processor.impl;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import utils.StringUtils;
import zyq.annotation.GenServiceImpl;
import zyq.processor.BaseCodeGenProcessor;
import zyq.processor.MethodCreator;
import zyq.spi.CodeGenProcessor;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * @author YiQing
 */
@AutoService(CodeGenProcessor.class)
public class GenServiceImplProcessor extends BaseCodeGenProcessor implements MethodCreator {

    public static final String SUFFIX = "ServiceImpl";
    String mapperName;

    @Override
    public void genClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        methodCreator = this;
        mapperName = StringUtils.camel(context.getMapperClassName());
        TypeSpec typeSpec = TypeSpec.classBuilder(context.getImplClassName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(context.getServicePackageName(),context.getServiceClassName()))
                .addAnnotation(ClassName.get("org.springframework.stereotype", "Service"))
                .addAnnotation(Slf4j.class)
                .addField(FieldSpec.builder(ClassName.get(context.getMapperPackageName(), context.getMapperClassName()), "mapperName")
                        .addModifiers(Modifier.PRIVATE)
                        .addAnnotation(Autowired.class).build())
                .addMethods(createCommonMethod())
                .build();

        GenServiceImpl ann = typeElement.getAnnotation(GenServiceImpl.class);
        genJavaFile(ann.pkName(), typeSpec, ann.sourcePath(), ann.overrideSource());
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenServiceImpl.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenServiceImpl.class).pkName();
    }

    @Override
    public Optional<MethodSpec> list() {

        String dtoPackageName = context.getDtoPackageName();
        String dtoClassName = context.getDtoClassName();
        String voPackageName = context.getVoPackageName();
        String voClassName = context.getVoClassName();

        if (StringUtils.containsNull(dtoPackageName, dtoClassName, voPackageName, voClassName)) {
            return Optional.empty();
        }
        MethodSpec.Builder build = MethodSpec.methodBuilder("list")
                .addParameter(ClassName.get(dtoPackageName, dtoClassName), "dto")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class),ClassName.get(voPackageName, voClassName)));

        CodeBlock codeBlock = CodeBlock.of("return $N.select(dto);", mapperName);
        build.addCode(codeBlock);

        return Optional.of(build.build());
    }

    @Override
    public Optional<MethodSpec> insert() {


        String dtoPackageName = context.getDtoPackageName();
        String dtoClassName = context.getDtoClassName();
        String voPackageName = context.getVoPackageName();
        String voClassName = context.getVoClassName();
        String entityPackageName = context.getEntityPackageName();
        String entityClassName = context.getEntityClassName();

        if (StringUtils.containsNull(dtoPackageName, dtoClassName, voPackageName, voClassName)) {
            return Optional.empty();
        }
        MethodSpec.Builder build = MethodSpec.methodBuilder("insert")
                .addParameter(ClassName.get(entityPackageName, entityClassName), "record")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.INT);

        CodeBlock codeBlock = CodeBlock.of("return $N.insert(record);", mapperName);
        build.addCode(codeBlock);

        return Optional.of(build.build());
    }

    @Override
    public Optional<MethodSpec> update() {

        String dtoPackageName = context.getDtoPackageName();
        String dtoClassName = context.getDtoClassName();
        String voPackageName = context.getVoPackageName();
        String voClassName = context.getVoClassName();
        String entityPackageName = context.getEntityPackageName();
        String entityClassName = context.getEntityClassName();

        if (StringUtils.containsNull(dtoPackageName, dtoClassName, voPackageName, voClassName)) {
            return Optional.empty();
        }


        MethodSpec.Builder build = MethodSpec.methodBuilder("update")
                .addParameter(ClassName.get(entityPackageName, entityClassName), "record")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.INT);

        CodeBlock codeBlock = CodeBlock.of("return $N.update(record);", mapperName);
        build.addCode(codeBlock);

        return Optional.of(build.build());
    }

    @Override
    public Optional<MethodSpec> delete() {

        String dtoPackageName = context.getDtoPackageName();
        String dtoClassName = context.getDtoClassName();
        String voPackageName = context.getVoPackageName();
        String voClassName = context.getVoClassName();

        if (StringUtils.containsNull(dtoPackageName, dtoClassName, voPackageName, voClassName)) {
            return Optional.empty();
        }
        MethodSpec.Builder build = MethodSpec.methodBuilder("delete")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.LONG.box(), "id")
                .addAnnotation(Override.class)
                .returns(TypeName.INT);

        CodeBlock codeBlock = CodeBlock.of("return $N.delete(id);", mapperName);
        build.addCode(codeBlock);

        return Optional.of(build.build());
    }

    @Override
    public Optional<MethodSpec> findById() {

        String dtoPackageName = context.getDtoPackageName();
        String dtoClassName = context.getDtoClassName();
        String voPackageName = context.getVoPackageName();
        String voClassName = context.getVoClassName();
        String entityPackageName = context.getEntityPackageName();
        String entityClassName = context.getEntityClassName();

        if (StringUtils.containsNull(dtoPackageName, dtoClassName, voPackageName, voClassName)) {
            return Optional.empty();
        }
        MethodSpec.Builder build = MethodSpec.methodBuilder("findById")
                .addParameter(TypeName.LONG.box(), "id")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(ClassName.get(entityPackageName, entityClassName));

        CodeBlock codeBlock = CodeBlock.of("return $N.findById(id);", mapperName);
        build.addCode(codeBlock);

        return Optional.of(build.build());
    }

    @Override
    public Optional<MethodSpec> findByPage() {

        String dtoPackageName = context.getDtoPackageName();
        String dtoClassName = context.getDtoClassName();
        String voPackageName = context.getVoPackageName();
        String voClassName = context.getVoClassName();

        if (StringUtils.containsNull(dtoPackageName, dtoClassName, voPackageName, voClassName)) {
            return Optional.empty();
        }
        MethodSpec.Builder build = MethodSpec.methodBuilder("findByPage")
                .addParameter(ClassName.get(dtoPackageName, dtoClassName), "dto")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class),ClassName.get(voPackageName, voClassName)));

        CodeBlock codeBlock = CodeBlock.of("return $N.select(dto);", mapperName);
        build.addCode(codeBlock);

        return Optional.of(build.build());
    }

    @Override
    public Optional<MethodSpec> voList() {
        return Optional.empty();
    }


}
