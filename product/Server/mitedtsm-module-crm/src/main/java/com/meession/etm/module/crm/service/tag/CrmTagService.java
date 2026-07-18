package com.meession.etm.module.crm.service.tag;

import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.dal.dataobject.tag.CrmCustomerTagDO;
import com.meession.etm.module.crm.dal.dataobject.tag.CrmTagDO;

import java.util.List;

/**
 * 标签 Service 接口
 *
 * @author system
 */
public interface CrmTagService {

    /**
     * 创建标签
     */
    Long createTag(CrmTagDO tagDO);

    /**
     * 更新标签
     */
    void updateTag(CrmTagDO tagDO);

    /**
     * 删除标签
     */
    void deleteTag(Long id);

    /**
     * 获得标签分页
     */
    PageResult<CrmTagDO> getTagPage(PageParam pageParam, String name, String groupName);

    /**
     * 获得所有标签
     */
    List<CrmTagDO> getAllTags();

    /**
     * 按组获得标签列表
     */
    List<CrmTagDO> getTagsByGroup(String groupName);

    /**
     * 为客户添加标签
     */
    void addCustomerTags(Long customerId, List<Long> tagIds);

    /**
     * 批量添加客户标签
     */
    void batchAddCustomerTags(List<Long> customerIds, List<Long> tagIds);

    /**
     * 移除客户标签
     */
    void removeCustomerTag(Long customerId, Long tagId);

    /**
     * 获得客户的标签列表
     */
    List<CrmTagDO> getCustomerTags(Long customerId);

    /**
     * 获得多个客户的标签Map
     */
    List<CrmCustomerTagDO> getCustomerTagsByCustomerIds(List<Long> customerIds);

}
