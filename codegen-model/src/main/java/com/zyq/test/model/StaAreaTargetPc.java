package com.zyq.test.model;


import constant.PackageConstant;
import lombok.Data;
import zyq.annotation.*;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 
 * 支局关键动作
 */
@Data
@GenConfig(projectPath = PackageConstant.PROJECT_PATH)
@GenVo(pkName = "com.zjhc.pc.controller.djrest", export = true)
@GenDto(pkName = "com.zjhc.pc.controller.djrest")
@GenService(pkName = "com.zjhc.pc.controller.djrest")
@GenServiceImpl(pkName = "com.zjhc.pc.controller.djrest")
@GenController(pkName = "com.zjhc.pc.controller.djrest")
public class StaAreaTargetPc implements Serializable {
    /**
     * 主键
     */
    private Long fid;

    /**
     * 日期
     */
    @DtoItem(converter = String.class)
    private String dateCd;

    /**
     * 地市 默认76
     */
    private Long latnId;

    /**
     * 区域id
     */
    private Long areaId;

    /**
     * 区域名
     */
    private String areaName;

    /**
     * 县分id
     */
    private Long xsId;

    /**
     * 支局id
     */
    private Long zjId;

    /**
     * 支局类型 1=综合支局 2=政企支局 3=BU
     */
    private String zjType;

    /**
     * 支局高套目标
     */
    private BigDecimal gtValue;

    /**
     * 支局高套完成
     */
    private BigDecimal gtRate;

    /**
     *  是否完成任务 -1 不需要 0:否 1:是
     */
    @VoItem
    private String isFinish;

    /**
     * 统计类型3 上午产能(10:30) 4中午复盘(12:30)  
5中午复盘(15:00) 6跟班计划(17:00) 
7入户补课(20:30) 8跟班计划(16:00)
     */
    @VoItem
    private String staType;

    @VoItem()
    private Date updateTime;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}