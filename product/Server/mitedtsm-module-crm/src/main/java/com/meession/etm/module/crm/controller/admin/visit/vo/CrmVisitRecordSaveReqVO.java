package com.meession.etm.module.crm.controller.admin.visit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 拜访签到/报告 Request VO")
@Data
public class CrmVisitRecordSaveReqVO {
    @Schema(description = "编号")
    private Long id;
    @Schema(description = "拜访计划编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "拜访计划不能为空")
    private Long planId;
    @Schema(description = "签到地点")
    private String checkInLocation;
    @Schema(description = "纬度")
    private String latitude;
    @Schema(description = "经度")
    private String longitude;
    @Schema(description = "拜访内容/报告")
    private String content;
    @Schema(description = "客户反馈")
    private String customerFeedback;
    @Schema(description = "下一步计划")
    private String nextStep;
}
