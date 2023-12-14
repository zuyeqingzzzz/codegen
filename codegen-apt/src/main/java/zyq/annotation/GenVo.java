package zyq.annotation;

import java.lang.annotation.*;

/**
 * @author YiQing
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenVo {

    String pkName();

    String sourcePath() default "src/main/java";

    boolean overrideSource() default false;

    // 是否需要导出 为true 自动加上Excel注解
    boolean export() default false;
}
