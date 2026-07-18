package com.meession.etm.module.crm.dal.dataobject.seapool;

import com.baomidou.mybatisplus.annotation.*;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 客户公海流转记录 DO
 *
 * @author 密讯
 */
@TableName(value = "crm_customer_pool_record")
@KeySequence("crm_customer_pool_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmCustomerPoolRecordDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 客户编号
     */
    private Long customerId;

    /**
     * 来源用户编号（回收前归属人/退回人）
     */
    private Long fromUserId;

    /**
     * 目标用户编号（领取人/回收后归属）
     */
    private Long toUserId;

    /**
     * 操作类型: 1-自动回收 2-手动退回 3-主动领取 4-管理员分配
     */
    private Integer operateType;

    /**
     * 操作原因/备注
     */
    private String reason;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

}