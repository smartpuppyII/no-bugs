import request from '@/config/axios'

export interface CluePoolConfigVO {
  enabled?: boolean
  clueExpireDays?: number
  followUpResetEnabled?: boolean
  aLevelExemptEnabled?: boolean
  notifyDays?: number
}

// 获取线索公海规则设置
export const getCluePoolConfig = async () => {
  return await request.get({ url: `/crm/clue-pool-config/get` })
}

// 更新线索公海规则设置
export const saveCluePoolConfig = async (data: CluePoolConfigVO) => {
  return await request.put({ url: `/crm/clue-pool-config/save`, data })
}