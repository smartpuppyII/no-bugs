package com.meession.etm.module.crm.controller.admin.handover.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - CRM 交接执行 Request VO")
@Data
public class CrmHandoverExecuteReqVO {

    @Schema(description = "原负责人用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "原负责人不能为空")
    private Long fromUserId;

    @Schema(description = "客户接收人编号（为空则不转移客户）", example = "1025")
    private Long customerToUserId;

    @Schema(description = "线索接收人编号", example = "1025")
    private Long clueToUserId;

    @Schema(description = "商机接收人编号", example = "1025")
    private Long businessToUserId;

    @Schema(description = "合同接收人编号", example = "1025")
    private Long contractToUserId;

    @Schema(description = "联系人接收人编号", example = "1025")
    private Long contactToUserId;

    @Schema(description = "回款计划接收人编号", example = "1025")
    private Long receivablePlanToUserId;

    @Schema(description = "要转移的客户编号列表（为空则全部转移）")
    private List<Long> customerIds;

    @Schema(description = "要转移的线索编号列表（为空则全部转移）")
    private List<Long> clueIds;

    @Schema(description = "要转移的商机编号列表（为空则全部转移）")
    private List<Long> businessIds;

    @Schema(description = "要转移的合同编号列表（为空则全部转移）")
    private List<Long> contractIds;

    @Schema(description = "要转移的联系人编号列表（为空则全部转移）")
    private List<Long> contactIds;

    @Schema(description = "要转移的回款计划编号列表（为空则全部转移）")
    private List<Long> receivablePlanIds;

    @Schema(description = "备注", example = "张三离职，交接给李四")
    private String remark;

}
