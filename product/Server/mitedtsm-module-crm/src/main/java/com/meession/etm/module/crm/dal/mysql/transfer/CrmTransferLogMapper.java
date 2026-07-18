package com.meession.etm.module.crm.dal.mysql.transfer;

import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.dal.dataobject.transfer.CrmTransferLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 转移日志 Mapper
 *
 * @author system
 */
@Mapper
public interface CrmTransferLogMapper extends BaseMapperX<CrmTransferLogDO> {

    default PageResult<CrmTransferLogDO> selectPage(PageParam pageParam, Integer bizType) {
        LambdaQueryWrapperX<CrmTransferLogDO> query = new LambdaQueryWrapperX<CrmTransferLogDO>()
                .eqIfPresent(CrmTransferLogDO::getBizType, bizType)
                .orderByDesc(CrmTransferLogDO::getCreateTime);
        return selectPage(pageParam, query);
    }

}
