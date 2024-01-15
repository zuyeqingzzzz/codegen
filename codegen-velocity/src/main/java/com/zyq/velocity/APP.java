package com.zyq.velocity;

import com.zyq.common.constant.PackageConstant;
import com.zyq.velocity.common.DbType;
import com.zyq.velocity.domain.GenContext;
import com.zyq.velocity.service.GenService;

import java.util.Arrays;
import java.util.List;

public class APP {
    public static void main(String[] args) {

        GenContext context = new GenContext();
        context.setProjectPath(PackageConstant.SYSTEM_SERVICE_PATH);
        context.setXmlPkName("mybatis.gkmapper");
        context.setMapperPkName("com.zjhc.platform.gkmapper");
        context.setEntityPkName("com.zjhc.platform.gkdomain.entity");

        GenService service = new GenService();
        List<String> tableName = Arrays.asList("sta_user_yxkc_target","sta_area_yxkc_target");
        service.generatorCode(tableName, context, DbType.PgSQL);
    }
}