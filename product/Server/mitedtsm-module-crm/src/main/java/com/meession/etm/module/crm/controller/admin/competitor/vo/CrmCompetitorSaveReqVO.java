package com.meession.etm.module.crm.controller.admin.competitor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 竞争对手创建/更新 Request VO")
@Data
public class CrmCompetitorSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "竞争对手名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "XX公司")
    @NotBlank(message = "竞争对手名称不能为空")
    private String name;

    @Schema(description = "优势", example = "价格便宜")
    private String advantage;

    @Schema(description = "劣势", example = "售后服务差")
    private String disadvantage;

    @Schema(description = "产品对比", example = "功能对比说明")
    private String productCompare;

    @Schema(description = "官网", example = "https://example.com")
    private String website;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态", example = "0")
    private Integer status;

}
