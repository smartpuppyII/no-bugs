package com.meession.etm.module.crm.dal.dataobject.seapool;

import com.baomidou.mybatisplus.annotation.*;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 线索公海配置 DO
 *
 * @author 密讯
 */
@TableName(value = "crm_clue_pool_config")
@KeySequence("crm_clue_pool_config_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmCluePoolConfigDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 是否启用线索公海
     */
    private Boolean enabled;

    /**
     * 线索回收时效天数
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer clueExpireDays;

    /**
     * 跟进是否重置计时（0-否 1-是）
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Boolean followUpResetEnabled;

    /**
     * A级线索是否免回收（0-否 1-是）
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Boolean aLevelExemptEnabled;

    /**
     * 提前提醒天数
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer notifyDays;

}