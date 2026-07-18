package com.meession.etm.module.crm.dal.dataobject.quotation;

import com.baomidou.mybatisplus.annotation.KeySequence; import com.baomidou.mybatisplus.annotation.TableId; import com.baomidou.mybatisplus.annotation.TableName;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO; import lombok.*; import java.math.BigDecimal;
@TableName("crm_quotation") @KeySequence("crm_quotation_seq")
@Data @EqualsAndHashCode(callSuper = true) @ToString(callSuper = true) @Builder @NoArgsConstructor @AllArgsConstructor
public class CrmQuotationDO extends BaseDO {
    @TableId private Long id; private String no; private Long businessId; private String businessName;
    private Long customerId; private String customerName; private BigDecimal totalPrice; private BigDecimal discountPercent;
    private BigDecimal discountPrice; private BigDecimal finalPrice; private String remark; private Integer auditStatus;
    private String processInstanceId; private Long ownerUserId;
}
