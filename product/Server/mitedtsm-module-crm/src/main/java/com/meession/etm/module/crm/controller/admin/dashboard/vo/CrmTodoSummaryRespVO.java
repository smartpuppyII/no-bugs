package com.meession.etm.module.crm.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - CRM 待办汇总 Response VO")
@Data
public class CrmTodoSummaryRespVO {

    @Schema(description = "今日需联系客户数量", example = "5")
    private Long todayContactCount;

    @Schema(description = "待跟进线索数量", example = "3")
    private Long clueFollowCount;

    @Schema(description = "待跟进客户数量", example = "2")
    private Long customerFollowCount;

    @Schema(description = "待进入公海客户数量", example = "1")
    private Long customerPutPoolRemindCount;

    @Schema(description = "待审核合同数量", example = "0")
    private Long contractAuditCount;

    @Schema(description = "即将到期合同数量", example = "2")
    private Long contractRemindCount;

    @Schema(description = "待回款提醒数量", example = "3")
    private Long receivablePlanRemindCount;

    @Schema(description = "待审核回款数量", example = "0")
    private Long receivableAuditCount;

    @Schema(description = "已逾期未跟进客户数量", example = "4")
    private Long expiredFollowCount;

}
