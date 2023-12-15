package com.zyq.apt.processor.impl;

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
import java.util.Objects;
import java.util.Set;

/**
 * @author YiQing
 * @version 1.0
 */
@AutoService(CodeGenProcessor.class)
public class GenVoProcessor extends BaseCodeGenProcessor {


    public static final String SUFFIX = "Vo";

    @Override
    public void genClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {

        TypeSpec.Builder builder = TypeSpec.classBuilder(context.getVoClassName()).addAnnotation(Data.class);

        Set<VariableElement> fields = getFields(typeElement, e -> Objects.nonNull(e.getAnnotation(VoItem.class)));

        fields.forEach(field -> {

            FieldSpec.Builder fieldBuilder = FieldSpec.builder(TypeName.get(field.asType()),
                    field.getSimpleName().toString(), Modifier.PRIVATE);

            String fieldComment = context.getFieldComment().get(field);
            String comment = fieldComment == null ? "" : fieldComment;

            if (context.isExport()) {
                fieldBuilder.addAnnotation(AnnotationSpec
                        .builder(ClassName.get("com.zjhc.common.annotation", "Excel"))
                        .addMember("name", "$S", comment)
                        .build());
            }

            fieldBuilder.addAnnotation(AnnotationSpec.builder(ApiModelProperty.class)
                    .addMember("value", "$S", comment).build());

            builder.addField(fieldBuilder.build());
        });

        GenVo ann = typeElement.getAnnotation(GenVo.class);
        genJavaFile(ann.pkName(), ann.pkName(), builder.build(), ann.sourcePath(), ann.overrideSource());
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenVo.class;
    }

}
