import request from '@/config/axios'

export interface TodoSummaryVO {
  todayContactCount: number
  clueFollowCount: number
  customerFollowCount: number
  customerPutPoolRemindCount: number
  contractAuditCount: number
  contractRemindCount: number
  receivablePlanRemindCount: number
  receivableAuditCount: number
  expiredFollowCount: number
}

// 获得待办汇总
export const getTodoSummary = async () => {
  return await request.get({ url: '/crm/dashboard/todo-summary' })
}

// ======================= 公海概览统计 =======================

/** 公海概览统计数据 */
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
  return await request.get({ url: '/crm/dashboard/sea-pool-statistics' })
}

// ======================= 公海趋势数据 =======================

/** 公海趋势数据点 */
export interface SeaPoolTrendItemVO {
  /** 日期 */
  date: string
  /** 流入公海数量 */
  inflowCount: number
  /** 公海领取数量 */
  claimCount: number
  /** 确权成功数量 */
  confirmCount: number
}

/** 获取公海趋势数据（近30天） */
export const getSeaPoolTrend = async (): Promise<SeaPoolTrendItemVO[]> => {
  return await request.get({ url: '/crm/dashboard/sea-pool-trend' })
}

// ======================= 个人公海概览 =======================

/** 个人公海概览数据 */
export interface PersonalSeaPoolVO {
  /** 个人归还数量 */
  returnCount: number
  /** 个人领取转化率 */
  conversionRate: number
  /** 个人今日已领取线索数 */
  dailyClueClaimCount: number
  /** 个人今日已领取客户数 */
  dailyCustomerClaimCount: number
}

/** 获取个人公海概览数据 */
export const getPersonalSeaPool = async (): Promise<PersonalSeaPoolVO> => {
  return await request.get({ url: '/crm/dashboard/personal-sea-pool' })
}