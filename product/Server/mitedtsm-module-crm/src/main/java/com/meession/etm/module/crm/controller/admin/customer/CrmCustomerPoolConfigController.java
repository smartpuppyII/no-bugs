package com.meession.etm.module.crm.controller.admin.customer;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.customer.vo.poolconfig.CrmCustomerLevelExpireSaveReqVO;
import com.meession.etm.module.crm.controller.admin.customer.vo.poolconfig.CrmCustomerPauseConfigSaveReqVO;
import com.meession.etm.module.crm.controller.admin.customer.vo.poolconfig.CrmCustomerPoolConfigRespVO;
import com.meession.etm.module.crm.controller.admin.customer.vo.poolconfig.CrmCustomerPoolConfigSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerPoolConfigDO;
import com.meession.etm.module.crm.service.customer.CrmCustomerPoolConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - CRM 客户公海配置")
@RestController
@RequestMapping("/crm/customer-pool-config")
@Validated
public class CrmCustomerPoolConfigController {

    @Resource
    private CrmCustomerPoolConfigService customerPoolConfigService;

    @GetMapping("/get")
    @Operation(summary = "获取客户公海规则设置")
    @PreAuthorize("@ss.hasPermission('crm:customer-pool-config:query')")
    public CommonResult<CrmCustomerPoolConfigRespVO> getCustomerPoolConfig() {
        CrmCustomerPoolConfigDO poolConfig = customerPoolConfigService.getCustomerPoolConfig();
        return success(BeanUtils.toBean(poolConfig, CrmCustomerPoolConfigRespVO.class));
    }

    @PutMapping("/save")
    @Operation(summary = "更新客户公海规则设置")
    @PreAuthorize("@ss.hasPermission('crm:customer-pool-config:update')")
    public CommonResult<Boolean> saveCustomerPoolConfig(@Valid @RequestBody CrmCustomerPoolConfigSaveReqVO updateReqVO) {
        customerPoolConfigService.saveCustomerPoolConfig(updateReqVO);
        return success(true);
    }

    // ======================= 客户等级回收时效 =======================

    @GetMapping("/get-level-expire-config")
    @Operation(summary = "获取客户等级回收时效配置")
    @PreAuthorize("@ss.hasPermission('crm:customer-pool-config:query')")
    public CommonResult<Map<String, Object>> getLevelExpireConfig() {
        CrmCustomerPoolConfigDO poolConfig = customerPoolConfigService.getCustomerPoolConfig();
        Map<String, Object> result = new HashMap<>();
        if (poolConfig != null) {
            result.put("levelExpireDays", poolConfig.getLevelExpireDays());
            result.put("extendMaxCount", poolConfig.getExtendMaxCount());
        }
        return success(result);
    }

    @PutMapping("/save-level-expire-config")
    @Operation(summary = "保存客户等级回收时效配置")
    @PreAuthorize("@ss.hasPermission('crm:customer-pool-config:update')")
    public CommonResult<Boolean> saveLevelExpireConfig(@Valid @RequestBody CrmCustomerLevelExpireSaveReqVO updateReqVO) {
        customerPoolConfigService.updateLevelExpireConfig(updateReqVO.getLevelExpireDays());
        return success(true);
    }

    // ======================= 合同/回款暂停配置 =======================

    @GetMapping("/get-pause-config")
    @Operation(summary = "获取合同/回款暂停回收配置")
    @PreAuthorize("@ss.hasPermission('crm:customer-pool-config:query')")
    public CommonResult<Map<String, Object>> getPauseConfig() {
        CrmCustomerPoolConfigDO poolConfig = customerPoolConfigService.getCustomerPoolConfig();
        Map<String, Object> result = new HashMap<>();
        if (poolConfig != null) {
            result.put("pauseContractEnabled", poolConfig.getPauseContractEnabled());
            result.put("pauseReceivableEnabled", poolConfig.getPauseReceivableEnabled());
        }
        return success(result);
    }

    @PutMapping("/save-pause-config")
    @Operation(summary = "保存合同/回款暂停回收配置")
    @PreAuthorize("@ss.hasPermission('crm:customer-pool-config:update')")
    public CommonResult<Boolean> savePauseConfig(@Valid @RequestBody CrmCustomerPauseConfigSaveReqVO updateReqVO) {
        customerPoolConfigService.updatePauseConfig(
                updateReqVO.getPauseContractEnabled(), updateReqVO.getPauseReceivableEnabled());
        return success(true);
    }

}
