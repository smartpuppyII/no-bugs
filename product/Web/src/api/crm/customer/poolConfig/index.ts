import request from '@/config/axios'

export interface CustomerPoolConfigVO {
  enabled?: boolean
  contactExpireDays?: number
  dealExpireDays?: number
  notifyEnabled?: boolean
  notifyDays?: number
  /** 客户等级回收时效JSON，格式: {"A":30,"B":15,"C":7} */
  levelExpireDays?: string
  /** 是否暂停有合同客户的回收 0-否 1-是 */
  pauseContractEnabled?: boolean
  /** 是否暂停有应收款客户的回收 0-否 1-是 */
  pauseReceivableEnabled?: boolean
  /** 最大延期次数 */
  extendMaxCount?: number
}

// 获取客户公海规则设置
export const getCustomerPoolConfig = async () => {
  return await request.get({ url: `/crm/customer-pool-config/get` })
}

// 更新客户公海规则设置
export const saveCustomerPoolConfig = async (data: CustomerPoolConfigVO) => {
  return await request.put({ url: `/crm/customer-pool-config/save`, data })
}
