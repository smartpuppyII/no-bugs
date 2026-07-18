package com.meession.etm.module.crm.dal.mysql.tag;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.dal.dataobject.tag.CrmTagDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 标签 Mapper
 *
 * @author system
 */
@Mapper
public interface CrmTagMapper extends BaseMapperX<CrmTagDO> {

    default List<CrmTagDO> selectListByGroup(String groupName) {
        return selectList(new LambdaQueryWrapperX<CrmTagDO>()
                .eqIfPresent(CrmTagDO::getGroupName, groupName)
                .orderByAsc(CrmTagDO::getSortOrder));
    }

    default List<CrmTagDO> selectAllTags() {
        return selectList(new LambdaQueryWrapperX<CrmTagDO>()
                .orderByAsc(CrmTagDO::getSortOrder));
    }

}
