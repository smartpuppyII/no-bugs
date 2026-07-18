package com.meession.etm.module.crm.service.customer;

import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.customer.vo.poolconfig.CrmCustomerPoolConfigSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerPoolConfigDO;
import com.meession.etm.module.crm.dal.mysql.customer.CrmCustomerPoolConfigMapper;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

import static com.meession.etm.module.crm.enums.LogRecordConstants.*;

/**
 * 客户公海配置 Service 实现类
 *
 * @author Wanwan
 */
@Service
@Validated
public class CrmCustomerPoolConfigServiceImpl implements CrmCustomerPoolConfigService {

    @Resource
    private CrmCustomerPoolConfigMapper customerPoolConfigMapper;

    @Override
    public CrmCustomerPoolConfigDO getCustomerPoolConfig() {
        return customerPoolConfigMapper.selectOne();
    }

    @Override
    @LogRecord(type = CRM_CUSTOMER_POOL_CONFIG_TYPE, subType = CRM_CUSTOMER_POOL_CONFIG_SUB_TYPE, bizNo = "{{#poolConfigId}}",
            success = CRM_CUSTOMER_POOL_CONFIG_SUCCESS)
    public void saveCustomerPoolConfig(CrmCustomerPoolConfigSaveReqVO saveReqVO) {
        // 1. 存在，则进行更新
        CrmCustomerPoolConfigDO dbConfig = getCustomerPoolConfig();
        CrmCustomerPoolConfigDO poolConfig = BeanUtils.toBean(saveReqVO, CrmCustomerPoolConfigDO.class);
        if (Objects.nonNull(dbConfig)) {
            customerPoolConfigMapper.updateById(poolConfig.setId(dbConfig.getId()));
            // 记录操作日志上下文
            LogRecordContext.putVariable("isPoolConfigUpdate", Boolean.TRUE);
            LogRecordContext.putVariable("poolConfigId", poolConfig.getId());
            return;
        }

        // 2. 不存在，则进行插入
        customerPoolConfigMapper.insert(poolConfig);
        // 记录操作日志上下文
        LogRecordContext.putVariable("isPoolConfigUpdate", Boolean.FALSE);
        LogRecordContext.putVariable("poolConfigId", poolConfig.getId());
    }

    @Override
    @LogRecord(type = CRM_CUSTOMER_POOL_CONFIG_TYPE, subType = "更新客户等级回收时效", bizNo = "{{#poolConfigId}}",
            success = "更新了客户等级回收时效为【{{#levelExpireDays}}】")
    public void updateLevelExpireConfig(String levelExpireDays) {
        CrmCustomerPoolConfigDO dbConfig = getCustomerPoolConfig();
        if (dbConfig == null) {
            dbConfig = new CrmCustomerPoolConfigDO();
            dbConfig.setLevelExpireDays(levelExpireDays);
            customerPoolConfigMapper.insert(dbConfig);
            LogRecordContext.putVariable("poolConfigId", dbConfig.getId());
            return;
        }
        dbConfig.setLevelExpireDays(levelExpireDays);
        customerPoolConfigMapper.updateById(dbConfig);
        LogRecordContext.putVariable("poolConfigId", dbConfig.getId());
    }

    @Override
    @LogRecord(type = CRM_CUSTOMER_POOL_CONFIG_TYPE, subType = "更新合同/回款暂停配置", bizNo = "{{#poolConfigId}}",
            success = "更新了合同/回款暂停配置：合同暂停={{#pauseContractEnabled}}，回款暂停={{#pauseReceivableEnabled}}")
    public void updatePauseConfig(Boolean pauseContractEnabled, Boolean pauseReceivableEnabled) {
        CrmCustomerPoolConfigDO dbConfig = getCustomerPoolConfig();
        if (dbConfig == null) {
            dbConfig = new CrmCustomerPoolConfigDO();
            dbConfig.setPauseContractEnabled(pauseContractEnabled);
            dbConfig.setPauseReceivableEnabled(pauseReceivableEnabled);
            customerPoolConfigMapper.insert(dbConfig);
            LogRecordContext.putVariable("poolConfigId", dbConfig.getId());
            return;
        }
        dbConfig.setPauseContractEnabled(pauseContractEnabled);
        dbConfig.setPauseReceivableEnabled(pauseReceivableEnabled);
        customerPoolConfigMapper.updateById(dbConfig);
        LogRecordContext.putVariable("poolConfigId", dbConfig.getId());
    }

}
