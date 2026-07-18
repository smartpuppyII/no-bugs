package com.meession.etm.module.crm.service.customer;

import com.meession.etm.module.crm.controller.admin.customer.vo.poolconfig.CrmCustomerPoolConfigSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerPoolConfigDO;

import jakarta.validation.Valid;

/**
 * 客户公海配置 Service 接口
 *
 * @author Wanwan
 */
public interface CrmCustomerPoolConfigService {

    /**
     * 获得客户公海配置
     *
     * @return 客户公海配置
     */
    CrmCustomerPoolConfigDO getCustomerPoolConfig();

    /**
     * 保存客户公海配置
     *
     * @param saveReqVO 更新信息
     */
    void saveCustomerPoolConfig(@Valid CrmCustomerPoolConfigSaveReqVO saveReqVO);

    /**
     * 更新客户等级回收时效配置
     *
     * @param levelExpireDays 客户等级回收时效JSON
     */
    void updateLevelExpireConfig(String levelExpireDays);

    /**
     * 更新合同/回款暂停回收配置
     *
     * @param pauseContractEnabled   是否暂停有合同客户的回收
     * @param pauseReceivableEnabled 是否暂停有应收款客户的回收
     */
    void updatePauseConfig(Boolean pauseContractEnabled, Boolean pauseReceivableEnabled);

}
