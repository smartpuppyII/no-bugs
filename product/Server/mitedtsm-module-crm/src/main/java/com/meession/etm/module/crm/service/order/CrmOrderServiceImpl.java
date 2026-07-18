package com.meession.etm.module.crm.service.order;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjUtil;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.number.MoneyUtils;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.framework.common.util.object.ObjectUtils;
import com.meession.etm.module.bpm.api.task.BpmProcessInstanceApi;
import com.meession.etm.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderPageReqVO;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderSaveReqVO;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderTransferReqVO;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderDO;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderProductDO;
import com.meession.etm.module.crm.dal.mysql.order.CrmOrderMapper;
import com.meession.etm.module.crm.dal.mysql.order.CrmOrderProductMapper;
import com.meession.etm.module.crm.dal.redis.no.CrmNoRedisDAO;
import com.meession.etm.module.crm.enums.common.CrmAuditStatusEnum;
import com.meession.etm.module.crm.enums.common.CrmBizTypeEnum;
import com.meession.etm.module.crm.enums.permission.CrmPermissionLevelEnum;
import com.meession.etm.module.crm.framework.permission.core.annotations.CrmPermission;
import com.meession.etm.module.crm.service.business.CrmBusinessService;
import com.meession.etm.module.crm.service.contact.CrmContactService;
import com.meession.etm.module.crm.service.customer.CrmCustomerService;
import com.meession.etm.module.crm.service.permission.CrmPermissionService;
import com.meession.etm.module.crm.service.permission.bo.CrmPermissionCreateReqBO;
import com.meession.etm.module.crm.service.permission.bo.CrmPermissionTransferReqBO;
import com.meession.etm.module.crm.service.product.CrmProductService;
import com.meession.etm.module.system.api.user.AdminUserApi;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static com.meession.etm.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.meession.etm.framework.common.util.collection.CollectionUtils.*;
import static com.meession.etm.module.crm.enums.ErrorCodeConstants.*;
import static com.meession.etm.module.crm.enums.LogRecordConstants.*;
import static com.meession.etm.module.crm.util.CrmAuditStatusUtils.convertBpmResultToAuditStatus;

/**
 * CRM 订单 Service 实现类
 *
 * @author HUIHUI
 */
@Service
@Validated
@Slf4j
public class CrmOrderServiceImpl implements CrmOrderService {

    /**
     * BPM 订单审批流程标识
     */
    public static final String BPM_PROCESS_DEFINITION_KEY = "crm-order-audit";

    @Resource
    private CrmOrderMapper orderMapper;
    @Resource
    private CrmOrderProductMapper orderProductMapper;

    @Resource
    private CrmNoRedisDAO noRedisDAO;

    @Resource
    private CrmPermissionService crmPermissionService;
    @Resource
    private CrmProductService productService;
    @Resource
    private CrmCustomerService customerService;
    @Resource
    private CrmBusinessService businessService;
    @Resource
    private CrmContactService contactService;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_ORDER_TYPE, subType = CRM_ORDER_CREATE_SUB_TYPE, bizNo = "{{#order.id}}",
            success = CRM_ORDER_CREATE_SUCCESS)
    public Long createOrder(CrmOrderSaveReqVO createReqVO, Long userId) {
        // 1.1 校验产品项的有效性
        List<CrmOrderProductDO> orderProducts = validateOrderProducts(createReqVO.getProducts());
        // 1.2 校验关联字段
        validateRelationDataExists(createReqVO);
        // 1.3 生成序号
        String no = noRedisDAO.generate(CrmNoRedisDAO.ORDER_NO_PREFIX);
        if (orderMapper.selectByNo(no) != null) {
            throw exception(ORDER_NO_EXISTS);
        }

        // 2.1 插入订单
        CrmOrderDO order = BeanUtils.toBean(createReqVO, CrmOrderDO.class).setNo(no);
        calculateTotalPrice(order, orderProducts);
        orderMapper.insert(order);
        // 2.2 插入订单关联商品
        if (CollUtil.isNotEmpty(orderProducts)) {
            orderProducts.forEach(item -> item.setOrderId(order.getId()));
            orderProductMapper.insertBatch(orderProducts);
        }

        // 3. 创建数据权限
        crmPermissionService.createPermission(new CrmPermissionCreateReqBO().setUserId(order.getOwnerUserId())
                .setBizType(CrmBizTypeEnum.CRM_ORDER.getType()).setBizId(order.getId())
                .setLevel(CrmPermissionLevelEnum.OWNER.getLevel()));

        // 4. 记录操作日志上下文
        LogRecordContext.putVariable("order", order);
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_ORDER_TYPE, subType = CRM_ORDER_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = CRM_ORDER_UPDATE_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_ORDER, bizId = "#updateReqVO.id", level = CrmPermissionLevelEnum.WRITE)
    public void updateOrder(CrmOrderSaveReqVO updateReqVO) {
        Assert.notNull(updateReqVO.getId(), "订单编号不能为空");
        updateReqVO.setOwnerUserId(null); // 不允许更新的字段
        // 1.1 校验存在
        CrmOrderDO oldOrder = validateOrderExists(updateReqVO.getId());
        // 1.2 只有草稿、审批中，可以编辑；
        if (!ObjectUtils.equalsAny(oldOrder.getAuditStatus(), CrmAuditStatusEnum.DRAFT.getStatus(),
                CrmAuditStatusEnum.PROCESS.getStatus())) {
            throw exception(ORDER_UPDATE_FAIL_NOT_DRAFT);
        }
        // 1.3 校验产品项的有效性
        List<CrmOrderProductDO> orderProducts = validateOrderProducts(updateReqVO.getProducts());
        // 1.4 校验关联字段
        validateRelationDataExists(updateReqVO);

        // 2.1 更新订单
        CrmOrderDO updateObj = BeanUtils.toBean(updateReqVO, CrmOrderDO.class);
        calculateTotalPrice(updateObj, orderProducts);
        orderMapper.updateById(updateObj);
        // 2.2 更新订单关联商品
        updateOrderProduct(updateReqVO.getId(), orderProducts);

        // 3. 记录操作日志上下文
        updateReqVO.setOwnerUserId(oldOrder.getOwnerUserId()); // 避免操作日志出现"删除负责人"的情况
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldOrder, CrmOrderSaveReqVO.class));
        LogRecordContext.putVariable("orderName", oldOrder.getName());
    }

    private void updateOrderProduct(Long id, List<CrmOrderProductDO> newList) {
        List<CrmOrderProductDO> oldList = orderProductMapper.selectListByOrderId(id);
        List<List<CrmOrderProductDO>> diffList = diffList(oldList, newList, // id 不同，就认为是不同的记录
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(o -> o.setOrderId(id));
            orderProductMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            orderProductMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            orderProductMapper.deleteByIds(convertSet(diffList.get(2), CrmOrderProductDO::getId));
        }
    }

    /**
     * 校验关联数据是否存在
     *
     * @param reqVO 请求
     */
    private void validateRelationDataExists(CrmOrderSaveReqVO reqVO) {
        // 1. 校验客户
        if (reqVO.getCustomerId() != null) {
            customerService.validateCustomer(reqVO.getCustomerId());
        }
        // 2. 校验负责人
        if (reqVO.getOwnerUserId() != null) {
            adminUserApi.validateUser(reqVO.getOwnerUserId());
        }
        // 3. 如果有关联商机，则需要校验存在
        if (reqVO.getBusinessId() != null) {
            businessService.validateBusiness(reqVO.getBusinessId());
        }
        // 4. 校验签约相关字段
        if (reqVO.getSignContactId() != null) {
            contactService.validateContact(reqVO.getSignContactId());
        }
        if (reqVO.getSignUserId() != null) {
            adminUserApi.validateUser(reqVO.getSignUserId());
        }
    }

    private List<CrmOrderProductDO> validateOrderProducts(List<CrmOrderSaveReqVO.Product> list) {
        // 1. 校验产品存在
        productService.validProductList(convertSet(list, CrmOrderSaveReqVO.Product::getProductId));
        // 2. 转化为 CrmOrderProductDO 列表
        return convertList(list, o -> BeanUtils.toBean(o, CrmOrderProductDO.class,
                item -> item.setTotalPrice(MoneyUtils.priceMultiply(item.getOrderPrice(), item.getCount()))));
    }

    private void calculateTotalPrice(CrmOrderDO order, List<CrmOrderProductDO> orderProducts) {
        order.setTotalProductPrice(getSumValue(orderProducts, CrmOrderProductDO::getTotalPrice, BigDecimal::add, BigDecimal.ZERO));
        BigDecimal discountPrice = MoneyUtils.priceMultiplyPercent(order.getTotalProductPrice(), order.getDiscountPercent());
        order.setTotalPrice(order.getTotalProductPrice().subtract(discountPrice));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_ORDER_TYPE, subType = CRM_ORDER_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = CRM_ORDER_DELETE_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_ORDER, bizId = "#id", level = CrmPermissionLevelEnum.OWNER)
    public void deleteOrder(Long id) {
        // 1.1 校验存在
        CrmOrderDO order = validateOrderExists(id);
        // 1.2 如果被回款所使用，则不允许删除
        // TODO: 后续接入回款模块后补充校验

        // 2.1 删除订单
        orderMapper.deleteById(id);
        // 2.2 删除数据权限
        crmPermissionService.deletePermission(CrmBizTypeEnum.CRM_ORDER.getType(), id);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("orderName", order.getName());
    }

    private CrmOrderDO validateOrderExists(Long id) {
        CrmOrderDO order = orderMapper.selectById(id);
        if (order == null) {
            throw exception(ORDER_NOT_EXISTS);
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_ORDER_TYPE, subType = CRM_ORDER_TRANSFER_SUB_TYPE, bizNo = "{{#reqVO.id}}",
            success = CRM_ORDER_TRANSFER_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_ORDER, bizId = "#reqVO.id", level = CrmPermissionLevelEnum.OWNER)
    public void transferOrder(CrmOrderTransferReqVO reqVO, Long userId) {
        // 1. 校验订单是否存在
        CrmOrderDO order = validateOrderExists(reqVO.getId());

        // 2.1 数据权限转移
        crmPermissionService.transferPermission(new CrmPermissionTransferReqBO(userId, CrmBizTypeEnum.CRM_ORDER.getType(),
                reqVO.getId(), reqVO.getNewOwnerUserId(), reqVO.getOldOwnerPermissionLevel()));
        // 2.2 设置负责人
        orderMapper.updateById(new CrmOrderDO().setId(reqVO.getId()).setOwnerUserId(reqVO.getNewOwnerUserId()));

        // 3. 记录转移日志
        LogRecordContext.putVariable("order", order);
    }

    @Override
    @LogRecord(type = CRM_ORDER_TYPE, subType = CRM_ORDER_FOLLOW_UP_SUB_TYPE, bizNo = "{{#id}}",
            success = CRM_ORDER_FOLLOW_UP_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_ORDER, bizId = "#id", level = CrmPermissionLevelEnum.WRITE)
    public void updateOrderFollowUp(Long id, LocalDateTime contactNextTime, String contactLastContent) {
        // 1. 校验存在
        CrmOrderDO order = validateOrderExists(id);

        // 2. 更新联系人的跟进信息
        orderMapper.updateById(new CrmOrderDO().setId(id).setContactLastTime(LocalDateTime.now()));

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("orderName", order.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_ORDER_TYPE, subType = CRM_ORDER_SUBMIT_SUB_TYPE, bizNo = "{{#id}}",
            success = CRM_ORDER_SUBMIT_SUCCESS)
    public void submitOrder(Long id, Long userId) {
        // 1. 校验订单是否在审批
        CrmOrderDO order = validateOrderExists(id);
        if (ObjUtil.notEqual(order.getAuditStatus(), CrmAuditStatusEnum.DRAFT.getStatus())) {
            throw exception(ORDER_SUBMIT_FAIL_NOT_DRAFT);
        }

        // 2. 创建订单审批流程实例
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, new BpmProcessInstanceCreateReqDTO()
                .setProcessDefinitionKey(BPM_PROCESS_DEFINITION_KEY).setBusinessKey(String.valueOf(id)));

        // 3. 更新订单工作流编号
        orderMapper.updateById(new CrmOrderDO().setId(id).setProcessInstanceId(processInstanceId)
                .setAuditStatus(CrmAuditStatusEnum.PROCESS.getStatus()));

        // 3. 记录日志
        LogRecordContext.putVariable("orderName", order.getName());
    }

    @Override
    public void updateOrderAuditStatus(Long id, Integer bpmResult) {
        // 1.1 校验订单是否存在
        CrmOrderDO order = validateOrderExists(id);
        // 1.2 只有审批中，可以更新审批结果
        if (ObjUtil.notEqual(order.getAuditStatus(), CrmAuditStatusEnum.PROCESS.getStatus())) {
            log.error("[updateOrderAuditStatus][order({}) 不处于审批中，无法更新审批结果({})]",
                    order.getId(), bpmResult);
            throw exception(ORDER_UPDATE_AUDIT_STATUS_FAIL_NOT_PROCESS);
        }

        // 2. 更新订单审批结果
        Integer auditStatus = convertBpmResultToAuditStatus(bpmResult);
        orderMapper.updateById(new CrmOrderDO().setId(id).setAuditStatus(auditStatus));
    }

    // ======================= 查询相关 =======================

    @Override
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_ORDER, bizId = "#id", level = CrmPermissionLevelEnum.READ)
    public CrmOrderDO getOrder(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public CrmOrderDO validateOrder(Long id) {
        return validateOrderExists(id);
    }

    @Override
    public List<CrmOrderDO> getOrderList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return ListUtil.empty();
        }
        return orderMapper.selectByIds(ids);
    }

    @Override
    public PageResult<CrmOrderDO> getOrderPage(CrmOrderPageReqVO pageReqVO, Long userId) {
        return orderMapper.selectPage(pageReqVO, userId);
    }

    @Override
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_CUSTOMER, bizId = "#pageReqVO.customerId", level = CrmPermissionLevelEnum.READ)
    public PageResult<CrmOrderDO> getOrderPageByCustomerId(CrmOrderPageReqVO pageReqVO) {
        return orderMapper.selectPageByCustomerId(pageReqVO);
    }

    @Override
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_BUSINESS, bizId = "#pageReqVO.businessId", level = CrmPermissionLevelEnum.READ)
    public PageResult<CrmOrderDO> getOrderPageByBusinessId(CrmOrderPageReqVO pageReqVO) {
        return orderMapper.selectPageByBusinessId(pageReqVO);
    }

    @Override
    public Long getOrderCountByContactId(Long contactId) {
        return orderMapper.selectCountByContactId(contactId);
    }

    @Override
    public Long getOrderCountByCustomerId(Long customerId) {
        return orderMapper.selectCount(CrmOrderDO::getCustomerId, customerId);
    }

    @Override
    public Long getOrderCountByBusinessId(Long businessId) {
        return orderMapper.selectCountByBusinessId(businessId);
    }

    @Override
    public List<CrmOrderProductDO> getOrderProductListByOrderId(Long orderId) {
        return orderProductMapper.selectListByOrderId(orderId);
    }

    @Override
    public Long getAuditOrderCount(Long userId) {
        return orderMapper.selectCountByAudit(userId);
    }

    @Override
    public Long getRemindOrderCount(Long userId) {
        return orderMapper.selectCountByRemind(userId);
    }

    @Override
    public List<CrmOrderDO> getOrderListByCustomerIdOwnerUserId(Long customerId, Long ownerUserId) {
        return orderMapper.selectListByCustomerIdOwnerUserId(customerId, ownerUserId);
    }

}
