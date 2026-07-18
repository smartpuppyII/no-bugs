package com.meession.etm.module.crm.controller.admin.visit;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.visit.vo.CrmVisitPlanSaveReqVO;
import com.meession.etm.module.crm.controller.admin.visit.vo.CrmVisitRecordSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.visit.CrmVisitPlanDO;
import com.meession.etm.module.crm.dal.dataobject.visit.CrmVisitRecordDO;
import com.meession.etm.module.crm.dal.mysql.visit.CrmVisitPlanMapper;
import com.meession.etm.module.crm.dal.mysql.visit.CrmVisitRecordMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.meession.etm.framework.common.pojo.CommonResult.success;
import static com.meession.etm.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - CRM 拜访管理")
@RestController
@RequestMapping("/crm/visit")
@Validated
public class CrmVisitController {

    @Resource private CrmVisitPlanMapper visitPlanMapper;
    @Resource private CrmVisitRecordMapper visitRecordMapper;

    // ========== 拜访计划 ==========

    @PostMapping("/plan/create")
    @Operation(summary = "创建拜访计划")
    @PreAuthorize("@ss.hasPermission('crm:visit:create')")
    public CommonResult<Long> createPlan(@Valid @RequestBody CrmVisitPlanSaveReqVO reqVO) {
        CrmVisitPlanDO plan = BeanUtils.toBean(reqVO, CrmVisitPlanDO.class).setStatus(0).setOwnerUserId(getLoginUserId());
        visitPlanMapper.insert(plan);
        return success(plan.getId());
    }

    @PutMapping("/plan/update")
    @Operation(summary = "更新拜访计划")
    @PreAuthorize("@ss.hasPermission('crm:visit:update')")
    public CommonResult<Boolean> updatePlan(@Valid @RequestBody CrmVisitPlanSaveReqVO reqVO) {
        visitPlanMapper.updateById(BeanUtils.toBean(reqVO, CrmVisitPlanDO.class));
        return success(true);
    }

    @DeleteMapping("/plan/delete")
    @Operation(summary = "删除拜访计划")
    @PreAuthorize("@ss.hasPermission('crm:visit:delete')")
    public CommonResult<Boolean> deletePlan(@RequestParam("id") Long id) {
        visitPlanMapper.deleteById(id);
        return success(true);
    }

    @GetMapping("/plan/page")
    @Operation(summary = "拜访计划分页")
    @PreAuthorize("@ss.hasPermission('crm:visit:query')")
    public CommonResult<PageResult<CrmVisitPlanDO>> getPlanPage(@Valid PageParam pageParam,
            @RequestParam(required = false) String customerName, @RequestParam(required = false) Integer status) {
        return success(visitPlanMapper.selectPage(pageParam, getLoginUserId(), customerName, status));
    }

    // ========== 拜访签到/报告 ==========

    @PostMapping("/record/check-in")
    @Operation(summary = "拜访签到")
    @PreAuthorize("@ss.hasPermission('crm:visit:update')")
    public CommonResult<Long> checkIn(@Valid @RequestBody CrmVisitRecordSaveReqVO reqVO) {
        CrmVisitPlanDO plan = visitPlanMapper.selectById(reqVO.getPlanId());
        if (plan != null) visitPlanMapper.updateById(new CrmVisitPlanDO().setId(plan.getId()).setStatus(1));
        CrmVisitRecordDO record = BeanUtils.toBean(reqVO, CrmVisitRecordDO.class)
                .setCheckInTime(LocalDateTime.now()).setOwnerUserId(getLoginUserId())
                .setCustomerId(plan != null ? plan.getCustomerId() : null)
                .setCustomerName(plan != null ? plan.getCustomerName() : null);
        visitRecordMapper.insert(record);
        return success(record.getId());
    }

    @PutMapping("/record/report")
    @Operation(summary = "填写拜访报告")
    @PreAuthorize("@ss.hasPermission('crm:visit:update')")
    public CommonResult<Boolean> report(@Valid @RequestBody CrmVisitRecordSaveReqVO reqVO) {
        CrmVisitRecordDO record = BeanUtils.toBean(reqVO, CrmVisitRecordDO.class);
        visitRecordMapper.updateById(record);
        CrmVisitRecordDO saved = visitRecordMapper.selectById(reqVO.getId());
        if (saved != null) {
            visitPlanMapper.updateById(new CrmVisitPlanDO().setId(saved.getPlanId()).setStatus(2));
        }
        return success(true);
    }

    @GetMapping("/record/page")
    @Operation(summary = "拜访记录分页")
    @PreAuthorize("@ss.hasPermission('crm:visit:query')")
    public CommonResult<PageResult<CrmVisitRecordDO>> getRecordPage(@Valid PageParam pageParam) {
        return success(visitRecordMapper.selectPage(pageParam, getLoginUserId()));
    }

    @GetMapping("/record/count")
    @Operation(summary = "拜访次数统计")
    @PreAuthorize("@ss.hasPermission('crm:visit:query')")
    public CommonResult<Long> getRecordCount() {
        return success(visitRecordMapper.selectCountByUserId(getLoginUserId()));
    }

    @GetMapping("/plan/today")
    @Operation(summary = "今日拜访计划")
    @PreAuthorize("@ss.hasPermission('crm:visit:query')")
    public CommonResult<List<CrmVisitPlanDO>> getTodayPlans() {
        return success(visitPlanMapper.selectList(CrmVisitPlanDO::getOwnerUserId, getLoginUserId()));
    }
}
