package com.meession.etm.module.crm.dal.dataobject.seapool;

import com.baomidou.mybatisplus.annotation.*;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDate;

/**
 * 公海每日领取限量 DO
 *
 * @author 密讯
 */
@TableName(value = "crm_sea_pool_daily_limit")
@KeySequence("crm_sea_pool_daily_limit_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmSeaPoolDailyLimitDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 资源类型: 1-客户 2-线索
     */
    private Integer resourceType;

    /**
     * 当日已领取数量
     */
    private Integer count;

    /**
     * 限量日期
     */
    private LocalDate limitDate;

}