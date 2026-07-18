package com.meession.etm.module.crm.controller.admin.competitor;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.competitor.vo.CrmCompetitorPageReqVO;
import com.meession.etm.module.crm.controller.admin.competitor.vo.CrmCompetitorRespVO;
import com.meession.etm.module.crm.controller.admin.competitor.vo.CrmCompetitorSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.competitor.CrmCompetitorDO;
import com.meession.etm.module.crm.service.competitor.CrmCompetitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - CRM 竞争对手")
@RestController
@RequestMapping("/crm/competitor")
@Validated
public class CrmCompetitorController {

    @Resource
    private CrmCompetitorService competitorService;

    @PostMapping("/create")
    @Operation(summary = "创建竞争对手")
    @PreAuthorize("@ss.hasPermission('crm:competitor:create')")
    public CommonResult<Long> createCompetitor(@Valid @RequestBody CrmCompetitorSaveReqVO createReqVO) {
        return success(competitorService.createCompetitor(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新竞争对手")
    @PreAuthorize("@ss.hasPermission('crm:competitor:update')")
    public CommonResult<Boolean> updateCompetitor(@Valid @RequestBody CrmCompetitorSaveReqVO updateReqVO) {
        competitorService.updateCompetitor(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除竞争对手")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:competitor:delete')")
    public CommonResult<Boolean> deleteCompetitor(@RequestParam("id") Long id) {
        competitorService.deleteCompetitor(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得竞争对手")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:competitor:query')")
    public CommonResult<CrmCompetitorRespVO> getCompetitor(@RequestParam("id") Long id) {
        CrmCompetitorDO competitor = competitorService.getCompetitor(id);
        return success(BeanUtils.toBean(competitor, CrmCompetitorRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得竞争对手分页")
    @PreAuthorize("@ss.hasPermission('crm:competitor:query')")
    public CommonResult<PageResult<CrmCompetitorRespVO>> getCompetitorPage(@Valid CrmCompetitorPageReqVO pageReqVO) {
        PageResult<CrmCompetitorDO> pageResult = competitorService.getCompetitorPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CrmCompetitorRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得竞争对手精简列表（用于下拉框）")
    @PreAuthorize("@ss.hasPermission('crm:competitor:query')")
    public CommonResult<List<CrmCompetitorRespVO>> getCompetitorSimpleList() {
        List<CrmCompetitorDO> list = competitorService.getCompetitorList();
        return success(BeanUtils.toBean(list, CrmCompetitorRespVO.class));
    }
}
