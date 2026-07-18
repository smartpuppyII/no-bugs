package com.meession.etm.module.crm.controller.admin.duplicate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 查重规则配置 Request VO")
@Data
public class CrmDuplicateConfigSaveReqVO {

    @Schema(description = "是否检查名称", example = "true")
    private Boolean checkName;

    @Schema(description = "是否检查手机号", example = "true")
    private Boolean checkMobile;

    @Schema(description = "是否检查邮箱", example = "false")
    private Boolean checkEmail;

    @Schema(description = "是否检查微信", example = "false")
    private Boolean checkWechat;

    @Schema(description = "查重范围：ALL/DEPT/SELF", example = "ALL")
    @NotBlank(message = "查重范围不能为空")
    private String checkScope;

}
