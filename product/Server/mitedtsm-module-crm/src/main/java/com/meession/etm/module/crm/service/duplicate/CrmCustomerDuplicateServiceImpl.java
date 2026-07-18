package com.meession.etm.module.crm.service.duplicate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO;
import com.meession.etm.module.crm.dal.dataobject.contact.CrmContactDO;
import com.meession.etm.module.crm.dal.dataobject.contract.CrmContractDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.dal.dataobject.duplicate.CrmDuplicateConfigDO;
import com.meession.etm.module.crm.dal.mysql.business.CrmBusinessMapper;
import com.meession.etm.module.crm.dal.mysql.contact.CrmContactMapper;
import com.meession.etm.module.crm.dal.mysql.contract.CrmContractMapper;
import com.meession.etm.module.crm.dal.mysql.duplicate.CrmDuplicateConfigMapper;
import com.meession.etm.module.crm.dal.mysql.customer.CrmCustomerMapper;
import com.meession.etm.module.crm.service.business.CrmBusinessService;
import com.meession.etm.module.crm.service.contact.CrmContactService;
import com.meession.etm.module.crm.service.contract.CrmContractService;
import com.meession.etm.module.crm.service.tag.CrmTagService;
import com.meession.etm.module.system.api.user.AdminUserApi;
import com.meession.etm.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

/**
 * 客户查重 Service 实现
 *
 * @author system
 */
@Service
@Slf4j
@Validated
public class CrmCustomerDuplicateServiceImpl implements CrmCustomerDuplicateService {

    @Resource
    private CrmCustomerMapper customerMapper;
    @Resource
    private CrmDuplicateConfigMapper duplicateConfigMapper;
    @Resource
    private CrmContactMapper contactMapper;
    @Resource
    private CrmContactService contactService;
    @Resource
    private CrmBusinessMapper businessMapper;
    @Resource
    private CrmBusinessService businessService;
    @Resource
    private CrmContractMapper contractMapper;
    @Resource
    private CrmContractService contractService;
    @Resource
    private CrmTagService tagService;
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public List<Map<String, Object>> checkDuplicate(String name, String mobile, String email, String wechat, Long userId) {
        CrmDuplicateConfigDO config = duplicateConfigMapper.selectOne();
        if (config == null) {
            // 默认检查规则
            config = new CrmDuplicateConfigDO();
            config.setCheckName(true);
            config.setCheckMobile(true);
            config.setCheckEmail(false);
            config.setCheckWechat(false);
            config.setCheckScope("ALL");
        }

        // 获取当前用户信息，用于范围过滤
        Long userDeptId = null;
        if ("DEPT".equals(config.getCheckScope()) || "SELF".equals(config.getCheckScope())) {
            try {
                AdminUserRespDTO user = adminUserApi.getUser(userId);
                if (user != null) {
                    userDeptId = user.getDeptId();
                }
            } catch (Exception e) {
                log.warn("[checkDuplicate][获取用户信息失败, userId={}, error={}]", userId, e.getMessage());
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        Set<Long> seenIds = new HashSet<>();

        // 1. 按名称模糊匹配
        if (config.getCheckName() && StrUtil.isNotBlank(name)) {
            List<CrmCustomerDO> nameMatches = customerMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmCustomerDO>()
                            .like(CrmCustomerDO::getName, name));
            for (CrmCustomerDO c : nameMatches) {
                if (seenIds.add(c.getId()) && withinScope(c, config.getCheckScope(), userId, userDeptId)) {
                    result.add(Map.of("customer", c, "matchField", "name", "matchValue", c.getName()));
                }
            }
        }

        // 2. 按手机号精确匹配
        if (config.getCheckMobile() && StrUtil.isNotBlank(mobile)) {
            List<CrmCustomerDO> mobileMatches = customerMapper.selectList(CrmCustomerDO::getMobile, mobile);
            for (CrmCustomerDO c : mobileMatches) {
                if (seenIds.add(c.getId()) && withinScope(c, config.getCheckScope(), userId, userDeptId)) {
                    result.add(Map.of("customer", c, "matchField", "mobile", "matchValue", c.getMobile()));
                }
            }
        }

        // 3. 按邮箱精确匹配
        if (config.getCheckEmail() && StrUtil.isNotBlank(email)) {
            List<CrmCustomerDO> emailMatches = customerMapper.selectList(CrmCustomerDO::getEmail, email);
            for (CrmCustomerDO c : emailMatches) {
                if (seenIds.add(c.getId()) && withinScope(c, config.getCheckScope(), userId, userDeptId)) {
                    result.add(Map.of("customer", c, "matchField", "email", "matchValue", c.getEmail()));
                }
            }
        }

        // 4. 按微信精确匹配
        if (config.getCheckWechat() && StrUtil.isNotBlank(wechat)) {
            List<CrmCustomerDO> wechatMatches = customerMapper.selectList(CrmCustomerDO::getWechat, wechat);
            for (CrmCustomerDO c : wechatMatches) {
                if (seenIds.add(c.getId()) && withinScope(c, config.getCheckScope(), userId, userDeptId)) {
                    result.add(Map.of("customer", c, "matchField", "wechat", "matchValue", c.getWechat()));
                }
            }
        }

        return result;
    }

    /**
     * 判断客户是否在查重范围内
     *
     * @param customer   客户
     * @param checkScope 查重范围：ALL/DEPT/SELF
     * @param userId     当前用户编号
     * @param userDeptId 当前用户部门编号
     * @return 是否在范围内
     */
    private boolean withinScope(CrmCustomerDO customer, String checkScope, Long userId, Long userDeptId) {
        if ("ALL".equals(checkScope) || checkScope == null) {
            return true;
        }
        if ("SELF".equals(checkScope)) {
            // 只检查当前用户自己的客户
            return Objects.equals(customer.getOwnerUserId(), userId);
        }
        if ("DEPT".equals(checkScope)) {
            // 只检查当前用户同部门的客户
            if (userDeptId == null) {
                return true; // 无法获取部门信息时，不过滤
            }
            if (customer.getOwnerUserId() == null) {
                return false;
            }
            try {
                AdminUserRespDTO ownerUser = adminUserApi.getUser(customer.getOwnerUserId());
                return ownerUser != null && Objects.equals(ownerUser.getDeptId(), userDeptId);
            } catch (Exception e) {
                log.warn("[withinScope][获取负责人部门信息失败, ownerUserId={}, error={}]", customer.getOwnerUserId(), e.getMessage());
                return true; // 无法获取时不过滤
            }
        }
        return true;
    }

    @Override
    public List<List<CrmCustomerDO>> findDuplicateCustomers(Boolean checkName, Boolean checkMobile) {
        List<CrmCustomerDO> all = customerMapper.selectList();
        Map<String, List<CrmCustomerDO>> byName = new LinkedHashMap<>();
        Map<String, List<CrmCustomerDO>> byMobile = new LinkedHashMap<>();

        for (CrmCustomerDO customer : all) {
            if (checkName && StrUtil.isNotBlank(customer.getName())) {
                byName.computeIfAbsent(customer.getName().trim(), k -> new ArrayList<>()).add(customer);
            }
            if (checkMobile && StrUtil.isNotBlank(customer.getMobile())) {
                byMobile.computeIfAbsent(customer.getMobile().trim(), k -> new ArrayList<>()).add(customer);
            }
        }

        List<List<CrmCustomerDO>> duplicates = new ArrayList<>();
        for (List<CrmCustomerDO> group : byName.values()) {
            if (group.size() > 1) duplicates.add(group);
        }
        for (List<CrmCustomerDO> group : byMobile.values()) {
            if (group.size() > 1) duplicates.add(group);
        }

        return duplicates;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergeCustomers(Long sourceCustomerId, Long targetCustomerId) {
        if (Objects.equals(sourceCustomerId, targetCustomerId)) {
            return;
        }
        log.info("[mergeCustomers][开始合并客户, sourceId={}, targetId={}]", sourceCustomerId, targetCustomerId);

        // 1. 转移联系人：将源客户下的联系人转移到目标客户
        List<CrmContactDO> contacts = contactMapper.selectListByCustomerId(sourceCustomerId);
        if (CollUtil.isNotEmpty(contacts)) {
            for (CrmContactDO contact : contacts) {
                contactMapper.updateById(new CrmContactDO().setId(contact.getId()).setCustomerId(targetCustomerId));
                log.info("[mergeCustomers][转移联系人, contactId={}, sourceCustomerId={}, targetCustomerId={}]",
                        contact.getId(), sourceCustomerId, targetCustomerId);
            }
        }

        // 2. 转移商机：将源客户下的商机转移到目标客户
        List<CrmBusinessDO> businesses = businessMapper.selectListByCustomerIdOwnerUserId(sourceCustomerId, null);
        // Use a more direct approach - select by customerId
        List<CrmBusinessDO> allBusinesses = businessMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmBusinessDO>()
                        .eq(CrmBusinessDO::getCustomerId, sourceCustomerId));
        if (CollUtil.isNotEmpty(allBusinesses)) {
            for (CrmBusinessDO business : allBusinesses) {
                businessMapper.updateById(new CrmBusinessDO().setId(business.getId()).setCustomerId(targetCustomerId));
                log.info("[mergeCustomers][转移商机, businessId={}, sourceCustomerId={}, targetCustomerId={}]",
                        business.getId(), sourceCustomerId, targetCustomerId);
            }
        }

        // 3. 转移合同：将源客户下的合同转移到目标客户
        List<CrmContractDO> contracts = contractMapper.selectListByCustomerIdOwnerUserId(sourceCustomerId, null);
        // Also try direct select
        List<CrmContractDO> allContracts = contractMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrmContractDO>()
                        .eq(CrmContractDO::getCustomerId, sourceCustomerId));
        if (CollUtil.isNotEmpty(allContracts)) {
            for (CrmContractDO contract : allContracts) {
                contractMapper.updateById(new CrmContractDO().setId(contract.getId()).setCustomerId(targetCustomerId));
                log.info("[mergeCustomers][转移合同, contractId={}, sourceCustomerId={}, targetCustomerId={}]",
                        contract.getId(), sourceCustomerId, targetCustomerId);
            }
        }

        // 4. 转移标签：将源客户的标签添加到目标客户
        try {
            List<com.meession.etm.module.crm.dal.dataobject.tag.CrmTagDO> sourceTags =
                    tagService.getCustomerTags(sourceCustomerId);
            if (CollUtil.isNotEmpty(sourceTags)) {
                List<Long> tagIds = sourceTags.stream()
                        .map(com.meession.etm.module.crm.dal.dataobject.tag.CrmTagDO::getId)
                        .toList();
                tagService.batchAddCustomerTags(Collections.singletonList(targetCustomerId), tagIds);
                log.info("[mergeCustomers][转移标签, sourceCustomerId={}, targetCustomerId={}, tagCount={}]",
                        sourceCustomerId, targetCustomerId, tagIds.size());
            }
        } catch (Exception e) {
            log.warn("[mergeCustomers][转移标签失败, sourceCustomerId={}, targetCustomerId={}, error={}]",
                    sourceCustomerId, targetCustomerId, e.getMessage());
        }

        // 5. 删除源客户
        customerMapper.deleteById(sourceCustomerId);
        log.info("[mergeCustomers][合并完成, sourceId={}, targetId={}]", sourceCustomerId, targetCustomerId);
    }

}
