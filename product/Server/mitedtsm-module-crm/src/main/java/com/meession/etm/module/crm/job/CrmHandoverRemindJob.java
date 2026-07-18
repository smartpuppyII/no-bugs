package com.meession.etm.module.crm.job;

import com.meession.etm.framework.quartz.core.handler.JobHandler;
import com.meession.etm.framework.tenant.core.job.TenantJob;
import com.meession.etm.module.crm.dal.dataobject.transfer.CrmTransferLogDO;
import com.meession.etm.module.crm.dal.mysql.transfer.CrmTransferLogMapper;
import com.meession.etm.module.system.api.notify.NotifyMessageSendApi;
import com.meession.etm.module.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * CRM 离职交接未处理提醒 Job
 * 扫描超过3天未处理的交接记录，向接收人发送提醒
 *
 * @author system
 */
@Component
@Slf4j
public class CrmHandoverRemindJob implements JobHandler {

    @Resource
    private CrmTransferLogMapper transferLogMapper;
    @Resource
    private NotifyMessageSendApi notifyMessageSendApi;

    @Override
    @TenantJob
    public String execute(String param) {
        log.info("[CrmHandoverRemindJob] 开始扫描未处理交接记录...");
        int totalReminded = 0;

        // 查询3天前创建的离职交接记录（transferType=2）
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        List<CrmTransferLogDO> pendingTransfers = transferLogMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmTransferLogDO>()
                        .eq(CrmTransferLogDO::getTransferType, 2) // 离职交接
                        .le(CrmTransferLogDO::getCreateTime, threeDaysAgo));

        for (CrmTransferLogDO transfer : pendingTransfers) {
            try {
                notifyMessageSendApi.sendSingleMessageToAdmin(
                        new NotifySendSingleToUserReqDTO()
                                .setUserId(transfer.getToUserId())
                                .setTemplateCode("crm_handover_notify")
                                .setTemplateParams(Map.of(
                                        "fromUserName", "系统",
                                        "bizTypeName", "离职交接数据")));
                totalReminded++;
            } catch (Exception e) {
                log.warn("[CrmHandoverRemindJob][提醒用户({})处理交接({})失败]",
                        transfer.getToUserId(), transfer.getId(), e);
            }
        }

        log.info("[CrmHandoverRemindJob] 完成，提醒 {} 条未处理交接记录", totalReminded);
        return String.format("提醒 %d 条未处理交接记录", totalReminded);
    }

}
