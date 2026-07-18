package com.meession.etm.module.crm.service.clue;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.clue.vo.CrmCluePageReqVO;
import com.meession.etm.module.crm.controller.admin.clue.vo.CrmClueSaveReqVO;
import com.meession.etm.module.crm.controller.admin.clue.vo.CrmClueTransferReqVO;
import com.meession.etm.module.crm.controller.admin.customer.vo.customer.CrmCustomerSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.clue.CrmClueDO;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmCluePoolRecordDO;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmSeaPoolDailyLimitDO;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmSeaPoolLimitConfigDO;
import com.meession.etm.module.crm.dal.dataobject.followup.CrmFollowUpRecordDO;
import com.meession.etm.module.crm.dal.dataobject.transfer.CrmTransferLogDO;
import com.meession.etm.module.crm.dal.mysql.clue.CrmClueMapper;
import com.meession.etm.module.crm.dal.mysql.seapool.CrmCluePoolRecordMapper;
import com.meession.etm.module.crm.dal.mysql.seapool.CrmSeaPoolDailyLimitMapper;
import com.meession.etm.module.crm.enums.common.CrmBizTypeEnum;
import com.meession.etm.module.crm.enums.permission.CrmPermissionLevelEnum;
import com.meession.etm.module.crm.framework.permission.core.annotations.CrmPermission;
import com.meession.etm.module.crm.service.customer.CrmCustomerService;
import com.meession.etm.module.crm.service.customer.bo.CrmCustomerCreateReqBO;
import com.meession.etm.module.crm.service.duplicate.CrmCustomerDuplicateService;
import com.meession.etm.module.crm.service.followup.CrmFollowUpRecordService;
import com.meession.etm.module.crm.service.followup.bo.CrmFollowUpCreateReqBO;
import com.meession.etm.module.crm.service.permission.CrmPermissionService;
import com.meession.etm.module.crm.service.permission.bo.CrmPermissionCreateReqBO;
import com.meession.etm.module.crm.service.permission.bo.CrmPermissionTransferReqBO;
import com.meession.etm.module.crm.service.transfer.CrmTransferLogService;
import com.meession.etm.module.system.api.user.AdminUserApi;
import com.meession.etm.module.system.api.user.dto.AdminUserRespDTO;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.meession.etm.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.meession.etm.framework.common.util.collection.CollectionUtils.convertList;
import static com.meession.etm.framework.common.util.collection.CollectionUtils.singleton;
import static com.meession.etm.module.crm.enums.ErrorCodeConstants.CLUE_DAILY_RECEIVE_LIMIT_EXCEEDED;
import static com.meession.etm.module.crm.enums.ErrorCodeConstants.CLUE_IN_COOLING_PERIOD;
import static com.meession.etm.module.crm.enums.ErrorCodeConstants.CLUE_NOT_EXISTS;
import static com.meession.etm.module.crm.enums.ErrorCodeConstants.CLUE_SEA_POOL_CONFLICT;
import static com.meession.etm.module.crm.enums.ErrorCodeConstants.CLUE_TRANSFORM_FAIL_ALREADY;
import static com.meession.etm.module.crm.enums.LogRecordConstants.*;
import static com.meession.etm.module.system.enums.ErrorCodeConstants.USER_NOT_EXISTS;

/**
 * 线索 Service 实现类
 *
 * @author Wanwan
 */
@Service
@Slf4j
@Validated
public class CrmClueServiceImpl implements CrmClueService {

    @Resource
    private CrmClueMapper clueMapper;

    @Resource
    private CrmCustomerService customerService;
    @Resource
    private CrmPermissionService crmPermissionService;
    @Resource
    private CrmFollowUpRecordService followUpRecordService;

    @Resource
    private CrmCustomerDuplicateService duplicateService;

    @Resource
    private CrmTransferLogService transferLogService;

    // ==================== 公海增强依赖 ====================
    @Resource
    private CrmCluePoolRecordMapper cluePoolRecordMapper;
    @Resource
    private CrmSeaPoolDailyLimitMapper seaPoolDailyLimitMapper;
    @Resource
    @Lazy
    private com.meession.etm.module.crm.service.seapool.CrmSeaPoolLimitConfigService seaPoolLimitConfigService;

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_CLUE_TYPE, subType = CRM_CLUE_CREATE_SUB_TYPE, bizNo = "{{#clue.id}}",
            success = CRM_CLUE_CREATE_SUCCESS)
    public Long createClue(CrmClueSaveReqVO createReqVO) {
        // 1.1 校验关联数据
        validateRelationDataExists(createReqVO);
        // 1.2 校验负责人是否存在
        adminUserApi.validateUser(createReqVO.getOwnerUserId());

        // 1.3 查重检查（仅记录日志，不阻断创建）
        try {
            List<Map<String, Object>> duplicates = duplicateService.checkDuplicate(
                    createReqVO.getName(), createReqVO.getMobile(),
                    createReqVO.getEmail(), createReqVO.getWechat(), createReqVO.getOwnerUserId());
            if (CollUtil.isNotEmpty(duplicates)) {
                log.warn("[createClue][创建线索时发现重复客户, name={}, mobile={}, email={}, wechat={}, duplicatesCount={}]",
                        createReqVO.getName(), createReqVO.getMobile(),
                        createReqVO.getEmail(), createReqVO.getWechat(), duplicates.size());
            }
        } catch (Exception e) {
            log.warn("[createClue][查重检查异常, 忽略继续创建, error={}]", e.getMessage());
        }

        // 2. 插入线索
        CrmClueDO clue = BeanUtils.toBean(createReqVO, CrmClueDO.class);
        clueMapper.insert(clue);

        // 3. 创建数据权限
        CrmPermissionCreateReqBO createReqBO = new CrmPermissionCreateReqBO().setBizType(CrmBizTypeEnum.CRM_CLUE.getType())
                .setBizId(clue.getId()).setUserId(clue.getOwnerUserId()).setLevel(CrmPermissionLevelEnum.OWNER.getLevel());
        crmPermissionService.createPermission(createReqBO);

        // 4. 记录操作日志上下文
        LogRecordContext.putVariable("clue", clue);
        return clue.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_CLUE_TYPE, subType = CRM_CLUE_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = CRM_CLUE_UPDATE_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_CLUE, bizId = "#updateReqVO.id", level = CrmPermissionLevelEnum.OWNER)
    public void updateClue(CrmClueSaveReqVO updateReqVO) {
        Assert.notNull(updateReqVO.getId(), "线索编号不能为空");
        // 1.1 校验线索是否存在
        CrmClueDO oldClue = validateClueExists(updateReqVO.getId());
        // 1.2 校验关联数据
        validateRelationDataExists(updateReqVO);

        // 2. 更新线索
        CrmClueDO updateObj = BeanUtils.toBean(updateReqVO, CrmClueDO.class);
        clueMapper.updateById(updateObj);

        // 3. 记录操作日志上下文
        updateReqVO.setOwnerUserId(oldClue.getOwnerUserId()); // 避免操作日志出现“删除负责人”的情况
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldClue, CrmClueSaveReqVO.class));
        LogRecordContext.putVariable("clueName", oldClue.getName());
    }

    private void validateRelationDataExists(CrmClueSaveReqVO reqVO) {
        // 校验负责人
        if (Objects.nonNull(reqVO.getOwnerUserId()) &&
                Objects.isNull(adminUserApi.getUser(reqVO.getOwnerUserId()))) {
            throw exception(USER_NOT_EXISTS);
        }
    }

    @Override
    @LogRecord(type = CRM_CLUE_TYPE, subType = CRM_CLUE_FOLLOW_UP_SUB_TYPE, bizNo = "{{#id}}",
            success = CRM_CLUE_FOLLOW_UP_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_CLUE, bizId = "#id", level = CrmPermissionLevelEnum.WRITE)
    public void updateClueFollowUp(Long id, LocalDateTime contactNextTime, String contactLastContent) {
        // 校验线索是否存在
        CrmClueDO oldClue = validateClueExists(id);

        // 更新线索
        clueMapper.updateById(new CrmClueDO().setId(id).setFollowUpStatus(true).setContactNextTime(contactNextTime)
                .setContactLastTime(LocalDateTime.now()).setContactLastContent(contactLastContent));

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("clueName", oldClue.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_CLUE_TYPE, subType = CRM_CLUE_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = CRM_CLUE_DELETE_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_CLUE, bizId = "#id", level = CrmPermissionLevelEnum.OWNER)
    public void deleteClue(Long id) {
        // 1. 校验存在
        CrmClueDO clue = validateClueExists(id);

        // 2. 删除
        clueMapper.deleteById(id);

        // 3. 删除数据权限
        crmPermissionService.deletePermission(CrmBizTypeEnum.CRM_CLUE.getType(), id);

        // 4. 删除跟进
        followUpRecordService.deleteFollowUpRecordByBiz(CrmBizTypeEnum.CRM_CLUE.getType(), id);

        // 5. 记录操作日志上下文
        LogRecordContext.putVariable("clueName", clue.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_CLUE_TYPE, subType = CRM_CLUE_TRANSFER_SUB_TYPE, bizNo = "{{#reqVO.id}}",
            success = CRM_CLUE_TRANSFER_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_CLUE, bizId = "#reqVO.id", level = CrmPermissionLevelEnum.OWNER)
    public void transferClue(CrmClueTransferReqVO reqVO, Long userId) {
        // 1 校验线索是否存在
        CrmClueDO clue = validateClueExists(reqVO.getId());

        // 2.1 数据权限转移
        crmPermissionService.transferPermission(new CrmPermissionTransferReqBO(userId, CrmBizTypeEnum.CRM_CLUE.getType(),
                        reqVO.getId(), reqVO.getNewOwnerUserId(), reqVO.getOldOwnerPermissionLevel()));
        // 2.2 设置新的负责人
        clueMapper.updateById(new CrmClueDO().setId(reqVO.getId()).setOwnerUserId(reqVO.getNewOwnerUserId()));

        // 3. 记录转移日志
        LogRecordContext.putVariable("clue", clue);

        // 4. 记录手动转移日志
        CrmTransferLogDO transferLog = CrmTransferLogDO.builder()
                .bizType(CrmBizTypeEnum.CRM_CLUE.getType())
                .bizId(clue.getId())
                .bizName(clue.getName())
                .fromUserId(clue.getOwnerUserId())
                .toUserId(reqVO.getNewOwnerUserId())
                .transferType(1)
                .remark("手动转移")
                .createTime(LocalDateTime.now())
                .build();
        transferLogService.createTransferLog(transferLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_CLUE_TYPE, subType = CRM_CLUE_TRANSLATE_SUB_TYPE, bizNo = "{{#id}}",
            success = CRM_CLUE_TRANSLATE_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_CLUE, bizId = "#id", level = CrmPermissionLevelEnum.OWNER)
    public void transformClue(Long id, Long userId) {
        // 1.1 校验线索都存在
        CrmClueDO clue = validateClueExists(id);
        // 1.2 存在已经转化的
        if (clue.getTransformStatus()) {
            throw exception(CLUE_TRANSFORM_FAIL_ALREADY);
        }

        // 1.3 查重检查（仅记录日志，不阻断转化）
        try {
            List<Map<String, Object>> duplicates = duplicateService.checkDuplicate(
                    clue.getName(), clue.getMobile(),
                    clue.getEmail(), clue.getWechat(), userId);
            if (CollUtil.isNotEmpty(duplicates)) {
                log.warn("[transformClue][线索转化为客户时发现重复, clueId={}, name={}, mobile={}, duplicatesCount={}]",
                        id, clue.getName(), clue.getMobile(), duplicates.size());
            }
        } catch (Exception e) {
            log.warn("[transformClue][查重检查异常, 忽略继续转化, error={}]", e.getMessage());
        }

        // 2.1 遍历线索(未转化的线索)，创建对应的客户
        Long customerId = customerService.createCustomer(BeanUtils.toBean(clue, CrmCustomerCreateReqBO.class), userId);
        // 2.2 更新线索
        clueMapper.updateById(new CrmClueDO().setId(id).setTransformStatus(Boolean.TRUE).setCustomerId(customerId));
        // 2.3 复制跟进记录
        List<CrmFollowUpRecordDO> followUpRecords = followUpRecordService.getFollowUpRecordByBiz(
                CrmBizTypeEnum.CRM_CLUE.getType(), singleton(clue.getId()));
        if (CollUtil.isNotEmpty(followUpRecords)) {
            followUpRecordService.createFollowUpRecordBatch(convertList(followUpRecords, record ->
                    BeanUtils.toBean(record, CrmFollowUpCreateReqBO.class)
                            .setBizType(CrmBizTypeEnum.CRM_CUSTOMER.getType()).setBizId(customerId)));
        }

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("clueName", clue.getName());
    }

    private CrmClueDO validateClueExists(Long id) {
        CrmClueDO crmClueDO = clueMapper.selectById(id);
        if (crmClueDO == null) {
            throw exception(CLUE_NOT_EXISTS);
        }
        return crmClueDO;
    }

    @Override
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_CLUE, bizId = "#id", level = CrmPermissionLevelEnum.READ)
    public CrmClueDO getClue(Long id) {
        return clueMapper.selectById(id);
    }

    @Override
    public PageResult<CrmClueDO> getCluePage(CrmCluePageReqVO pageReqVO, Long userId) {
        return clueMapper.selectPage(pageReqVO, userId);
    }

    @Override
    public Long getFollowClueCount(Long userId) {
        return clueMapper.selectCountByFollow(userId);
    }

    // ==================== 公海相关操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_CLUE_TYPE, subType = CRM_CLUE_POOL_SUB_TYPE, bizNo = "{{#id}}",
            success = CRM_CLUE_POOL_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_CLUE, bizId = "#id", level = CrmPermissionLevelEnum.OWNER)
    public void putCluePool(Long id) {
        putCluePoolInternal(id, "系统自动回收");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_CLUE_TYPE, subType = CRM_CLUE_POOL_SUB_TYPE, bizNo = "{{#id}}",
            success = CRM_CLUE_POOL_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_CLUE, bizId = "#id", level = CrmPermissionLevelEnum.OWNER)
    public void putCluePool(Long id, String reason) {
        putCluePoolInternal(id, reason != null ? reason : "主动归还");
    }

    /**
     * 线索放入公海内部实现
     */
    private void putCluePoolInternal(Long id, String reason) {
        // 1. 校验存在
        CrmClueDO clue = validateClueExists(id);

        Long originalOwnerUserId = clue.getOwnerUserId();

        // 2. 设置负责人为 NULL
        int updateOwnerUserIncr = clueMapper.updateOwnerUserIdById(clue.getId(), null);
        if (updateOwnerUserIncr == 0) {
            throw exception(CLUE_NOT_EXISTS);
        }

        // 3. 更新公海状态
        clueMapper.updateById(new CrmClueDO().setId(clue.getId())
                .setPoolStatus(1).setPoolEnterTime(LocalDateTime.now()));

        // 4. 删除负责人数据权限
        crmPermissionService.deletePermission(CrmBizTypeEnum.CRM_CLUE.getType(), clue.getId(),
                CrmPermissionLevelEnum.OWNER.getLevel());

        // 5. 记录冷却记录（流转日志）
        cluePoolRecordMapper.insert(CrmCluePoolRecordDO.builder()
                .clueId(clue.getId())
                .fromUserId(originalOwnerUserId)
                .toUserId(null)
                .operateType(reason != null && reason.contains("自动回收") ? 1 : 2) // 1-自动回收 2-手动退回
                .reason(reason)
                .operateTime(LocalDateTime.now())
                .build());

        // 6. 记录操作日志上下文
        LogRecordContext.putVariable("clueName", clue.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveClue(List<Long> ids, Long ownerUserId, Boolean isReceive) {
        // 1.1 校验存在
        List<CrmClueDO> clues = clueMapper.selectByIds(ids);
        if (clues.size() != ids.size()) {
            throw exception(CLUE_NOT_EXISTS);
        }
        // 1.2 校验负责人是否存在
        adminUserApi.validateUserList(singleton(ownerUserId));

        // 1.3 校验每日领取上限
        validateClueDailyReceiveLimit(ownerUserId, clues.size());

        // 1.4 校验冷却时间
        for (CrmClueDO clue : clues) {
            validateClueCoolingPeriod(clue.getId(), ownerUserId);
        }

        // 2. 领取公海线索（乐观锁并发控制）
        CrmSeaPoolLimitConfigDO limitConfig = seaPoolLimitConfigService.getLimitConfig();
        int protectHours = (limitConfig != null && limitConfig.getProtectHours() != null)
                ? limitConfig.getProtectHours() : 24;
        LocalDateTime protectDeadline = LocalDateTime.now().plusHours(protectHours);

        List<CrmPermissionCreateReqBO> createPermissions = new ArrayList<>();
        for (CrmClueDO clue : clues) {
            // 2.1 乐观锁更新：仅在 ownerUserId 为 null 时才能领取
            int updated = clueMapper.updateOwnerUserIdByIdWithLock(clue.getId(), ownerUserId, null);
            if (updated == 0) {
                throw exception(CLUE_SEA_POOL_CONFLICT, clue.getName());
            }
            // 2.2 更新保护期和领取时间
            clueMapper.updateById(new CrmClueDO().setId(clue.getId())
                    .setOwnerTime(LocalDateTime.now())
                    .setPoolStatus(0)
                    .setProtectDeadline(protectDeadline)
                    .setProtectUserId(ownerUserId));
            // 2.3 创建负责人数据权限
            createPermissions.add(new CrmPermissionCreateReqBO().setBizType(CrmBizTypeEnum.CRM_CLUE.getType())
                    .setBizId(clue.getId()).setUserId(ownerUserId).setLevel(CrmPermissionLevelEnum.OWNER.getLevel()));
        }
        // 2.4 创建负责人数据权限
        crmPermissionService.createPermissionBatch(createPermissions);

        // 2.5 更新每日领取计数
        updateClueDailyReceiveCount(ownerUserId, 2, clues.size());

        // 3. 记录操作日志和流转日志
        AdminUserRespDTO user = null;
        if (!isReceive) {
            user = adminUserApi.getUser(ownerUserId);
        }
        for (CrmClueDO clue : clues) {
            getSelf().receiveClueLog(clue, user == null ? null : user.getNickname());
            // 记录流转日志
            cluePoolRecordMapper.insert(CrmCluePoolRecordDO.builder()
                    .clueId(clue.getId())
                    .fromUserId(null) // 公海无归属
                    .toUserId(ownerUserId)
                    .operateType(3) // 主动领取
                    .reason("从公海领取")
                    .operateTime(LocalDateTime.now())
                    .build());
        }
    }

    // ==================== 公海校验增强方法 ====================

    /**
     * 校验线索每日领取上限
     */
    private void validateClueDailyReceiveLimit(Long userId, int newCount) {
        CrmSeaPoolLimitConfigDO limitConfig = seaPoolLimitConfigService.getLimitConfig();
        if (limitConfig == null || limitConfig.getDailyClueLimit() == null) {
            return;
        }
        int dailyLimit = limitConfig.getDailyClueLimit();
        LocalDate today = LocalDate.now();

        CrmSeaPoolDailyLimitDO todayLimit = seaPoolDailyLimitMapper.selectByUserAndDate(userId, 2, today);
        int todayCount = todayLimit != null ? todayLimit.getCount() : 0;
        if (todayCount + newCount > dailyLimit) {
            throw exception(CLUE_DAILY_RECEIVE_LIMIT_EXCEEDED, todayCount, dailyLimit);
        }
    }

    /**
     * 校验线索冷却时间
     */
    private void validateClueCoolingPeriod(Long clueId, Long userId) {
        CrmSeaPoolLimitConfigDO limitConfig = seaPoolLimitConfigService.getLimitConfig();
        if (limitConfig == null || limitConfig.getCoolingDays() == null || limitConfig.getCoolingDays() <= 0) {
            return;
        }
        int coolingDays = limitConfig.getCoolingDays();
        CrmCluePoolRecordDO lastReturn = cluePoolRecordMapper.selectLastReturnByClueIdAndUserId(clueId, userId);
        if (lastReturn != null) {
            LocalDateTime coolingEnd = lastReturn.getOperateTime().plusDays(coolingDays);
            if (LocalDateTime.now().isBefore(coolingEnd)) {
                throw exception(CLUE_IN_COOLING_PERIOD, clueId);
            }
        }
    }

    /**
     * 更新线索每日领取计数
     */
    private void updateClueDailyReceiveCount(Long userId, int resourceType, int delta) {
        LocalDate today = LocalDate.now();
        CrmSeaPoolDailyLimitDO todayLimit = seaPoolDailyLimitMapper.selectByUserAndDate(userId, resourceType, today);
        if (todayLimit == null) {
            seaPoolDailyLimitMapper.insert(CrmSeaPoolDailyLimitDO.builder()
                    .userId(userId).resourceType(resourceType).count(delta).limitDate(today).build());
        } else {
            seaPoolDailyLimitMapper.incrementCount(todayLimit.getId(), delta);
        }
    }

    @LogRecord(type = CRM_CLUE_TYPE, subType = CRM_CLUE_RECEIVE_SUB_TYPE, bizNo = "{{#clue.id}}",
            success = CRM_CLUE_RECEIVE_SUCCESS)
    public void receiveClueLog(CrmClueDO clue, String ownerUserName) {
        // 记录操作日志上下文
        LogRecordContext.putVariable("clue", clue);
        LogRecordContext.putVariable("ownerUserName", ownerUserName);
    }

    @Override
    public Long getCluePoolCount() {
        return clueMapper.selectCountByPool();
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private CrmClueServiceImpl getSelf() {
        return cn.hutool.extra.spring.SpringUtil.getBean(getClass());
    }

}
