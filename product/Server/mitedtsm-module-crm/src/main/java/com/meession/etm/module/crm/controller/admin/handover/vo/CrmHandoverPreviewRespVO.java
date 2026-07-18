package com.meession.etm.module.crm.controller.admin.handover.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - CRM 交接预览 Response VO")
@Data
public class CrmHandoverPreviewRespVO {

    @Schema(description = "总数据量", example = "100")
    private Long totalCount;

    @Schema(description = "客户数据")
    private BizDataGroup customerData;

    @Schema(description = "线索数据")
    private BizDataGroup clueData;

    @Schema(description = "商机数据")
    private BizDataGroup businessData;

    @Schema(description = "合同数据")
    private BizDataGroup contractData;

    @Schema(description = "联系人数据")
    private BizDataGroup contactData;

    @Schema(description = "回款计划数据")
    private BizDataGroup receivablePlanData;

    @Data
    public static class BizDataGroup {
        @Schema(description = "数量", example = "10")
        private Long count;

        @Schema(description = "数据编号列表")
        private List<Long> bizIds;

        @Schema(description = "明细列表（含名称等基本信息）")
        private List<DetailItem> detailList;
    }

    @Data
    public static class DetailItem {
        @Schema(description = "编号")
        private Long id;

        @Schema(description = "名称")
        private String name;

        @Schema(description = "手机号")
        private String mobile;

        @Schema(description = "负责人")
        private String ownerUserName;

        @Schema(description = "创建时间")
        private String createTime;
    }

}
