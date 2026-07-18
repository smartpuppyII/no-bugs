package com.meession.etm.module.crm.controller.admin.tag.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - CRM 标签创建/更新 Request VO")
@Data
public class CrmTagSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "高意向")
    @NotEmpty(message = "标签名称不能为空")
    private String name;

    @Schema(description = "标签颜色", example = "#67C23A")
    private String color;

    @Schema(description = "标签组名称", example = "客户特征")
    private String groupName;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

}
