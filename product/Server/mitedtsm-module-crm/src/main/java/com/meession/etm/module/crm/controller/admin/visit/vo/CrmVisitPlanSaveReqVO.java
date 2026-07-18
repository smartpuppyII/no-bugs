package com.meession.etm.module.crm.controller.admin.visit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 拜访计划创建/更新 Request VO")
@Data
public class CrmVisitPlanSaveReqVO {
    @Schema(description = "编号")
    private Long id;
    @Schema(description = "客户编号")
    private Long customerId;
    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "客户名称不能为空")
    private String customerName;
    @Schema(description = "计划拜访时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "拜访时间不能为空")
    private LocalDateTime visitTime;
    @Schema(description = "拜访目的")
    private String purpose;
    @Schema(description = "拜访地址")
    private String address;
}
