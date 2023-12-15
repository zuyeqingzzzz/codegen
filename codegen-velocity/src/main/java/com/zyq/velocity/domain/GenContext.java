package com.zyq.velocity.domain;


import lombok.Data;

@Data
public class GenContext {

    private String projectPath;
    private String xmlPkName;
    private String entityPkName;
    private String mapperPkName;

}
