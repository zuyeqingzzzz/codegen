package com.zyq.velocity.util;

import com.zyq.common.utils.StringUtils;
import com.zyq.velocity.domain.GenContext;
import com.zyq.velocity.constant.GenConstants;
import com.zyq.velocity.domain.GenTable;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.velocity.VelocityContext;
import com.zyq.velocity.domain.GenTableColumn;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

/**
 * 模板处理工具类
 * 
 * @author ruoyi
 */
public class VelocityUtils
{
    /** 项目空间路径 */
    private static final String PROJECT_PATH = "src/main/java";

    /** mybatis空间路径 */
    private static final String MYBATIS_PATH = "src/main/resources";

    /**
     * 设置模板变量信息
     *
     * @return 模板列表
     */
    public static VelocityContext prepareContext(GenTable genTable, GenContext genContext)
    {

        String packageName = genTable.getPackageName();
        String functionName = genTable.getTableComment();
        genTable.setClassName(StringUtils.convertToCamelCase(genTable.getTableName()));

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("tableName", genTable.getTableName());
        velocityContext.put("functionName", StringUtils.isNotEmpty(functionName) ? functionName : "【请填写功能名称】");
        velocityContext.put("ClassName", StringUtils.convertToCamelCase(genTable.getTableName()));
        velocityContext.put("className", StringUtils.uncapitalize(genTable.getClassName()));
        velocityContext.put("moduleName", genTable.getModuleName());
        velocityContext.put("entityPkName", genContext.getEntityPkName());
        velocityContext.put("mapperPkName", genContext.getMapperPkName());
        velocityContext.put("xmlPkName", genContext.getXmlPkName());
        velocityContext.put("modelPkName", "com.zyq.model");
        velocityContext.put("packageName", packageName);
        velocityContext.put("datetime", DateFormatUtils.format(new Date(), "yyyy-mm-dd"));
        velocityContext.put("pkColumn", genTable.getPkColumn());
        velocityContext.put("importList", getImportList(genTable));
        velocityContext.put("columns", genTable.getColumns());
        velocityContext.put("table", genTable);

        // 判断生成的是pc端代码还是app端代码
        boolean pc = genContext.getProjectPath().contains("system");
        String dtoPkName = pc ? "com.zjhc.system.domain.dto" : "com.zjhc.platform.gkdomain.dto";
        String voPkName = pc ? "com.zjhc.system.domain.vo" : "com.zjhc.platform.gkdomain.vo";
        String servicePkName = pc ? "com.zjhc.system.service" : "com.zjhc.platform.gkservice";
        String serviceImplPkName = pc ? "com.zjhc.system.service.impl" : "com.zjhc.platform.gkservice.impl";
        String controllerPkName = pc ? "com.zjhc.pc.controller.system" : "com.zjhc.app.gkcontroller";

        String controllerPath = pc ? "PackageConstant.ADMIN_CONTROLLER_PATH" : "PackageConstant.APP_CONTROLLER_PATH";
        String servicePath = pc ? "PackageConstant.SYSTEM_SERVICE_PATH" : "PackageConstant.PLATFORM_SERVICE_PATH";

        velocityContext.put("dtoPkName", dtoPkName);
        velocityContext.put("voPkName", voPkName);
        velocityContext.put("servicePkName", servicePkName);
        velocityContext.put("serviceImplPkName", serviceImplPkName);
        velocityContext.put("controllerPkName", controllerPkName);
        velocityContext.put("controllerPath", controllerPath);
        velocityContext.put("servicePath", servicePath);
        return velocityContext;
    }

    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<String> getTemplateList()
    {
        List<String> templates = new ArrayList<>();
        templates.add("vm/java/domain.java.vm");
        templates.add("vm/java/model.java.vm");
        templates.add("vm/java/mapper.java.vm");
        templates.add("vm/xml/mapper.xml.vm");
        return templates;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, GenTable genTable,GenContext genContext)
    {
        // 文件名称
        String fileName = "";
        // 包路径
        String xmlPkName = genContext.getXmlPkName().replace(".", File.separator);
        String mapperPkName = genContext.getMapperPkName().replace(".", File.separator);
        String entityPkName = genContext.getEntityPkName().replace(".", File.separator);

        // 大写类名
        String className = genTable.getClassName();

        String javaPath = PROJECT_PATH;


        if (template.contains("domain.java.vm"))
        {
            fileName = MessageFormat.format("{0}/{1}/{2}.java", javaPath,entityPkName, className);
        }

        else if (template.contains("model.java.vm"))
        {
            // model 直接生成到codegen-model里面
            String userDir = System.getProperty("user.dir");
            String modelPath = userDir + "\\codegen-model\\src\\main\\java\\com\\zyq\\model";
            fileName = MessageFormat.format("{0}/{1}.java", modelPath, className);
        }
        else if (template.contains("mapper.java.vm"))
        {
            fileName = MessageFormat.format("{0}/{1}/{2}Mapper.java", javaPath, mapperPkName, className);
        }

        else if (template.contains("mapper.xml.vm"))
        {
            fileName = MessageFormat.format("{0}/{1}/{2}Mapper.xml", MYBATIS_PATH, xmlPkName, className);
        }

        return fileName;
    }


    /**
     * 根据列类型获取导入包
     * 
     * @param genTable 业务表对象
     * @return 返回需要导入的包列表
     */
    public static HashSet<String> getImportList(GenTable genTable)
    {
        List<GenTableColumn> columns = genTable.getColumns();
        HashSet<String> importList = new HashSet<>();

        for (GenTableColumn column : columns)
        {  if (GenConstants.TYPE_DATE.equals(column.getJavaType()))
        {
            importList.add("java.util.Date");
        }
        else if (GenConstants.TYPE_BIGDECIMAL.equals(column.getJavaType()))
            {
                importList.add("java.math.BigDecimal");
            }
        }
        return importList;
    }


    /**
     * 初始化列属性字段
     */
    public static void initColumnField(GenTableColumn column, GenTable table)
    {
        String dataType = getDbType(column.getColumnType());
        String columnName = column.getColumnName();
        column.setTableId(table.getTableId());
        // 设置java字段名
        column.setJavaField(StringUtils.toCamelCase(columnName));
        // 设置默认类型
        column.setJavaType(GenConstants.TYPE_STRING);
        column.setQueryType(GenConstants.QUERY_EQ);

        // 数据库字段有name 给它设置模糊搜索
        if (StringUtils.endsWithIgnoreCase(columnName, "name"))
        {
            column.setQueryType(GenConstants.QUERY_LIKE);
        }

        if (arraysContains(GenConstants.COLUMNTYPE_STR, dataType) || arraysContains(GenConstants.COLUMNTYPE_TEXT, dataType))
        {
            column.setJavaType(GenConstants.TYPE_STRING);
        }
        else if (arraysContains(GenConstants.COLUMNTYPE_TIME, dataType))
        {
            column.setJavaType(GenConstants.TYPE_DATE);
        }
        else if (arraysContains(GenConstants.COLUMNTYPE_NUMBER, dataType))
        {

            // 如果是浮点型 统一用BigDecimal
            String[] str = StringUtils.split(StringUtils.substringBetween(column.getColumnType(), "(", ")"), ",");
            if (str != null && str.length == 2 && Integer.parseInt(str[1]) > 0)
            {
                column.setJavaType(GenConstants.TYPE_BIGDECIMAL);
            }
            // 如果是整形
            else if (str != null && str.length == 1 && Integer.parseInt(str[0]) <= 10)
            {
                column.setJavaType(GenConstants.TYPE_INTEGER);
            } else if (dataType.equals("numeric")) {
                column.setJavaType(GenConstants.TYPE_BIGDECIMAL);
            } // 长整形
            else
            {
                column.setJavaType(GenConstants.TYPE_LONG);
            }
        }
    }

    /**
     * 校验数组是否包含指定值
     *
     * @param arr 数组
     * @param targetValue 值
     * @return 是否包含
     */
    public static boolean arraysContains(String[] arr, String targetValue)
    {
        return Arrays.asList(arr).contains(targetValue);
    }

    /**
     * 获取数据库类型字段
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static String getDbType(String columnType)
    {
        if (StringUtils.indexOf(columnType, "(") > 0)
        {
            return StringUtils.substringBefore(columnType, "(");
        }
        else
        {
            return columnType;
        }
    }
}
