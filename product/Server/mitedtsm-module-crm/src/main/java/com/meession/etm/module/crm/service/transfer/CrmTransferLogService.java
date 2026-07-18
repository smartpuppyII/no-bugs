package com.meession.etm.module.crm.service.transfer;

import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.dal.dataobject.transfer.CrmTransferLogDO;

/**
 * 转移日志 Service 接口
 *
 * @author system
 */
public interface CrmTransferLogService {

    /**
     * 创建转移日志
     *
     * @param logDO 转移日志
     */
    void createTransferLog(CrmTransferLogDO logDO);

    /**
     * 获得转移日志分页
     *
     * @param pageParam 分页参数
     * @param bizType   业务类型（可选）
     * @return 分页结果
     */
    PageResult<CrmTransferLogDO> getTransferLogPage(PageParam pageParam, Integer bizType);

}
