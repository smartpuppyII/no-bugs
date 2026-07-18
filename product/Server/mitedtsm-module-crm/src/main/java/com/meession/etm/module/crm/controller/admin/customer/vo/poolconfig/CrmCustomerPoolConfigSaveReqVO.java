package com.meession.etm.module.crm.controller.admin.customer.vo.poolconfig;

import cn.hutool.core.util.BooleanUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Objects;

@Schema(description = "管理后台 - CRM 客户公海配置的创建/更新 Request VO")
@Data
public class CrmCustomerPoolConfigSaveReqVO {

    @Schema(description = "是否启用客户公海", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @DiffLogField(name = "是否启用客户公海")
    @NotNull(message = "是否启用客户公海不能为空")
    private Boolean enabled;

    @Schema(description = "未跟进放入公海天数", example = "2")
    @DiffLogField(name = "未跟进放入公海天数")
    private Integer contactExpireDays;

    @Schema(description = "未成交放入公海天数", example = "2")
    @DiffLogField(name = "未成交放入公海天数")
    private Integer dealExpireDays;

    @Schema(description = "是否开启提前提醒", example = "true")
    @DiffLogField(name = "是否开启提前提醒")
    private Boolean notifyEnabled;

    @Schema(description = "提前提醒天数", example = "2")
    @DiffLogField(name = "提前提醒天数")
    private Integer notifyDays;

    @Schema(description = "客户等级回收时效JSON，格式: {\"A\":30,\"B\":15,\"C\":7}", example = "{\"A\":90,\"B\":30,\"C\":15}")
    @DiffLogField(name = "客户等级回收时效")
    private String levelExpireDays;

    @Schema(description = "是否暂停有合同客户的回收", example = "true")
    @DiffLogField(name = "是否暂停有合同客户的回收")
    private Boolean pauseContractEnabled;

    @Schema(description = "是否暂停有应收款客户的回收", example = "true")
    @DiffLogField(name = "是否暂停有应收款客户的回收")
    private Boolean pauseReceivableEnabled;

    @Schema(description = "最大延期次数", example = "3")
    @DiffLogField(name = "最大延期次数")
    private Integer extendMaxCount;

    @AssertTrue(message = "未成交放入公海天数不能为空")
    @JsonIgnore
    public boolean isDealExpireDaysValid() {
        if (!BooleanUtil.isTrue(getEnabled())) {
            return true;
        }
        return Objects.nonNull(getDealExpireDays());
    }

    @AssertTrue(message = "未跟进放入公海天数不能为空")
    @JsonIgnore
    public boolean isContactExpireDaysValid() {
        if (!BooleanUtil.isTrue(getEnabled())) {
            return true;
        }
        return Objects.nonNull(getContactExpireDays());
    }

    @AssertTrue(message = "提前提醒天数不能为空")
    @JsonIgnore
    public boolean isNotifyDaysValid() {
        if (!BooleanUtil.isTrue(getNotifyEnabled())) {
            return true;
        }
        return Objects.nonNull(getNotifyDays());
    }

}
