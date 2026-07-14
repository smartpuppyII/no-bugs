package com.meession.etm.module.crm.service.customer;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.test.core.ut.BaseDbUnitTest;
import com.meession.etm.framework.test.core.util.AssertUtils;
import com.meession.etm.module.crm.controller.admin.customer.vo.customer.*;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerLimitConfigDO;
import com.meession.etm.module.crm.dal.mysql.customer.CrmCustomerMapper;
import com.meession.etm.module.crm.enums.common.CrmBizTypeEnum;
import com.meession.etm.module.crm.service.business.CrmBusinessService;
import com.meession.etm.module.crm.service.contact.CrmContactService;
import com.meession.etm.module.crm.service.contract.CrmContractService;
import com.meession.etm.module.crm.service.customer.bo.CrmCustomerCreateReqBO;
import com.meession.etm.module.crm.service.permission.CrmPermissionService;
import com.meession.etm.module.crm.service.permission.bo.CrmPermissionTransferReqBO;
import com.meession.etm.module.system.api.user.AdminUserApi;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.*;

import static com.meession.etm.framework.test.core.util.AssertUtils.assertPojoEquals;
import static com.meession.etm.framework.test.core.util.AssertUtils.assertServiceException;
import static com.meession.etm.framework.test.core.util.RandomUtils.*;
import static com.meession.etm.module.crm.enums.ErrorCodeConstants.*;
import static com.meession.etm.module.crm.enums.customer.CrmCustomerLimitConfigTypeEnum.CUSTOMER_LOCK_LIMIT;
import static com.meession.etm.module.crm.enums.customer.CrmCustomerLimitConfigTypeEnum.CUSTOMER_OWNER_LIMIT;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link CrmCustomerServiceImpl} 的单元测试类
 *
 * @author Wanwan
 */
@Import(CrmCustomerServiceImpl.class)
public class CrmCustomerServiceTest extends BaseDbUnitTest {

    @Resource
    private CrmCustomerServiceImpl customerService;

    @Resource
    private CrmCustomerMapper customerMapper;

    @MockitoBean
    private CrmPermissionService permissionService;
    @MockitoBean
    private CrmCustomerLimitConfigService customerLimitConfigService;
    @MockitoBean
    private CrmCustomerPoolConfigService customerPoolConfigService;
    @MockitoBean
    private CrmContactService contactService;
    @MockitoBean
    private CrmBusinessService businessService;
    @MockitoBean
    private CrmContractService contractService;
    @MockitoBean
    private AdminUserApi adminUserApi;

    // ==================== createCustomer(CrmCustomerSaveReqVO) ====================

    @Test
    public void testCreateCustomer_success() {
        // 准备参数
        Long userId = randomLongId();
        CrmCustomerSaveReqVO reqVO = randomPojo(CrmCustomerSaveReqVO.class, o -> {
            o.setId(null);
            o.setMobile("13800138000");
            o.setOwnerUserId(userId);
        });
        // mock 客户数上限校验通过
        when(customerLimitConfigService.getCustomerLimitConfigListByUserId(
                eq(CUSTOMER_OWNER_LIMIT.getType()), eq(userId))).thenReturn(Collections.emptyList());

        // 调用
        Long customerId = customerService.createCustomer(reqVO, userId);
        // 断言
        assertNotNull(customerId);
        CrmCustomerDO customer = customerMapper.selectById(customerId);
        assertPojoEquals(reqVO, customer, "id", "ownerUserId", "ownerTime");
        assertEquals(userId, customer.getOwnerUserId());
        assertNotNull(customer.getOwnerTime());
        // 校验权限创建被调用
        verify(permissionService, times(1)).createPermission(argThat(bo ->
                bo.getBizType().equals(CrmBizTypeEnum.CRM_CUSTOMER.getType())
                        && bo.getBizId().equals(customerId)
                        && bo.getUserId().equals(userId)));
    }

    @Test
    public void testCreateCustomer_exceedOwnerLimit() {
        // 准备参数
        Long userId = randomLongId();
        CrmCustomerSaveReqVO reqVO = randomPojo(CrmCustomerSaveReqVO.class, o -> {
            o.setId(null);
            o.setMobile("13800138000");
            o.setOwnerUserId(userId);
        });
        // mock 客户数已达上限
        List<CrmCustomerLimitConfigDO> limitConfigs = singletonList(
                randomPojo(CrmCustomerLimitConfigDO.class, o -> {
                    o.setType(CUSTOMER_OWNER_LIMIT.getType());
                    o.setMaxCount(0);
                    o.setDealCountEnabled(false);
                }));
        when(customerLimitConfigService.getCustomerLimitConfigListByUserId(
                eq(CUSTOMER_OWNER_LIMIT.getType()), eq(userId))).thenReturn(limitConfigs);

        // 调用，断言异常
        assertServiceException(() -> customerService.createCustomer(reqVO, userId), CUSTOMER_OWNER_EXCEED_LIMIT);
    }

    // ==================== updateCustomer ====================

    @Test
    public void testUpdateCustomer_success() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO();
        customerMapper.insert(dbCustomer);
        // 准备参数（所有字段都设非 null 值，确保 MyBatis-Plus updateById 更新全部字段）
        CrmCustomerSaveReqVO reqVO = randomPojo(CrmCustomerSaveReqVO.class, o -> {
            o.setId(dbCustomer.getId());
            o.setMobile("13800138000");
            o.setTelephone("010-12345678");
            o.setQq("123456789");
            o.setWechat("test_wechat");
            o.setEmail("test@example.com");
            o.setAreaId(110000);
            o.setDetailAddress("测试地址");
            o.setIndustryId(1);
            o.setLevel(1);
            o.setSource(1);
            o.setRemark("测试备注");
            o.setContactNextTime(LocalDateTime.now());
        });

        // 调用
        customerService.updateCustomer(reqVO);
        // 断言
        CrmCustomerDO updated = customerMapper.selectById(reqVO.getId());
        assertPojoEquals(reqVO, updated, "ownerUserId");
    }

    @Test
    public void testUpdateCustomer_notExists() {
        // 准备参数
        CrmCustomerSaveReqVO reqVO = randomPojo(CrmCustomerSaveReqVO.class, o -> {
            o.setMobile("13800138000");
        });

        // 调用，断言异常
        assertServiceException(() -> customerService.updateCustomer(reqVO), CUSTOMER_NOT_EXISTS);

    }

    // ==================== updateCustomerDealStatus ====================

    @Test
    public void testUpdateCustomerDealStatus_success() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO(o -> o.setDealStatus(false));
        customerMapper.insert(dbCustomer);

        // 调用
        customerService.updateCustomerDealStatus(dbCustomer.getId(), true);
        // 断言
        CrmCustomerDO updated = customerMapper.selectById(dbCustomer.getId());
        assertTrue(updated.getDealStatus());
    }

    @Test
    public void testUpdateCustomerDealStatus_duplicate() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO(o -> o.setDealStatus(true));
        customerMapper.insert(dbCustomer);

        // 调用，断言异常
        assertServiceException(() -> customerService.updateCustomerDealStatus(dbCustomer.getId(), true),
                CUSTOMER_UPDATE_DEAL_STATUS_FAIL);
    }

    @Test
    public void testUpdateCustomerDealStatus_notExists() {
        // 调用，断言异常
        assertServiceException(() -> customerService.updateCustomerDealStatus(randomLongId(), true),
                CUSTOMER_NOT_EXISTS);
    }

    // ==================== updateCustomerFollowUp ====================

    @Test
    public void testUpdateCustomerFollowUp_success() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO();
        customerMapper.insert(dbCustomer);
        // 准备参数
        LocalDateTime nextTime = randomLocalDateTime();
        String lastContent = randomString();

        // 调用
        customerService.updateCustomerFollowUp(dbCustomer.getId(), nextTime, lastContent);
        // 断言
        CrmCustomerDO updated = customerMapper.selectById(dbCustomer.getId());
        assertTrue(updated.getFollowUpStatus());
        assertEquals(nextTime, updated.getContactNextTime());
        assertEquals(lastContent, updated.getContactLastContent());
        assertNotNull(updated.getContactLastTime());
    }

    @Test
    public void testUpdateCustomerFollowUp_notExists() {
        // 调用，断言异常
        assertServiceException(() -> customerService.updateCustomerFollowUp(randomLongId(),
                randomLocalDateTime(), randomString()), CUSTOMER_NOT_EXISTS);
    }

    // ==================== deleteCustomer ====================

    @Test
    public void testDeleteCustomer_success() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO();
        customerMapper.insert(dbCustomer);
        // mock 引用校验通过
        when(contactService.getContactCountByCustomerId(eq(dbCustomer.getId()))).thenReturn(0L);
        when(businessService.getBusinessCountByCustomerId(eq(dbCustomer.getId()))).thenReturn(0L);
        when(contractService.getContractCountByCustomerId(eq(dbCustomer.getId()))).thenReturn(0L);

        // 调用
        customerService.deleteCustomer(dbCustomer.getId());
        // 断言
        assertNull(customerMapper.selectById(dbCustomer.getId()));
        // 校验权限删除被调用
        verify(permissionService, times(1)).deletePermission(
                eq(CrmBizTypeEnum.CRM_CUSTOMER.getType()), eq(dbCustomer.getId()));
    }

    @Test
    public void testDeleteCustomer_hasContactReference() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO();
        customerMapper.insert(dbCustomer);
        // mock 联系人有引用
        when(contactService.getContactCountByCustomerId(eq(dbCustomer.getId()))).thenReturn(1L);

        // 调用，断言异常
        assertServiceException(() -> customerService.deleteCustomer(dbCustomer.getId()),
                CUSTOMER_DELETE_FAIL_HAVE_REFERENCE, CrmBizTypeEnum.CRM_CONTACT.getName());
    }

    @Test
    public void testDeleteCustomer_hasBusinessReference() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO();
        customerMapper.insert(dbCustomer);
        // mock 引用校验
        when(contactService.getContactCountByCustomerId(eq(dbCustomer.getId()))).thenReturn(0L);
        when(businessService.getBusinessCountByCustomerId(eq(dbCustomer.getId()))).thenReturn(1L);

        // 调用，断言异常
        assertServiceException(() -> customerService.deleteCustomer(dbCustomer.getId()),
                CUSTOMER_DELETE_FAIL_HAVE_REFERENCE, CrmBizTypeEnum.CRM_BUSINESS.getName());
    }

    @Test
    public void testDeleteCustomer_notExists() {
        // 调用，断言异常
        assertServiceException(() -> customerService.deleteCustomer(randomLongId()), CUSTOMER_NOT_EXISTS);
    }

    // ==================== transferCustomer ====================

    @Test
    public void testTransferCustomer_success() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO();
        customerMapper.insert(dbCustomer);
        Long newOwnerUserId = randomLongId();
        // 准备参数
        CrmCustomerTransferReqVO reqVO = randomPojo(CrmCustomerTransferReqVO.class, o -> {
            o.setId(dbCustomer.getId());
            o.setNewOwnerUserId(newOwnerUserId);
            o.setToBizTypes(null);
        });
        Long userId = dbCustomer.getOwnerUserId();
        // mock 客户数上限校验通过
        when(customerLimitConfigService.getCustomerLimitConfigListByUserId(
                eq(CUSTOMER_OWNER_LIMIT.getType()), eq(newOwnerUserId))).thenReturn(Collections.emptyList());

        // 调用
        customerService.transferCustomer(reqVO, userId);
        // 断言
        CrmCustomerDO updated = customerMapper.selectById(dbCustomer.getId());
        assertEquals(newOwnerUserId, updated.getOwnerUserId());
        assertNotNull(updated.getOwnerTime());
        // 校验权限转移被调用
        verify(permissionService, times(1)).transferPermission(any(CrmPermissionTransferReqBO.class));
    }

    @Test
    public void testTransferCustomer_notExists() {
        CrmCustomerTransferReqVO reqVO = randomPojo(CrmCustomerTransferReqVO.class);
        assertServiceException(() -> customerService.transferCustomer(reqVO, randomLongId()), CUSTOMER_NOT_EXISTS);
    }

    // ==================== lockCustomer ====================

    @Test
    public void testLockCustomer_success() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO(o -> o.setLockStatus(false));
        customerMapper.insert(dbCustomer);
        Long userId = dbCustomer.getOwnerUserId();
        // 准备参数
        CrmCustomerLockReqVO reqVO = new CrmCustomerLockReqVO();
        reqVO.setId(dbCustomer.getId());
        reqVO.setLockStatus(true);
        // mock 锁定上限校验通过
        when(customerLimitConfigService.getCustomerLimitConfigListByUserId(
                eq(CUSTOMER_LOCK_LIMIT.getType()), eq(userId))).thenReturn(Collections.emptyList());

        // 调用
        customerService.lockCustomer(reqVO, userId);
        // 断言
        CrmCustomerDO updated = customerMapper.selectById(dbCustomer.getId());
        assertTrue(updated.getLockStatus());
    }

    @Test
    public void testUnlockCustomer_success() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO(o -> o.setLockStatus(true));
        customerMapper.insert(dbCustomer);
        // 准备参数
        CrmCustomerLockReqVO reqVO = new CrmCustomerLockReqVO();
        reqVO.setId(dbCustomer.getId());
        reqVO.setLockStatus(false);

        // 调用
        customerService.lockCustomer(reqVO, dbCustomer.getOwnerUserId());
        // 断言
        CrmCustomerDO updated = customerMapper.selectById(dbCustomer.getId());
        assertFalse(updated.getLockStatus());
    }

    @Test
    public void testLockCustomer_duplicateLock() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO(o -> o.setLockStatus(true));
        customerMapper.insert(dbCustomer);
        CrmCustomerLockReqVO reqVO = new CrmCustomerLockReqVO();
        reqVO.setId(dbCustomer.getId());
        reqVO.setLockStatus(true);

        // 调用，断言异常
        assertServiceException(() -> customerService.lockCustomer(reqVO, dbCustomer.getOwnerUserId()),
                CUSTOMER_LOCK_FAIL_IS_LOCK);
    }

    @Test
    public void testLockCustomer_duplicateUnlock() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO(o -> o.setLockStatus(false));
        customerMapper.insert(dbCustomer);
        CrmCustomerLockReqVO reqVO = new CrmCustomerLockReqVO();
        reqVO.setId(dbCustomer.getId());
        reqVO.setLockStatus(false);

        // 调用，断言异常
        assertServiceException(() -> customerService.lockCustomer(reqVO, dbCustomer.getOwnerUserId()),
                CUSTOMER_UNLOCK_FAIL_IS_UNLOCK);
    }

    @Test
    public void testLockCustomer_exceedLockLimit() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO(o -> o.setLockStatus(false));
        customerMapper.insert(dbCustomer);
        Long userId = dbCustomer.getOwnerUserId();
        CrmCustomerLockReqVO reqVO = new CrmCustomerLockReqVO();
        reqVO.setId(dbCustomer.getId());
        reqVO.setLockStatus(true);
        // mock 锁定已达上限
        List<CrmCustomerLimitConfigDO> limitConfigs = singletonList(
                randomPojo(CrmCustomerLimitConfigDO.class, o -> {
                    o.setType(CUSTOMER_LOCK_LIMIT.getType());
                    o.setMaxCount(0);
                }));
        when(customerLimitConfigService.getCustomerLimitConfigListByUserId(
                eq(CUSTOMER_LOCK_LIMIT.getType()), eq(userId))).thenReturn(limitConfigs);

        // 调用，断言异常
        assertServiceException(() -> customerService.lockCustomer(reqVO, userId), CUSTOMER_LOCK_EXCEED_LIMIT);
    }

    // ==================== createCustomer(CrmCustomerCreateReqBO) ====================

    @Test
    public void testCreateCustomerByBO_success() {
        // 准备参数
        CrmCustomerCreateReqBO reqBO = randomPojo(CrmCustomerCreateReqBO.class, o -> {
            o.setMobile("13800138000");
        });
        Long userId = randomLongId();

        // 调用
        Long customerId = customerService.createCustomer(reqBO, userId);
        // 断言
        assertNotNull(customerId);
        CrmCustomerDO customer = customerMapper.selectById(customerId);
        assertEquals(userId, customer.getOwnerUserId());
        // 校验权限创建被调用
        verify(permissionService, times(1)).createPermission(argThat(bo ->
                bo.getBizType().equals(CrmBizTypeEnum.CRM_CUSTOMER.getType())
                        && bo.getBizId().equals(customerId)
                        && bo.getUserId().equals(userId)));
    }

    // ==================== importCustomerList ====================

    @Test
    public void testImportCustomerList_empty() {
        // 准备参数（空列表）
        CrmCustomerImportReqVO importReqVO = CrmCustomerImportReqVO.builder()
                .updateSupport(true).ownerUserId(randomLongId()).build();

        // 调用，断言异常
        assertServiceException(() -> customerService.importCustomerList(Collections.emptyList(), importReqVO),
                CUSTOMER_IMPORT_LIST_IS_EMPTY);
    }

    @Test
    public void testImportCustomerList_createNew() {
        // 准备参数（名称在数据库中不存在）
        String customerName = randomString();
        CrmCustomerImportExcelVO importCustomer = randomPojo(CrmCustomerImportExcelVO.class, o -> {
            o.setName(customerName);
            o.setMobile("13800138000");
        });
        Long ownerUserId = randomLongId();
        CrmCustomerImportReqVO importReqVO = CrmCustomerImportReqVO.builder()
                .updateSupport(true).ownerUserId(ownerUserId).build();

        // 调用
        CrmCustomerImportRespVO respVO = customerService.importCustomerList(
                singletonList(importCustomer), importReqVO);
        // 断言
        assertEquals(1, respVO.getCreateCustomerNames().size());
        assertEquals(customerName, respVO.getCreateCustomerNames().get(0));
        assertEquals(0, respVO.getUpdateCustomerNames().size());
        assertEquals(0, respVO.getFailureCustomerNames().size());
    }

    @Test
    public void testImportCustomerList_updateExisting() {
        // mock 数据（先插入记录，使名称在数据库中已存在）
        CrmCustomerDO dbCustomer = randomCrmCustomerDO();
        customerMapper.insert(dbCustomer);
        // 准备参数
        CrmCustomerImportExcelVO importCustomer = randomPojo(CrmCustomerImportExcelVO.class, o -> {
            o.setName(dbCustomer.getName());
            o.setMobile("13800138000");
        });
        CrmCustomerImportReqVO importReqVO = CrmCustomerImportReqVO.builder()
                .updateSupport(true).ownerUserId(randomLongId()).build();

        // 调用
        CrmCustomerImportRespVO respVO = customerService.importCustomerList(
                singletonList(importCustomer), importReqVO);
        // 断言
        assertEquals(0, respVO.getCreateCustomerNames().size());
        assertEquals(1, respVO.getUpdateCustomerNames().size());
        assertEquals(dbCustomer.getName(), respVO.getUpdateCustomerNames().get(0));
        assertEquals(0, respVO.getFailureCustomerNames().size());
    }

    @Test
    public void testImportCustomerList_notSupportUpdate() {
        // mock 数据（先插入记录，使名称在数据库中已存在）
        CrmCustomerDO dbCustomer = randomCrmCustomerDO();
        customerMapper.insert(dbCustomer);
        // 准备参数
        CrmCustomerImportExcelVO importCustomer = randomPojo(CrmCustomerImportExcelVO.class, o -> {
            o.setName(dbCustomer.getName());
            o.setMobile("13800138000");
        });
        CrmCustomerImportReqVO importReqVO = CrmCustomerImportReqVO.builder()
                .updateSupport(false).ownerUserId(randomLongId()).build();

        // 调用
        CrmCustomerImportRespVO respVO = customerService.importCustomerList(
                singletonList(importCustomer), importReqVO);
        // 断言
        assertEquals(0, respVO.getCreateCustomerNames().size());
        assertEquals(0, respVO.getUpdateCustomerNames().size());
        assertEquals(1, respVO.getFailureCustomerNames().size());
        assertTrue(respVO.getFailureCustomerNames().containsKey(dbCustomer.getName()));
    }

    @Test
    public void testImportCustomerList_validateNameEmpty() {
        // 准备参数（名称为空，过滤后为空）
        CrmCustomerImportExcelVO importCustomer = randomPojo(CrmCustomerImportExcelVO.class, o -> {
            o.setName(null);
        });
        CrmCustomerImportReqVO importReqVO = CrmCustomerImportReqVO.builder()
                .updateSupport(true).ownerUserId(randomLongId()).build();

        // 调用，断言异常（过滤后为空列表）
        assertServiceException(() -> customerService.importCustomerList(
                singletonList(importCustomer), importReqVO), CUSTOMER_IMPORT_LIST_IS_EMPTY);
    }

    // ==================== putCustomerPool ====================

    @Test
    public void testPutCustomerPool_success() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO(o -> {
            o.setOwnerUserId(randomLongId());
            o.setLockStatus(false);
        });
        customerMapper.insert(dbCustomer);

        // 调用
        customerService.putCustomerPool(dbCustomer.getId());
        // 断言
        CrmCustomerDO updated = customerMapper.selectById(dbCustomer.getId());
        assertNull(updated.getOwnerUserId());
        // 校验关联操作
        verify(contactService, times(1)).updateOwnerUserIdByCustomerId(eq(dbCustomer.getId()), isNull());
        verify(permissionService, times(1)).deletePermission(
                eq(CrmBizTypeEnum.CRM_CUSTOMER.getType()), eq(dbCustomer.getId()), anyInt());
    }

    @Test
    public void testPutCustomerPool_notExists() {
        assertServiceException(() -> customerService.putCustomerPool(randomLongId()), CUSTOMER_NOT_EXISTS);
    }

    @Test
    public void testPutCustomerPool_alreadyInPool() {
        // mock 数据（公海客户，无负责人）
        CrmCustomerDO dbCustomer = randomCrmCustomerDO(o -> {
            o.setOwnerUserId(null);
            o.setLockStatus(false);
        });
        customerMapper.insert(dbCustomer);

        // 调用，断言异常
        assertServiceException(() -> customerService.putCustomerPool(dbCustomer.getId()),
                CUSTOMER_IN_POOL, dbCustomer.getName());
    }

    @Test
    public void testPutCustomerPool_locked() {
        // mock 数据（已锁定客户）
        CrmCustomerDO dbCustomer = randomCrmCustomerDO(o -> {
            o.setOwnerUserId(randomLongId());
            o.setLockStatus(true);
        });
        customerMapper.insert(dbCustomer);

        // 调用，断言异常
        assertServiceException(() -> customerService.putCustomerPool(dbCustomer.getId()),
                CUSTOMER_LOCKED_PUT_POOL_FAIL, dbCustomer.getName());
    }

    // ==================== receiveCustomer ====================

    @Test
    public void testReceiveCustomer_success() {
        // mock 数据（公海客户，无负责人）
        CrmCustomerDO dbCustomer = randomCrmCustomerDO(o -> {
            o.setOwnerUserId(null);
            o.setLockStatus(false);
            o.setDealStatus(false);
        });
        customerMapper.insert(dbCustomer);
        Long newOwnerUserId = randomLongId();
        List<Long> ids = singletonList(dbCustomer.getId());
        // mock 客户数上限校验通过
        when(customerLimitConfigService.getCustomerLimitConfigListByUserId(
                eq(CUSTOMER_OWNER_LIMIT.getType()), eq(newOwnerUserId))).thenReturn(Collections.emptyList());

        // 调用
        customerService.receiveCustomer(ids, newOwnerUserId, true);
        // 断言
        CrmCustomerDO updated = customerMapper.selectById(dbCustomer.getId());
        assertEquals(newOwnerUserId, updated.getOwnerUserId());
        assertNotNull(updated.getOwnerTime());
        // 校验权限创建被调用
        verify(permissionService, times(1)).createPermissionBatch(anyList());
    }

    @Test
    public void testReceiveCustomer_notExists() {
        List<Long> ids = singletonList(randomLongId());
        assertServiceException(() -> customerService.receiveCustomer(ids, randomLongId(), true),
                CUSTOMER_NOT_EXISTS);
    }

    @Test
    public void testReceiveCustomer_alreadyHasOwner() {
        // mock 数据（已有负责人）
        CrmCustomerDO dbCustomer = randomCrmCustomerDO(o -> {
            o.setOwnerUserId(randomLongId());
            o.setLockStatus(false);
            o.setDealStatus(false);
        });
        customerMapper.insert(dbCustomer);
        List<Long> ids = singletonList(dbCustomer.getId());

        // 调用，断言异常
        assertServiceException(() -> customerService.receiveCustomer(ids, randomLongId(), true),
                CUSTOMER_OWNER_EXISTS, dbCustomer.getName());
    }

    @Test
    public void testReceiveCustomer_locked() {
        // mock 数据（已锁定）
        CrmCustomerDO dbCustomer = randomCrmCustomerDO(o -> {
            o.setOwnerUserId(null);
            o.setLockStatus(true);
            o.setDealStatus(false);
        });
        customerMapper.insert(dbCustomer);
        List<Long> ids = singletonList(dbCustomer.getId());

        // 调用，断言异常
        assertServiceException(() -> customerService.receiveCustomer(ids, randomLongId(), true),
                CUSTOMER_LOCKED, dbCustomer.getName());
    }

    // ==================== autoPutCustomerPool ====================

    @Test
    public void testAutoPutCustomerPool_disabled() {
        // mock 公海未启用
        when(customerPoolConfigService.getCustomerPoolConfig()).thenReturn(null);

        // 调用
        int count = customerService.autoPutCustomerPool();
        // 断言
        assertEquals(0, count);
    }

    // ==================== 查询方法 ====================

    @Test
    public void testGetCustomer_success() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO();
        customerMapper.insert(dbCustomer);

        // 调用
        CrmCustomerDO customer = customerService.getCustomer(dbCustomer.getId());
        // 断言
        assertPojoEquals(dbCustomer, customer);
    }

    @Test
    public void testGetCustomerList_success() {
        // mock 数据
        CrmCustomerDO customer1 = randomCrmCustomerDO();
        customerMapper.insert(customer1);
        CrmCustomerDO customer2 = randomCrmCustomerDO();
        customerMapper.insert(customer2);
        // 测试不匹配的
        customerMapper.insert(randomCrmCustomerDO());
        // 准备参数
        Collection<Long> ids = Arrays.asList(customer1.getId(), customer2.getId());

        // 调用
        List<CrmCustomerDO> list = customerService.getCustomerList(ids);
        // 断言
        assertEquals(2, list.size());
    }

    @Test
    public void testGetCustomerList_empty() {
        // 调用
        List<CrmCustomerDO> list = customerService.getCustomerList(Collections.emptyList());
        // 断言
        assertTrue(list.isEmpty());
    }

    @Test
    public void testGetCustomerPage_success() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO();
        customerMapper.insert(dbCustomer);
        // 准备参数
        CrmCustomerPageReqVO pageReqVO = new CrmCustomerPageReqVO();
        pageReqVO.setPageNo(1);
        pageReqVO.setPageSize(10);
        Long userId = dbCustomer.getOwnerUserId();

        // 调用
        PageResult<CrmCustomerDO> pageResult = customerService.getCustomerPage(pageReqVO, userId);
        // 断言
        assertNotNull(pageResult);
    }

    @Test
    public void testGetTodayContactCustomerCount() {
        Long userId = randomLongId();
        Long count = customerService.getTodayContactCustomerCount(userId);
        assertNotNull(count);
    }

    @Test
    public void testGetFollowCustomerCount() {
        Long userId = randomLongId();
        Long count = customerService.getFollowCustomerCount(userId);
        assertNotNull(count);
    }

    // ==================== validateCustomer ====================

    @Test
    public void testValidateCustomer_success() {
        // mock 数据
        CrmCustomerDO dbCustomer = randomCrmCustomerDO();
        customerMapper.insert(dbCustomer);

        // 调用（不抛异常即为通过）
        customerService.validateCustomer(dbCustomer.getId());
    }

    @Test
    public void testValidateCustomer_notExists() {
        assertServiceException(() -> customerService.validateCustomer(randomLongId()), CUSTOMER_NOT_EXISTS);
    }

    // ==================== getPutPoolRemindCustomerPage ====================

    @Test
    public void testGetPutPoolRemindCustomerPage_disabled() {
        // mock 公海未启用
        when(customerPoolConfigService.getCustomerPoolConfig()).thenReturn(null);
        CrmCustomerPageReqVO pageVO = new CrmCustomerPageReqVO();
        pageVO.setPageNo(1);
        pageVO.setPageSize(10);

        // 调用
        PageResult<CrmCustomerDO> result = customerService.getPutPoolRemindCustomerPage(pageVO, randomLongId());
        // 断言
        assertTrue(result.getList().isEmpty());
    }

    @Test
    public void testGetPutPoolRemindCustomerCount_disabled() {
        // mock 公海未启用
        when(customerPoolConfigService.getCustomerPoolConfig()).thenReturn(null);

        // 调用
        Long count = customerService.getPutPoolRemindCustomerCount(randomLongId());
        // 断言
        assertEquals(0L, count);
    }

    // ==================== 随机对象辅助方法 ====================

    @SafeVarargs
    private static CrmCustomerDO randomCrmCustomerDO(java.util.function.Consumer<CrmCustomerDO>... consumers) {
        return randomPojo(CrmCustomerDO.class, consumers);
    }

}