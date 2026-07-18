package com.meession.etm.module.crm.controller.admin.order;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.meession.etm.framework.apilog.core.annotation.ApiAccessLog;
import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.collection.MapUtils;
import com.meession.etm.framework.common.util.number.NumberUtils;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.framework.excel.core.util.ExcelUtils;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderPageReqVO;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderRespVO;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderSaveReqVO;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderTransferReqVO;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO;
import com.meession.etm.module.crm.dal.dataobject.contact.CrmContactDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderDO;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderProductDO;
import com.meession.etm.module.crm.dal.dataobject.product.CrmProductDO;
import com.meession.etm.module.crm.service.business.CrmBusinessService;
import com.meession.etm.module.crm.service.contact.CrmContactService;
import com.meession.etm.module.crm.service.customer.CrmCustomerService;
import com.meession.etm.module.crm.service.order.CrmOrderService;
import com.meession.etm.module.crm.service.product.CrmProductService;
import com.meession.etm.module.system.api.dept.DeptApi;
import com.meession.etm.module.system.api.dept.dto.DeptRespDTO;
import com.meession.etm.module.system.api.user.AdminUserApi;
import com.meession.etm.module.system.api.user.dto.AdminUserRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.meession.etm.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.meession.etm.framework.common.pojo.CommonResult.success;
import static com.meession.etm.framework.common.util.collection.CollectionUtils.*;
import static com.meession.etm.framework.common.util.collection.MapUtils.findAndThen;
import static com.meession.etm.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static java.util.Collections.singletonList;

@Tag(name = "管理后台 - CRM 订单")
@RestController
@RequestMapping("/crm/order")
@Validated
public class CrmOrderController {

    @Resource
    private CrmOrderService orderService;
    @Resource
    private CrmCustomerService customerService;
    @Resource
    private CrmContactService contactService;
    @Resource
    private CrmBusinessService businessService;
    @Resource
    private CrmProductService productService;

    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private DeptApi deptApi;

    @PostMapping("/create")
    @Operation(summary = "创建订单")
    @PreAuthorize("@ss.hasPermission('crm:order:create')")
    public CommonResult<Long> createOrder(@Valid @RequestBody CrmOrderSaveReqVO createReqVO) {
        return success(orderService.createOrder(createReqVO, getLoginUserId()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新订单")
    @PreAuthorize("@ss.hasPermission('crm:order:update')")
    public CommonResult<Boolean> updateOrder(@Valid @RequestBody CrmOrderSaveReqVO updateReqVO) {
        orderService.updateOrder(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除订单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:order:delete')")
    public CommonResult<Boolean> deleteOrder(@RequestParam("id") Long id) {
        orderService.deleteOrder(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得订单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:order:query')")
    public CommonResult<CrmOrderRespVO> getOrder(@RequestParam("id") Long id) {
        CrmOrderDO order = orderService.getOrder(id);
        return success(buildOrderDetail(order));
    }

    private CrmOrderRespVO buildOrderDetail(CrmOrderDO order) {
        if (order == null) {
            return null;
        }
        CrmOrderRespVO orderVO = buildOrderDetailList(singletonList(order)).get(0);
        // 拼接产品项
        List<CrmOrderProductDO> orderProducts = orderService.getOrderProductListByOrderId(orderVO.getId());
        Map<Long, CrmProductDO> productMap = productService.getProductMap(
                convertSet(orderProducts, CrmOrderProductDO::getProductId));
        orderVO.setProducts(BeanUtils.toBean(orderProducts, CrmOrderRespVO.Product.class, orderProductVO ->
                MapUtils.findAndThen(productMap, orderProductVO.getProductId(),
                        product -> orderProductVO.setProductName(product.getName())
                                .setProductNo(product.getNo()).setProductUnit(product.getUnit()))));
        return orderVO;
    }

    @GetMapping("/page")
    @Operation(summary = "获得订单分页")
    @PreAuthorize("@ss.hasPermission('crm:order:query')")
    public CommonResult<PageResult<CrmOrderRespVO>> getOrderPage(@Valid CrmOrderPageReqVO pageVO) {
        PageResult<CrmOrderDO> pageResult = orderService.getOrderPage(pageVO, getLoginUserId());
        return success(BeanUtils.toBean(pageResult, CrmOrderRespVO.class).setList(buildOrderDetailList(pageResult.getList())));
    }

    @GetMapping("/page-by-customer")
    @Operation(summary = "获得订单分页，基于指定客户")
    public CommonResult<PageResult<CrmOrderRespVO>> getOrderPageByCustomer(@Valid CrmOrderPageReqVO pageVO) {
        Assert.notNull(pageVO.getCustomerId(), "客户编号不能为空");
        PageResult<CrmOrderDO> pageResult = orderService.getOrderPageByCustomerId(pageVO);
        return success(BeanUtils.toBean(pageResult, CrmOrderRespVO.class).setList(buildOrderDetailList(pageResult.getList())));
    }

    @GetMapping("/page-by-business")
    @Operation(summary = "获得订单分页，基于指定商机")
    public CommonResult<PageResult<CrmOrderRespVO>> getOrderPageByBusiness(@Valid CrmOrderPageReqVO pageVO) {
        Assert.notNull(pageVO.getBusinessId(), "商机编号不能为空");
        PageResult<CrmOrderDO> pageResult = orderService.getOrderPageByBusinessId(pageVO);
        return success(BeanUtils.toBean(pageResult, CrmOrderRespVO.class).setList(buildOrderDetailList(pageResult.getList())));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出订单 Excel")
    @PreAuthorize("@ss.hasPermission('crm:order:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOrderExcel(@Valid CrmOrderPageReqVO exportReqVO,
                                  HttpServletResponse response) throws IOException {
        PageResult<CrmOrderDO> pageResult = orderService.getOrderPage(exportReqVO, getLoginUserId());
        // 导出 Excel
        ExcelUtils.write(response, "订单.xls", "数据", CrmOrderRespVO.class,
                BeanUtils.toBean(pageResult.getList(), CrmOrderRespVO.class));
    }

    @PutMapping("/transfer")
    @Operation(summary = "订单转移")
    @PreAuthorize("@ss.hasPermission('crm:order:update')")
    public CommonResult<Boolean> transferOrder(@Valid @RequestBody CrmOrderTransferReqVO reqVO) {
        orderService.transferOrder(reqVO, getLoginUserId());
        return success(true);
    }

    @PutMapping("/submit")
    @Operation(summary = "提交订单审批")
    @PreAuthorize("@ss.hasPermission('crm:order:update')")
    public CommonResult<Boolean> submitOrder(@RequestParam("id") Long id) {
        orderService.submitOrder(id, getLoginUserId());
        return success(true);
    }

    private List<CrmOrderRespVO> buildOrderDetailList(List<CrmOrderDO> orderList) {
        if (CollUtil.isEmpty(orderList)) {
            return Collections.emptyList();
        }
        // 1.1 获取客户列表
        Map<Long, CrmCustomerDO> customerMap = customerService.getCustomerMap(
                convertSet(orderList, CrmOrderDO::getCustomerId));
        // 1.2 获取创建人、负责人列表
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(convertListByFlatMap(orderList,
                contact -> Stream.of(NumberUtils.parseLong(contact.getCreator()), contact.getOwnerUserId())));
        Map<Long, DeptRespDTO> deptMap = deptApi.getDeptMap(convertSet(userMap.values(), AdminUserRespDTO::getDeptId));
        // 1.3 获取联系人
        Map<Long, CrmContactDO> contactMap = convertMap(contactService.getContactList(convertSet(orderList,
                CrmOrderDO::getSignContactId)), CrmContactDO::getId);
        // 1.4 获取商机
        Map<Long, CrmBusinessDO> businessMap = businessService.getBusinessMap(
                convertSet(orderList, CrmOrderDO::getBusinessId));
        // 2. 拼接数据
        return BeanUtils.toBean(orderList, CrmOrderRespVO.class, orderVO -> {
            // 2.1 设置客户信息
            findAndThen(customerMap, orderVO.getCustomerId(), customer -> orderVO.setCustomerName(customer.getName()));
            // 2.2 设置用户信息
            findAndThen(userMap, Long.parseLong(orderVO.getCreator()), user -> orderVO.setCreatorName(user.getNickname()));
            MapUtils.findAndThen(userMap, orderVO.getOwnerUserId(), user -> {
                orderVO.setOwnerUserName(user.getNickname());
                MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> orderVO.setOwnerUserDeptName(dept.getName()));
            });
            findAndThen(userMap, orderVO.getSignUserId(), user -> orderVO.setSignUserName(user.getNickname()));
            // 2.3 设置联系人信息
            findAndThen(contactMap, orderVO.getSignContactId(), contact -> orderVO.setSignContactName(contact.getName()));
            // 2.4 设置商机信息
            findAndThen(businessMap, orderVO.getBusinessId(), business -> orderVO.setBusinessName(business.getName()));
            // 2.5 设置已回款金额（订单暂无回款模块，设置为零）
            orderVO.setTotalReceivablePrice(BigDecimal.ZERO);
        });
    }

    @GetMapping("/audit-count")
    @Operation(summary = "获得待审核订单数量")
    @PreAuthorize("@ss.hasPermission('crm:order:query')")
    public CommonResult<Long> getAuditOrderCount() {
        return success(orderService.getAuditOrderCount(getLoginUserId()));
    }

    @GetMapping("/remind-count")
    @Operation(summary = "获得即将到期（提醒）的订单数量")
    @PreAuthorize("@ss.hasPermission('crm:order:query')")
    public CommonResult<Long> getRemindOrderCount() {
        return success(orderService.getRemindOrderCount(getLoginUserId()));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得订单精简列表", description = "只包含的订单，主要用于前端的下拉选项")
    @Parameter(name = "customerId", description = "客户编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:order:query')")
    public CommonResult<List<CrmOrderRespVO>> getOrderSimpleList(@RequestParam("customerId") Long customerId) {
        CrmOrderPageReqVO pageReqVO = new CrmOrderPageReqVO().setCustomerId(customerId);
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE); // 不分页
        PageResult<CrmOrderDO> pageResult = orderService.getOrderPageByCustomerId(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(Collections.emptyList());
        }
        // 拼接数据
        return success(convertList(pageResult.getList(), order -> new CrmOrderRespVO() // 只返回 id、name 等精简字段
                .setId(order.getId()).setName(order.getName()).setAuditStatus(order.getAuditStatus())
                .setTotalPrice(order.getTotalPrice())
                .setTotalReceivablePrice(BigDecimal.ZERO)));
    }

}
