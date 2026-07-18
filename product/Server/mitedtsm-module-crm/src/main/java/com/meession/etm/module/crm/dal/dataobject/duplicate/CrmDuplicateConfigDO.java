package com.meession.etm.module.crm.dal.dataobject.duplicate;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * CRM 查重规则配置 DO
 *
 * @author system
 */
@TableName("crm_duplicate_config")
@KeySequence("crm_duplicate_config_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmDuplicateConfigDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 是否检查名称
     */
    private Boolean checkName;
    /**
     * 是否检查手机号
     */
    private Boolean checkMobile;
    /**
     * 是否检查邮箱
     */
    private Boolean checkEmail;
    /**
     * 是否检查微信
     */
    private Boolean checkWechat;
    /**
     * 查重范围：ALL/DEPT/SELF
     */
    private String checkScope;
    /**
     * 租户编号
     */
    private Long tenantId;

}
