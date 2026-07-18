package com.meession.etm.module.crm.controller.admin.handover;

import cn.hutool.core.collection.CollUtil;
import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.business.vo.business.CrmBusinessTransferReqVO;
import com.meession.etm.module.crm.controller.admin.clue.vo.CrmClueTransferReqVO;
import com.meession.etm.module.crm.controller.admin.contact.vo.CrmContactTransferReqVO;
import com.meession.etm.module.crm.controller.admin.contract.vo.contract.CrmContractTransferReqVO;
import com.meession.etm.module.crm.controller.admin.customer.vo.customer.CrmCustomerTransferReqVO;
import com.meession.etm.module.crm.controller.admin.handover.vo.CrmHandoverExecuteReqVO;
import com.meession.etm.module.crm.controller.admin.handover.vo.CrmHandoverPreviewRespVO;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO;
import com.meession.etm.module.crm.dal.dataobject.clue.CrmClueDO;
import com.meession.etm.module.crm.dal.dataobject.contact.CrmContactDO;
import com.meession.etm.module.crm.dal.dataobject.contract.CrmContractDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.dal.dataobject.receivable.CrmReceivablePlanDO;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmCluePoolRecordDO;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmCustomerPoolRecordDO;
import com.meession.etm.module.crm.dal.dataobject.transfer.CrmTransferLogDO;
import com.meession.etm.module.crm.dal.mysql.business.CrmBusinessMapper;
import com.meession.etm.module.crm.dal.mysql.clue.CrmClueMapper;
import com.meession.etm.module.crm.dal.mysql.contact.CrmContactMapper;
import com.meession.etm.module.crm.dal.mysql.contract.CrmContractMapper;
import com.meession.etm.module.crm.dal.mysql.customer.CrmCustomerMapper;
import com.meession.etm.module.crm.dal.mysql.receivable.CrmReceivablePlanMapper;
import com.meession.etm.module.crm.dal.mysql.seapool.CrmCluePoolRecordMapper;
import com.meession.etm.module.crm.dal.mysql.seapool.CrmCustomerPoolRecordMapper;
import com.meession.etm.module.crm.enums.common.CrmBizTypeEnum;
import com.meession.etm.module.crm.service.business.CrmBusinessService;
import com.meession.etm.module.crm.service.clue.CrmClueService;
import com.meession.etm.module.crm.service.contact.CrmContactService;
import com.meession.etm.module.crm.service.contract.CrmContractService;
import com.meession.etm.module.crm.service.customer.CrmCustomerService;
import com.meession.etm.module.crm.service.transfer.CrmTransferLogService;
import com.meession.etm.module.system.api.notify.NotifyMessageSendApi;
import com.meession.etm.module.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import com.meession.etm.module.system.api.user.AdminUserApi;
import com.meession.etm.module.system.api.user.dto.AdminUserRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meession.etm.framework.common.pojo.CommonResult.success;
import static com.meession.etm.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - CRM 离职交接")
@RestController
@RequestMapping("/crm/handover")
@Validated
@Slf4j
public class CrmHandoverController {

    @Resource
    private CrmCustomerMapper customerMapper;
    @Resource
    private CrmClueMapper clueMapper;
    @Resource
    private CrmContactMapper contactMapper;
    @Resource
    private CrmBusinessMapper businessMapper;
    @Resource
    private CrmContractMapper contractMapper;
    @Resource
    private CrmReceivablePlanMapper receivablePlanMapper;

    @Resource
    private CrmCustomerService customerService;
    @Resource
    private CrmClueService clueService;
    @Resource
    private CrmBusinessService businessService;
    @Resource
    private CrmContractService contractService;
    @Resource
    private CrmContactService contactService;

    @Resource
    private CrmTransferLogService transferLogService;
    @Resource
    private CrmCustomerPoolRecordMapper customerPoolRecordMapper;
    @Resource
    private CrmCluePoolRecordMapper cluePoolRecordMapper;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private NotifyMessageSendApi notifyMessageSendApi;

    @GetMapping("/preview")
    @Operation(summary = "预览离职人员名下数据")
    @Parameter(name = "fromUserId", description = "原负责人用户编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:handover:query')")
    public CommonResult<CrmHandoverPreviewRespVO> preview(@RequestParam("fromUserId") Long fromUserId) {
        CrmHandoverPreviewRespVO respVO = new CrmHandoverPreviewRespVO();

        // 批量查用户信息，用于展示负责人名称
        Map<Long, String> userMap = new java.util.HashMap<>();

        List<CrmCustomerDO> customers = customerMapper.selectList(CrmCustomerDO::getOwnerUserId, fromUserId);
        CrmHandoverPreviewRespVO.BizDataGroup customerGroup = new CrmHandoverPreviewRespVO.BizDataGroup();
        customerGroup.setCount((long) customers.size());
        customerGroup.setBizIds(customers.stream().map(CrmCustomerDO::getId).collect(Collectors.toList()));
        customerGroup.setDetailList(customers.stream().map(c -> new CrmHandoverPreviewRespVO.DetailItem()
                .setId(c.getId()).setName(c.getName()).setMobile(c.getMobile())
                .setOwnerUserName(getUserName(userMap, c.getOwnerUserId()))
                .setCreateTime(c.getCreateTime() != null ? c.getCreateTime().toString() : null))
                .collect(Collectors.toList()));
        respVO.setCustomerData(customerGroup);

        List<CrmBusinessDO> businesses = businessMapper.selectList(CrmBusinessDO::getOwnerUserId, fromUserId);
        CrmHandoverPreviewRespVO.BizDataGroup businessGroup = new CrmHandoverPreviewRespVO.BizDataGroup();
        businessGroup.setCount((long) businesses.size());
        businessGroup.setBizIds(businesses.stream().map(CrmBusinessDO::getId).collect(Collectors.toList()));
        businessGroup.setDetailList(businesses.stream().map(b -> new CrmHandoverPreviewRespVO.DetailItem()
                .setId(b.getId()).setName(b.getName()).setMobile(null)
                .setOwnerUserName(getUserName(userMap, b.getOwnerUserId()))
                .setCreateTime(b.getCreateTime() != null ? b.getCreateTime().toString() : null))
                .collect(Collectors.toList()));
        respVO.setBusinessData(businessGroup);

        List<CrmClueDO> clues = clueMapper.selectList(CrmClueDO::getOwnerUserId, fromUserId);
        CrmHandoverPreviewRespVO.BizDataGroup clueGroup = new CrmHandoverPreviewRespVO.BizDataGroup();
        clueGroup.setCount((long) clues.size());
        clueGroup.setBizIds(clues.stream().map(CrmClueDO::getId).collect(Collectors.toList()));
        clueGroup.setDetailList(clues.stream().map(c -> new CrmHandoverPreviewRespVO.DetailItem()
                .setId(c.getId()).setName(c.getName()).setMobile(c.getMobile())
                .setOwnerUserName(getUserName(userMap, c.getOwnerUserId()))
                .setCreateTime(c.getCreateTime() != null ? c.getCreateTime().toString() : null))
                .collect(Collectors.toList()));
        respVO.setClueData(clueGroup);

        List<CrmContractDO> contracts = contractMapper.selectList(CrmContractDO::getOwnerUserId, fromUserId);
        CrmHandoverPreviewRespVO.BizDataGroup contractGroup = new CrmHandoverPreviewRespVO.BizDataGroup();
        contractGroup.setCount((long) contracts.size());
        contractGroup.setBizIds(contracts.stream().map(CrmContractDO::getId).collect(Collectors.toList()));
        contractGroup.setDetailList(contracts.stream().map(c -> new CrmHandoverPreviewRespVO.DetailItem()
                .setId(c.getId()).setName(c.getName()).setMobile(null)
                .setOwnerUserName(getUserName(userMap, c.getOwnerUserId()))
                .setCreateTime(c.getCreateTime() != null ? c.getCreateTime().toString() : null))
                .collect(Collectors.toList()));
        respVO.setContractData(contractGroup);

        List<CrmContactDO> contacts = contactMapper.selectList(CrmContactDO::getOwnerUserId, fromUserId);
        CrmHandoverPreviewRespVO.BizDataGroup contactGroup = new CrmHandoverPreviewRespVO.BizDataGroup();
        contactGroup.setCount((long) contacts.size());
        contactGroup.setBizIds(contacts.stream().map(CrmContactDO::getId).collect(Collectors.toList()));
        contactGroup.setDetailList(contacts.stream().map(c -> new CrmHandoverPreviewRespVO.DetailItem()
                .setId(c.getId()).setName(c.getName()).setMobile(c.getMobile())
                .setOwnerUserName(getUserName(userMap, c.getOwnerUserId()))
                .setCreateTime(c.getCreateTime() != null ? c.getCreateTime().toString() : null))
                .collect(Collectors.toList()));
        respVO.setContactData(contactGroup);

        List<CrmReceivablePlanDO> plans = receivablePlanMapper.selectList(CrmReceivablePlanDO::getOwnerUserId, fromUserId);
        CrmHandoverPreviewRespVO.BizDataGroup planGroup = new CrmHandoverPreviewRespVO.BizDataGroup();
        planGroup.setCount((long) plans.size());
        planGroup.setBizIds(plans.stream().map(CrmReceivablePlanDO::getId).collect(Collectors.toList()));
        planGroup.setDetailList(plans.stream().map(p -> new CrmHandoverPreviewRespVO.DetailItem()
                .setId(p.getId()).setName("第" + p.getPeriod() + "期").setMobile(null)
                .setOwnerUserName(getUserName(userMap, p.getOwnerUserId()))
                .setCreateTime(p.getCreateTime() != null ? p.getCreateTime().toString() : null))
                .collect(Collectors.toList()));
        respVO.setReceivablePlanData(planGroup);

        respVO.setTotalCount(customerGroup.getCount() + clueGroup.getCount() + businessGroup.getCount()
                + contractGroup.getCount() + contactGroup.getCount() + planGroup.getCount());

        return success(respVO);
    }

    private String getUserName(Map<Long, String> userMap, Long userId) {
        if (userId == null) return null;
        return userMap.computeIfAbsent(userId, id -> {
            try {
                AdminUserRespDTO user = adminUserApi.getUser(id);
                return user != null ? user.getNickname() : null;
            } catch (Exception e) {
                return null;
            }
        });
    }

    @PostMapping("/execute")
    @Operation(summary = "执行离职交接")
    @PreAuthorize("@ss.hasPermission('crm:handover:execute')")
    public CommonResult<Boolean> execute(@Valid @RequestBody CrmHandoverExecuteReqVO reqVO) {
        Long loginUserId = getLoginUserId();
        AdminUserRespDTO fromUser = adminUserApi.getUser(reqVO.getFromUserId());

        // 1. 转移客户
        if (reqVO.getCustomerToUserId() != null) {
            List<CrmCustomerDO> customers;
            if (CollUtil.isNotEmpty(reqVO.getCustomerIds())) {
                customers = customerMapper.selectList(CrmCustomerDO::getId, reqVO.getCustomerIds());
            } else {
                customers = customerMapper.selectList(CrmCustomerDO::getOwnerUserId, reqVO.getFromUserId());
            }
            for (CrmCustomerDO customer : customers) {
                try {
                    CrmCustomerTransferReqVO transferReq = new CrmCustomerTransferReqVO();
                    transferReq.setId(customer.getId());
                    transferReq.setNewOwnerUserId(reqVO.getCustomerToUserId());
                    customerService.transferCustomer(transferReq, loginUserId);
                    saveTransferLog(customer.getId(), customer.getName(), CrmBizTypeEnum.CRM_CUSTOMER.getType(),
                            reqVO.getFromUserId(), reqVO.getCustomerToUserId(), 2, reqVO.getRemark());
                } catch (Exception e) {
                    log.error("[handover][转移客户({})失败]", customer.getId(), e);
                }
            }
            sendNotify(reqVO.getCustomerToUserId(), fromUser, "客户");
        } else {
            // 未指定交接人，客户自动流入公海
            List<CrmCustomerDO> customers;
            if (CollUtil.isNotEmpty(reqVO.getCustomerIds())) {
                customers = customerMapper.selectList(CrmCustomerDO::getId, reqVO.getCustomerIds());
            } else {
                customers = customerMapper.selectList(CrmCustomerDO::getOwnerUserId, reqVO.getFromUserId());
            }
            for (CrmCustomerDO customer : customers) {
                try {
                    customerService.putCustomerPool(customer.getId());
                    saveCustomerPoolRecord(customer.getId(), reqVO.getFromUserId(), null,
                            5, "员工离职自动流入公海");
                    saveTransferLog(customer.getId(), customer.getName(), CrmBizTypeEnum.CRM_CUSTOMER.getType(),
                            reqVO.getFromUserId(), null, 2, "离职自动流入公海");
                } catch (Exception e) {
                    log.error("[handover][客户流入公海({})失败]", customer.getId(), e);
                }
            }
        }

        // 2. 转移线索
        if (reqVO.getClueToUserId() != null) {
            List<CrmClueDO> clues;
            if (CollUtil.isNotEmpty(reqVO.getClueIds())) {
                clues = clueMapper.selectList(CrmClueDO::getId, reqVO.getClueIds());
            } else {
                clues = clueMapper.selectList(CrmClueDO::getOwnerUserId, reqVO.getFromUserId());
            }
            for (CrmClueDO clue : clues) {
                try {
                    CrmClueTransferReqVO transferReq = new CrmClueTransferReqVO();
                    transferReq.setId(clue.getId());
                    transferReq.setNewOwnerUserId(reqVO.getClueToUserId());
                    clueService.transferClue(transferReq, loginUserId);
                    saveTransferLog(clue.getId(), clue.getName(), CrmBizTypeEnum.CRM_CLUE.getType(),
                            reqVO.getFromUserId(), reqVO.getClueToUserId(), 2, reqVO.getRemark());
                } catch (Exception e) {
                    log.error("[handover][转移线索({})失败]", clue.getId(), e);
                }
            }
            sendNotify(reqVO.getClueToUserId(), fromUser, "线索");
        } else {
            // 未指定交接人，线索自动流入公海
            List<CrmClueDO> clues;
            if (CollUtil.isNotEmpty(reqVO.getClueIds())) {
                clues = clueMapper.selectList(CrmClueDO::getId, reqVO.getClueIds());
            } else {
                clues = clueMapper.selectList(CrmClueDO::getOwnerUserId, reqVO.getFromUserId());
            }
            for (CrmClueDO clue : clues) {
                try {
                    clueService.putCluePool(clue.getId());
                    saveCluePoolRecord(clue.getId(), reqVO.getFromUserId(), null,
                            5, "员工离职自动流入公海");
                    saveTransferLog(clue.getId(), clue.getName(), CrmBizTypeEnum.CRM_CLUE.getType(),
                            reqVO.getFromUserId(), null, 2, "离职自动流入公海");
                } catch (Exception e) {
                    log.error("[handover][线索流入公海({})失败]", clue.getId(), e);
                }
            }
        }

        // 3. 转移商机
        if (reqVO.getBusinessToUserId() != null) {
            List<CrmBusinessDO> businesses;
            if (CollUtil.isNotEmpty(reqVO.getBusinessIds())) {
                businesses = businessMapper.selectList(CrmBusinessDO::getId, reqVO.getBusinessIds());
            } else {
                businesses = businessMapper.selectList(CrmBusinessDO::getOwnerUserId, reqVO.getFromUserId());
            }
            for (CrmBusinessDO business : businesses) {
                try {
                    CrmBusinessTransferReqVO transferReq = new CrmBusinessTransferReqVO();
                    transferReq.setId(business.getId());
                    transferReq.setNewOwnerUserId(reqVO.getBusinessToUserId());
                    businessService.transferBusiness(transferReq, loginUserId);
                    saveTransferLog(business.getId(), business.getName(), CrmBizTypeEnum.CRM_BUSINESS.getType(),
                            reqVO.getFromUserId(), reqVO.getBusinessToUserId(), 2, reqVO.getRemark());
                } catch (Exception e) {
                    log.error("[handover][转移商机({})失败]", business.getId(), e);
                }
            }
            sendNotify(reqVO.getBusinessToUserId(), fromUser, "商机");
        }

        // 4. 转移合同 (if specified)
        if (reqVO.getContractToUserId() != null) {
            List<CrmContractDO> contracts;
            if (CollUtil.isNotEmpty(reqVO.getContractIds())) {
                contracts = contractMapper.selectList(CrmContractDO::getId, reqVO.getContractIds());
            } else {
                contracts = contractMapper.selectList(CrmContractDO::getOwnerUserId, reqVO.getFromUserId());
            }
            for (CrmContractDO contract : contracts) {
                try {
                    CrmContractTransferReqVO transferReq = new CrmContractTransferReqVO();
                    transferReq.setId(contract.getId());
                    transferReq.setNewOwnerUserId(reqVO.getContractToUserId());
                    contractService.transferContract(transferReq, loginUserId);
                    saveTransferLog(contract.getId(), contract.getName(), CrmBizTypeEnum.CRM_CONTRACT.getType(),
                            reqVO.getFromUserId(), reqVO.getContractToUserId(), 2, reqVO.getRemark());
                } catch (Exception e) {
                    log.error("[handover][转移合同({})失败]", contract.getId(), e);
                }
            }
            sendNotify(reqVO.getContractToUserId(), fromUser, "合同");
        }

        // 5. 转移联系人
        if (reqVO.getContactToUserId() != null) {
            List<CrmContactDO> contacts;
            if (CollUtil.isNotEmpty(reqVO.getContactIds())) {
                contacts = contactMapper.selectList(CrmContactDO::getId, reqVO.getContactIds());
            } else {
                contacts = contactMapper.selectList(CrmContactDO::getOwnerUserId, reqVO.getFromUserId());
            }
            for (CrmContactDO contact : contacts) {
                try {
                    CrmContactTransferReqVO transferReq = new CrmContactTransferReqVO();
                    transferReq.setId(contact.getId());
                    transferReq.setNewOwnerUserId(reqVO.getContactToUserId());
                    contactService.transferContact(transferReq, loginUserId);
                    saveTransferLog(contact.getId(), contact.getName(), CrmBizTypeEnum.CRM_CONTACT.getType(),
                            reqVO.getFromUserId(), reqVO.getContactToUserId(), 2, reqVO.getRemark());
                } catch (Exception e) {
                    log.error("[handover][转移联系人({})失败]", contact.getId(), e);
                }
            }
            sendNotify(reqVO.getContactToUserId(), fromUser, "联系人");
        }

        // 6. 转移回款计划
        if (reqVO.getReceivablePlanToUserId() != null) {
            List<CrmReceivablePlanDO> plans;
            if (CollUtil.isNotEmpty(reqVO.getReceivablePlanIds())) {
                plans = receivablePlanMapper.selectList(CrmReceivablePlanDO::getId, reqVO.getReceivablePlanIds());
            } else {
                plans = receivablePlanMapper.selectList(CrmReceivablePlanDO::getOwnerUserId, reqVO.getFromUserId());
            }
            for (CrmReceivablePlanDO plan : plans) {
                try {
                    receivablePlanMapper.updateById(new CrmReceivablePlanDO().setId(plan.getId())
                            .setOwnerUserId(reqVO.getReceivablePlanToUserId()));
                    saveTransferLog(plan.getId(), "第" + plan.getPeriod() + "期", CrmBizTypeEnum.CRM_RECEIVABLE_PLAN.getType(),
                            reqVO.getFromUserId(), reqVO.getReceivablePlanToUserId(), 2, reqVO.getRemark());
                } catch (Exception e) {
                    log.error("[handover][转移回款计划({})失败]", plan.getId(), e);
                }
            }
            sendNotify(reqVO.getReceivablePlanToUserId(), fromUser, "回款计划");
        }

        return success(true);
    }

    @GetMapping("/transfer-log-page")
    @Operation(summary = "获得转移日志分页")
    @PreAuthorize("@ss.hasPermission('crm:handover:query')")
    public CommonResult<PageResult<CrmTransferLogDO>> getTransferLogPage(@Valid PageParam pageParam,
                                                                          @RequestParam(required = false) Integer bizType) {
        return success(transferLogService.getTransferLogPage(pageParam, bizType));
    }

    private void saveTransferLog(Long bizId, String bizName, Integer bizType,
                                  Long fromUserId, Long toUserId, Integer transferType, String remark) {
        CrmTransferLogDO logDO = CrmTransferLogDO.builder()
                .bizType(bizType)
                .bizId(bizId)
                .bizName(bizName)
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .transferType(transferType)
                .remark(remark)
                .createTime(LocalDateTime.now())
                .build();
        transferLogService.createTransferLog(logDO);
    }

    /**
     * 保存客户公海流转记录
     *
     * @param customerId  客户编号
     * @param fromUserId  来源用户编号
     * @param toUserId    目标用户编号
     * @param operateType 操作类型: 1-自动回收 2-手动退回 3-主动领取 4-管理员分配 5-离职回收
     * @param reason      操作原因
     */
    private void saveCustomerPoolRecord(Long customerId, Long fromUserId, Long toUserId,
                                         Integer operateType, String reason) {
        CrmCustomerPoolRecordDO record = CrmCustomerPoolRecordDO.builder()
                .customerId(customerId)
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .operateType(operateType)
                .reason(reason)
                .operateTime(LocalDateTime.now())
                .build();
        customerPoolRecordMapper.insert(record);
    }

    /**
     * 保存线索公海流转记录
     *
     * @param clueId      线索编号
     * @param fromUserId  来源用户编号
     * @param toUserId    目标用户编号
     * @param operateType 操作类型: 1-自动回收 2-手动退回 3-主动领取 4-管理员分配 5-离职回收
     * @param reason      操作原因
     */
    private void saveCluePoolRecord(Long clueId, Long fromUserId, Long toUserId,
                                     Integer operateType, String reason) {
        CrmCluePoolRecordDO record = CrmCluePoolRecordDO.builder()
                .clueId(clueId)
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .operateType(operateType)
                .reason(reason)
                .operateTime(LocalDateTime.now())
                .build();
        cluePoolRecordMapper.insert(record);
    }

    private void sendNotify(Long toUserId, AdminUserRespDTO fromUser, String bizTypeName) {
        try {
            notifyMessageSendApi.sendSingleMessageToAdmin(
                    new NotifySendSingleToUserReqDTO()
                            .setUserId(toUserId)
                            .setTemplateCode("crm_handover_notify")
                            .setTemplateParams(Map.of(
                                    "fromUserName", fromUser != null ? fromUser.getNickname() : "系统",
                                    "bizTypeName", bizTypeName
                            )));
        } catch (Exception e) {
            log.warn("[handover][发送通知失败, toUserId={}]", toUserId, e);
        }
    }

}
