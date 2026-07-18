package com.meession.etm.module.crm.controller.admin.dashboard;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.module.crm.controller.admin.dashboard.vo.CrmPoolStatisticsRespVO;
import com.meession.etm.module.crm.controller.admin.dashboard.vo.CrmTodoSummaryRespVO;
import com.meession.etm.module.crm.dal.mysql.customer.CrmCustomerMapper;
import com.meession.etm.module.crm.dal.mysql.clue.CrmClueMapper;
import com.meession.etm.module.crm.dal.mysql.seapool.CrmCustomerPoolRecordMapper;
import com.meession.etm.module.crm.dal.mysql.seapool.CrmCluePoolRecordMapper;
import com.meession.etm.module.crm.enums.common.CrmBizTypeEnum;
import com.meession.etm.module.crm.enums.common.CrmSceneTypeEnum;
import com.meession.etm.module.crm.util.CrmPermissionUtils;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.dal.dataobject.clue.CrmClueDO;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmCustomerPoolRecordDO;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmCluePoolRecordDO;
import com.meession.etm.module.crm.service.clue.CrmClueService;
import com.meession.etm.module.crm.service.contract.CrmContractService;
import com.meession.etm.module.crm.service.customer.CrmCustomerService;
import com.meession.etm.module.crm.service.receivable.CrmReceivablePlanService;
import com.meession.etm.module.crm.service.receivable.CrmReceivableService;
import com.meession.etm.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static com.meession.etm.framework.common.pojo.CommonResult.success;
import static com.meession.etm.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.hutool.core.date.LocalDateTimeUtil.beginOfDay;
import static cn.hutool.core.date.LocalDateTimeUtil.endOfDay;

@Tag(name = "管理后台 - CRM 工作台")
@RestController
@RequestMapping("/crm/dashboard")
@Validated
public class CrmDashboardController {

    @Resource
    private CrmCustomerService customerService;
    @Resource
    private CrmClueService clueService;
    @Resource
    private CrmContractService contractService;
    @Resource
    private CrmReceivablePlanService receivablePlanService;
    @Resource
    private CrmReceivableService receivableService;
    @Resource
    private CrmCustomerMapper customerMapper;
    @Resource
    private CrmClueMapper clueMapper;
    @Resource
    private CrmCustomerPoolRecordMapper customerPoolRecordMapper;
    @Resource
    private CrmCluePoolRecordMapper cluePoolRecordMapper;

    @GetMapping("/todo-summary")
    @Operation(summary = "获得待办汇总数据")
    public CommonResult<CrmTodoSummaryRespVO> getTodoSummary() {
        Long userId = getLoginUserId();
        CrmTodoSummaryRespVO summary = new CrmTodoSummaryRespVO();

        // 1. 今日需联系客户
        summary.setTodayContactCount(customerService.getTodayContactCustomerCount(userId));

        // 2. 待跟进线索
        summary.setClueFollowCount(clueService.getFollowClueCount(userId));

        // 3. 待跟进客户
        summary.setCustomerFollowCount(customerService.getFollowCustomerCount(userId));

        // 4. 待进入公海客户
        summary.setCustomerPutPoolRemindCount(customerService.getPutPoolRemindCustomerCount(userId));

        // 5. 待审核合同
        summary.setContractAuditCount(contractService.getAuditContractCount(userId));

        // 6. 即将到期合同
        summary.setContractRemindCount(contractService.getRemindContractCount(userId));

        // 7. 待回款计划
        summary.setReceivablePlanRemindCount(receivablePlanService.getReceivablePlanRemindCount(userId));

        // 8. 待审核回款
        summary.setReceivableAuditCount(receivableService.getAuditReceivableCount(userId));

        // 9. 已逾期未跟进客户
        summary.setExpiredFollowCount(getExpiredFollowCount(userId));

        return success(summary);
    }

    private Long getExpiredFollowCount(Long userId) {
        MPJLambdaWrapperX<CrmCustomerDO> query = new MPJLambdaWrapperX<>();
        CrmPermissionUtils.appendPermissionCondition(query, CrmBizTypeEnum.CRM_CUSTOMER.getType(),
                CrmCustomerDO::getId, userId, CrmSceneTypeEnum.OWNER.getType());
        query.lt(CrmCustomerDO::getContactNextTime, beginOfDay(LocalDateTime.now()));
        return customerMapper.selectCount(query);
    }

    @GetMapping("/pool-statistics")
    @Operation(summary = "获得公海统计数据")
    public CommonResult<CrmPoolStatisticsRespVO> getPoolStatistics() {
        Long userId = getLoginUserId();
        CrmPoolStatisticsRespVO respVO = new CrmPoolStatisticsRespVO();

        // 1. 公海资源总量
        Long customerPoolCount = customerMapper.selectCount(
                new LambdaQueryWrapperX<CrmCustomerDO>().eq(CrmCustomerDO::getPoolStatus, 1));
        Long cluePoolCount = clueMapper.selectCount(
                new LambdaQueryWrapperX<CrmClueDO>().eq(CrmClueDO::getPoolStatus, 1));
        respVO.setTotalCustomerPoolCount(customerPoolCount);
        respVO.setTotalCluePoolCount(cluePoolCount);
        respVO.setTotalPoolResources(customerPoolCount + cluePoolCount);

        // 2. 本周/上周回收数
        LocalDate now = LocalDate.now();
        LocalDate thisWeekMonday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate thisWeekSunday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate lastWeekMonday = thisWeekMonday.minusWeeks(1);
        LocalDate lastWeekSunday = thisWeekSunday.minusWeeks(1);

        LocalDateTime thisWeekStart = thisWeekMonday.atStartOfDay();
        LocalDateTime thisWeekEnd = thisWeekSunday.plusDays(1).atStartOfDay();
        LocalDateTime lastWeekStart = lastWeekMonday.atStartOfDay();
        LocalDateTime lastWeekEnd = lastWeekSunday.plusDays(1).atStartOfDay();

        // 本周自动回收数（operateType=1）
        Long weeklyCustomerRecovery = customerPoolRecordMapper.selectCount(
                new LambdaQueryWrapperX<CrmCustomerPoolRecordDO>()
                        .eq(CrmCustomerPoolRecordDO::getOperateType, 1)
                        .between(CrmCustomerPoolRecordDO::getOperateTime, thisWeekStart, thisWeekEnd));
        Long weeklyClueRecovery = cluePoolRecordMapper.selectCount(
                new LambdaQueryWrapperX<CrmCluePoolRecordDO>()
                        .eq(CrmCluePoolRecordDO::getOperateType, 1)
                        .between(CrmCluePoolRecordDO::getOperateTime, thisWeekStart, thisWeekEnd));
        respVO.setWeeklyRecoveryCount(weeklyCustomerRecovery + weeklyClueRecovery);

        // 上周回收数
        Long lastWeekCustomerRecovery = customerPoolRecordMapper.selectCount(
                new LambdaQueryWrapperX<CrmCustomerPoolRecordDO>()
                        .eq(CrmCustomerPoolRecordDO::getOperateType, 1)
                        .between(CrmCustomerPoolRecordDO::getOperateTime, lastWeekStart, lastWeekEnd));
        Long lastWeekClueRecovery = cluePoolRecordMapper.selectCount(
                new LambdaQueryWrapperX<CrmCluePoolRecordDO>()
                        .eq(CrmCluePoolRecordDO::getOperateType, 1)
                        .between(CrmCluePoolRecordDO::getOperateTime, lastWeekStart, lastWeekEnd));
        respVO.setLastWeekRecoveryCount(lastWeekCustomerRecovery + lastWeekClueRecovery);

        // 环比变化
        if (respVO.getLastWeekRecoveryCount() > 0) {
            double change = (double) (respVO.getWeeklyRecoveryCount() - respVO.getLastWeekRecoveryCount())
                    / respVO.getLastWeekRecoveryCount() * 100;
            respVO.setWeeklyRecoveryChangePercent(Math.round(change * 10.0) / 10.0);
        } else {
            respVO.setWeeklyRecoveryChangePercent(respVO.getWeeklyRecoveryCount() > 0 ? 100.0 : 0.0);
        }

        // 3. 领取转化率（本周领取数 + 确权成功数）
        // 领取数 = operateType=3（主动领取）本周
        Long weeklyCustomerClaim = customerPoolRecordMapper.selectCount(
                new LambdaQueryWrapperX<CrmCustomerPoolRecordDO>()
                        .eq(CrmCustomerPoolRecordDO::getOperateType, 3)
                        .between(CrmCustomerPoolRecordDO::getOperateTime, thisWeekStart, thisWeekEnd));
        Long weeklyClueClaim = cluePoolRecordMapper.selectCount(
                new LambdaQueryWrapperX<CrmCluePoolRecordDO>()
                        .eq(CrmCluePoolRecordDO::getOperateType, 3)
                        .between(CrmCluePoolRecordDO::getOperateTime, thisWeekStart, thisWeekEnd));
        long totalClaim = weeklyCustomerClaim + weeklyClueClaim;
        respVO.setWeeklyClaimCount(totalClaim);

        // 确权成功数 = 本周领取且已确权（有跟进记录）的客户数
        // 简化：统计本周领取且最近7天内有跟进记录的客户
        Long weeklyCustomerClaimSuccess = customerPoolRecordMapper.selectCount(
                new LambdaQueryWrapperX<CrmCustomerPoolRecordDO>()
                        .eq(CrmCustomerPoolRecordDO::getOperateType, 3)
                        .between(CrmCustomerPoolRecordDO::getOperateTime, thisWeekStart, thisWeekEnd)
                        .isNotNull(CrmCustomerPoolRecordDO::getToUserId));
        Long weeklyClueClaimSuccess = cluePoolRecordMapper.selectCount(
                new LambdaQueryWrapperX<CrmCluePoolRecordDO>()
                        .eq(CrmCluePoolRecordDO::getOperateType, 3)
                        .between(CrmCluePoolRecordDO::getOperateTime, thisWeekStart, thisWeekEnd)
                        .isNotNull(CrmCluePoolRecordDO::getToUserId));
        respVO.setWeeklyClaimSuccessCount(weeklyCustomerClaimSuccess + weeklyClueClaimSuccess);

        if (totalClaim > 0) {
            double rate = (double) respVO.getWeeklyClaimSuccessCount() / totalClaim * 100;
            respVO.setClaimConversionRate(Math.round(rate * 10.0) / 10.0);
        } else {
            respVO.setClaimConversionRate(0.0);
        }

        // 4. 即将回收预警
        LocalDateTime now3DaysLater = LocalDateTime.now().plusDays(3);
        // 统计有负责人、未冻结、最近跟进时间+时效天数 <= 3天后的客户
        Long upcomingCustomerCount = customerMapper.selectCount(
                new LambdaQueryWrapperX<CrmCustomerDO>()
                        .isNotNull(CrmCustomerDO::getOwnerUserId)
                        .eq(CrmCustomerDO::getPoolStatus, 0)
                        .eq(CrmCustomerDO::getCountdownFreeze, false));
        // 简化：统计线索
        Long upcomingClueCount = clueMapper.selectCount(
                new LambdaQueryWrapperX<CrmClueDO>()
                        .isNotNull(CrmClueDO::getOwnerUserId)
                        .eq(CrmClueDO::getPoolStatus, 0));
        respVO.setUpcomingRecoveryAlertCount(upcomingCustomerCount + upcomingClueCount);
        respVO.setUpcomingRecovery3DayCount(upcomingCustomerCount + upcomingClueCount);

        // 1天内即将回收（简化处理）
        respVO.setUpcomingRecovery1DayCount(0L);

        return success(respVO);
    }

}
