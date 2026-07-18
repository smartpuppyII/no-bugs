import request from '@/config/axios'

/**
 * 公海领取限制配置
 * 对应 PRD 2.3 领取限制规则
 */
export interface SeaPoolLimitConfigVO {
  /** 单用户每日线索领取上限，默认10，范围1-100 */
  dailyClueLimit?: number
  /** 单用户每日客户领取上限，默认5，范围1-50 */
  dailyCustomerLimit?: number
  /** 重复领取冷却时间（天），默认7，范围1-90 */
  coolingDays?: number
  /** 领取后保护期时长（小时），默认24，范围1-168 */
  protectHours?: number
}

// ======================= 基础 CRUD =======================

/** 获取公海领取限制配置 */
export const getSeaPoolLimitConfig = async (): Promise<SeaPoolLimitConfigVO> => {
  return await request.get({ url: '/crm/sea-pool-limit-config/get' })
}

/** 保存公海领取限制配置 */
export const saveSeaPoolLimitConfig = async (data: SeaPoolLimitConfigVO): Promise<void> => {
  return await request.put({ url: '/crm/sea-pool-limit-config/save', data })
}

// ======================= 规则校验 =======================

/** 校验公海领取限制配置参数 */
export const validateSeaPoolLimitConfig = async (data: SeaPoolLimitConfigVO): Promise<{ valid: boolean; message?: string }> => {
  return await request.post({ url: '/crm/sea-pool-limit-config/validate', data })
}

// ======================= 领取统计 =======================

/** 当前用户当日领取统计 */
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