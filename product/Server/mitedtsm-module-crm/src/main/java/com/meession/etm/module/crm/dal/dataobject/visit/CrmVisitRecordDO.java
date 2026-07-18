package com.meession.etm.module.crm.dal.dataobject.visit;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;
import java.time.LocalDateTime;

@TableName("crm_visit_record")
@KeySequence("crm_visit_record_seq")
@Data @EqualsAndHashCode(callSuper = true) @ToString(callSuper = true)
@Builder @NoArgsConstructor @AllArgsConstructor
public class CrmVisitRecordDO extends BaseDO {
    @TableId private Long id;
    private Long planId;
    private Long customerId;
    private String customerName;
    private LocalDateTime checkInTime;
    private String checkInLocation;
    private String latitude;
    private String longitude;
    private String content;
    private String customerFeedback;
    private String nextStep;
    private Long ownerUserId;
}
