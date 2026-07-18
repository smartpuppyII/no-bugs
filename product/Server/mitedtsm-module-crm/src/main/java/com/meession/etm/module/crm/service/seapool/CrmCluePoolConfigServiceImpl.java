package com.meession.etm.module.crm.service.seapool;

import com.meession.etm.module.crm.dal.dataobject.seapool.CrmCluePoolConfigDO;
import com.meession.etm.module.crm.dal.mysql.seapool.CrmCluePoolConfigMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 线索公海配置 Service 实现类
 *
 * @author 密讯
 */
@Service("seapoolCrmCluePoolConfigServiceImpl")
@Validated
public class CrmCluePoolConfigServiceImpl implements CrmCluePoolConfigService {

    @Resource
    private CrmCluePoolConfigMapper cluePoolConfigMapper;

    @Override
    public CrmCluePoolConfigDO getCluePoolConfig() {
        return cluePoolConfigMapper.selectOne();
    }

}