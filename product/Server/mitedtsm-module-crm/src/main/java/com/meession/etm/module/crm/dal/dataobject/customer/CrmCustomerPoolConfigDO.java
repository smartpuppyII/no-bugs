package com.meession.etm.module.crm.dal.dataobject.customer;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

/**
 * 客户公海配置 DO
 *
 * @author Wanwan
 */
@TableName(value = "crm_customer_pool_config")
@KeySequence("crm_customer_pool_config_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmCustomerPoolConfigDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 是否启用客户公海
     */
    private Boolean enabled;
    /**
     * 未跟进放入公海天数
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer contactExpireDays;
    /**
     * 未成交放入公海天数
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer dealExpireDays;
    /**
     * 是否开启提前提醒
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Boolean notifyEnabled;
    /**
     * 提前提醒天数
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer notifyDays;
    /**
     * 客户等级回收时效JSON，格式: {"A":30,"B":15,"C":7}
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String levelExpireDays;
    /**
     * 是否暂停有合同客户的回收（0-否 1-是）
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Boolean pauseContractEnabled;
    /**
     * 是否暂停有应收款客户的回收（0-否 1-是）
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Boolean pauseReceivableEnabled;
    /**
     * 最大延期次数
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer extendMaxCount;

}
