package com.zyq.apt.processor.impl;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.zyq.apt.processor.BaseCodeGenProcessor;
import com.zyq.apt.processor.MethodCreator;
import com.zyq.apt.spi.CodeGenProcessor;
import com.zyq.common.utils.StringUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zyq.apt.annotation.GenController;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author YiQing
 * @version 1.0
 */
@AutoService(CodeGenProcessor.class)
public class GenControllerProcessor extends BaseCodeGenProcessor implements MethodCreator {

    public static final String SUFFIX = "Controller";

    private String serviceName;
    private String tableName;
    private String dtoPackageName;
    private String dtoClassName;
    private String voPackageName;
    private String voClassName;
    private String servicePackageName;
    private String serviceClassName;
    private String entityPackageName;
    private String entityClassName;

    private ClassName dto;
    private ClassName vo;
    private ClassName entity;
    private ClassName excelUtil;
    private ClassName ajaxResult;

    private boolean export;

    public GenControllerProcessor() {

    }

    @Override
    public void genClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        methodCreator = this;
        initName();
        Name simpleName = typeElement.getSimpleName();

        if (StringUtils.containsNull(tableName, serviceClassName, servicePackageName)) {
            return;
        }

        // 注解
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(typeElement.getSimpleName() + "Controller")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(ApiOperation.class).addMember("value", "$S", tableName).build())
                .addAnnotation(RestController.class)
                .addAnnotation(AnnotationSpec.builder(RequestMapping.class).addMember("value", "$S", "/" + simpleName).build())
                .addAnnotation(Slf4j.class)
                .superclass(ClassName.get("com.zjhc.common.core.controller", "BaseController"));

        // service字段
        ClassName serviceClass = ClassName.get(servicePackageName, serviceClassName);
        serviceName = StringUtils.camel(simpleName + "Service");
        FieldSpec serviceField = FieldSpec.builder(serviceClass, serviceName, Modifier.PRIVATE)
                .addAnnotation(Autowired.class)
                .build();
        typeBuilder.addField(serviceField);

        if (export) {
            ClassName response = ClassName.get("javax.servlet.http", "HttpServletResponse");
            FieldSpec responseField = FieldSpec.builder(response, "response", Modifier.PRIVATE)
                    .addAnnotation(Autowired.class)
                    .build();
            typeBuilder.addField(responseField);
        }

        // 方法
        typeBuilder.addMethods(createCommonMethod());
        if (export) {
            export().ifPresent(typeBuilder::addMethod);
        }
        // 生成java文件
        GenController ann = typeElement.getAnnotation(GenController.class);
        genJavaFile(ann.projectPath(), ann.pkName(), typeBuilder.build(), ann.sourcePath(), ann.overrideSource());
    }

    private void initName() {
        dtoPackageName = context.getDtoPackageName();
        dtoClassName = context.getDtoClassName();
        voPackageName = context.getVoPackageName();
        voClassName = context.getVoClassName();
        tableName = context.getTableComment();
        servicePackageName = context.getServicePackageName();
        serviceClassName = context.getServiceClassName();
        entityPackageName = "com.zjhc.platform.gkdomain.entity";
        entityClassName = "StaAreaTargetPc";
        export = context.isExport();

        dto = ClassName.get(dtoPackageName, dtoClassName);
        vo = ClassName.get(voPackageName, voClassName);
        entity = ClassName.get(entityPackageName, entityClassName);
        excelUtil = ClassName.get("com.zjhc.common.utils.poi", "ExcelUtil");
        ajaxResult = ClassName.get("com.zjhc.common.core.domain", "AjaxResult");


    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenController.class;
    }

    @Override
    public Optional<MethodSpec> list() {
        if (StringUtils.containsNull(entityClassName, entityPackageName)) {
            return Optional.empty();
        }

        MethodSpec.Builder builder = MethodSpec.methodBuilder("list")
                .addAnnotation(AnnotationSpec.builder(GetMapping.class).addMember("value", "$S","/list").build())
//                .addAnnotation(AnnotationSpec.builder(ApiOperation.class).addMember("value", "$S", tableName).build())
                .addParameter(ParameterSpec.builder(entity, "param").build())
                .returns(ajaxResult);

        CodeBlock context = CodeBlock.builder()
                .add("$T<$T> list = $N.list($N);\n", List.class, entity, serviceName, "param")
                .add("return AjaxResult.success(list);")
                .build();
        builder.addCode(context);
        return Optional.of(builder.build());
    }

    @Override
    public Optional<MethodSpec> insert() {

        if (StringUtils.containsNull(entityPackageName, entityClassName, serviceName, tableName)) {
            return Optional.empty();
        }

        MethodSpec.Builder builder = MethodSpec.methodBuilder("add")
                .addAnnotation(AnnotationSpec.builder(PostMapping.class).addMember("value", "$S","/add").build())
                .addParameter(ParameterSpec.builder(entity, "record").build())
                .returns(ajaxResult);

        CodeBlock codeBlock = CodeBlock.builder()
                .add("return AjaxResult.success($N.insert(record));\n", serviceName)
                .build();
        builder.addCode(codeBlock);
        return Optional.of(builder.build());
    }

    @Override
    public Optional<MethodSpec> update() {

        if (StringUtils.containsNull(tableName, entityPackageName, entityClassName, serviceName)) {
            return Optional.empty();
        }

        MethodSpec.Builder builder = MethodSpec.methodBuilder("update")
                .addAnnotation(AnnotationSpec.builder(GetMapping.class).addMember("value", "$S","/update").build())
//                .addAnnotation(AnnotationSpec.builder(ApiOperation.class).addMember("value", "$S", tableName).build())
                .addParameter(ParameterSpec.builder(entity, "record").build())
                .returns(ajaxResult);

        CodeBlock codeBlock = CodeBlock.builder()
                .add("return AjaxResult.success($N.update(record));\n", serviceName)
                .build();
        builder.addCode(codeBlock);
        return Optional.of(builder.build());
    }

    @Override
    public Optional<MethodSpec> delete() {

        if (StringUtils.containsNull(tableName, serviceName)) {
            return Optional.empty();
        }

        MethodSpec.Builder builder = MethodSpec.methodBuilder("delete")
                .addAnnotation(AnnotationSpec.builder(GetMapping.class).addMember("value", "$S","/delete").build())
//                .addAnnotation(AnnotationSpec.builder(ApiOperation.class).addMember("value", "$S", tableName).build())
                .addParameter(TypeName.LONG, "id")
                .returns(ajaxResult);

        CodeBlock codeBlock = CodeBlock.builder()
                .add("return AjaxResult.success($N.delete(id));\n", serviceName)
                .build();
        builder.addCode(codeBlock);
        return Optional.of(builder.build());
    }

    @Override
    public Optional<MethodSpec> findById() {

        if (StringUtils.containsNull(tableName, serviceName)) {
            return Optional.empty();
        }

        MethodSpec.Builder builder = MethodSpec.methodBuilder("findById")
                .addAnnotation(AnnotationSpec.builder(GetMapping.class).addMember("value", "$S","/findById").build())
//                .addAnnotation(AnnotationSpec.builder(ApiOperation.class).addMember("value", "$S", tableName).build())
                .addParameter(TypeName.LONG, "id")
                .returns(ajaxResult);

        CodeBlock codeBlock = CodeBlock.builder()
                .add("return AjaxResult.success($N.findById(id));\n", serviceName)
                .build();
        builder.addCode(codeBlock);
        return Optional.of(builder.build());
    }

    @Override
    public Optional<MethodSpec> findByPage() {
        if (StringUtils.containsNull(tableName, dtoPackageName, dtoClassName, voClassName, serviceName)) {
            return Optional.empty();
        }
        MethodSpec.Builder builder = MethodSpec.methodBuilder("findByPage")
                .addAnnotation(AnnotationSpec.builder(GetMapping.class).addMember("value", "$S","/findByPage").build())
//                .addAnnotation(AnnotationSpec.builder(ApiOperation.class).addMember("value", "$S", tableName).build())
                .addParameter(ParameterSpec.builder(dto, "dto").build())
                .returns(ajaxResult);



        CodeBlock.Builder excelCode = CodeBlock.builder()
                .add("List<$T> list = $N.list($N);\n", vo, serviceName, "dto")
                .add("Object[] fieldAndRemarks = $N.obtainFieldAndExcelAnnRemark($N.class);\n", excelUtil, vo)
                .add("TableDataInfo dataTable = getDataTable(list);\n")
                .add("Map<String, Object> map = new HashMap<>();\n")
                .add("map.put(\"data\",dataTable);\n")
                .add("map.put(\"header\",fieldAndRemarks[0]);\n")
                .add("map.put(\"key\",fieldAndRemarks[1]);\n")
                .add("return AjaxResult.success(map);")
                ;

        CodeBlock.Builder normalCode = CodeBlock.builder()
                .add("List<$T> list = $N.list($N);\n", vo, serviceName, "dto")
                .add("Object[] fieldAndRemarks = $N.obtainFieldAndExcelAnnRemark($N.class);\n", excelUtil, vo)
                .add("TableDataInfo dataTable = getDataTable(list);\n")
                .add("Map<String, Object> map = new HashMap<>();\n")
                .add("map.put(\"data\",dataTable);\n")
                .add("return AjaxResult.success(map);")
                ;

        builder.addCode(context.isExport() ? excelCode.build() : normalCode.build());
        return Optional.of(builder.build());
    }

    @Override
    public Optional<MethodSpec> voList() {

        if (StringUtils.containsNull(tableName, dtoPackageName, dtoClassName, voClassName, serviceName)) {
            return Optional.empty();
        }

        MethodSpec.Builder builder = MethodSpec.methodBuilder("volist")
                .addAnnotation(AnnotationSpec.builder(GetMapping.class).addMember("value", "$S","/list").build())
//                .addAnnotation(AnnotationSpec.builder(ApiOperation.class).addMember("value", "$S", tableName).build())
                .addParameter(ParameterSpec.builder(dto, "dto").build())
                .returns(ajaxResult);

        CodeBlock codeBlock = CodeBlock.builder()
                .add("List<$T> list = $N.list($N);\n", voClassName, serviceName, "dto")
                .add("Object[] fieldAndRemarks = $N.obtainFieldAndExcelAnnRemark($N.class);\n", excelUtil, voClassName)
                .add("Map<String, Object> map = new HashMap<>();\n")
                .add("map.put(\"data\",dataTable);\n")
                .add("map.put(\"header\",fieldAndRemarks[0]);\n")
                .add("map.put(\"key\",fieldAndRemarks[1]);\n")
                .add("return AjaxResult.success(map);")
                .build();
        builder.addCode(codeBlock);
        return Optional.of(builder.build());
    }

    public Optional<MethodSpec> export() {

        if (StringUtils.containsNull(tableName, dtoPackageName, dtoClassName, voClassName, serviceName)) {
            return Optional.empty();
        }

        MethodSpec.Builder builder = MethodSpec.methodBuilder("export")
                .addAnnotation(AnnotationSpec.builder(PostMapping.class).addMember("value", "$S","/export").build())
//                .addAnnotation(AnnotationSpec.builder(ApiOperation.class).addMember("value", "$S", tableName).build())
                .addParameter(ParameterSpec.builder(dto, "dto").build())
                .returns(ajaxResult);


        CodeBlock codeBlock = CodeBlock.builder()
                .add("List<$T> list = $N.list($N);\n", vo, serviceName, "dto")
                .add("$T<$T> util = new $T<>($T.class);\n", excelUtil, vo, excelUtil, vo)
                .add("util.exportExcel(response, list,\"数据\");\n")
                .add("return AjaxResult.success();")
                .build();

        builder.addCode(codeBlock);
        return Optional.of(builder.build());
    }
}
