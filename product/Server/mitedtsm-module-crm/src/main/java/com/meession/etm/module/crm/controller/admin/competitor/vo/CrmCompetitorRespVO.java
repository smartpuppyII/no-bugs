package com.meession.etm.module.crm.controller.admin.competitor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 竞争对手 Response VO")
@Data
public class CrmCompetitorRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "竞争对手名称", example = "XX公司")
    private String name;

    @Schema(description = "优势", example = "价格便宜")
    private String advantage;

    @Schema(description = "劣势", example = "售后服务差")
    private String disadvantage;

    @Schema(description = "产品对比")
    private String productCompare;

    @Schema(description = "官网")
    private String website;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
