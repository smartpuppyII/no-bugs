package com.meession.etm.module.crm.dal.mysql.seapool;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmCustomerPoolRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户公海流转记录 Mapper
 *
 * @author 密讯
 */
@Mapper
public interface CrmCustomerPoolRecordMapper extends BaseMapperX<CrmCustomerPoolRecordDO> {

    /**
     * 查询客户和用户在冷却期内的最近一次归还记录
     */
    default CrmCustomerPoolRecordDO selectLastReturnByCustomerIdAndUserId(Long customerId, Long userId) {
        return selectOne(new LambdaQueryWrapperX<CrmCustomerPoolRecordDO>()
                .eq(CrmCustomerPoolRecordDO::getCustomerId, customerId)
                .eq(CrmCustomerPoolRecordDO::getFromUserId, userId)
                .eq(CrmCustomerPoolRecordDO::getOperateType, 2) // 手动退回
                .orderByDesc(CrmCustomerPoolRecordDO::getOperateTime)
                .last("LIMIT 1"));
    }

}