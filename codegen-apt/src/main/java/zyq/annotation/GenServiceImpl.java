package zyq.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author YiQing
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface GenServiceImpl {

    String pkName();

    String sourcePath() default "src/main/java";

    boolean overrideSource() default false;

    // 映射层框架用的是啥 目前仅支持mybatis 后面有需要在扩展
    String ormType() default "mybatis";
}
