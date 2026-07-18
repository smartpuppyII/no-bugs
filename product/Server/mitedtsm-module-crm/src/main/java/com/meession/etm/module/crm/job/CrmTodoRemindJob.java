package com.meession.etm.module.crm.job;

import com.meession.etm.framework.quartz.core.handler.JobHandler;
import com.meession.etm.framework.tenant.core.job.TenantJob;
import com.meession.etm.module.crm.dal.dataobject.contract.CrmContractConfigDO;
import com.meession.etm.module.crm.dal.dataobject.contract.CrmContractDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.dal.dataobject.receivable.CrmReceivablePlanDO;
import com.meession.etm.module.crm.dal.mysql.contract.CrmContractConfigMapper;
import com.meession.etm.module.crm.dal.mysql.contract.CrmContractMapper;
import com.meession.etm.module.crm.dal.mysql.customer.CrmCustomerMapper;
import com.meession.etm.module.crm.dal.mysql.receivable.CrmReceivablePlanMapper;
import com.meession.etm.module.system.api.notify.NotifyMessageSendApi;
import com.meession.etm.module.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * CRM 待办提醒 Job
 * 每日上午汇总推送待办事项
 *
 * @author system
 */
@Component
@Slf4j
public class CrmTodoRemindJob implements JobHandler {

    @Resource
    private CrmCustomerMapper customerMapper;
    @Resource
    private CrmContractMapper contractMapper;
    @Resource
    private CrmContractConfigMapper contractConfigMapper;
    @Resource
    private CrmReceivablePlanMapper receivablePlanMapper;
    @Resource
    private NotifyMessageSendApi notifyMessageSendApi;

    @Override
    @TenantJob
    public String execute(String param) {
        log.info("[CrmTodoRemindJob] 开始执行待办提醒...");
        int totalNotified = 0;

        // 1. 汇总所有有待办的用户
        Map<Long, List<String>> userTodoMap = new HashMap<>();

        // 1.1 今日需联系客户
        List<CrmCustomerDO> todayCustomers = customerMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmCustomerDO>()
                        .isNotNull(CrmCustomerDO::getOwnerUserId)
                        .isNotNull(CrmCustomerDO::getContactNextTime)
                        .ge(CrmCustomerDO::getContactNextTime,
                                cn.hutool.core.date.LocalDateTimeUtil.beginOfDay(LocalDateTime.now()))
                        .le(CrmCustomerDO::getContactNextTime,
                                cn.hutool.core.date.LocalDateTimeUtil.endOfDay(LocalDateTime.now())));
        for (CrmCustomerDO c : todayCustomers) {
            userTodoMap.computeIfAbsent(c.getOwnerUserId(), k -> new ArrayList<>())
                    .add("客户[" + c.getName() + "]今日需联系");
        }

        // 1.2 即将到期合同
        CrmContractConfigDO config = contractConfigMapper.selectOne();
        if (config != null && config.getNotifyEnabled() && config.getNotifyDays() != null) {
            LocalDateTime remindStart = LocalDateTime.now();
            LocalDateTime remindEnd = LocalDateTime.now().plusDays(config.getNotifyDays());
            List<CrmContractDO> expiringContracts = contractMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmContractDO>()
                            .isNotNull(CrmContractDO::getOwnerUserId)
                            .isNotNull(CrmContractDO::getEndTime)
                            .ge(CrmContractDO::getEndTime, remindStart)
                            .le(CrmContractDO::getEndTime, remindEnd));
            for (CrmContractDO ct : expiringContracts) {
                userTodoMap.computeIfAbsent(ct.getOwnerUserId(), k -> new ArrayList<>())
                        .add("合同[" + ct.getName() + "]即将到期");
            }
        }

        // 1.3 待回款提醒
        List<CrmReceivablePlanDO> remindPlans = receivablePlanMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmReceivablePlanDO>()
                        .isNotNull(CrmReceivablePlanDO::getOwnerUserId)
                        .isNotNull(CrmReceivablePlanDO::getRemindTime)
                        .isNull(CrmReceivablePlanDO::getReceivableId) // 未回款
                        .le(CrmReceivablePlanDO::getRemindTime, LocalDateTime.now()));
        for (CrmReceivablePlanDO p : remindPlans) {
            userTodoMap.computeIfAbsent(p.getOwnerUserId(), k -> new ArrayList<>())
                    .add("回款计划第[" + p.getPeriod() + "]期待回款");
        }

        // 2. 发送站内信通知
        for (Map.Entry<Long, List<String>> entry : userTodoMap.entrySet()) {
            try {
                String content = String.join("；", entry.getValue());
                notifyMessageSendApi.sendSingleMessageToAdmin(
                        new NotifySendSingleToUserReqDTO()
                                .setUserId(entry.getKey())
                                .setTemplateCode("crm_todo_remind")
                                .setTemplateParams(Map.of("content", content, "count", String.valueOf(entry.getValue().size()))));
                totalNotified++;
            } catch (Exception e) {
                log.warn("[CrmTodoRemindJob][通知用户({})失败]", entry.getKey(), e);
            }
        }

        log.info("[CrmTodoRemindJob] 完成，通知 {} 个用户", totalNotified);
        return String.format("通知 %d 个用户", totalNotified);
    }

}
