package com.meession.etm.module.crm.dal.dataobject.tag;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * CRM 标签定义 DO
 *
 * @author system
 */
@TableName("crm_tag")
@KeySequence("crm_tag_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmTagDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 标签名称
     */
    private String name;
    /**
     * 标签颜色
     */
    private String color;
    /**
     * 标签组名称
     */
    private String groupName;
    /**
     * 排序
     */
    private Integer sortOrder;
    /**
     * 租户编号
     */
    private Long tenantId;

}
