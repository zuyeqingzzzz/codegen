package com.zyq.model;

import java.util.Date;
import com.zyq.apt.annotation.*;
import com.zyq.common.constant.PackageConstant;
import lombok.Data;


/**
 * 晨会表
 */
@Data
@GenVo(pkName = "com.zjhc.platform.gkdomain.vo", projectPath = PackageConstant.PLATFORM_SERVICE_PATH)
@GenDto(pkName = "com.zjhc.platform.gkdomain.dto", projectPath = PackageConstant.PLATFORM_SERVICE_PATH)
@GenService(pkName= "com.zjhc.platform.gkservice", projectPath = PackageConstant.PLATFORM_SERVICE_PATH)
@GenServiceImpl(pkName = "com.zjhc.platform.gkservice.impl", projectPath = PackageConstant.PLATFORM_SERVICE_PATH)
@GenController(pkName = "com.zjhc.app.gkcontroller", projectPath = PackageConstant.APP_CONTROLLER_PATH)
public class StdCh
{

    /**  */
    @Key
    private Long chId;


    /**  */
    private String chName;


    /** 晨会编号 */
    @DtoItem
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
