package com.zyq.velocity;

import com.zyq.velocity.common.DbType;
import com.zyq.velocity.domain.GenContext;
import com.zyq.velocity.service.GenService;

public class APP {
    public static void main(String[] args) {

        GenContext context = new GenContext();
        context.setProjectPath("D:\\work\\project\\mys\\codegen\\codegen-model");
        context.setXmlPkName("mybatis");
        context.setEntityPkName("com.zyq.model.java");
        context.setMapperPkName("com.zyq.model.java");

        GenService service = new GenService();
        service.generatorCode("std_ch", context, DbType.PgSQL);
    }
}