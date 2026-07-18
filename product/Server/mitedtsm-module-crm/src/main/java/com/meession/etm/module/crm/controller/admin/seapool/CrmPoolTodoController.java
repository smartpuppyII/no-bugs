package com.meession.etm.module.crm.controller.admin.seapool;

import cn.hutool.core.collection.CollUtil;
import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.date.LocalDateTimeUtils;
import com.meession.etm.module.crm.controller.admin.customer.vo.customer.CrmCustomerPageReqVO;
import com.meession.etm.module.crm.controller.admin.seapool.vo.CrmPoolExtendReqVO;
import com.meession.etm.module.crm.controller.admin.seapool.vo.CrmPoolTodoPageReqVO;
import com.meession.etm.module.crm.controller.admin.seapool.vo.CrmPoolTodoRespVO;
import com.meession.etm.module.crm.dal.dataobject.clue.CrmClueDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerPoolConfigDO;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmCluePoolConfigDO;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmSeaPoolLimitConfigDO;
import com.meession.etm.module.crm.dal.mysql.clue.CrmClueMapper;
import com.meession.etm.module.crm.dal.mysql.customer.CrmCustomerMapper;
import com.meession.etm.module.crm.dal.mysql.customer.CrmCustomerPoolConfigMapper;
import com.meession.etm.module.crm.dal.mysql.seapool.CrmCluePoolConfigMapper;
import com.meession.etm.module.crm.dal.mysql.seapool.CrmSeaPoolLimitConfigMapper;
import com.meession.etm.module.crm.service.clue.CrmClueService;
import com.meession.etm.module.crm.service.customer.CrmCustomerService;
import com.meession.etm.module.system.api.user.AdminUserApi;
import com.meession.etm.module.system.api.user.dto.AdminUserRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.meession.etm.framework.common.pojo.CommonResult.success;
import static com.meession.etm.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - CRM 公海待办事项")
@RestController
@RequestMapping("/crm/pool-todo")
@Validated
@Slf4j
public class CrmPoolTodoController {

    /**
     * 资源类型: 客户
     */
    private static final int RESOURCE_TYPE_CUSTOMER = 1;
    /**
     * 资源类型: 线索
     */
    private static final int RESOURCE_TYPE_CLUE = 2;

    @Resource
    private CrmCustomerService customerService;
    @Resource
    private CrmClueService clueService;
    @Resource
    private CrmCustomerMapper customerMapper;
    @Resource
    private CrmClueMapper clueMapper;
    @Resource
    private CrmCustomerPoolConfigMapper customerPoolConfigMapper;
    @Resource
    private CrmCluePoolConfigMapper cluePoolConfigMapper;
    @Resource
    private CrmSeaPoolLimitConfigMapper seaPoolLimitConfigMapper;
    @Resource
    private AdminUserApi adminUserApi;

    @GetMapping("/page")
    @Operation(summary = "获得待回收资源列表（含剩余天数）")
    @PreAuthorize("@ss.hasPermission('crm:pool-todo:query')")
    public CommonResult<PageResult<CrmPoolTodoRespVO>> getPoolTodoPage(@Valid CrmPoolTodoPageReqVO pageReqVO) {
        Long userId = getLoginUserId();
        List<CrmPoolTodoRespVO> allItems = new ArrayList<>();

        // 查询客户公海配置
        CrmCustomerPoolConfigDO customerPoolConfig = customerPoolConfigMapper.selectOne(
                new com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX<CrmCustomerPoolConfigDO>()
                        .eq(CrmCustomerPoolConfigDO::getEnabled, true));
        Integer customerNotifyDays = (customerPoolConfig != null && customerPoolConfig.getNotifyEnabled() != null
                && customerPoolConfig.getNotifyEnabled()) ? customerPoolConfig.getNotifyDays() : null;
        Integer customerExtendMax = customerPoolConfig != null ? customerPoolConfig.getExtendMaxCount() : 0;

        // 查询线索公海配置
        CrmCluePoolConfigDO cluePoolConfig = cluePoolConfigMapper.selectOne(
                new com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX<CrmCluePoolConfigDO>()
                        .eq(CrmCluePoolConfigDO::getEnabled, true));
        Integer clueNotifyDays = (cluePoolConfig != null && cluePoolConfig.getEnabled() != null
                && cluePoolConfig.getEnabled()) ? cluePoolConfig.getNotifyDays() : null;

        // 查询客户待回收列表
        if (pageReqVO.getResourceType() == null || pageReqVO.getResourceType() == RESOURCE_TYPE_CUSTOMER) {
            CrmCustomerPageReqVO customerPageReq = new CrmCustomerPageReqVO();
            customerPageReq.setPageNo(pageReqVO.getPageNo());
            customerPageReq.setPageSize(pageReqVO.getPageSize());
            PageResult<CrmCustomerDO> customerPage = customerService.getPutPoolRemindCustomerPage(customerPageReq, userId);
            for (CrmCustomerDO customer : customerPage.getList()) {
                CrmPoolTodoRespVO vo = buildCustomerTodoVO(customer, customerPoolConfig, customerNotifyDays, customerExtendMax);
                if (vo != null) {
                    allItems.add(vo);
                }
            }
        }

        // 查询线索待回收列表
        if (pageReqVO.getResourceType() == null || pageReqVO.getResourceType() == RESOURCE_TYPE_CLUE) {
            // 线索使用类似逻辑，查询有负责人且即将到期的线索
            List<CrmClueDO> clueList = clueMapper.selectList(
                    new com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX<CrmClueDO>()
                            .eq(CrmClueDO::getOwnerUserId, userId)
                            .isNotNull(CrmClueDO::getOwnerUserId)
                            .eq(CrmClueDO::getPoolStatus, 0));
            for (CrmClueDO clue : clueList) {
                CrmPoolTodoRespVO vo = buildClueTodoVO(clue, cluePoolConfig, clueNotifyDays);
                if (vo != null) {
                    allItems.add(vo);
                }
            }
        }

        // 按剩余天数排序（升序，即将到期排前面）
        allItems.sort(Comparator.comparingLong(CrmPoolTodoRespVO::getRemainDays));

        // 筛选：剩余天数
        if (pageReqVO.getRemainDaysLe() != null) {
            allItems = allItems.stream()
                    .filter(item -> item.getRemainDays() <= pageReqVO.getRemainDaysLe())
                    .collect(Collectors.toList());
        }

        // 筛选：仅即将到期
        if (Boolean.TRUE.equals(pageReqVO.getOnlyExpiring())) {
            allItems = allItems.stream()
                    .filter(item -> item.getRemainDays() <= 3)
                    .collect(Collectors.toList());
        }

        // 分页
        int total = allItems.size();
        int fromIndex = (pageReqVO.getPageNo() - 1) * pageReqVO.getPageSize();
        int toIndex = Math.min(fromIndex + pageReqVO.getPageSize(), total);
        List<CrmPoolTodoRespVO> pageItems = fromIndex < total ? allItems.subList(fromIndex, toIndex) : Collections.emptyList();

        return success(new PageResult<>(pageItems, (long) total));
    }

    @PostMapping("/extend")
    @Operation(summary = "一键延期")
    @PreAuthorize("@ss.hasPermission('crm:pool-todo:extend')")
    public CommonResult<Boolean> extend(@Valid @RequestBody CrmPoolExtendReqVO reqVO) {
        Long userId = getLoginUserId();
        validateExtendCount(reqVO.getId(), reqVO.getResourceType());

        if (reqVO.getResourceType() == RESOURCE_TYPE_CUSTOMER) {
            extendCustomer(reqVO.getId(), reqVO.getExtendDays());
        } else {
            extendClue(reqVO.getId(), reqVO.getExtendDays());
        }
        log.info("[pool-todo][用户({})延期资源 type={} id={} days={}]", userId,
                reqVO.getResourceType(), reqVO.getId(), reqVO.getExtendDays());
        return success(true);
    }

    @PostMapping("/batch-extend")
    @Operation(summary = "批量延期")
    @PreAuthorize("@ss.hasPermission('crm:pool-todo:extend')")
    public CommonResult<Boolean> batchExtend(@Valid @RequestBody CrmPoolExtendReqVO reqVO) {
        Long userId = getLoginUserId();
        if (CollUtil.isEmpty(reqVO.getBatchItems())) {
            return success(true);
        }
        for (CrmPoolExtendReqVO.CrmPoolExtendItem item : reqVO.getBatchItems()) {
            try {
                validateExtendCount(item.getId(), item.getResourceType());
                if (item.getResourceType() == RESOURCE_TYPE_CUSTOMER) {
                    extendCustomer(item.getId(), item.getExtendDays());
                } else {
                    extendClue(item.getId(), item.getExtendDays());
                }
            } catch (Exception e) {
                log.warn("[pool-todo][批量延期失败 type={} id={}]", item.getResourceType(), item.getId(), e);
            }
        }
        log.info("[pool-todo][用户({})批量延期 {} 条]", userId, reqVO.getBatchItems().size());
        return success(true);
    }

    // ==================== 私有方法 ====================

    private CrmPoolTodoRespVO buildCustomerTodoVO(CrmCustomerDO customer, CrmCustomerPoolConfigDO config,
                                                   Integer notifyDays, Integer extendMaxCount) {
        if (customer.getOwnerUserId() == null) {
            return null; // 已在公海，不展示
        }
        // 计算剩余天数
        LocalDateTime lastTime = customer.getContactLastTime() != null
                ? customer.getContactLastTime() : customer.getOwnerTime();
        if (lastTime == null) {
            return null;
        }
        int expireDays = config != null && config.getContactExpireDays() != null
                ? config.getContactExpireDays() : 30;
        long remainDays = expireDays - LocalDateTimeUtils.between(lastTime);
        if (notifyDays != null && remainDays > notifyDays) {
            return null; // 未到提醒时间
        }

        CrmPoolTodoRespVO vo = new CrmPoolTodoRespVO();
        vo.setId(customer.getId());
        vo.setResourceType(RESOURCE_TYPE_CUSTOMER);
        vo.setName(customer.getName());
        vo.setOwnerUserId(customer.getOwnerUserId());
        vo.setContactLastTime(customer.getContactLastTime());
        vo.setExpireDays(expireDays);
        vo.setRemainDays(Math.max(remainDays, 0));
        vo.setRemainDaysColor(getRemainDaysColor(remainDays));
        vo.setExtendCount(customer.getExtendCount() != null ? customer.getExtendCount() : 0);
        vo.setExtendMaxCount(extendMaxCount != null ? extendMaxCount : 3);
        vo.setCanExtend(vo.getExtendCount() < vo.getExtendMaxCount()
                && !Boolean.TRUE.equals(customer.getCountdownFreeze()));
        vo.setCountdownFreeze(customer.getCountdownFreeze());
        vo.setFreezeReason(customer.getFreezeReason());
        vo.setLevel(customer.getLevel());
        return vo;
    }

    private CrmPoolTodoRespVO buildClueTodoVO(CrmClueDO clue, CrmCluePoolConfigDO config, Integer notifyDays) {
        if (clue.getOwnerUserId() == null) {
            return null;
        }
        LocalDateTime lastTime = clue.getContactLastTime() != null
                ? clue.getContactLastTime() : clue.getCreateTime();
        if (lastTime == null) {
            return null;
        }
        int expireDays = config != null && config.getClueExpireDays() != null
                ? config.getClueExpireDays() : 7;
        long remainDays = expireDays - LocalDateTimeUtils.between(lastTime);
        if (notifyDays != null && remainDays > notifyDays) {
            return null;
        }

        CrmPoolTodoRespVO vo = new CrmPoolTodoRespVO();
        vo.setId(clue.getId());
        vo.setResourceType(RESOURCE_TYPE_CLUE);
        vo.setName(clue.getName());
        vo.setOwnerUserId(clue.getOwnerUserId());
        vo.setContactLastTime(clue.getContactLastTime());
        vo.setExpireDays(expireDays);
        vo.setRemainDays(Math.max(remainDays, 0));
        vo.setRemainDaysColor(getRemainDaysColor(remainDays));
        vo.setExtendCount(clue.getExtendCount() != null ? clue.getExtendCount() : 0);
        vo.setExtendMaxCount(3);
        vo.setCanExtend(vo.getExtendCount() < vo.getExtendMaxCount());
        vo.setCountdownFreeze(false);
        vo.setLevel(clue.getLevel());
        return vo;
    }

    private String getRemainDaysColor(long remainDays) {
        if (remainDays <= 1) return "red";
        if (remainDays <= 3) return "yellow";
        return "normal";
    }

    private void validateExtendCount(Long resourceId, Integer resourceType) {
        int extendMaxCount = 3;
        int currentExtendCount;

        if (resourceType == RESOURCE_TYPE_CUSTOMER) {
            CrmCustomerDO customer = customerMapper.selectById(resourceId);
            if (customer == null) {
                throw new IllegalArgumentException("客户不存在");
            }
            currentExtendCount = customer.getExtendCount() != null ? customer.getExtendCount() : 0;
            CrmCustomerPoolConfigDO config = customerPoolConfigMapper.selectOne(
                    new com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX<CrmCustomerPoolConfigDO>()
                            .eq(CrmCustomerPoolConfigDO::getEnabled, true));
            if (config != null && config.getExtendMaxCount() != null) {
                extendMaxCount = config.getExtendMaxCount();
            }
        } else {
            CrmClueDO clue = clueMapper.selectById(resourceId);
            if (clue == null) {
                throw new IllegalArgumentException("线索不存在");
            }
            currentExtendCount = clue.getExtendCount() != null ? clue.getExtendCount() : 0;
            CrmCluePoolConfigDO clueConfig = cluePoolConfigMapper.selectOne(
                    new com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX<CrmCluePoolConfigDO>()
                            .eq(CrmCluePoolConfigDO::getEnabled, true));
            // 线索没有独立的延期次数配置，默认3次
        }

        if (currentExtendCount >= extendMaxCount) {
            throw new IllegalArgumentException("该资源已达最大延期次数（" + extendMaxCount + "次），请尽快跟进");
        }
    }

    private void extendCustomer(Long customerId, int extendDays) {
        CrmCustomerDO customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("客户不存在");
        }
        // 延期：更新最近跟进时间，重置回收倒计时
        CrmCustomerDO update = new CrmCustomerDO();
        update.setId(customerId);
        update.setContactLastTime(LocalDateTime.now());
        update.setExtendCount((customer.getExtendCount() != null ? customer.getExtendCount() : 0) + 1);
        customerMapper.updateById(update);
    }

    private void extendClue(Long clueId, int extendDays) {
        CrmClueDO clue = clueMapper.selectById(clueId);
        if (clue == null) {
            throw new IllegalArgumentException("线索不存在");
        }
        CrmClueDO update = new CrmClueDO();
        update.setId(clueId);
        update.setContactLastTime(LocalDateTime.now());
        update.setExtendCount((clue.getExtendCount() != null ? clue.getExtendCount() : 0) + 1);
        clueMapper.updateById(update);
    }

}