package com.meession.etm.module.crm.dal.dataobject.order;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.meession.etm.module.crm.dal.dataobject.product.CrmProductDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * CRM 订单产品关联表 DO
 *
 * @author HUIHUI
 */
@TableName("crm_order_product")
@KeySequence("crm_order_product_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmOrderProductDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 订单编号
     *
     * 关联 {@link CrmOrderDO#getId()}
     */
    private Long orderId;
    /**
     * 产品编号
     *
     * 关联 {@link CrmProductDO#getId()}
     */
    private Long productId;
    /**
     * 产品单价，单位：元
     */
    private BigDecimal productPrice;
    /**
     * 订单价格, 单位：元
     */
    private BigDecimal orderPrice;
    /**
     * 数量
     */
    private BigDecimal count;
    /**
     * 总计价格，单位：元
     *
     * totalPrice = orderPrice * count
     */
    private BigDecimal totalPrice;

}
