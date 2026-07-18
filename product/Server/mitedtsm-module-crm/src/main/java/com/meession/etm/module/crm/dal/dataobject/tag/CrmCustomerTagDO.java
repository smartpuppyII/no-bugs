package com.meession.etm.module.crm.dal.dataobject.tag;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.meession.etm.framework.tenant.core.aop.TenantIgnore;
import lombok.*;

import java.time.LocalDateTime;

/**
 * CRM 客户-标签关联 DO
 *
 * @author system
 */
@TableName("crm_customer_tag")
@KeySequence("crm_customer_tag_seq")
@TenantIgnore
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmCustomerTagDO {

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
     * 标签编号
     */
    private Long tagId;
    /**
     * 创建者
     */
    private String creator;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
