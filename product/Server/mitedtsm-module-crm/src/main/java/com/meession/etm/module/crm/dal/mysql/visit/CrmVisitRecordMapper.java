package com.meession.etm.module.crm.dal.mysql.visit;

import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.dal.dataobject.visit.CrmVisitRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CrmVisitRecordMapper extends BaseMapperX<CrmVisitRecordDO> {

    default PageResult<CrmVisitRecordDO> selectPage(PageParam pageParam, Long userId) {
        return selectPage(pageParam, new LambdaQueryWrapperX<CrmVisitRecordDO>()
                .eqIfPresent(CrmVisitRecordDO::getOwnerUserId, userId)
                .orderByDesc(CrmVisitRecordDO::getCheckInTime));
    }

    default List<CrmVisitRecordDO> selectByPlanId(Long planId) {
        return selectList(CrmVisitRecordDO::getPlanId, planId);
    }

    default Long selectCountByUserId(Long userId) {
        return selectCount(CrmVisitRecordDO::getOwnerUserId, userId);
    }
}
