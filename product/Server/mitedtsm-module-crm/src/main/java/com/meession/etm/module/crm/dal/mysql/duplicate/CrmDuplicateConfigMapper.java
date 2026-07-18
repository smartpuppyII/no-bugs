package com.meession.etm.module.crm.dal.mysql.duplicate;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.dal.dataobject.duplicate.CrmDuplicateConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 查重规则配置 Mapper
 *
 * @author system
 */
@Mapper
public interface CrmDuplicateConfigMapper extends BaseMapperX<CrmDuplicateConfigDO> {

    default CrmDuplicateConfigDO selectOne() {
        return selectOne(new LambdaQueryWrapperX<CrmDuplicateConfigDO>());
    }

}
