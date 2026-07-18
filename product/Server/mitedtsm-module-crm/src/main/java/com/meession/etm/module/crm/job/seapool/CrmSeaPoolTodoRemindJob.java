package com.meession.etm.module.crm.job.seapool;

import com.meession.etm.framework.quartz.core.handler.JobHandler;
import com.meession.etm.framework.tenant.core.job.TenantJob;
import com.meession.etm.module.crm.dal.dataobject.clue.CrmClueDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerPoolConfigDO;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmCluePoolConfigDO;
import com.meession.etm.module.crm.dal.mysql.clue.CrmClueMapper;
import com.meession.etm.module.crm.dal.mysql.customer.CrmCustomerMapper;
import com.meession.etm.module.crm.service.customer.CrmCustomerPoolConfigService;
import com.meession.etm.module.crm.service.seapool.CrmCluePoolConfigService;
import com.meession.etm.module.system.api.notify.NotifyMessageSendApi;
import com.meession.etm.module.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 公海回收到期前N天生成待办预警条目
 * 每天定时扫描即将到期客户/线索，生成待办提醒并推送通知
 *
 * @author 密讯
 */
@Component
@Slf4j
public class CrmSeaPoolTodoRemindJob implements JobHandler {

    @Resource
    private CrmCustomerMapper customerMapper;

    @Resource
    private CrmClueMapper clueMapper;

    @Resource
    private CrmCustomerPoolConfigService customerPoolConfigService;

    @Resource
    private CrmCluePoolConfigService cluePoolConfigService;

    @Resource
    private NotifyMessageSendApi notifyMessageSendApi;

    @Override
    @TenantJob
    public String execute(String param) {
        log.info("[CrmSeaPoolTodoRemindJob] 开始执行公海回收预警...");

        // 汇总所有待办预警 → 按用户分组
        Map<Long, List<String>> userTodoMap = new HashMap<>();

        // 1. 客户公海预警
        CrmCustomerPoolConfigDO customerConfig = customerPoolConfigService.getCustomerPoolConfig();
        if (customerConfig != null && customerConfig.getEnabled()
                && customerConfig.getNotifyEnabled() && customerConfig.getNotifyDays() != null) {
            int notifyDays = customerConfig.getNotifyDays();
            // 查询即将到期客户：contactLastTime 在 (now - expireDays + notifyDays) 到 (now - expireDays) 之间
            int expireDays = customerConfig.getDealExpireDays() != null ? customerConfig.getDealExpireDays() : 30;
            LocalDateTime warnStart = LocalDateTime.now().minusDays(expireDays);
            LocalDateTime warnEnd = LocalDateTime.now().minusDays(Math.max(expireDays - notifyDays, 0));

            List<CrmCustomerDO> warningCustomers = customerMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmCustomerDO>()
                            .gt(CrmCustomerDO::getOwnerUserId, 0)
                            .eq(CrmCustomerDO::getLockStatus, false)
                            .eq(CrmCustomerDO::getDealStatus, false)
                            .eq(CrmCustomerDO::getCountdownFreeze, false)
                            .and(q -> q.between(CrmCustomerDO::getContactLastTime, warnStart, warnEnd)
                                    .or().isNull(CrmCustomerDO::getContactLastTime)));

            for (CrmCustomerDO c : warningCustomers) {
                if (c.getOwnerUserId() != null) {
                    long remainingDays = calculateRemainingDays(c.getContactLastTime(), expireDays);
                    userTodoMap.computeIfAbsent(c.getOwnerUserId(), k -> new ArrayList<>())
                            .add("客户[" + c.getName() + "]将于" + remainingDays + "天后自动回收至公海");
                }
            }
        }

        // 2. 线索公海预警
        CrmCluePoolConfigDO clueConfig = cluePoolConfigService.getCluePoolConfig();
        if (clueConfig != null && clueConfig.getEnabled() && clueConfig.getNotifyDays() != null) {
            int expireDays = clueConfig.getClueExpireDays() != null ? clueConfig.getClueExpireDays() : 7;
            int notifyDays = clueConfig.getNotifyDays();
            LocalDateTime warnStart = LocalDateTime.now().minusDays(expireDays);
            LocalDateTime warnEnd = LocalDateTime.now().minusDays(Math.max(expireDays - notifyDays, 0));

            List<CrmClueDO> warningClues = clueMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmClueDO>()
                            .gt(CrmClueDO::getOwnerUserId, 0)
                            .eq(CrmClueDO::getTransformStatus, false)
                            .and(q -> q.between(CrmClueDO::getContactLastTime, warnStart, warnEnd)
                                    .or().isNull(CrmClueDO::getContactLastTime)));

            // A类豁免检查
            for (CrmClueDO c : warningClues) {
                if (Boolean.TRUE.equals(clueConfig.getALevelExemptEnabled()) && c.getLevel() != null && c.getLevel() == 1) {
                    // A类线索：生成预警但不回收，提醒持续生成
                    if (c.getOwnerUserId() != null) {
                        userTodoMap.computeIfAbsent(c.getOwnerUserId(), k -> new ArrayList<>())
                                .add("A类线索[" + c.getName() + "]已超过回收时效，请尽快跟进（A类自动豁免）");
                    }
                    continue;
                }
                if (c.getOwnerUserId() != null) {
                    long remainingDays = calculateRemainingDays(c.getContactLastTime(), expireDays);
                    userTodoMap.computeIfAbsent(c.getOwnerUserId(), k -> new ArrayList<>())
                            .add("线索[" + c.getName() + "]将于" + remainingDays + "天后自动回收至公海");
                }
            }
        }

        // 3. 发送站内信通知
        int totalNotified = 0;
        for (Map.Entry<Long, List<String>> entry : userTodoMap.entrySet()) {
            try {
                String content = String.join("；", entry.getValue());
                notifyMessageSendApi.sendSingleMessageToAdmin(
                        new NotifySendSingleToUserReqDTO()
                                .setUserId(entry.getKey())
                                .setTemplateCode("crm_sea_pool_todo_remind")
                                .setTemplateParams(Map.of("content", content, "count",
                                        String.valueOf(entry.getValue().size()))));
                totalNotified++;
            } catch (Exception e) {
                log.warn("[CrmSeaPoolTodoRemindJob][通知用户({})失败]", entry.getKey(), e);
            }
        }

        log.info("[CrmSeaPoolTodoRemindJob] 完成，通知 {} 个用户", totalNotified);
        return String.format("通知 %d 个用户", totalNotified);
    }

    /**
     * 计算剩余回收天数
     */
    private long calculateRemainingDays(LocalDateTime lastFollowUpTime, int expireDays) {
        if (lastFollowUpTime == null) {
            return 0;
        }
        LocalDateTime expireTime = lastFollowUpTime.plusDays(expireDays);
        long days = java.time.Duration.between(LocalDateTime.now(), expireTime).toDays();
        return Math.max(0, days);
    }

}