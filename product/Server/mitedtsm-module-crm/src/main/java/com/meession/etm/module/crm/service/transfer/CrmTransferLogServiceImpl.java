package com.meession.etm.module.crm.service.transfer;

import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.dal.dataobject.transfer.CrmTransferLogDO;
import com.meession.etm.module.crm.dal.mysql.transfer.CrmTransferLogMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 转移日志 Service 实现
 *
 * @author system
 */
@Service
@Validated
public class CrmTransferLogServiceImpl implements CrmTransferLogService {

    @Resource
    private CrmTransferLogMapper transferLogMapper;

    @Override
    public void createTransferLog(CrmTransferLogDO logDO) {
        transferLogMapper.insert(logDO);
    }

    @Override
    public PageResult<CrmTransferLogDO> getTransferLogPage(PageParam pageParam, Integer bizType) {
        return transferLogMapper.selectPage(pageParam, bizType);
    }

}
