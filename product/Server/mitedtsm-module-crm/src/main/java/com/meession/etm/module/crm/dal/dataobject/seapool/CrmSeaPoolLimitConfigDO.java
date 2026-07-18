package com.meession.etm.module.crm.dal.dataobject.seapool;

import com.baomidou.mybatisplus.annotation.*;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 公海领取限制配置 DO
 *
 * @author 密讯
 */
@TableName(value = "crm_sea_pool_limit_config")
@KeySequence("crm_sea_pool_limit_config_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmSeaPoolLimitConfigDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 每日线索领取上限
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer dailyClueLimit;

    /**
     * 每日客户领取上限
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer dailyCustomerLimit;

    /**
     * 冷却天数（退回公海后多少天内不可再次领取）
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer coolingDays;

    /**
     * 保护小时数（新领取后保护期内不可被回收）
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer protectHours;

}