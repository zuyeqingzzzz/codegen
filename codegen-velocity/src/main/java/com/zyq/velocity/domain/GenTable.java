package com.zyq.velocity.domain;

import lombok.Data;
import java.util.List;

/**
 * 业务表 gen_table
 * 
 * @author ruoyi
 */
@Data
public class GenTable 
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long tableId;

    /** 表名称 */
    private String tableName;

    /** 表描述 */
    private String tableComment;

    /** 关联父表的表名 */
    private String subTableName;

    /** 本表关联父表的外键名 */
    private String subTableFkName;

    /** 实体类名称(首字母大写) */
    private String className;

    /** 生成包路径 */
    private String packageName;

    private String entityPkName;

    private String mapperPkName;

    private String xmlPkName;

    /** 生成模块名 */
    private String moduleName;

    /** 生成业务名 */
    private String businessName;

    /** 生成功能名 */
    private String functionName;

    /** 生成作者 */
    private String functionAuthor;

    /** 生成代码方式（0zip压缩包 1自定义路径） */
    private String genType;

    /** 生成路径（不填默认项目路径） */
    private String genPath;

    /** 主键信息 */
    private GenTableColumn pkColumn;

    /** 表列信息 */
    private List<GenTableColumn> columns;

}