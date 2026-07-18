import request from '@/config/axios'

/**
 * 线索公海规则配置
 * 对应 PRD 2.1 线索公海回收规则
 */
export interface CluePoolConfigVO {
  /** 是否启用线索公海规则 */
  enabled?: boolean
  /** 线索自动回收时效（天），默认7，范围1-90 */
  clueExpireDays?: number
  /** 跟进记录是否重置倒计时 */
  followUpResetEnabled?: boolean
  /** A类重点线索是否豁免自动回收 */
  aLevelExemptEnabled?: boolean
  /** 预热提醒天数，默认3，范围1-7 */
  notifyDays?: number
}

// ======================= 基础 CRUD =======================

/** 获取线索公海规则配置 */
export const getCluePoolConfig = async (): Promise<CluePoolConfigVO> => {
  return await request.get({ url: '/crm/clue-pool-config/get' })
}

/** 保存线索公海规则配置 */
export const saveCluePoolConfig = async (data: CluePoolConfigVO): Promise<void> => {
  return await request.put({ url: '/crm/clue-pool-config/save', data })
}

// ======================= 规则校验 =======================

/** 校验线索公海规则配置参数 */
export const validateCluePoolConfig = async (data: CluePoolConfigVO): Promise<{ valid: boolean; message?: string }> => {
  return await request.post({ url: '/crm/clue-pool-config/validate', data })
}

// ======================= 规则启停 =======================

/** 启用/禁用线索公海规则 */
export const toggleCluePoolConfig = async (enabled: boolean): Promise<void> => {
  return await request.put({ url: '/crm/clue-pool-config/toggle', params: { enabled } })
}