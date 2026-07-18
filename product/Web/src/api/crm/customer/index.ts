import request from '@/config/axios'
import { TransferReqVO } from '@/api/crm/permission'
import type { TagVO } from '../tag'

export interface CustomerVO {
  id: number // 编号
  name: string // 客户名称
  followUpStatus: boolean // 跟进状态
  contactLastTime: Date // 最后跟进时间
  contactLastContent: string // 最后跟进内容
  contactNextTime: Date // 下次联系时间
  ownerUserId: number // 负责人的用户编号
  ownerUserName?: string // 负责人的用户名称
  ownerUserDept?: string // 负责人的部门名称
  lockStatus?: boolean
  dealStatus?: boolean
  mobile: string // 手机号
  telephone: string // 电话
  qq: string // QQ
  wechat: string // wechat
  email: string // email
  areaId: number // 所在地
  areaName?: string // 所在地名称
  detailAddress: string // 详细地址
  industryId: number // 所属行业
  level: number // 客户等级
  source: number // 客户来源
  remark: string // 备注
  creator: string // 创建人
  creatorName?: string // 创建人名称
  createTime: Date // 创建时间
  updateTime: Date // 更新时间
  tags?: TagVO[] // 客户标签
  // ===== 公海回收机制新增字段 =====
  /** 回收倒计时（剩余天数），≤1天标红 */
  poolRemainDays?: number
  /** 归还原因：无法联系/无意向/已成交/其他 */
  putPoolReason?: string
  /** 原归属销售姓名 */
  previousOwnerName?: string
  /** 预约关注人数 */
  reservationCount?: number
  /** 流入公海时间 */
  poolEntryTime?: Date
  /** 流入方式：1-自动回收 2-主动归还 3-强制回收 4-离职回收 */
  poolEntryType?: number
  /** 是否处于冷却期（同一用户归还后不可重复领取） */
  coolingStatus?: boolean
  /** 是否暂停回收计时 */
  recoveryPaused?: boolean
  /** 暂停回收原因 */
  pauseReason?: string
}

// 查询客户列表
export const getCustomerPage = async (params) => {
  return await request.get({ url: `/crm/customer/page`, params })
}

// 进入公海客户提醒的客户列表
export const getPutPoolRemindCustomerPage = async (params) => {
  return await request.get({ url: `/crm/customer/put-pool-remind-page`, params })
}

// 获得待进入公海客户数量
export const getPutPoolRemindCustomerCount = async () => {
  return await request.get({ url: `/crm/customer/put-pool-remind-count` })
}

// 获得今日需联系客户数量
export const getTodayContactCustomerCount = async () => {
  return await request.get({ url: `/crm/customer/today-contact-count` })
}

// 获得分配给我、待跟进的线索数量的客户数量
export const getFollowCustomerCount = async () => {
  return await request.get({ url: `/crm/customer/follow-count` })
}

// 查询客户详情
export const getCustomer = async (id: number) => {
  return await request.get({ url: `/crm/customer/get?id=` + id })
}

// 新增客户
export const createCustomer = async (data: CustomerVO) => {
  return await request.post({ url: `/crm/customer/create`, data })
}

// 修改客户
export const updateCustomer = async (data: CustomerVO) => {
  return await request.put({ url: `/crm/customer/update`, data })
}

// 更新客户的成交状态
export const updateCustomerDealStatus = async (id: number, dealStatus: boolean) => {
  return await request.put({ url: `/crm/customer/update-deal-status`, params: { id, dealStatus } })
}

// 删除客户
export const deleteCustomer = async (id: number) => {
  return await request.delete({ url: `/crm/customer/delete?id=` + id })
}

// 导出客户 Excel
export const exportCustomer = async (params: any) => {
  return await request.download({ url: `/crm/customer/export-excel`, params })
}

// 下载客户导入模板
export const importCustomerTemplate = () => {
  return request.download({ url: '/crm/customer/get-import-template' })
}

// 导入客户
export const handleImport = async (formData) => {
  return await request.upload({ url: `/crm/customer/import`, data: formData })
}

// 客户列表
export const getCustomerSimpleList = async () => {
  return await request.get({ url: `/crm/customer/simple-list` })
}

// ======================= 业务操作 =======================

// 客户转移
export const transferCustomer = async (data: TransferReqVO) => {
  return await request.put({ url: '/crm/customer/transfer', data })
}

// 锁定/解锁客户
export const lockCustomer = async (id: number, lockStatus: boolean) => {
  return await request.put({ url: `/crm/customer/lock`, data: { id, lockStatus } })
}

// 领取公海客户
export const receiveCustomer = async (ids: any[]) => {
  return await request.put({ url: '/crm/customer/receive', params: { ids: ids.join(',') } })
}

// 分配公海给对应负责人
export const distributeCustomer = async (ids: any[], ownerUserId: number) => {
  return await request.put({
    url: '/crm/customer/distribute',
    data: { ids: ids, ownerUserId }
  })
}

// 客户放入公海（支持归还原因）
export const putCustomerPool = async (id: number, reason?: string) => {
  return await request.put({ url: `/crm/customer/put-pool`, params: { id, reason } })
}

// ======================= 查重操作 =======================

// 检查客户是否重复
export const checkDuplicateCustomer = async (data: { name?: string; mobile?: string; email?: string; wechat?: string }) => {
  return await request.post({ url: '/crm/customer/check-duplicate', data })
}

// 获得重复客户列表
export const getDuplicateCustomerList = async (checkName?: boolean, checkMobile?: boolean) => {
  return await request.get({ url: '/crm/customer/duplicate-list', params: { checkName, checkMobile } })
}

// 合并客户
export const mergeCustomer = async (sourceId: number, targetId: number) => {
  return await request.post({ url: '/crm/customer/merge', params: { sourceId, targetId } })
}

// ======================= 批量操作 =======================

// 批量锁定/解锁客户
export const batchLockCustomer = async (ids: number[], lockStatus: boolean) => {
  return await request.put({ url: '/crm/customer/batch-lock', params: { ids: ids.join(','), lockStatus } })
}

// 批量放入公海（发送 ids 数组）
export const batchPutPool = async (ids: number[], reason?: string) => {
  return await request.put({ url: '/crm/customer/batch-put-pool', data: ids })
}

// 批量添加客户标签
export const batchAddCustomerTag = async (customerIds: number[], tagIds: number[]) => {
  return await request.post({ url: '/crm/customer/batch-tag', params: { customerIds: customerIds.join(','), tagIds: tagIds.join(',') } })
}

// ======================= 查重配置 =======================

// 获得查重规则配置
export const getDuplicateConfig = async () => {
  return await request.get({ url: '/crm/duplicate-config/get' })
}

// 保存查重规则配置
export const saveDuplicateConfig = async (data: any) => {
  return await request.put({ url: '/crm/duplicate-config/save', data })
}

// ======================= 公海回收机制 =======================

// 单个延期：重置待进入公海客户的回收倒计时
export const extendPutPoolRemind = async (id: number, extendDays: number) => {
  return await request.post({ url: `/crm/pool-todo/extend`, data: { id, resourceType: 1, extendDays } })
}

// 批量延期
export const batchExtendPutPoolRemind = async (ids: number[], extendDays: number) => {
  const batchItems = ids.map(id => ({ id, resourceType: 1, extendDays }))
  return await request.post({ url: `/crm/pool-todo/batch-extend`, data: { batchItems } })
}

// ======================= 线索查重 =======================

// 检查线索与已有客户是否重复
export const checkDuplicateClue = async (data: { name?: string; mobile?: string; email?: string; wechat?: string }) => {
  return await request.post({ url: '/crm/clue/check-duplicate', data })
}

// ======================= 公海回收 - 预约关注 =======================

/** 预约人信息 */
export interface ReservationUserVO {
  userId: number
  userName: string
  deptName: string
  reservationTime: Date
}

/** 获取客户预约人列表 */
export const getCustomerReservationList = async (customerId: number): Promise<ReservationUserVO[]> => {
  return await request.get({ url: '/crm/customer/pool/reservation-list', params: { customerId } })
}

/** 预约关注客户 */
export const reserveCustomer = async (customerId: number): Promise<void> => {
  return await request.post({ url: '/crm/customer/pool/reserve', params: { customerId } })
}

/** 取消预约关注客户 */
export const cancelCustomerReservation = async (customerId: number): Promise<void> => {
  return await request.delete({ url: '/crm/customer/pool/cancel-reserve', params: { customerId } })
}

// ======================= 公海回收 - 强制回收 =======================

/** 主管批量强制回收客户至公海（不受豁免规则和暂停限制） */
export const batchForceRecovery = async (ids: number[], reason: string): Promise<void> => {
  return await request.put({ url: '/crm/customer/batch-force-recovery', data: { ids, reason } })
}

// ======================= 公海回收 - 流转记录 =======================

/** 公海流转记录 */
export interface CustomerSeaPoolFlowRecordVO {
  id: number
  /** 操作类型：auto_recovery / manual_return / force_recovery / leave_recovery / pool_claim */
  operationType: string
  /** 操作人 */
  operatorName: string
  /** 操作时间 */
  operationTime: Date
  /** 资源类型 */
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
export const getCustomerSeaPoolFlowRecords = async (customerId: number): Promise<CustomerSeaPoolFlowRecordVO[]> => {
  return await request.get({ url: '/crm/customer/sea-pool-flow-records', params: { customerId } })
}

// ======================= 公海回收 - 领取限制 =======================

/** 客户领取限制信息 */
export interface CustomerReceiveLimitVO {
  /** 今日已领取客户数 */
  dailyClaimedCount: number
  /** 每日客户领取上限 */
  dailyLimit: number
  /** 是否已达上限 */
  limitReached: boolean
}

/** 获取当前用户的客户领取限制信息 */
export const getCustomerReceiveLimit = async (): Promise<CustomerReceiveLimitVO> => {
  return await request.get({ url: '/crm/customer/receive-limit' })
}
