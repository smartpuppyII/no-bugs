package com.meession.etm.module.crm.dal.mysql.visit;

import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.dal.dataobject.visit.CrmVisitPlanDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CrmVisitPlanMapper extends BaseMapperX<CrmVisitPlanDO> {

    default PageResult<CrmVisitPlanDO> selectPage(PageParam pageParam, Long userId, String customerName, Integer status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<CrmVisitPlanDO>()
                .eqIfPresent(CrmVisitPlanDO::getOwnerUserId, userId)
                .likeIfPresent(CrmVisitPlanDO::getCustomerName, customerName)
                .eqIfPresent(CrmVisitPlanDO::getStatus, status)
                .orderByDesc(CrmVisitPlanDO::getVisitTime));
    }
}
