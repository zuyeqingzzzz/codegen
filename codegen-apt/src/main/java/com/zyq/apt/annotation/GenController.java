package com.zyq.apt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author YiQing
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface GenController {

    String pkName();

    String projectPath();

    String sourcePath() default "src/main/java";

    boolean overrideSource() default false;
}
