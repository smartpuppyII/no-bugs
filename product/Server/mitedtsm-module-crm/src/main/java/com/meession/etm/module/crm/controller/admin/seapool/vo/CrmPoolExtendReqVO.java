package com.meession.etm.module.crm.controller.admin.seapool.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 公海延期 Request VO")
@Data
public class CrmPoolExtendReqVO {

    @Schema(description = "资源编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "资源编号不能为空")
    private Long id;

    @Schema(description = "资源类型: 1-客户 2-线索", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "资源类型不能为空")
    private Integer resourceType;

    @Schema(description = "延期天数: 7/15/30", requiredMode = Schema.RequiredMode.REQUIRED, example = "7")
    @NotNull(message = "延期天数不能为空")
    @Min(value = 1, message = "延期天数最小为1")
    @Max(value = 30, message = "延期天数最大为30")
    private Integer extendDays;

    @Schema(description = "批量延期时的资源编号列表")
    private List<CrmPoolExtendItem> batchItems;

    @Data
    public static class CrmPoolExtendItem {

        @Schema(description = "资源编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "资源编号不能为空")
        private Long id;

        @Schema(description = "资源类型: 1-客户 2-线索", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "资源类型不能为空")
        private Integer resourceType;

        @Schema(description = "延期天数", requiredMode = Schema.RequiredMode.REQUIRED, example = "7")
        @NotNull(message = "延期天数不能为空")
        private Integer extendDays;
    }

}