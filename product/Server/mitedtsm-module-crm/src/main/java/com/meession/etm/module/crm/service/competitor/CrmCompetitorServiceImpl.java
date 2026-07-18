package com.meession.etm.module.crm.service.competitor;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.competitor.vo.CrmCompetitorPageReqVO;
import com.meession.etm.module.crm.controller.admin.competitor.vo.CrmCompetitorSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.competitor.CrmCompetitorDO;
import com.meession.etm.module.crm.dal.mysql.competitor.CrmCompetitorMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.meession.etm.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.meession.etm.module.crm.enums.ErrorCodeConstants.COMPETITOR_NOT_EXISTS;

@Service
@Validated
public class CrmCompetitorServiceImpl implements CrmCompetitorService {

    @Resource
    private CrmCompetitorMapper competitorMapper;

    @Override
    public Long createCompetitor(CrmCompetitorSaveReqVO createReqVO) {
        CrmCompetitorDO competitor = BeanUtils.toBean(createReqVO, CrmCompetitorDO.class);
        competitorMapper.insert(competitor);
        return competitor.getId();
    }

    @Override
    public void updateCompetitor(CrmCompetitorSaveReqVO updateReqVO) {
        validateCompetitorExists(updateReqVO.getId());
        CrmCompetitorDO updateObj = BeanUtils.toBean(updateReqVO, CrmCompetitorDO.class);
        competitorMapper.updateById(updateObj);
    }

    @Override
    public void deleteCompetitor(Long id) {
        validateCompetitorExists(id);
        competitorMapper.deleteById(id);
    }

    @Override
    public CrmCompetitorDO getCompetitor(Long id) {
        return competitorMapper.selectById(id);
    }

    @Override
    public PageResult<CrmCompetitorDO> getCompetitorPage(CrmCompetitorPageReqVO pageReqVO) {
        return competitorMapper.selectPage(pageReqVO, pageReqVO.getName());
    }

    @Override
    public List<CrmCompetitorDO> getCompetitorList() {
        return competitorMapper.selectList();
    }

    private void validateCompetitorExists(Long id) {
        if (competitorMapper.selectById(id) == null) {
            throw exception(COMPETITOR_NOT_EXISTS);
        }
    }
}
