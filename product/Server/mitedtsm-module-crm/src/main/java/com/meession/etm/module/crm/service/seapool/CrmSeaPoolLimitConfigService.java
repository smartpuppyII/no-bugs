package com.meession.etm.module.crm.service.seapool;

import com.meession.etm.module.crm.dal.dataobject.seapool.CrmSeaPoolLimitConfigDO;

/**
 * 公海领取限制配置 Service 接口
 *
 * @author 密讯
 */
public interface CrmSeaPoolLimitConfigService {

    /**
     * 获得公海领取限制配置
     *
     * @return 公海领取限制配置
     */
    CrmSeaPoolLimitConfigDO getLimitConfig();

}