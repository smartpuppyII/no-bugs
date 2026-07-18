package com.meession.etm.module.crm.dal.mysql.quotation;

import com.meession.etm.framework.common.pojo.PageParam; import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX; import com.meession.etm.module.crm.dal.dataobject.quotation.CrmQuotationDO;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface CrmQuotationMapper extends BaseMapperX<CrmQuotationDO> {
    default PageResult<CrmQuotationDO> selectPage(PageParam pageParam) { return selectPage(pageParam, new com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX<CrmQuotationDO>().orderByDesc(CrmQuotationDO::getId)); }
}
