import request from '@/config/axios'

/** 公海概览统计 */
export interface SeaPoolStatisticsVO {
  /** 公海资源总量（线索公海 + 客户公海） */
  totalCount: number
  /** 线索公海数量 */
  clueCount: number
  /** 客户公海数量 */
  customerCount: number
  /** 本周回收数 */
  weeklyRecoveryCount: number
  /** 环比上周回收数变化百分比 */
  weeklyRecoveryChange: number
  /** 本周领取转化率（确权成功数 / 总领取数） */
  conversionRate: number
  /** 即将回收预警数（未来3天内） */
  upcomingRecoveryCount: number
}

/** 获取公海概览统计数据 */
export const getSeaPoolStatistics = async (): Promise<SeaPoolStatisticsVO> => {
  return await request.get({ url: '/crm/sea-pool/statistics' })
}

/** 公海流转记录 */
export interface SeaPoolFlowRecordVO {
  id: number
  /** 操作类型：auto_recovery/manual_return/force_recovery/leave_recovery/pool_claim */
  operationType: string
  /** 操作人 */
  operatorName: string
  /** 操作时间 */
  operationTime: Date
  /** 资源类型：clue/customer */
  resourceType: string
  /** 资源ID */
  resourceId: number
  /** 资源名称 */
  resourceName: string
  /** 原归属销售 */
  previousOwnerName: string
  /** 操作原因 */
  operationReason: string
}

/** 获取客户公海流转记录 */
export const getCustomerSeaPoolFlowRecords = async (customerId: number): Promise<SeaPoolFlowRecordVO[]> => {
  const result = await request.get({ url: `/crm/customer/pool-record-page`, params: { customerId, pageNo: 1, pageSize: 100 } })
  return (result?.list || []).map((item: any) => ({
    id: item.id,
    operationType: mapOperateType(item.operateType),
    operatorName: item.fromUserName || '',
    operationTime: item.operateTime,
    resourceType: 'customer',
    resourceId: item.resourceId,
    resourceName: '',
    previousOwnerName: item.fromUserName || '',
    operationReason: item.reason || ''
  }))
}

/** 操作类型数字映射为字符串 */
function mapOperateType(type: number): string {
  switch (type) {
    case 1: return 'auto_recovery'
    case 2: return 'manual_return'
    case 3: return 'pool_claim'
    case 4: return 'force_recovery'
    case 5: return 'leave_recovery'
    default: return 'unknown'
  }
}

/** 归还客户至公海 */
export const returnCustomerToPool = async (customerId: number, reason: string) => {
  return await request.put({ url: `/crm/customer/put-pool`, params: { id: customerId, reason } })
}

/** 获取当前用户当日领取统计 */
export interface DailyClaimCountVO {
  /** 今日已领取线索数 */
  dailyClueClaimCount: number
  /** 每日线索领取上限 */
  dailyClueLimit: number
  /** 今日已领取客户数 */
  dailyCustomerClaimCount: number
  /** 每日客户领取上限 */
  dailyCustomerLimit: number
}

/** 获取当前用户当日领取统计 */
export const getDailyClaimCount = async (): Promise<DailyClaimCountVO> => {
  return await request.get({ url: '/crm/sea-pool/daily-claim-count' })
}