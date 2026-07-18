package com.meession.etm.module.crm.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - CRM 公海统计 Response VO")
@Data
public class CrmPoolStatisticsRespVO {

    @Schema(description = "公海客户总量", example = "150")
    private Long totalCustomerPoolCount;

    @Schema(description = "公海线索总量", example = "80")
    private Long totalCluePoolCount;

    @Schema(description = "公海资源总量（客户+线索）", example = "230")
    private Long totalPoolResources;

    @Schema(description = "本周回收数（自动回收流入公海的资源数）", example = "25")
    private Long weeklyRecoveryCount;

    @Schema(description = "上周回收数", example = "20")
    private Long lastWeekRecoveryCount;

    @Schema(description = "本周回收环比变化百分比", example = "25.0")
    private Double weeklyRecoveryChangePercent;

    @Schema(description = "本周领取总数", example = "30")
    private Long weeklyClaimCount;

    @Schema(description = "本周确权成功数（领取后完成有效跟进的）", example = "18")
    private Long weeklyClaimSuccessCount;

    @Schema(description = "领取转化率（确权成功数/总领取数）", example = "60.0")
    private Double claimConversionRate;

    @Schema(description = "即将回收预警数（未来3天内即将回收的资源数）", example = "12")
    private Long upcomingRecoveryAlertCount;

    @Schema(description = "未来1天内即将回收数", example = "3")
    private Long upcomingRecovery1DayCount;

    @Schema(description = "未来3天内即将回收数", example = "12")
    private Long upcomingRecovery3DayCount;

}