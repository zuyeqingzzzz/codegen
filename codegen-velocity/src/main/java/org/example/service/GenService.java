package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.example.GenContext;
import org.example.common.DbType;
import org.example.domain.GenTable;
import org.example.util.VelocityInitializer;
import org.example.util.VelocityUtils;
import utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Slf4j
public class GenService {


    private final String url = "jdbc:postgresql://134.108.88.103:18802/hc_bch?currentSchema=stdwk&ApplicationName=stdwkAPP&serverTimezone=CST&binaryTransfer=true&stringtype=unspecified";
    private final String userName = "hc_bch";
    private final String passWord = "|hc_bch_njp";


    private final Map<DbType, QueryDataSource> queryDataSourceMap = new HashMap<>();

    public GenService() {
        queryDataSourceMap.put(DbType.PgSQL, new QueryPgSQL());
        queryDataSourceMap.put(DbType.MySQL, new QueryMySQL());
        queryDataSourceMap.put(DbType.Oracle, new QueryOracle());
    }

    /**
     * 生成代码（自定义路径）
     *
     * @param tableName 表名称
     */
    public void generatorCode(String tableName, GenContext genContext, DbType dbType)
    {
        // 查询表信息
        QueryDataSource queryDataSource = queryDataSourceMap.get(dbType);
        queryDataSource.iniDateSource(url, userName, passWord);
        GenTable table = queryDataSource.queryDb(tableName);

        fillGenTable(table, genContext);

        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList();
        for (String template : templates)
        {
            if (!StringUtils.containsAny(template, "sql.vm", "api.js.vm", "index.vue.vm", "index-tree.vue.vm"))
            {
                // 渲染模板
                StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, StandardCharsets.UTF_8.name());
                tpl.merge(context, sw);
                try
                {
                    String path = getGenPath(table, template);
                    FileUtils.writeStringToFile(new File(path), sw.toString(), StandardCharsets.UTF_8);
                }
                catch (IOException e)
                {
                    throw new RuntimeException("渲染模板失败，表名：" + table.getTableName());
                }
            }
        }
    }

    private void fillGenTable(GenTable table, GenContext context) {

        table.setClassName(StringUtils.convertToCamelCase(table.getTableName()));

        table.setFunctionName(table.getTableComment());
        table.setEntityPkName(context.getEntityPkName());
        table.setMapperPkName(context.getMapperPkName());
        table.setXmlPkName(context.getXmlPkName());
    }


    /**
     * 获取代码生成地址
     *
     * @param table 业务表信息
     * @param template 模板文件路径
     * @return 生成地址
     */
    public static String getGenPath(GenTable table, String template)
    {
        String genPath = "";
        if (StringUtils.equals(genPath, "/"))
        {
            return System.getProperty("user.dir") + File.separator + "src" + File.separator + VelocityUtils.getFileName(template, table);
        }
        return genPath + File.separator + VelocityUtils.getFileName(template, table);
    }


}
