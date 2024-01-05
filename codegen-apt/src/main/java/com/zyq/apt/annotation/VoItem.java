package com.zyq.apt.annotation;

import com.zyq.apt.constant.ConvertEnum;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface VoItem {

    ConvertEnum converter() default ConvertEnum.DEFAULT;
}
