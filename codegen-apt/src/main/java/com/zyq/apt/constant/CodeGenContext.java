package com.zyq.apt.constant;

import com.squareup.javapoet.TypeName;
import com.zyq.common.constant.MethodEnum;
import lombok.Data;
import javax.lang.model.element.VariableElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YiQing
 * @version 1.0
 */
@Data
public class CodeGenContext {

    private String projectPath;
    MethodEnum[] methodNames;

    private String controllerClassName;
    private String controllerPackageName;
    private boolean overrideController;
    private String serviceClassName;
    private String servicePackageName;
    private boolean overrideService;
    private String implClassName;
    private String implPackageName;
    private boolean overrideImpl;
    private String voClassName;
    private String voPackageName;
    private boolean overrideVo;
    private String dtoClassName;
    private String dtoPackageName;
    private boolean overrideDto;
    private String entityClassName;
    private String entityPackageName;
    private boolean overrideEntity;

    private String mapperClassName;
    private String mapperPackageName;
    private String key;

    /**
     * 是否需要导出 如果是的话 vo会加上Excel注解 controller也会有导出方法
     */
    private boolean export;

    /**
     * 字段注释
     */
    private Map<VariableElement, String> fieldComment = new HashMap<>();
    private String tableComment;

    private Map<VariableElement, TypeName> voConvertMap = new HashMap<>();
    private Map<VariableElement, TypeName> dtoConvertMap = new HashMap<>();
}
