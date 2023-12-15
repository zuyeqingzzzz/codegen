package com.zyq.model.java;

import java.util.List;
import com.zyq.model.java.StdCh;

/**
 * 晨会表
 */
public interface StdChMapper 
{
    /**
     * 查询晨会表
     * 
     * @param chId 晨会表主键
     * @return 晨会表
     */
    StdCh selectStdChByChId(Long chId);

    /**
     * 查询晨会表列表
     * 
     * @param stdCh 晨会表
     * @return 晨会表集合
     */
    List<StdCh> selectStdChList(StdCh stdCh);

    /**
     * 新增晨会表
     * 
     * @param stdCh 晨会表
     * @return 成功数
     */
    int insertStdCh(StdCh stdCh);

    /**
     * 批量新增晨会表
     *
     * @param stdChList 晨会表
     * @return 成功数
     */
    int insertBatch(List<StdCh> stdChList);

    /**
     * 修改晨会表
     * 
     * @param stdCh 晨会表
     * @return 成功数
     */
    int updateStdCh(StdCh stdCh);

    /**
     * 删除晨会表
     * 
     * @param chId 晨会表主键
     * @return 成功数
     */
    int deleteStdChByChId(Long chId);

    /**
     * 批量删除晨会表
     * 
     * @param chIds 需要删除的数据主键集合
     * @return 成功数
     */
    int deleteStdChByChIds(Long[] chIds);

}