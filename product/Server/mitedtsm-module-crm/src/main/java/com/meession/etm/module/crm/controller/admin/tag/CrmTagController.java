package com.meession.etm.module.crm.controller.admin.tag;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.tag.vo.CrmTagRespVO;
import com.meession.etm.module.crm.controller.admin.tag.vo.CrmTagSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.tag.CrmTagDO;
import com.meession.etm.module.crm.service.tag.CrmTagService;
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

@Tag(name = "管理后台 - CRM 标签管理")
@RestController
@RequestMapping("/crm/tag")
@Validated
public class CrmTagController {

    @Resource
    private CrmTagService tagService;

    @PostMapping("/create")
    @Operation(summary = "创建标签")
    @PreAuthorize("@ss.hasPermission('crm:tag:create')")
    public CommonResult<Long> createTag(@Valid @RequestBody CrmTagSaveReqVO createReqVO) {
        CrmTagDO tagDO = BeanUtils.toBean(createReqVO, CrmTagDO.class);
        return success(tagService.createTag(tagDO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新标签")
    @PreAuthorize("@ss.hasPermission('crm:tag:update')")
    public CommonResult<Boolean> updateTag(@Valid @RequestBody CrmTagSaveReqVO updateReqVO) {
        CrmTagDO tagDO = BeanUtils.toBean(updateReqVO, CrmTagDO.class);
        tagService.updateTag(tagDO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除标签")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:tag:delete')")
    public CommonResult<Boolean> deleteTag(@RequestParam("id") Long id) {
        tagService.deleteTag(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得标签分页")
    @PreAuthorize("@ss.hasPermission('crm:tag:query')")
    public CommonResult<PageResult<CrmTagRespVO>> getTagPage(@Valid PageParam pageParam,
                                                              @RequestParam(required = false) String name,
                                                              @RequestParam(required = false) String groupName) {
        PageResult<CrmTagDO> pageResult = tagService.getTagPage(pageParam, name, groupName);
        return success(BeanUtils.toBean(pageResult, CrmTagRespVO.class));
    }

    @GetMapping("/list-all")
    @Operation(summary = "获得所有标签")
    @PreAuthorize("@ss.hasPermission('crm:tag:query')")
    public CommonResult<List<CrmTagRespVO>> getAllTags() {
        List<CrmTagDO> list = tagService.getAllTags();
        return success(BeanUtils.toBean(list, CrmTagRespVO.class));
    }

    @GetMapping("/list-by-group")
    @Operation(summary = "按组获得标签列表")
    @Parameter(name = "groupName", description = "标签组名称", required = true)
    @PreAuthorize("@ss.hasPermission('crm:tag:query')")
    public CommonResult<List<CrmTagRespVO>> getTagsByGroup(@RequestParam("groupName") String groupName) {
        List<CrmTagDO> list = tagService.getTagsByGroup(groupName);
        return success(BeanUtils.toBean(list, CrmTagRespVO.class));
    }

}
