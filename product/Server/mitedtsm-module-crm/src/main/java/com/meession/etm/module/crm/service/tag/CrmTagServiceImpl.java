package com.meession.etm.module.crm.service.tag;

import cn.hutool.core.collection.CollUtil;
import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.dal.dataobject.tag.CrmCustomerTagDO;
import com.meession.etm.module.crm.dal.dataobject.tag.CrmTagDO;
import com.meession.etm.module.crm.dal.mysql.tag.CrmCustomerTagMapper;
import com.meession.etm.module.crm.dal.mysql.tag.CrmTagMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签 Service 实现
 *
 * @author system
 */
@Service
@Validated
public class CrmTagServiceImpl implements CrmTagService {

    @Resource
    private CrmTagMapper tagMapper;

    @Resource
    private CrmCustomerTagMapper customerTagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTag(CrmTagDO tagDO) {
        tagMapper.insert(tagDO);
        return tagDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTag(CrmTagDO tagDO) {
        tagMapper.updateById(tagDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long id) {
        tagMapper.deleteById(id);
    }

    @Override
    public PageResult<CrmTagDO> getTagPage(PageParam pageParam, String name, String groupName) {
        return tagMapper.selectPage(pageParam,
                new com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX<CrmTagDO>()
                        .likeIfPresent(CrmTagDO::getName, name)
                        .eqIfPresent(CrmTagDO::getGroupName, groupName)
                        .orderByAsc(CrmTagDO::getSortOrder));
    }

    @Override
    public List<CrmTagDO> getAllTags() {
        return tagMapper.selectAllTags();
    }

    @Override
    public List<CrmTagDO> getTagsByGroup(String groupName) {
        return tagMapper.selectListByGroup(groupName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCustomerTags(Long customerId, List<Long> tagIds) {
        if (CollUtil.isEmpty(tagIds)) {
            return;
        }
        for (Long tagId : tagIds) {
            List<CrmCustomerTagDO> existing = customerTagMapper.selectList(
                    CrmCustomerTagDO::getCustomerId, customerId,
                    CrmCustomerTagDO::getTagId, tagId);
            if (CollUtil.isEmpty(existing)) {
                CrmCustomerTagDO customerTag = CrmCustomerTagDO.builder()
                        .customerId(customerId)
                        .tagId(tagId)
                        .createTime(LocalDateTime.now())
                        .build();
                customerTagMapper.insert(customerTag);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddCustomerTags(List<Long> customerIds, List<Long> tagIds) {
        for (Long customerId : customerIds) {
            addCustomerTags(customerId, tagIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCustomerTag(Long customerId, Long tagId) {
        customerTagMapper.deleteByCustomerIdAndTagId(customerId, tagId);
    }

    @Override
    public List<CrmTagDO> getCustomerTags(Long customerId) {
        List<CrmCustomerTagDO> relations = customerTagMapper.selectListByCustomerId(customerId);
        if (CollUtil.isEmpty(relations)) {
            return Collections.emptyList();
        }
        List<Long> tagIds = relations.stream().map(CrmCustomerTagDO::getTagId).collect(Collectors.toList());
        return tagMapper.selectList(CrmTagDO::getId, tagIds);
    }

    @Override
    public List<CrmCustomerTagDO> getCustomerTagsByCustomerIds(List<Long> customerIds) {
        if (CollUtil.isEmpty(customerIds)) {
            return Collections.emptyList();
        }
        return customerTagMapper.selectListByCustomerIds(customerIds);
    }

}
