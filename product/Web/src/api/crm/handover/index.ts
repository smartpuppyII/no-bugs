import request from '@/config/axios'

export interface BizDataGroup {
  count: number
  bizIds: number[]
}

export interface HandoverPreviewVO {
  totalCount: number
  customerData: BizDataGroup
  clueData: BizDataGroup
  businessData: BizDataGroup
  contractData: BizDataGroup
  contactData: BizDataGroup
  receivablePlanData: BizDataGroup
}

export interface HandoverExecuteVO {
  fromUserId: number
  customerToUserId?: number
  clueToUserId?: number
  businessToUserId?: number
  contractToUserId?: number
  contactToUserId?: number
  receivablePlanToUserId?: number
  customerIds?: number[]
  clueIds?: number[]
  remark?: string
}

// 预览离职人员名下数据
export const previewHandover = async (fromUserId: number) => {
  return await request.get({ url: '/crm/handover/preview', params: { fromUserId } })
}

// 执行离职交接
export const executeHandover = async (data: HandoverExecuteVO) => {
  return await request.post({ url: '/crm/handover/execute', data })
}

// 获得转移日志分页
export const getTransferLogPage = async (params: any) => {
  return await request.get({ url: '/crm/handover/transfer-log-page', params })
}
