package zyq.spi;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

public interface CodeGenProcessor {


    /**
     * 获取被注解标记的类
     */
    Class<? extends Annotation> getAnnotation();


    /**
     * 获取生成的包路径
     */
    String generatePackage(TypeElement typeElement);


    /**
     * 生产代码的逻辑
     */
    void generate(TypeElement typeElement, RoundEnvironment roundEnvironment);


}
