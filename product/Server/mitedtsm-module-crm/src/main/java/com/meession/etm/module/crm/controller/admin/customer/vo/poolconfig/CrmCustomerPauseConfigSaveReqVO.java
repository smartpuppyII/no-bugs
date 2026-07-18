package com.meession.etm.module.crm.controller.admin.customer.vo.poolconfig;

import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - CRM 合同/回款暂停配置 Request VO")
@Data
public class CrmCustomerPauseConfigSaveReqVO {

    @Schema(description = "是否暂停有合同客户的回收", example = "true")
    @DiffLogField(name = "是否暂停有合同客户的回收")
    private Boolean pauseContractEnabled;

    @Schema(description = "是否暂停有应收款客户的回收", example = "true")
    @DiffLogField(name = "是否暂停有应收款客户的回收")
    private Boolean pauseReceivableEnabled;

}