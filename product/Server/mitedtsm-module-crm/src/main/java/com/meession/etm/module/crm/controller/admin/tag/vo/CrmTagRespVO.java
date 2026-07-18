package com.meession.etm.module.crm.controller.admin.tag.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - CRM 标签 Response VO")
@Data
public class CrmTagRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "标签名称", example = "高意向")
    private String name;

    @Schema(description = "标签颜色", example = "#67C23A")
    private String color;

    @Schema(description = "标签组名称", example = "客户特征")
    private String groupName;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
