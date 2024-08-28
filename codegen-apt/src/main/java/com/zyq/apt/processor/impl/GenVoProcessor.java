package com.zyq.apt.processor.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.zyq.apt.annotation.VoItem;
import com.zyq.apt.processor.BaseCodeGenProcessor;
import com.zyq.apt.spi.CodeGenProcessor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.zyq.apt.annotation.GenVo;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author YiQing
 * @version 1.0
 */
@AutoService(CodeGenProcessor.class)
public class GenVoProcessor extends BaseCodeGenProcessor {


    public static final String SUFFIX = "Vo";

    @Override
    public void genClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {

        TypeSpec.Builder builder = TypeSpec.classBuilder(context.getVoClassName()).addAnnotation(Data.class)
                .addModifiers(Modifier.PUBLIC);

        List<VariableElement> fields = getFields(typeElement, e -> Objects.nonNull(e.getAnnotation(VoItem.class)));
        Map<VariableElement, TypeName> voConvertMap = context.getVoConvertMap();

        fields.forEach(field -> {
            FieldSpec.Builder fieldBuilder = FieldSpec.builder(voConvertMap.get(field),
                    field.getSimpleName().toString(), Modifier.PRIVATE);

            String fieldComment = context.getFieldComment().get(field);
            String comment = fieldComment == null ? "" : fieldComment;

            if (context.isExport()) {

                AnnotationSpec.Builder excelBuilder = AnnotationSpec
                        .builder(ClassName.get("com.zjhc.common.annotation", "Excel"))
                        .addMember("name", "$S", comment);

                if (voConvertMap.get(field).equals(TypeName.get(Date.class))) {
                    excelBuilder.addMember("dateFormat", "$S", "yyyy-MM-dd HH:mm:ss");
                }
                fieldBuilder.addAnnotation(excelBuilder.build());

            }

            if (!context.isExport()) {
                fieldBuilder.addAnnotation(AnnotationSpec.builder(ApiModelProperty.class)
                        .addMember("value", "$S", comment).build());
            }

            if (voConvertMap.get(field).equals(TypeName.get(Date.class))) {
                fieldBuilder.addAnnotation(AnnotationSpec.builder(JsonFormat.class)
                        .addMember("pattern", "$S", "yyyy-MM-dd HH:mm:ss")
                        .addMember("timezone", "$S", "GMT+8")
                        .build());
            }

            builder.addField(fieldBuilder.build());
        });

        GenVo ann = typeElement.getAnnotation(GenVo.class);
        genJavaFile(ann.projectPath(), ann.pkName(), builder.build(), ann.sourcePath(), ann.overrideSource());
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenVo.class;
    }

}
