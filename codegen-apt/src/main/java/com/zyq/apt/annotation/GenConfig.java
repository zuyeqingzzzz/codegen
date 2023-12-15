package com.zyq.apt.annotation;


import com.zyq.common.constant.MethodEnum;

import java.lang.annotation.*;

/**
 * @author YiQing
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenConfig {

    String entityPkName();

    String mapperPkName();

    // 需要生成哪些方法
    MethodEnum[] methodName() default {MethodEnum.LIST, MethodEnum.INSERT, MethodEnum.DELETE, MethodEnum.EDIT};


}
