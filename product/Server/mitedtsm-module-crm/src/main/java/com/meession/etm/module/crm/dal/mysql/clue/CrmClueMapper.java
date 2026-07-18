package com.meession.etm.module.crm.dal.mysql.clue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.meession.etm.module.crm.controller.admin.clue.vo.CrmCluePageReqVO;
import com.meession.etm.module.crm.dal.dataobject.clue.CrmClueDO;
import com.meession.etm.module.crm.enums.common.CrmBizTypeEnum;
import com.meession.etm.module.crm.enums.common.CrmSceneTypeEnum;
import com.meession.etm.module.crm.util.CrmPermissionUtils;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 线索 Mapper
 *
 * @author Wanwan
 */
@Mapper
public interface CrmClueMapper extends BaseMapperX<CrmClueDO> {

    default PageResult<CrmClueDO> selectPage(CrmCluePageReqVO pageReqVO, Long userId) {
        MPJLambdaWrapperX<CrmClueDO> query = new MPJLambdaWrapperX<>();
        // 拼接数据权限的查询条件
        if (Boolean.TRUE.equals(pageReqVO.getPool())) {
            query.isNull(CrmClueDO::getOwnerUserId);
        } else {
            CrmPermissionUtils.appendPermissionCondition(query, CrmBizTypeEnum.CRM_CLUE.getType(),
                    CrmClueDO::getId, userId, pageReqVO.getSceneType());
        }
        // 拼接自身的查询条件
        query.selectAll(CrmClueDO.class)
                .likeIfPresent(CrmClueDO::getName, pageReqVO.getName())
                .eqIfPresent(CrmClueDO::getTransformStatus, pageReqVO.getTransformStatus())
                .likeIfPresent(CrmClueDO::getTelephone, pageReqVO.getTelephone())
                .likeIfPresent(CrmClueDO::getMobile, pageReqVO.getMobile())
                .eqIfPresent(CrmClueDO::getIndustryId, pageReqVO.getIndustryId())
                .eqIfPresent(CrmClueDO::getLevel, pageReqVO.getLevel())
                .eqIfPresent(CrmClueDO::getSource, pageReqVO.getSource())
                .eqIfPresent(CrmClueDO::getFollowUpStatus, pageReqVO.getFollowUpStatus())
                .betweenIfPresent(CrmClueDO::getCreateTime, pageReqVO.getCreateTime())
                .orderByDesc(CrmClueDO::getId);
        return selectJoinPage(pageReqVO, CrmClueDO.class, query);
    }

    default Long selectCountByFollow(Long userId) {
        MPJLambdaWrapperX<CrmClueDO> query = new MPJLambdaWrapperX<>();
        // 我负责的 + 非公海
        CrmPermissionUtils.appendPermissionCondition(query, CrmBizTypeEnum.CRM_CLUE.getType(),
                CrmClueDO::getId, userId, CrmSceneTypeEnum.OWNER.getType());
        // 未跟进 + 未转化
        query.eq(CrmClueDO::getFollowUpStatus, false)
                .eq(CrmClueDO::getTransformStatus, false);
        return selectCount(query);
    }

    default int updateOwnerUserIdById(Long id, Long ownerUserId) {
        return update(new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CrmClueDO>()
                .eq(CrmClueDO::getId, id)
                .set(CrmClueDO::getOwnerUserId, ownerUserId));
    }

    default Long selectCountByPool() {
        return selectCount(new LambdaQueryWrapperX<CrmClueDO>()
                .isNull(CrmClueDO::getOwnerUserId));
    }

    // ==================== 公海增强查询方法 ====================

    /**
     * 获得需要自动掉入公海的线索列表
     *
     * @param expireDays 回收时效天数
     * @return 线索列表
     */
    default List<CrmClueDO> selectListByAutoPool(int expireDays) {
        LambdaQueryWrapper<CrmClueDO> query = new LambdaQueryWrapper<>();
        // 有负责人
        query.gt(CrmClueDO::getOwnerUserId, 0);
        // 未转化
        query.eq(CrmClueDO::getTransformStatus, false);
        // 跟进超时：contactLastTime 早于 (now - expireDays) 或为空
        LocalDateTime expireTime = LocalDateTime.now().minusDays(expireDays);
        query.and(q -> q.lt(CrmClueDO::getContactLastTime, expireTime)
                .or().isNull(CrmClueDO::getContactLastTime));
        return selectList(query);
    }

    /**
     * 乐观锁更新负责人：仅当 ownerUserId 等于 expectedOwnerUserId 时才更新
     *
     * @param id                 线索编号
     * @param newOwnerUserId     新负责人（null 表示放入公海）
     * @param expectedOwnerUserId 期望的当前负责人
     * @return 更新行数
     */
    default int updateOwnerUserIdByIdWithLock(Long id, Long newOwnerUserId, Long expectedOwnerUserId) {
        LambdaUpdateWrapper<CrmClueDO> wrapper = new LambdaUpdateWrapper<CrmClueDO>()
                .eq(CrmClueDO::getId, id);
        if (expectedOwnerUserId == null) {
            wrapper.isNull(CrmClueDO::getOwnerUserId);
        } else {
            wrapper.eq(CrmClueDO::getOwnerUserId, expectedOwnerUserId);
        }
        return update(new CrmClueDO().setId(id).setOwnerUserId(newOwnerUserId)
                .setPoolStatus(newOwnerUserId == null ? 1 : 0)
                .setPoolEnterTime(newOwnerUserId == null ? LocalDateTime.now() : null)
                .setPoolReason(newOwnerUserId == null ? "自动回收" : null), wrapper);
    }

}
