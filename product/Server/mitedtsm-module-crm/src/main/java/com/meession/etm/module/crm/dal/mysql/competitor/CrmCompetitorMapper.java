package com.meession.etm.module.crm.dal.mysql.competitor;

import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.dal.dataobject.competitor.CrmCompetitorDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CrmCompetitorMapper extends BaseMapperX<CrmCompetitorDO> {

    default PageResult<CrmCompetitorDO> selectPage(PageParam pageParam, String name) {
        return selectPage(pageParam, new LambdaQueryWrapperX<CrmCompetitorDO>()
                .likeIfPresent(CrmCompetitorDO::getName, name)
                .orderByDesc(CrmCompetitorDO::getId));
    }

}
