package com.meession.etm.module.crm.service.competitor;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.competitor.vo.CrmCompetitorPageReqVO;
import com.meession.etm.module.crm.controller.admin.competitor.vo.CrmCompetitorSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.competitor.CrmCompetitorDO;

import java.util.List;

public interface CrmCompetitorService {

    Long createCompetitor(CrmCompetitorSaveReqVO createReqVO);

    void updateCompetitor(CrmCompetitorSaveReqVO updateReqVO);

    void deleteCompetitor(Long id);

    CrmCompetitorDO getCompetitor(Long id);

    PageResult<CrmCompetitorDO> getCompetitorPage(CrmCompetitorPageReqVO pageReqVO);

    List<CrmCompetitorDO> getCompetitorList();
}
