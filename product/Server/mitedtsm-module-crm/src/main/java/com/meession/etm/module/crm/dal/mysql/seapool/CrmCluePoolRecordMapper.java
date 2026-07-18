package com.meession.etm.module.crm.dal.mysql.seapool;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmCluePoolRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 线索公海流转记录 Mapper
 *
 * @author 密讯
 */
@Mapper
public interface CrmCluePoolRecordMapper extends BaseMapperX<CrmCluePoolRecordDO> {

    /**
     * 查询线索和用户在冷却期内的最近一次归还记录
     */
    default CrmCluePoolRecordDO selectLastReturnByClueIdAndUserId(Long clueId, Long userId) {
        return selectOne(new LambdaQueryWrapperX<CrmCluePoolRecordDO>()
                .eq(CrmCluePoolRecordDO::getClueId, clueId)
                .eq(CrmCluePoolRecordDO::getFromUserId, userId)
                .eq(CrmCluePoolRecordDO::getOperateType, 2) // 手动退回
                .orderByDesc(CrmCluePoolRecordDO::getOperateTime)
                .last("LIMIT 1"));
    }

}