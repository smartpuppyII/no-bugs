package com.meession.etm.module.crm.dal.mysql.seapool;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmSeaPoolLimitConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公海领取限制配置 Mapper
 *
 * @author 密讯
 */
@Mapper
public interface CrmSeaPoolLimitConfigMapper extends BaseMapperX<CrmSeaPoolLimitConfigDO> {

    default CrmSeaPoolLimitConfigDO selectOne() {
        return selectOne(new LambdaQueryWrapperX<CrmSeaPoolLimitConfigDO>().last("LIMIT 1"));
    }

}