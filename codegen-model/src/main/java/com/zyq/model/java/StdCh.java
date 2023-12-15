package com.zyq.model.java;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;


/**
 * 晨会表 std_ch
 */

@Data
public class StdCh implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long chId;


    /**  */
    private String chName;


    /** 晨会编号 */
    private String chNo;


    /** 填写人 */
    private Long fillUser;


    /** 所属部门 */
    private Long belongDept;


    /** 所属支局ID */
    private Long belongZj;


    /** 填写日期 格式20221114 */
    private String fillDate;


    /** 1填写 2汇总 */
    private String statisticsType;


    /** 晨会区域 */
    private Long chDept;


    /** 4：支局 5：片区  */
    private Long lvl;


    /**  */
    private Date createTime;


    /**  */
    private Long createBy;


    /**  */
    private Date updateTime;


    /**  */
    private Long updateBy;


    /** 删除：0 正常 1 删除 */
    private String del;



}
