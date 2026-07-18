package com.meession.etm.module.crm.controller.admin.customer.vo.poolconfig;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - CRM 客户公海规则 Response VO")
@Data
public class CrmCustomerPoolConfigRespVO {

    @Schema(description = "是否启用客户公海", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否启用客户公海不能为空")
    private Boolean enabled;

    @Schema(description = "未跟进放入公海天数", example = "2")
    private Integer contactExpireDays;

    @Schema(description = "未成交放入公海天数", example = "2")
    private Integer dealExpireDays;

    @Schema(description = "是否开启提前提醒", example = "true")
    private Boolean notifyEnabled;

    @Schema(description = "提前提醒天数", example = "2")
    private Integer notifyDays;

    @Schema(description = "客户等级回收时效JSON，格式: {\"A\":30,\"B\":15,\"C\":7}", example = "{\"A\":90,\"B\":30,\"C\":15}")
    private String levelExpireDays;

    @Schema(description = "是否暂停有合同客户的回收", example = "true")
    private Boolean pauseContractEnabled;

    @Schema(description = "是否暂停有应收款客户的回收", example = "true")
    private Boolean pauseReceivableEnabled;

    @Schema(description = "最大延期次数", example = "3")
    private Integer extendMaxCount;

}
