package com.meession.etm.module.crm.controller.admin.duplicate;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.module.crm.controller.admin.duplicate.vo.CrmDuplicateConfigSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.duplicate.CrmDuplicateConfigDO;
import com.meession.etm.module.crm.dal.mysql.duplicate.CrmDuplicateConfigMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 查重规则配置")
@RestController
@RequestMapping("/crm/duplicate-config")
@Validated
public class CrmDuplicateConfigController {

    @Resource
    private CrmDuplicateConfigMapper duplicateConfigMapper;

    @GetMapping("/get")
    @Operation(summary = "获得查重规则配置")
    @PreAuthorize("@ss.hasPermission('crm:duplicate-config:query')")
    public CommonResult<CrmDuplicateConfigDO> getConfig() {
        CrmDuplicateConfigDO config = duplicateConfigMapper.selectOne();
        if (config == null) {
            // 返回默认配置
            config = new CrmDuplicateConfigDO();
            config.setCheckName(true);
            config.setCheckMobile(true);
            config.setCheckEmail(false);
            config.setCheckWechat(false);
            config.setCheckScope("ALL");
        }
        return success(config);
    }

    @PutMapping("/save")
    @Operation(summary = "保存查重规则配置")
    @PreAuthorize("@ss.hasPermission('crm:duplicate-config:update')")
    public CommonResult<Boolean> saveConfig(@Valid @RequestBody CrmDuplicateConfigSaveReqVO saveReqVO) {
        CrmDuplicateConfigDO existing = duplicateConfigMapper.selectOne();
        if (existing == null) {
            CrmDuplicateConfigDO config = new CrmDuplicateConfigDO();
            config.setCheckName(saveReqVO.getCheckName());
            config.setCheckMobile(saveReqVO.getCheckMobile());
            config.setCheckEmail(saveReqVO.getCheckEmail());
            config.setCheckWechat(saveReqVO.getCheckWechat());
            config.setCheckScope(saveReqVO.getCheckScope());
            duplicateConfigMapper.insert(config);
        } else {
            existing.setCheckName(saveReqVO.getCheckName());
            existing.setCheckMobile(saveReqVO.getCheckMobile());
            existing.setCheckEmail(saveReqVO.getCheckEmail());
            existing.setCheckWechat(saveReqVO.getCheckWechat());
            existing.setCheckScope(saveReqVO.getCheckScope());
            duplicateConfigMapper.updateById(existing);
        }
        return success(true);
    }

}
