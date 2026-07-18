package com.meession.etm.module.crm.dal.mysql.quotation;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX; import com.meession.etm.module.crm.dal.dataobject.quotation.CrmQuotationProductDO;
import org.apache.ibatis.annotations.Mapper; import java.util.List;
@Mapper
public interface CrmQuotationProductMapper extends BaseMapperX<CrmQuotationProductDO> {
    default List<CrmQuotationProductDO> selectByQuotationId(Long quotationId) { return selectList(CrmQuotationProductDO::getQuotationId, quotationId); }
}
