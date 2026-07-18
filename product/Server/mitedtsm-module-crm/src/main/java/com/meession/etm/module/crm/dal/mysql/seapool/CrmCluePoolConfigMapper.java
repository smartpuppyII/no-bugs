package com.meession.etm.module.crm.dal.mysql.seapool;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmCluePoolConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 线索公海配置 Mapper
 *
 * @author 密讯
 */
@Mapper
public interface CrmCluePoolConfigMapper extends BaseMapperX<CrmCluePoolConfigDO> {

    default CrmCluePoolConfigDO selectOne() {
        return selectOne(new LambdaQueryWrapperX<CrmCluePoolConfigDO>().last("LIMIT 1"));
    }

}