package com.meession.etm.module.crm.service.order;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderPageReqVO;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderSaveReqVO;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderTransferReqVO;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderDO;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderProductDO;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.meession.etm.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * CRM 订单 Service 接口
 *
 * @author HUIHUI
 */
public interface CrmOrderService {

    /**
     * 创建订单
     *
     * @param createReqVO 创建信息
     * @param userId      用户编号
     * @return 编号
     */
    Long createOrder(@Valid CrmOrderSaveReqVO createReqVO, Long userId);

    /**
     * 更新订单
     *
     * @param updateReqVO 更新信息
     */
    void updateOrder(@Valid CrmOrderSaveReqVO updateReqVO);

    /**
     * 删除订单
     *
     * @param id 编号
     */
    void deleteOrder(Long id);

    /**
     * 订单转移
     *
     * @param reqVO  请求
     * @param userId 用户编号
     */
    void transferOrder(CrmOrderTransferReqVO reqVO, Long userId);

    /**
     * 更新订单相关的跟进信息
     *
     * @param id                 订单编号
     * @param contactNextTime    下次联系时间
     * @param contactLastContent 最后联系内容
     */
    void updateOrderFollowUp(Long id, LocalDateTime contactNextTime, String contactLastContent);

    /**
     * 发起订单审批流程
     *
     * @param id     订单编号
     * @param userId 用户编号
     */
    void submitOrder(Long id, Long userId);

    /**
     * 更新订单流程审批结果
     *
     * @param id        订单编号
     * @param bpmResult BPM 审批结果
     */
    void updateOrderAuditStatus(Long id, Integer bpmResult);

    /**
     * 获得订单
     *
     * @param id 编号
     * @return 订单
     */
    CrmOrderDO getOrder(Long id);

    /**
     * 校验订单是否合法
     *
     * @param id 编号
     * @return 订单
     */
    CrmOrderDO validateOrder(Long id);

    /**
     * 获得订单列表
     *
     * @param ids 编号
     * @return 订单列表
     */
    List<CrmOrderDO> getOrderList(Collection<Long> ids);

    /**
     * 获得订单 Map
     *
     * @param ids 编号
     * @return 订单 Map
     */
    default Map<Long, CrmOrderDO> getOrderMap(Collection<Long> ids) {
        return convertMap(getOrderList(ids), CrmOrderDO::getId);
    }

    /**
     * 获得订单分页
     *
     * 数据权限：基于 {@link CrmOrderDO} 读取
     *
     * @param pageReqVO 分页查询
     * @param userId    用户编号
     * @return 订单分页
     */
    PageResult<CrmOrderDO> getOrderPage(CrmOrderPageReqVO pageReqVO, Long userId);

    /**
     * 获得订单分页，基于指定客户
     *
     * 数据权限：基于客户读取
     *
     * @param pageReqVO 分页查询
     * @return 订单分页
     */
    PageResult<CrmOrderDO> getOrderPageByCustomerId(CrmOrderPageReqVO pageReqVO);

    /**
     * 获得订单分页，基于指定商机
     *
     * 数据权限：基于商机读取
     *
     * @param pageReqVO 分页查询
     * @return 订单分页
     */
    PageResult<CrmOrderDO> getOrderPageByBusinessId(CrmOrderPageReqVO pageReqVO);

    /**
     * 查询属于某个联系人的订单数量
     *
     * @param contactId 联系人ID
     * @return 订单
     */
    Long getOrderCountByContactId(Long contactId);

    /**
     * 获取关联客户的订单数量
     *
     * @param customerId 客户编号
     * @return 数量
     */
    Long getOrderCountByCustomerId(Long customerId);

    /**
     * 根据商机编号，获取关联客户的订单数量
     *
     * @param businessId 商机编号
     * @return 数量
     */
    Long getOrderCountByBusinessId(Long businessId);

    /**
     * 根据订单编号，获得订单的产品列表
     *
     * @param orderId 订单编号
     * @return 产品列表
     */
    List<CrmOrderProductDO> getOrderProductListByOrderId(Long orderId);

    /**
     * 获得待审核订单数量
     *
     * @param userId 用户编号
     * @return 提醒数量
     */
    Long getAuditOrderCount(Long userId);

    /**
     * 获得即将到期（提醒）的订单数量
     *
     * @param userId 用户编号
     * @return 提醒数量
     */
    Long getRemindOrderCount(Long userId);

    /**
     * 获得订单列表
     *
     * @param customerId  客户编号
     * @param ownerUserId 负责人编号
     * @return 订单列表
     */
    List<CrmOrderDO> getOrderListByCustomerIdOwnerUserId(Long customerId, Long ownerUserId);

}
