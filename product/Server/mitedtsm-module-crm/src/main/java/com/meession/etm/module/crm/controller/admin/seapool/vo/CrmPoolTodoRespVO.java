package com.meession.etm.module.crm.controller.admin.seapool.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 公海待回收列表 Response VO")
@Data
public class CrmPoolTodoRespVO {

    @Schema(description = "资源编号", example = "1")
    private Long id;

    @Schema(description = "资源类型: 1-客户 2-线索", example = "1")
    private Integer resourceType;

    @Schema(description = "资源名称", example = "张三科技有限公司")
    private String name;

    @Schema(description = "负责人编号", example = "1024")
    private Long ownerUserId;

    @Schema(description = "负责人名称", example = "李四")
    private String ownerUserName;

    @Schema(description = "最近跟进时间", example = "2026-07-15 10:00:00")
    private LocalDateTime contactLastTime;

    @Schema(description = "回收时效天数（配置值）", example = "30")
    private Integer expireDays;

    @Schema(description = "剩余天数（距自动回收还有N天）", example = "5")
    private Long remainDays;

    @Schema(description = "剩余天数颜色标识: red(≤1)/yellow(≤3)/normal(>3)", example = "yellow")
    private String remainDaysColor;

    @Schema(description = "已延期次数", example = "1")
    private Integer extendCount;

    @Schema(description = "最大延期次数", example = "3")
    private Integer extendMaxCount;

    @Schema(description = "是否可延期", example = "true")
    private Boolean canExtend;

    @Schema(description = "回收倒计时是否冻结", example = "false")
    private Boolean countdownFreeze;

    @Schema(description = "冻结原因", example = "存在未到期合同")
    private String freezeReason;

    @Schema(description = "客户等级（仅客户）", example = "A")
    private Integer level;

    @Schema(description = "是否A类重点客户（仅客户）", example = "false")
    private Boolean isALevel;

}