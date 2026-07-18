import request from '@/config/axios'

export interface SeaPoolLimitConfigVO {
  dailyClueLimit?: number
  dailyCustomerLimit?: number
  coolingDays?: number
  protectHours?: number
}

// 获取公海领取限制配置
export const getSeaPoolLimitConfig = async () => {
  return await request.get({ url: `/crm/sea-pool-limit-config/get` })
}

// 更新公海领取限制配置
export const saveSeaPoolLimitConfig = async (data: SeaPoolLimitConfigVO) => {
  return await request.put({ url: `/crm/sea-pool-limit-config/save`, data })
}