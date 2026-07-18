package com.meession.etm.module.crm.dal.dataobject.competitor;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

@TableName("crm_competitor")
@KeySequence("crm_competitor_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmCompetitorDO extends BaseDO {

    @TableId
    private Long id;

    private String name;

    private String advantage;

    private String disadvantage;

    private String productCompare;

    private String website;

    private String remark;

    private Integer status;

}
