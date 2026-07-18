package com.meession.etm.module.crm.job.seapool;

import com.meession.etm.framework.quartz.core.handler.JobHandler;
import com.meession.etm.framework.tenant.core.job.TenantJob;
import com.meession.etm.module.crm.dal.dataobject.clue.CrmClueDO;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmCluePoolConfigDO;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmCluePoolRecordDO;
import com.meession.etm.module.crm.dal.mysql.clue.CrmClueMapper;
import com.meession.etm.module.crm.dal.mysql.seapool.CrmCluePoolRecordMapper;
import com.meession.etm.module.crm.service.clue.CrmClueService;
import com.meession.etm.module.crm.service.seapool.CrmCluePoolConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 线索自动回收定时任务
 * 支持：A类豁免、跟进记录重置倒计时
 *
 * @author 密讯
 */
@Component
@Slf4j
public class CrmClueAutoPutPoolJob implements JobHandler {

    @Resource
    private CrmClueService clueService;

    @Resource
    private CrmClueMapper clueMapper;

    @Resource
    private CrmCluePoolConfigService cluePoolConfigService;

    @Resource
    private CrmCluePoolRecordMapper cluePoolRecordMapper;

    private static final int DEFAULT_EXPIRE_DAYS = 7;

    @Override
    @TenantJob
    public String execute(String param) {
        CrmCluePoolConfigDO poolConfig = cluePoolConfigService.getCluePoolConfig();
        if (poolConfig == null || !poolConfig.getEnabled()) {
            return "线索公海未启用，跳过";
        }

        int expireDays = poolConfig.getClueExpireDays() != null ? poolConfig.getClueExpireDays() : DEFAULT_EXPIRE_DAYS;
        int totalReclaimed = 0;
        int totalExempt = 0;
        int totalSkipped = 0;

        // 1. 查询超时未跟进的线索
        List<CrmClueDO> clueList = clueMapper.selectListByAutoPool(expireDays);

        for (CrmClueDO clue : clueList) {
            try {
                // 2. A类豁免检查
                if (Boolean.TRUE.equals(poolConfig.getALevelExemptEnabled()) && isALevelClue(clue.getLevel())) {
                    log.debug("[ClueAutoPutPool][线索({}) A类豁免，跳过自动回收]", clue.getId());
                    totalExempt++;
                    continue;
                }

                // 3. 跟进记录重置倒计时：已通过 selectListByAutoPool 的 contactLastTime 条件处理
                // 如果 followUpResetEnabled=true，每次跟进后 contactLastTime 自动更新，重置倒计时

                // 4. 执行回收
                clueService.putCluePool(clue.getId());
                totalReclaimed++;

                // 5. 记录流转日志
                cluePoolRecordMapper.insert(CrmCluePoolRecordDO.builder()
                        .clueId(clue.getId())
                        .fromUserId(clue.getOwnerUserId())
                        .toUserId(null)
                        .operateType(1) // 自动回收
                        .reason("自动回收：超过" + expireDays + "天未跟进")
                        .operateTime(LocalDateTime.now())
                        .build());

            } catch (Throwable e) {
                log.error("[ClueAutoPutPool][线索({}) 放入公海异常]", clue.getId(), e);
                totalSkipped++;
            }
        }

        String result = String.format("回收线索 %d 个，A类豁免 %d 个，跳过 %d 个",
                totalReclaimed, totalExempt, totalSkipped);
        log.info("[CrmClueAutoPutPoolJob] {}", result);
        return result;
    }

    /**
     * 判断是否为A类线索（level=1）
     */
    private boolean isALevelClue(Integer level) {
        return level != null && level == 1;
    }

}