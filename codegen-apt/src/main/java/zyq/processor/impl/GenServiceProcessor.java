package zyq.processor.impl;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import utils.StringUtils;
import zyq.annotation.GenService;
import zyq.processor.BaseCodeGenProcessor;
import zyq.processor.MethodCreator;
import zyq.spi.CodeGenProcessor;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;


/**
 * @author YiQing
 */
@AutoService(CodeGenProcessor.class)
public class GenServiceProcessor extends BaseCodeGenProcessor implements MethodCreator {

    public static final String PREFIX = "I";
    public static final String SUFFIX = "Service";

    public GenServiceProcessor() {

    }

    @Override
    public void genClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        methodCreator = this;
        TypeSpec typeSpec = TypeSpec.interfaceBuilder(context.getServiceClassName())
                .addModifiers(Modifier.PUBLIC)
                .addMethods(createCommonMethod())
                .build();

        GenService ann = typeElement.getAnnotation(GenService.class);
        genJavaFile(ann.pkName(), typeSpec, ann.sourcePath(), ann.overrideSource());

    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenService.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenService.class).pkName();
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
        MethodSpec build = MethodSpec.methodBuilder("list")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(ClassName.get(dtoPackageName, dtoClassName), "dto")
                .returns(ParameterizedTypeName.get(ClassName.get(List.class),ClassName.get(voPackageName, voClassName)))
                .build();
        return Optional.of(build);
    }

    @Override
    public Optional<MethodSpec> insert() {
        String entityClassName = context.getEntityClassName();
        String entityPackageName = context.getEntityPackageName();

        if (StringUtils.containsNull(entityClassName, entityPackageName)) {
            return Optional.empty();
        }

        MethodSpec build = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(ClassName.get(entityPackageName, entityClassName), "record")
                .returns(TypeName.INT).build();

        return Optional.of(build);
    }

    @Override
    public Optional<MethodSpec> update() {

        String entityClassName = context.getEntityClassName();
        String entityPackageName = context.getEntityPackageName();
        if (StringUtils.containsNull(entityClassName, entityPackageName)) {
            return Optional.empty();
        }

        MethodSpec build = MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(ClassName.get(entityPackageName, entityClassName), "record")
                .returns(TypeName.INT).build();

        return Optional.of(build);
    }

    @Override
    public Optional<MethodSpec> delete() {

        MethodSpec build = MethodSpec.methodBuilder("delete")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(TypeName.LONG.box(), "id")
                .returns(TypeName.INT).build();

        return Optional.of(build);
    }

    @Override
    public Optional<MethodSpec> findById() {

        String entityClassName = context.getEntityClassName();
        String entityPackageName = context.getEntityPackageName();
        if (StringUtils.containsNull(entityClassName, entityPackageName)) {
            return Optional.empty();
        }

        MethodSpec build = MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(TypeName.LONG.box(), "id")
                .returns(ClassName.get(entityPackageName, entityClassName)).build();

        return Optional.of(build);
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
        MethodSpec build = MethodSpec.methodBuilder("findByPage")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(ClassName.get(dtoPackageName, dtoClassName), "dto")
                .returns(ClassName.get(voPackageName, voClassName))
                .build();
        return Optional.of(build);
    }

    @Override
    public Optional<MethodSpec> voList() {
        return Optional.empty();
    }

}
