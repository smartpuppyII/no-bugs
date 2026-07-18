package com.meession.etm.module.crm.controller.admin.clue.vo.poolconfig;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - CRM 线索公海规则 Response VO")
@Data
public class CrmCluePoolConfigRespVO {

    @Schema(description = "是否启用线索公海", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否启用线索公海不能为空")
    private Boolean enabled;

    @Schema(description = "线索回收时效天数", example = "7")
    private Integer clueExpireDays;

    @Schema(description = "跟进是否重置计时", example = "true")
    private Boolean followUpResetEnabled;

    @Schema(description = "A类重点线索是否豁免回收", example = "true")
    private Boolean aLevelExemptEnabled;

    @Schema(description = "提前提醒天数", example = "3")
    private Integer notifyDays;

}