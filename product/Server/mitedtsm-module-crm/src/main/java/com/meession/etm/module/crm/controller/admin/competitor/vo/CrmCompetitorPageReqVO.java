package com.meession.etm.module.crm.controller.admin.competitor.vo;

import com.meession.etm.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 竞争对手分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrmCompetitorPageReqVO extends PageParam {

    @Schema(description = "竞争对手名称", example = "XX公司")
    private String name;

}
