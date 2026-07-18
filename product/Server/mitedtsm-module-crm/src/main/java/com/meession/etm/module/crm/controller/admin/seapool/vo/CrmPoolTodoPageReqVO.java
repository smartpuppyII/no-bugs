package com.meession.etm.module.crm.controller.admin.seapool.vo;

import com.meession.etm.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 公海待回收列表 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmPoolTodoPageReqVO extends PageParam {

    @Schema(description = "资源类型: 1-客户 2-线索", example = "1")
    private Integer resourceType;

    @Schema(description = "资源名称（模糊搜索）", example = "张三")
    private String name;

    @Schema(description = "剩余天数范围-起始（含），≤此天数", example = "3")
    private Integer remainDaysLe;

    @Schema(description = "是否只显示即将到期（≤3天）", example = "false")
    private Boolean onlyExpiring;

}