package com.meession.etm.module.crm.controller.admin.seapool.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 公海流转记录 Response VO")
@Data
public class CrmPoolRecordRespVO {

    @Schema(description = "记录编号", example = "1")
    private Long id;

    @Schema(description = "资源编号（客户/线索ID）", example = "1001")
    private Long resourceId;

    @Schema(description = "来源用户编号（回收前归属人/退回人）", example = "1024")
    private Long fromUserId;

    @Schema(description = "来源用户名称", example = "张三")
    private String fromUserName;

    @Schema(description = "目标用户编号（领取人/回收后归属）", example = "1025")
    private Long toUserId;

    @Schema(description = "目标用户名称", example = "李四")
    private String toUserName;

    @Schema(description = "操作类型: 1-自动回收 2-手动退回 3-主动领取 4-管理员分配 5-离职回收", example = "1")
    private Integer operateType;

    @Schema(description = "操作类型名称", example = "自动回收")
    private String operateTypeName;

    @Schema(description = "操作原因/备注", example = "超过30天未跟进，系统自动回收")
    private String reason;

    @Schema(description = "操作时间", example = "2026-07-18 02:00:00")
    private LocalDateTime operateTime;

}