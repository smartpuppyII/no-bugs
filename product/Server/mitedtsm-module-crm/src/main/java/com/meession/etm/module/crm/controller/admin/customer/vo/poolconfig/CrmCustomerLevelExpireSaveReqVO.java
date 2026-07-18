package com.meession.etm.module.crm.controller.admin.customer.vo.poolconfig;

import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - CRM 客户等级回收时效配置 Request VO")
@Data
public class CrmCustomerLevelExpireSaveReqVO {

    @Schema(description = "客户等级回收时效JSON，格式: {\"A\":30,\"B\":15,\"C\":7}", requiredMode = Schema.RequiredMode.REQUIRED, example = "{\"A\":90,\"B\":30,\"C\":15}")
    @DiffLogField(name = "客户等级回收时效")
    @NotNull(message = "客户等级回收时效不能为空")
    private String levelExpireDays;

}