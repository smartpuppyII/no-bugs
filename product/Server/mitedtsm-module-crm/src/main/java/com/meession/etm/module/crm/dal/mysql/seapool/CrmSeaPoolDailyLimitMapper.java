package com.meession.etm.module.crm.dal.mysql.seapool;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.dal.dataobject.seapool.CrmSeaPoolDailyLimitDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;

/**
 * 公海每日领取限量 Mapper
 *
 * @author 密讯
 */
@Mapper
public interface CrmSeaPoolDailyLimitMapper extends BaseMapperX<CrmSeaPoolDailyLimitDO> {

    /**
     * 根据用户、资源类型和日期查询当日限量记录
     */
    default CrmSeaPoolDailyLimitDO selectByUserAndDate(Long userId, Integer resourceType, LocalDate limitDate) {
        return selectOne(new LambdaQueryWrapperX<CrmSeaPoolDailyLimitDO>()
                .eq(CrmSeaPoolDailyLimitDO::getUserId, userId)
                .eq(CrmSeaPoolDailyLimitDO::getResourceType, resourceType)
                .eq(CrmSeaPoolDailyLimitDO::getLimitDate, limitDate));
    }

    /**
     * 递增当日领取计数（原子操作）
     *
     * @return 更新行数
     */
    default int incrementCount(Long id, int delta) {
        CrmSeaPoolDailyLimitDO current = selectById(id);
        if (current == null) {
            return 0;
        }
        return update(new CrmSeaPoolDailyLimitDO().setId(id).setCount(current.getCount() + delta),
                new LambdaUpdateWrapper<CrmSeaPoolDailyLimitDO>()
                        .eq(CrmSeaPoolDailyLimitDO::getId, id));
    }

}