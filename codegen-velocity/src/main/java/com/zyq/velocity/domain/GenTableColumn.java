package com.zyq.velocity.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 代码生成业务字段表 gen_table_column
 * 
 * @author ruoyi
 */
@Data
public class GenTableColumn implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long columnId;

    /** 归属表编号 */
    private Long tableId;

    /** 列名称 */
    private String columnName;

    /** 列描述 */
    private String columnComment;

    /** 列类型 */
    private String columnType;

    /** JAVA类型 */
    private String javaType;

    private String queryType;

    /** JAVA字段名 */
    private String javaField;

    /** 是否主键（1是） */
    private String isPk;

    /** 是否自增（1是） */
    private String isIncrement;

    public String getCapJavaField()
    {
        return StringUtils.capitalize(javaField);
    }
}
