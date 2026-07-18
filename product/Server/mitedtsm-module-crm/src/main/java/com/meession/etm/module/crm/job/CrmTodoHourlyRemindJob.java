package com.meession.etm.module.crm.job;

import com.meession.etm.framework.quartz.core.handler.JobHandler;
import com.meession.etm.framework.tenant.core.job.TenantJob;
import com.meession.etm.module.crm.dal.dataobject.contract.CrmContractConfigDO;
import com.meession.etm.module.crm.dal.dataobject.contract.CrmContractDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.dal.mysql.contract.CrmContractConfigMapper;
import com.meession.etm.module.crm.dal.mysql.contract.CrmContractMapper;
import com.meession.etm.module.crm.dal.mysql.customer.CrmCustomerMapper;
import com.meession.etm.module.system.api.notify.NotifyMessageSendApi;
import com.meession.etm.module.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * CRM 小时级待办提醒 Job
 * 每小时扫描即将到期的待办事项并推送提醒
 *
 * @author system
 */
@Component
@Slf4j
public class CrmTodoHourlyRemindJob implements JobHandler {

    @Resource
    private CrmCustomerMapper customerMapper;
    @Resource
    private CrmContractMapper contractMapper;
    @Resource
    private CrmContractConfigMapper contractConfigMapper;
    @Resource
    private NotifyMessageSendApi notifyMessageSendApi;

    @Override
    @TenantJob
    public String execute(String param) {
        log.info("[CrmTodoHourlyRemindJob] 开始执行小时级待办提醒...");
        int totalNotified = 0;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        Map<Long, List<String>> userTodoMap = new HashMap<>();

        // 1. 未来1小时内需联系的客户
        List<CrmCustomerDO> imminentCustomers = customerMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmCustomerDO>()
                        .isNotNull(CrmCustomerDO::getOwnerUserId)
                        .isNotNull(CrmCustomerDO::getContactNextTime)
                        .ge(CrmCustomerDO::getContactNextTime, now)
                        .le(CrmCustomerDO::getContactNextTime, oneHourLater));
        for (CrmCustomerDO c : imminentCustomers) {
            userTodoMap.computeIfAbsent(c.getOwnerUserId(), k -> new ArrayList<>())
                    .add("客户[" + c.getName() + "]即将需要联系（1小时内）");
        }

        // 2. 未来1小时内到期的合同
        CrmContractConfigDO config = contractConfigMapper.selectOne();
        if (config != null && config.getNotifyEnabled()) {
            List<CrmContractDO> imminentContracts = contractMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmContractDO>()
                            .isNotNull(CrmContractDO::getOwnerUserId)
                            .isNotNull(CrmContractDO::getEndTime)
                            .ge(CrmContractDO::getEndTime, now)
                            .le(CrmContractDO::getEndTime, oneHourLater));
            for (CrmContractDO ct : imminentContracts) {
                userTodoMap.computeIfAbsent(ct.getOwnerUserId(), k -> new ArrayList<>())
                        .add("合同[" + ct.getName() + "]即将到期（1小时内）");
            }
        }

        // 3. 发送通知
        for (Map.Entry<Long, List<String>> entry : userTodoMap.entrySet()) {
            try {
                String content = String.join("；", entry.getValue());
                notifyMessageSendApi.sendSingleMessageToAdmin(
                        new NotifySendSingleToUserReqDTO()
                                .setUserId(entry.getKey())
                                .setTemplateCode("crm_todo_remind")
                                .setTemplateParams(Map.of(
                                        "content", content,
                                        "count", String.valueOf(entry.getValue().size()))));
                totalNotified++;
            } catch (Exception e) {
                log.warn("[CrmTodoHourlyRemindJob][通知用户({})失败]", entry.getKey(), e);
            }
        }

        log.info("[CrmTodoHourlyRemindJob] 完成，通知 {} 个用户", totalNotified);
        return String.format("通知 %d 个用户", totalNotified);
    }

}
