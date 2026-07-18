package com.meession.etm.module.crm.dal.dataobject.clue;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.enums.DictTypeConstants;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * CRM 线索 DO
 *
 * @author Wanwan
 */
@TableName("crm_clue")
@KeySequence("crm_clue_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmClueDO extends BaseDO {

    /**
     * 编号，主键自增
     */
    @TableId
    private Long id;
    /**
     * 线索名称
     */
    private String name;

    /**
     * 跟进状态
     */
    private Boolean followUpStatus;
    /**
     * 最后跟进时间
     */
    private LocalDateTime contactLastTime;
    /**
     * 最后跟进内容
     */
    private String contactLastContent;
    /**
     * 下次联系时间
     */
    private LocalDateTime contactNextTime;

    /**
     * 负责人的用户编号
     *
     * 关联 AdminUserDO 的 id 字段
     */
    private Long ownerUserId;

    /**
     * 成为负责人时间
     */
    private LocalDateTime ownerTime;

    /**
     * 转化状态
     *
     * true 表示已转换，会更新 {@link #customerId} 字段
     */
    private Boolean transformStatus;
    /**
     * 客户编号
     *
     * 关联 {@link CrmCustomerDO#getId()}
     */
    private Long customerId;

    /**
     * 手机号
     */
    private String mobile;
    /**
     * 电话
     */
    private String telephone;
    /**
     * QQ
     */
    private String qq;
    /**
     * wechat
     */
    private String wechat;
    /**
     * email
     */
    private String email;
    /**
     * 所在地
     *
     * 关联 {@link com.meession.etm.framework.ip.core.Area#getId()} 字段
     */
    private Integer areaId;
    /**
     * 详细地址
     */
    private String detailAddress;
    /**
     * 所属行业
     *
     * 对应字典 {@link DictTypeConstants#CRM_CUSTOMER_INDUSTRY}
     */
    private Integer industryId;
    /**
     * 客户等级
     *
     * 对应字典 {@link DictTypeConstants#CRM_CUSTOMER_LEVEL}
     */
    private Integer level;
    /**
     * 客户来源
     *
     * 对应字典 {@link DictTypeConstants#CRM_CUSTOMER_SOURCE}
     */
    private Integer source;
    /**
     * 备注
     */
    private String remark;

    // ==================== 公海相关字段 ====================

    /**
     * 公海状态: 0-非公海(已领取) 1-在公海中
     */
    private Integer poolStatus;

    /**
     * 进入公海时间
     */
    private LocalDateTime poolEnterTime;

    /**
     * 进入公海原因
     */
    private String poolReason;

    /**
     * 延期次数
     */
    private Integer extendCount;

    /**
     * 保护截止时间（新领取后保护期内不可被回收）
     */
    private LocalDateTime protectDeadline;

    /**
     * 保护期归属用户编号
     */
    private Long protectUserId;

}
