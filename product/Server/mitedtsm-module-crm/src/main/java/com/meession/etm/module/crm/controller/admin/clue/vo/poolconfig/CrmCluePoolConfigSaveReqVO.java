package com.meession.etm.module.crm.controller.admin.clue.vo.poolconfig;

import cn.hutool.core.util.BooleanUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Objects;

@Schema(description = "管理后台 - CRM 线索公海配置的创建/更新 Request VO")
@Data
public class CrmCluePoolConfigSaveReqVO {

    @Schema(description = "是否启用线索公海", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @DiffLogField(name = "是否启用线索公海")
    @NotNull(message = "是否启用线索公海不能为空")
    private Boolean enabled;

    @Schema(description = "线索回收时效天数", example = "7")
    @DiffLogField(name = "线索回收时效天数")
    private Integer clueExpireDays;

    @Schema(description = "跟进是否重置计时", example = "true")
    @DiffLogField(name = "跟进是否重置计时")
    private Boolean followUpResetEnabled;

    @Schema(description = "A类重点线索是否豁免回收", example = "true")
    @DiffLogField(name = "A类重点线索是否豁免回收")
    private Boolean aLevelExemptEnabled;

    @Schema(description = "提前提醒天数", example = "3")
    @DiffLogField(name = "提前提醒天数")
    private Integer notifyDays;

    @AssertTrue(message = "线索回收时效天数不能为空")
    @JsonIgnore
    public boolean isClueExpireDaysValid() {
        if (!BooleanUtil.isTrue(getEnabled())) {
            return true;
        }
        return Objects.nonNull(getClueExpireDays());
    }

    @AssertTrue(message = "提前提醒天数不能为空")
    @JsonIgnore
    public boolean isNotifyDaysValid() {
        if (!BooleanUtil.isTrue(getEnabled())) {
            return true;
        }
        return Objects.nonNull(getNotifyDays());
    }

}