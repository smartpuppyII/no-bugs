package com.meession.etm.module.crm.dal.mysql.order;


import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderProductDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单产品 Mapper
 *
 * @author HUIHUI
 */
@Mapper
public interface CrmOrderProductMapper extends BaseMapperX<CrmOrderProductDO> {

    default List<CrmOrderProductDO> selectListByOrderId(Long orderId) {
        return selectList(new LambdaQueryWrapperX<CrmOrderProductDO>().eq(CrmOrderProductDO::getOrderId, orderId));
    }

}
