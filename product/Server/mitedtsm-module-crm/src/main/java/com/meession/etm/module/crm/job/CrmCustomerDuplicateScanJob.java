package com.meession.etm.module.crm.job;

import com.meession.etm.framework.quartz.core.handler.JobHandler;
import com.meession.etm.framework.tenant.core.job.TenantJob;
import com.meession.etm.module.crm.service.duplicate.CrmCustomerDuplicateService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import java.util.List;

/**
 * CRM 存量客户重复扫描 Job
 * 定时扫描所有客户，发现重复数据并记录
 *
 * @author system
 */
@Component
@Slf4j
public class CrmCustomerDuplicateScanJob implements JobHandler {

    @Resource
    private CrmCustomerDuplicateService duplicateService;

    @Override
    @TenantJob
    public String execute(String param) {
        log.info("[CrmCustomerDuplicateScanJob] 开始扫描存量重复客户...");

        // 扫描名称重复
        List<List<CrmCustomerDO>> nameDuplicates = duplicateService.findDuplicateCustomers(true, false);
        int nameDupCount = nameDuplicates != null ? nameDuplicates.size() : 0;

        // 扫描手机号重复
        List<List<CrmCustomerDO>> mobileDuplicates = duplicateService.findDuplicateCustomers(false, true);
        int mobileDupCount = mobileDuplicates != null ? mobileDuplicates.size() : 0;

        log.info("[CrmCustomerDuplicateScanJob] 完成，名称重复组: {}，手机号重复组: {}",
                nameDupCount, mobileDupCount);
        return String.format("名称重复组: %d，手机号重复组: %d", nameDupCount, mobileDupCount);
    }

}
