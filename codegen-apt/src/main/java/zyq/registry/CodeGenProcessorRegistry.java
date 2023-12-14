package zyq.registry;
import zyq.spi.CodeGenProcessor;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YiQing
 * @version 1.0
 */

public class CodeGenProcessorRegistry {

    private static Map<String, ? extends CodeGenProcessor> PROCESS;


    public static Set<String> getSupportAnnotations() {
        return PROCESS.keySet();
    }

    public static CodeGenProcessor find(String annClassName) {
        return PROCESS.get(annClassName);
    }

    /**
     * SPI加载所有的CodeGenProcessor
     */
    public static void iniProcessors() {
        Map<String, CodeGenProcessor> map = new TreeMap<>();

        ServiceLoader<CodeGenProcessor> load = ServiceLoader.load(CodeGenProcessor.class, CodeGenProcessor.class.getClassLoader());
        Iterator<CodeGenProcessor> iterator = load.iterator();
        while (iterator.hasNext()) {
            CodeGenProcessor next = iterator.next();
            Class<? extends Annotation> annotation = next.getAnnotation();
            map.put(annotation.getName(), next);
        }
        PROCESS = map;
    }
}
