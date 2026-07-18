package com.meession.etm.module.crm.job.customer;

import cn.hutool.core.util.StrUtil;
import com.meession.etm.framework.quartz.core.handler.JobHandler;
import com.meession.etm.framework.tenant.core.job.TenantJob;
import com.meession.etm.module.crm.dal.dataobject.contract.CrmContractDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerPoolConfigDO;
import com.meession.etm.module.crm.dal.dataobject.receivable.CrmReceivablePlanDO;
import com.meession.etm.module.crm.dal.mysql.contract.CrmContractMapper;
import com.meession.etm.module.crm.dal.mysql.customer.CrmCustomerMapper;
import com.meession.etm.module.crm.dal.mysql.receivable.CrmReceivablePlanMapper;
import com.meession.etm.module.crm.service.customer.CrmCustomerPoolConfigService;
import com.meession.etm.module.crm.service.customer.CrmCustomerService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户自动掉入公海 Job（V2：分级回收时效 + A类豁免 + 合同/回款暂停计时 + 跟进记录重置倒计时）
 *
 * @author 密讯
 */
@Component
@Slf4j
public class CrmCustomerAutoPutPoolJob implements JobHandler {

    @Resource
    private CrmCustomerService customerService;

    @Resource
    private CrmCustomerMapper customerMapper;

    @Resource
    private CrmCustomerPoolConfigService customerPoolConfigService;

    @Resource
    private CrmContractMapper contractMapper;

    @Resource
    private CrmReceivablePlanMapper receivablePlanMapper;

    /**
     * 客户等级 → 回收时效天数 的默认映射
     * 当 levelExpireDays JSON 未配置时使用
     */
    private static final int DEFAULT_EXPIRE_DAYS = 30;

    @Override
    @TenantJob
    public String execute(String param) {
        CrmCustomerPoolConfigDO poolConfig = customerPoolConfigService.getCustomerPoolConfig();
        if (poolConfig == null || !poolConfig.getEnabled()) {
            return "客户公海未启用，跳过";
        }

        int totalReclaimed = 0;
        int totalSkipped = 0;
        int totalExempt = 0;
        int totalFrozen = 0;

        // 1. 按等级分别处理：先获取所有有负责人的客户，按等级分组计算回收时效
        // 等级枚举值参考：A类=1, B类=2, C类=3 等（根据字典配置）
        // 这里直接遍历所有客户，根据各自等级计算时效
        List<CrmCustomerDO> allCustomers = customerMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmCustomerDO>()
                        .gt(CrmCustomerDO::getOwnerUserId, 0)
                        .eq(CrmCustomerDO::getLockStatus, false)
                        .eq(CrmCustomerDO::getDealStatus, false));

        for (CrmCustomerDO customer : allCustomers) {
            try {
                // 2. 计算该客户等级对应的回收时效
                int expireDays = getLevelExpireDays(poolConfig, customer.getLevel());

                // 3. A类客户豁免检查：A类(level=1)不回收
                if (isALevelCustomer(customer.getLevel()) && expireDays > DEFAULT_EXPIRE_DAYS) {
                    log.debug("[autoPutCustomerPool][客户({}) A类豁免，跳过自动回收]", customer.getId());
                    totalExempt++;
                    continue;
                }

                // 4. 倒计时冻结检查
                if (Boolean.TRUE.equals(customer.getCountdownFreeze())) {
                    log.debug("[autoPutCustomerPool][客户({}) 倒计时已冻结，原因: {}]", customer.getId(), customer.getFreezeReason());
                    totalFrozen++;
                    continue;
                }

                // 5. 合同暂停计时检查
                if (Boolean.TRUE.equals(poolConfig.getPauseContractEnabled())) {
                    if (hasUnexpiredContract(customer.getId())) {
                        // 冻结倒计时，等待合同到期后恢复
                        customerMapper.updateById(new CrmCustomerDO().setId(customer.getId())
                                .setCountdownFreeze(true).setFreezeReason("存在未到期合同"));
                        totalFrozen++;
                        continue;
                    }
                }

                // 6. 回款暂停计时检查
                if (Boolean.TRUE.equals(poolConfig.getPauseReceivableEnabled())) {
                    if (hasPendingReceivable(customer.getId())) {
                        // 冻结倒计时，等待回款完成后恢复
                        customerMapper.updateById(new CrmCustomerDO().setId(customer.getId())
                                .setCountdownFreeze(true).setFreezeReason("存在待回款计划"));
                        totalFrozen++;
                        continue;
                    }
                }

                // 7. 跟进记录重置倒计时：基于 contactLastTime 判断
                LocalDateTime lastFollowUpTime = customer.getContactLastTime();
                if (lastFollowUpTime == null) {
                    // 从未跟进过，使用 ownerTime 作为基准
                    lastFollowUpTime = customer.getOwnerTime();
                }
                if (lastFollowUpTime == null) {
                    continue;
                }

                LocalDateTime expireTime = LocalDateTime.now().minusDays(expireDays);
                if (lastFollowUpTime.isAfter(expireTime)) {
                    // 未超时，跳过
                    continue;
                }

                // 8. 超时，执行回收
                customerService.putCustomerPool(customer.getId());
                totalReclaimed++;

                log.debug("[autoPutCustomerPool][客户({}) 回收成功，时效: {}天]", customer.getId(), expireDays);

            } catch (Throwable e) {
                log.error("[autoPutCustomerPool][客户({}) 放入公海异常]", customer.getId(), e);
                totalSkipped++;
            }
        }

        String result = String.format("回收客户 %d 个，跳过 %d 个，A类豁免 %d 个，冻结 %d 个",
                totalReclaimed, totalSkipped, totalExempt, totalFrozen);
        log.info("[CrmCustomerAutoPutPoolJob] {}", result);
        return result;
    }

    /**
     * 根据客户等级获取回收时效天数
     */
    private int getLevelExpireDays(CrmCustomerPoolConfigDO poolConfig, Integer level) {
        if (StrUtil.isBlank(poolConfig.getLevelExpireDays()) || level == null) {
            // 回退到旧配置
            return poolConfig.getDealExpireDays() != null ? poolConfig.getDealExpireDays() : DEFAULT_EXPIRE_DAYS;
        }
        try {
            // levelExpireDays 格式: {"A":90,"B":30,"C":15}
            // 需要根据 level 数字编码映射到等级字母
            String levelKey = mapLevelToKey(level);
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Integer> levelMap = mapper.readValue(poolConfig.getLevelExpireDays(),
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Integer>>() {});
            Integer days = levelMap.get(levelKey);
            if (days != null) {
                return days;
            }
        } catch (Exception e) {
            log.warn("[getLevelExpireDays][解析 levelExpireDays JSON 失败: {}]", e.getMessage());
        }
        return poolConfig.getDealExpireDays() != null ? poolConfig.getDealExpireDays() : DEFAULT_EXPIRE_DAYS;
    }

    /**
     * 将等级数字编码映射为字母键
     * 约定：1=A, 2=B, 3=C（系统仅支持A/B/C三级客户）
     */
    private String mapLevelToKey(Integer level) {
        if (level == null) return "C";
        return switch (level) {
            case 1 -> "A";
            case 2 -> "B";
            case 3 -> "C";
            default -> "C";
        };
    }

    /**
     * 判断是否为A类客户（level=1）
     */
    private boolean isALevelCustomer(Integer level) {
        return level != null && level == 1;
    }

    /**
     * 检查客户是否存在未到期合同
     */
    private boolean hasUnexpiredContract(Long customerId) {
        List<CrmContractDO> contracts = contractMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmContractDO>()
                        .eq(CrmContractDO::getCustomerId, customerId)
                        .gt(CrmContractDO::getEndTime, LocalDateTime.now()));
        return contracts != null && !contracts.isEmpty();
    }

    /**
     * 检查客户是否存在待回款计划
     */
    private boolean hasPendingReceivable(Long customerId) {
        List<CrmReceivablePlanDO> plans = receivablePlanMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmReceivablePlanDO>()
                        .eq(CrmReceivablePlanDO::getCustomerId, customerId)
                        .isNull(CrmReceivablePlanDO::getReceivableId)); // 未回款
        return plans != null && !plans.isEmpty();
    }

}