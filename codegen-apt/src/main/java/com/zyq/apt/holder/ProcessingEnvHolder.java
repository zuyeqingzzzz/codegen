package com.zyq.apt.holder;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * @author YiQing
 * @version 1.0
 */
public class ProcessingEnvHolder {

    private static final ThreadLocal<ProcessingEnvironment> holder = new ThreadLocal<>();

    public static void setHolder(ProcessingEnvironment environment){
        holder.set(environment);
    }

    public static ProcessingEnvironment getHolder() {
        return holder.get();
    }

}
