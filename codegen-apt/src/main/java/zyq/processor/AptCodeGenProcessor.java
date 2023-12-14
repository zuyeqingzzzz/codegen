package zyq.processor;

import com.google.auto.service.AutoService;
import zyq.holder.ProcessingEnvHolder;
import zyq.registry.CodeGenProcessorRegistry;
import zyq.spi.CodeGenProcessor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * @author YiQing
 * @version 1.0
 */
@AutoService(Processor.class)
public class AptCodeGenProcessor extends AbstractProcessor {


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(an -> {
            Set<? extends Element> annotatedWith = roundEnv.getElementsAnnotatedWith(an);
            Set<TypeElement> typeElements = ElementFilter.typesIn(annotatedWith);
            for (TypeElement type : typeElements) {
                CodeGenProcessor codeGenProcessor = CodeGenProcessorRegistry.find(an.getQualifiedName().toString());
                try {
                    codeGenProcessor.generate(type, roundEnv);
                } catch (Exception e) {
                    ProcessingEnvHolder.getHolder().getMessager().printMessage(Diagnostic.Kind.WARNING, "代码生成异常了额" + e.getMessage());
                }
            }
        });
        ProcessingEnvHolder.getHolder().getMessager().printMessage(Diagnostic.Kind.NOTE,  "代码生成完毕");
        // 设置该次处理不是终止操作
        return false;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        ProcessingEnvHolder.setHolder(processingEnv);
        CodeGenProcessorRegistry.iniProcessors();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        // 指定源代码版本为java8
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        // 把标记的注解返回给框架处理
        return CodeGenProcessorRegistry.getSupportAnnotations();
    }
}
