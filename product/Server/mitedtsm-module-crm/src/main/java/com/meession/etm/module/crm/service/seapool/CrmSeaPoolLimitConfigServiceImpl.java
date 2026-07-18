package com.meession.etm.module.crm.service.seapool;

import com.meession.etm.module.crm.dal.dataobject.seapool.CrmSeaPoolLimitConfigDO;
import com.meession.etm.module.crm.dal.mysql.seapool.CrmSeaPoolLimitConfigMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 公海领取限制配置 Service 实现类
 *
 * @author 密讯
 */
@Service
@Validated
public class CrmSeaPoolLimitConfigServiceImpl implements CrmSeaPoolLimitConfigService {

    @Resource
    private CrmSeaPoolLimitConfigMapper limitConfigMapper;

    @Override
    public CrmSeaPoolLimitConfigDO getLimitConfig() {
        return limitConfigMapper.selectOne();
    }

}