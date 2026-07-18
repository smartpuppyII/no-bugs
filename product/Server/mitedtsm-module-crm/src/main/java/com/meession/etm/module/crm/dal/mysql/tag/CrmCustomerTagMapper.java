package com.meession.etm.module.crm.dal.mysql.tag;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.dal.dataobject.tag.CrmCustomerTagDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 客户-标签关联 Mapper
 *
 * @author system
 */
@Mapper
public interface CrmCustomerTagMapper extends BaseMapperX<CrmCustomerTagDO> {

    default List<CrmCustomerTagDO> selectListByCustomerId(Long customerId) {
        return selectList(new LambdaQueryWrapperX<CrmCustomerTagDO>()
                .eq(CrmCustomerTagDO::getCustomerId, customerId));
    }

    default List<CrmCustomerTagDO> selectListByCustomerIds(List<Long> customerIds) {
        return selectList(new LambdaQueryWrapperX<CrmCustomerTagDO>()
                .in(CrmCustomerTagDO::getCustomerId, customerIds));
    }

    default List<CrmCustomerTagDO> selectListByTagId(Long tagId) {
        return selectList(new LambdaQueryWrapperX<CrmCustomerTagDO>()
                .eq(CrmCustomerTagDO::getTagId, tagId));
    }

    default void deleteByCustomerIdAndTagId(Long customerId, Long tagId) {
        delete(new LambdaQueryWrapperX<CrmCustomerTagDO>()
                .eq(CrmCustomerTagDO::getCustomerId, customerId)
                .eq(CrmCustomerTagDO::getTagId, tagId));
    }

    default void deleteByCustomerId(Long customerId) {
        delete(new LambdaQueryWrapperX<CrmCustomerTagDO>()
                .eq(CrmCustomerTagDO::getCustomerId, customerId));
    }

}
