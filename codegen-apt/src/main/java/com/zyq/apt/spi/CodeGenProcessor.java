package com.zyq.apt.spi;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

public interface CodeGenProcessor {


    /**
     * 获取被注解标记的类
     */
    Class<? extends Annotation> getAnnotation();



    /**
     * 生成代码的逻辑
     */
    void generate(TypeElement typeElement, RoundEnvironment roundEnvironment);


}
