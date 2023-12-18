package com.zyq.velocity;

import com.zyq.velocity.common.DbType;
import com.zyq.velocity.domain.GenContext;
import com.zyq.velocity.service.GenService;

public class APP {
    public static void main(String[] args) {

        GenContext context = new GenContext();
        context.setProjectPath("D:\\work\\project\\tzcloud\\stdwk-tz\\std-service\\std-platform-service");
        context.setXmlPkName("mybatis.gkmapper");
        context.setMapperPkName("com.zjhc.platform.gkmapper");
        context.setEntityPkName("com.zjhc.platform.gkdomain.entity");

        GenService service = new GenService();
        service.generatorCode("std_manage_post", context, DbType.PgSQL);
    }
}