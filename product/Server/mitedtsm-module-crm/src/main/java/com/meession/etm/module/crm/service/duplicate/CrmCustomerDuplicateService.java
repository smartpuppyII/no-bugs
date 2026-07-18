package com.meession.etm.module.crm.service.duplicate;

import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;

import java.util.List;
import java.util.Map;

/**
 * 客户查重 Service 接口
 *
 * @author system
 */
public interface CrmCustomerDuplicateService {

    /**
     * 检查客户是否重复
     *
     * @param name   客户名称
     * @param mobile 手机号
     * @param email  邮箱
     * @param wechat 微信
     * @param userId 当前用户编号
     * @return 重复客户列表，key 为匹配字段
     */
    List<Map<String, Object>> checkDuplicate(String name, String mobile, String email, String wechat, Long userId);

    /**
     * 获取所有可能的重复客户对（用于存量去重）
     *
     * @param checkName   是否检查名称
     * @param checkMobile 是否检查手机号
     * @return 重复客户分组列表
     */
    List<List<CrmCustomerDO>> findDuplicateCustomers(Boolean checkName, Boolean checkMobile);

    /**
     * 合并客户：将源客户的数据迁移到目标客户，然后删除源客户
     *
     * @param sourceCustomerId 源客户编号
     * @param targetCustomerId 目标客户编号
     */
    void mergeCustomers(Long sourceCustomerId, Long targetCustomerId);

}
