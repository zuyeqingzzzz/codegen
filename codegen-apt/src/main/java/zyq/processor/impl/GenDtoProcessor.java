package zyq.processor.impl;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import constant.MethodEnum;
import lombok.Data;
import zyq.annotation.DtoItem;
import zyq.annotation.GenDto;
import zyq.processor.BaseCodeGenProcessor;
import zyq.spi.CodeGenProcessor;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * @author YiQing
 * @version 1.0
 */
@AutoService(CodeGenProcessor.class)
public class GenDtoProcessor extends BaseCodeGenProcessor{

    public static final String SUFFIX = "Dto";

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenDto.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenDto.class).pkName();
    }


    @Override
    public void genClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {

        TypeSpec.Builder builder = TypeSpec.classBuilder(context.getDtoClassName()).addAnnotation(Data.class);

        Set<VariableElement> fields = getFields(typeElement, e -> Objects.nonNull(e.getAnnotation(DtoItem.class)));
        fields.forEach(field -> {
            FieldSpec.Builder fieldBuilder = FieldSpec.builder(TypeName.get(field.asType()),
                    field.getSimpleName().toString(), Modifier.PRIVATE);
            builder.addField(fieldBuilder.build());
        });

        if (Arrays.asList(context.getMethodNames()).contains(MethodEnum.FINDBYID)) {
            builder.superclass(ClassName.get("com.zjhc.common.core.page", "QueryBaseDto"));
        }

        GenDto ann = typeElement.getAnnotation(GenDto.class);
        genJavaFile(ann.pkName(), builder.build(), ann.sourcePath(), ann.overrideSource());
    }



}
