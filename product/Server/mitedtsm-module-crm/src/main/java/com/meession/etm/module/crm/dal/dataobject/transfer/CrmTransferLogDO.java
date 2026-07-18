package com.meession.etm.module.crm.dal.dataobject.transfer;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.meession.etm.framework.tenant.core.aop.TenantIgnore;
import lombok.*;

import java.time.LocalDateTime;

/**
 * CRM 转移日志 DO
 *
 * @author system
 */
@TableName("crm_transfer_log")
@KeySequence("crm_transfer_log_seq")
@TenantIgnore
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmTransferLogDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 业务类型
     *
     * 枚举 {@link com.meession.etm.module.crm.enums.common.CrmBizTypeEnum}
     */
    private Integer bizType;
    /**
     * 业务数据编号
     */
    private Long bizId;
    /**
     * 业务数据名称
     */
    private String bizName;
    /**
     * 原负责人编号
     */
    private Long fromUserId;
    /**
     * 新负责人编号
     */
    private Long toUserId;
    /**
     * 转移类型：1-手动转移 2-离职交接 3-系统自动
     */
    private Integer transferType;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建者
     */
    private String creator;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
