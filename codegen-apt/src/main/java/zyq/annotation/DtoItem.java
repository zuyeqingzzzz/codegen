package zyq.annotation;

import java.lang.annotation.*;

/**
 * @author YiQing
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DtoItem {

    Class<?> converter();
}
