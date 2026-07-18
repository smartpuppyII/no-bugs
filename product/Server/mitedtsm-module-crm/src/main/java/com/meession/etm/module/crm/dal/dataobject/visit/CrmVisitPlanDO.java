package com.meession.etm.module.crm.dal.dataobject.visit;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;
import java.time.LocalDateTime;

@TableName("crm_visit_plan")
@KeySequence("crm_visit_plan_seq")
@Data @EqualsAndHashCode(callSuper = true) @ToString(callSuper = true)
@Builder @NoArgsConstructor @AllArgsConstructor
public class CrmVisitPlanDO extends BaseDO {
    @TableId private Long id;
    private Long customerId;
    private String customerName;
    private LocalDateTime visitTime;
    private String purpose;
    private String address;
    private Integer status;
    private Long ownerUserId;
}
