package org.example;

import org.example.common.DbType;
import org.example.service.GenService;

public class Main {
    public static void main(String[] args) {

        GenContext context = new GenContext();
        context.setXmlPkName("mybatis");
        context.setEntityPkName("org.example.domain");
        context.setMapperPkName("org.example.domain");

        GenService service = new GenService();
        service.generatorCode("std_ch", context, DbType.PgSQL);
    }
}