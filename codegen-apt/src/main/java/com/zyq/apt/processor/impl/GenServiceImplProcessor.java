package com.zyq.apt.processor.impl;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.zyq.apt.processor.BaseCodeGenProcessor;
import com.zyq.apt.processor.MethodCreator;
import com.zyq.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.zyq.apt.annotation.GenServiceImpl;
import com.zyq.apt.spi.CodeGenProcessor;

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
                .addSuperinterface(ClassName.get(context.getServicePackageName(), context.getServiceClassName()))
                .addAnnotation(ClassName.get("org.springframework.stereotype", "Service"))
                .addAnnotation(Slf4j.class)
                .addField(FieldSpec.builder(ClassName.get(context.getMapperPackageName(), context.getMapperClassName()), mapperName)
                        .addModifiers(Modifier.PRIVATE)
                        .addAnnotation(Autowired.class).build())
                .addMethods(createCommonMethod())
                .build();

        GenServiceImpl ann = typeElement.getAnnotation(GenServiceImpl.class);
        genJavaFile(ann.projectPath(), ann.pkName(), typeSpec, ann.sourcePath(), ann.overrideSource());
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenServiceImpl.class;
    }

    @Override
    public Optional<MethodSpec> list() {

        String entityPackageName = context.getEntityPackageName();
        String entityClassName = context.getEntityClassName();

        if (StringUtils.containsNull(entityClassName, entityPackageName)) {
            return Optional.empty();
        }
        MethodSpec.Builder build = MethodSpec.methodBuilder("list")
                .addParameter(ClassName.get(entityPackageName, entityClassName), "param")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(entityPackageName, entityClassName)));

        String listName = "select" + context.getEntityClassName() + "List";
        CodeBlock codeBlock = CodeBlock.of("return $N.$N(param);", mapperName, listName);
        build.addCode(codeBlock);

        return Optional.of(build.build());
    }

    @Override
    public Optional<MethodSpec> insert() {

        String entityPackageName = context.getEntityPackageName();
        String entityClassName = context.getEntityClassName();

        if (StringUtils.containsNull(entityPackageName, entityClassName, mapperName)) {
            return Optional.empty();
        }
        MethodSpec.Builder build = MethodSpec.methodBuilder("insert")
                .addParameter(ClassName.get(entityPackageName, entityClassName), "record")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.INT);

        String insertName = "insert" + context.getEntityClassName();
        CodeBlock codeBlock = CodeBlock.of("return $N.$N(record);", mapperName, insertName);
        build.addCode(codeBlock);

        return Optional.of(build.build());
    }

    @Override
    public Optional<MethodSpec> update() {

        String entityPackageName = context.getEntityPackageName();
        String entityClassName = context.getEntityClassName();

        if (StringUtils.containsNull(entityPackageName, entityClassName, mapperName)) {
            return Optional.empty();
        }


        MethodSpec.Builder build = MethodSpec.methodBuilder("update")
                .addParameter(ClassName.get(entityPackageName, entityClassName), "record")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.INT);

        String updateName = "update" + context.getEntityClassName();
        CodeBlock codeBlock = CodeBlock.of("return $N.$N(record);", mapperName, updateName);
        build.addCode(codeBlock);

        return Optional.of(build.build());
    }

    @Override
    public Optional<MethodSpec> delete() {

        if (StringUtils.containsNull(context.getEntityClassName(), context.getEntityPackageName(), mapperName)) {
            return Optional.empty();
        }
        MethodSpec.Builder build = MethodSpec.methodBuilder("delete")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.LONG.box(), "id")
                .addAnnotation(Override.class)
                .returns(TypeName.INT);

        String deleteName = "delete" + context.getEntityClassName() + "By" + context.getKey();
        CodeBlock codeBlock = CodeBlock.of("return $N.$N(id);", mapperName, deleteName);
        build.addCode(codeBlock);

        return Optional.of(build.build());
    }

    @Override
    public Optional<MethodSpec> findById() {

        String entityPackageName = context.getEntityPackageName();
        String entityClassName = context.getEntityClassName();

        if (StringUtils.containsNull(entityPackageName, entityClassName, mapperName)) {
            return Optional.empty();
        }
        MethodSpec.Builder build = MethodSpec.methodBuilder("findById")
                .addParameter(TypeName.LONG.box(), "id")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(ClassName.get(entityPackageName, entityClassName));

        String methodName = "select" + entityClassName + "By" + context.getKey();
        CodeBlock codeBlock = CodeBlock.of("return $N.$N(id);", mapperName, methodName);
        build.addCode(codeBlock);

        return Optional.of(build.build());
    }

    @Override
    public Optional<MethodSpec> findByPage() {

        String dtoPackageName = context.getDtoPackageName();
        String dtoClassName = context.getDtoClassName();
        String voPackageName = context.getVoPackageName();
        String voClassName = context.getVoClassName();

        if (StringUtils.containsNull(dtoPackageName, dtoClassName, voPackageName, voClassName, mapperName)) {
            return Optional.empty();
        }
        MethodSpec.Builder build = MethodSpec.methodBuilder("findByPage")
                .addParameter(ClassName.get(dtoPackageName, dtoClassName), "dto")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(voPackageName, voClassName)));

        CodeBlock codeBlock = CodeBlock.of("return $N.selectVoList(dto);", mapperName);
        build.addCode(codeBlock);

        return Optional.of(build.build());
    }

    @Override
    public Optional<MethodSpec> voList() {

        String dtoPackageName = context.getDtoPackageName();
        String dtoClassName = context.getDtoClassName();
        String voPackageName = context.getVoPackageName();
        String voClassName = context.getVoClassName();

        if (StringUtils.containsNull(dtoPackageName, dtoClassName, voPackageName, voClassName, mapperName)) {
            return Optional.empty();
        }
        MethodSpec.Builder build = MethodSpec.methodBuilder("voList")
                .addParameter(ClassName.get(dtoPackageName, dtoClassName), "dto")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(voPackageName, voClassName)));

        CodeBlock codeBlock = CodeBlock.of("return $N.selectVoList(dto);", mapperName);
        build.addCode(codeBlock);

        return Optional.of(build.build());
    }


}
