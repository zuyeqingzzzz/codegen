package com.zyq.apt.annotation;

import com.zyq.apt.constant.ConvertEnum;

import java.lang.annotation.*;

/**
 * @author YiQing
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface DtoItem {

    ConvertEnum converter() default ConvertEnum.DEFAULT;
}
