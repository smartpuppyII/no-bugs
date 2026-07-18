package com.meession.etm.module.crm.dal.dataobject.quotation;

import com.baomidou.mybatisplus.annotation.KeySequence; import com.baomidou.mybatisplus.annotation.TableId; import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*; import java.math.BigDecimal;
@TableName("crm_quotation_product") @KeySequence("crm_quotation_product_seq") @Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CrmQuotationProductDO { @TableId private Long id; private Long quotationId; private Long productId; private String productName; private String productNo; private String productUnit; private BigDecimal productPrice; private Integer count; private BigDecimal totalPrice; }
