package com.zyq.apt.processor.impl;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.zyq.apt.processor.BaseCodeGenProcessor;
import com.zyq.common.constant.MethodEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.zyq.apt.annotation.DtoItem;
import com.zyq.apt.annotation.GenDto;
import com.zyq.apt.spi.CodeGenProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author YiQing
 * @version 1.0
 */
@AutoService(CodeGenProcessor.class)
public class GenDtoProcessor extends BaseCodeGenProcessor {

    public static final String SUFFIX = "Dto";

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenDto.class;
    }

    @Override
    public void genClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {

        TypeSpec.Builder builder = TypeSpec.classBuilder(context.getDtoClassName()).addAnnotation(Data.class)
                .addModifiers(Modifier.PUBLIC);

        List<VariableElement> fields = getFields(typeElement, e -> Objects.nonNull(e.getAnnotation(DtoItem.class)));
        Map<VariableElement, TypeName> dtoConvertMap = context.getDtoConvertMap();

        fields.forEach(field -> {
            FieldSpec.Builder fieldBuilder = FieldSpec.builder(dtoConvertMap.get(field),
                    field.getSimpleName().toString(), Modifier.PRIVATE);

            String fieldComment = context.getFieldComment().get(field);
            String comment = fieldComment == null ? "" : fieldComment;

            fieldBuilder.addAnnotation(AnnotationSpec.builder(ApiModelProperty.class)
                    .addMember("value", "$S", comment).build());

            builder.addField(fieldBuilder.build());
        });

        if (Arrays.asList(context.getMethodNames()).contains(MethodEnum.FINDBYID)) {
            builder.superclass(ClassName.get("com.zjhc.common.core.page", "QueryBaseDto"));
        }

        GenDto ann = typeElement.getAnnotation(GenDto.class);
        genJavaFile(ann.projectPath(), ann.pkName(), builder.build(), ann.sourcePath(), ann.overrideSource());
    }



}
