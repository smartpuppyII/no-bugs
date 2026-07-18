package com.meession.etm.module.crm.dal.mysql.order;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderDO;
import com.meession.etm.module.crm.enums.common.CrmAuditStatusEnum;
import com.meession.etm.module.crm.enums.common.CrmBizTypeEnum;
import com.meession.etm.module.crm.enums.common.CrmSceneTypeEnum;
import com.meession.etm.module.crm.util.CrmPermissionUtils;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CRM 订单 Mapper
 *
 * @author HUIHUI
 */
@Mapper
public interface CrmOrderMapper extends BaseMapperX<CrmOrderDO> {

    default CrmOrderDO selectByNo(String no) {
        return selectOne(CrmOrderDO::getNo, no);
    }

    default PageResult<CrmOrderDO> selectPageByCustomerId(CrmOrderPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<CrmOrderDO>()
                .eq(CrmOrderDO::getCustomerId, pageReqVO.getCustomerId())
                .likeIfPresent(CrmOrderDO::getNo, pageReqVO.getNo())
                .likeIfPresent(CrmOrderDO::getName, pageReqVO.getName())
                .eqIfPresent(CrmOrderDO::getCustomerId, pageReqVO.getCustomerId())
                .eqIfPresent(CrmOrderDO::getBusinessId, pageReqVO.getBusinessId())
                .orderByDesc(CrmOrderDO::getId));
    }

    default PageResult<CrmOrderDO> selectPageByBusinessId(CrmOrderPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<CrmOrderDO>()
                .eq(CrmOrderDO::getBusinessId, pageReqVO.getBusinessId())
                .likeIfPresent(CrmOrderDO::getNo, pageReqVO.getNo())
                .likeIfPresent(CrmOrderDO::getName, pageReqVO.getName())
                .eqIfPresent(CrmOrderDO::getCustomerId, pageReqVO.getCustomerId())
                .eqIfPresent(CrmOrderDO::getBusinessId, pageReqVO.getBusinessId())
                .orderByDesc(CrmOrderDO::getId));
    }

    default PageResult<CrmOrderDO> selectPage(CrmOrderPageReqVO pageReqVO, Long userId) {
        MPJLambdaWrapperX<CrmOrderDO> query = new MPJLambdaWrapperX<>();
        // 拼接数据权限的查询条件
        CrmPermissionUtils.appendPermissionCondition(query, CrmBizTypeEnum.CRM_ORDER.getType(),
                CrmOrderDO::getId, userId, pageReqVO.getSceneType());
        // 拼接自身的查询条件
        query.selectAll(CrmOrderDO.class)
                .likeIfPresent(CrmOrderDO::getNo, pageReqVO.getNo())
                .likeIfPresent(CrmOrderDO::getName, pageReqVO.getName())
                .eqIfPresent(CrmOrderDO::getCustomerId, pageReqVO.getCustomerId())
                .eqIfPresent(CrmOrderDO::getBusinessId, pageReqVO.getBusinessId())
                .eqIfPresent(CrmOrderDO::getAuditStatus, pageReqVO.getAuditStatus())
                .orderByDesc(CrmOrderDO::getId);

        return selectJoinPage(pageReqVO, CrmOrderDO.class, query);
    }

    default Long selectCountByContactId(Long contactId) {
        return selectCount(CrmOrderDO::getSignContactId, contactId);
    }

    default Long selectCountByBusinessId(Long businessId) {
        return selectCount(CrmOrderDO::getBusinessId, businessId);
    }

    default Long selectCountByAudit(Long userId) {
        MPJLambdaWrapperX<CrmOrderDO> query = new MPJLambdaWrapperX<>();
        // 我负责的 + 非公海
        CrmPermissionUtils.appendPermissionCondition(query, CrmBizTypeEnum.CRM_ORDER.getType(),
                CrmOrderDO::getId, userId, CrmSceneTypeEnum.OWNER.getType());
        // 未审核
        query.eq(CrmOrderDO::getAuditStatus, CrmAuditStatusEnum.PROCESS.getStatus());
        return selectCount(query);
    }

    default Long selectCountByRemind(Long userId) {
        MPJLambdaWrapperX<CrmOrderDO> query = new MPJLambdaWrapperX<>();
        // 我负责的 + 非公海
        CrmPermissionUtils.appendPermissionCondition(query, CrmBizTypeEnum.CRM_ORDER.getType(),
                CrmOrderDO::getId, userId, CrmSceneTypeEnum.OWNER.getType());
        // 即将到期
        LocalDateTime beginOfToday = LocalDateTimeUtil.beginOfDay(LocalDateTime.now());
        LocalDateTime endOfToday = LocalDateTimeUtil.endOfDay(LocalDateTime.now());
        query.eq(CrmOrderDO::getAuditStatus, CrmAuditStatusEnum.APPROVE.getStatus()) // 必须审批通过！
                .between(CrmOrderDO::getEndTime, beginOfToday, endOfToday.plusDays(7));
        return selectCount(query);
    }

    default List<CrmOrderDO> selectListByCustomerIdOwnerUserId(Long customerId, Long ownerUserId) {
        return selectList(new LambdaQueryWrapperX<CrmOrderDO>()
                .eq(CrmOrderDO::getCustomerId, customerId)
                .eq(CrmOrderDO::getOwnerUserId, ownerUserId));
    }

}
