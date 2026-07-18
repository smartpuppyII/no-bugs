import request from '@/config/axios'
import { TransferReqVO } from '@/api/crm/permission'

export interface ClueVO {
  id: number // 编号
  name: string // 线索名称
  followUpStatus: boolean // 跟进状态
  contactLastTime: Date // 最后跟进时间
  contactLastContent: string // 最后跟进内容
  contactNextTime: Date // 下次联系时间
  ownerUserId: number // 负责人的用户编号
  ownerUserName?: string // 负责人的用户名称
  ownerUserDept?: string // 负责人的部门名称
  transformStatus: boolean // 转化状态
  customerId: number // 客户编号
  customerName?: string // 客户名称
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
}

// 查询线索列表
export const getCluePage = async (params: any) => {
  return await request.get({ url: `/crm/clue/page`, params })
}

// 查询线索详情
export const getClue = async (id: number) => {
  return await request.get({ url: `/crm/clue/get?id=` + id })
}

// 新增线索
export const createClue = async (data: ClueVO) => {
  return await request.post({ url: `/crm/clue/create`, data })
}

// 修改线索
export const updateClue = async (data: ClueVO) => {
  return await request.put({ url: `/crm/clue/update`, data })
}

// 删除线索
export const deleteClue = async (id: number) => {
  return await request.delete({ url: `/crm/clue/delete?id=` + id })
}

// 导出线索 Excel
export const exportClue = async (params) => {
  return await request.download({ url: `/crm/clue/export-excel`, params })
}

// 线索转移
export const transferClue = async (data: TransferReqVO) => {
  return await request.put({ url: '/crm/clue/transfer', data })
}

// 线索转化为客户
export const transformClue = async (id: number) => {
  return await request.put({ url: '/crm/clue/transform', params: { id } })
}

// 获得分配给我的、待跟进的线索数量
export const getFollowClueCount = async () => {
  return await request.get({ url: '/crm/clue/follow-count' })
}

// 线索放入公海（支持归还原因）
export const putCluePool = async (id: number, reason?: string) => {
  return await request.put({ url: `/crm/clue/put-pool`, params: { id, reason } })
}

// 领取公海线索
export const receiveClue = async (ids: number[]) => {
  return await request.put({ url: `/crm/clue/receive`, params: { ids: ids.join(',') } })
}

// 分配公海线索
export const distributeClue = async (data: { ids: number[]; ownerUserId: number }) => {
  return await request.put({ url: `/crm/clue/distribute`, data })
}

// 获取公海线索分页
export const getCluePoolPage = async (params: any) => {
  return await request.get({ url: `/crm/clue/pool-page`, params })
}

// 获取公海线索数量
export const getCluePoolCount = async () => {
  return await request.get({ url: `/crm/clue/pool-count` })
}

// 批量删除线索
export const batchDeleteClue = async (ids: number[]) => {
  return await request.delete({ url: '/crm/clue/batch-delete', params: { ids: ids.join(',') } })
}

// 批量转为客户
export const batchTransformClue = async (ids: number[]) => {
  return await request.put({ url: '/crm/clue/batch-transform', data: ids })
}

// 获取当前用户当日线索领取上限及已领取数量
export const getClueReceiveLimit = async () => {
  return await request.get({ url: '/crm/clue/receive-limit' })
}

// 获取公海线索的预约人数
export const getClueReservationCount = async (clueId: number) => {
  return await request.get({ url: `/crm/clue/pool/reservation-count`, params: { id: clueId } })
}

// ======================= 公海回收 - 批量操作 =======================

// 批量放入公海（支持归还原因）
export const batchPutCluePool = async (ids: number[], reason?: string) => {
  return await request.put({ url: '/crm/clue/batch-put-pool', data: { ids, reason } })
}

// ======================= 公海回收 - 预约关注 =======================

/** 预约人信息 */
export interface ClueReservationUserVO {
  userId: number
  userName: string
  deptName: string
  reservationTime: Date
}

/** 获取线索预约人列表 */
export const getClueReservationList = async (clueId: number): Promise<ClueReservationUserVO[]> => {
  return await request.get({ url: '/crm/clue/pool/reservation-list', params: { clueId } })
}

/** 预约关注线索 */
export const reserveClue = async (clueId: number): Promise<void> => {
  return await request.post({ url: '/crm/clue/pool/reserve', params: { clueId } })
}

/** 取消预约关注线索 */
export const cancelClueReservation = async (clueId: number): Promise<void> => {
  return await request.delete({ url: '/crm/clue/pool/cancel-reserve', params: { clueId } })
}

// ======================= 公海回收 - 待进入公海提醒 =======================

// 进入公海线索提醒的线索列表
export const getCluePutPoolRemindPage = async (params: any) => {
  return await request.get({ url: `/crm/clue/put-pool-remind-page`, params })
}

// 获得待进入公海线索数量
export const getCluePutPoolRemindCount = async () => {
  return await request.get({ url: `/crm/clue/put-pool-remind-count` })
}

// 单个延期：重置待进入公海线索的回收倒计时
export const extendCluePutPoolRemind = async (id: number, extendDays: number) => {
  return await request.put({ url: `/crm/clue/put-pool-remind/extend`, params: { id, extendDays } })
}

// 批量延期
export const batchExtendCluePutPoolRemind = async (ids: number[], extendDays: number) => {
  return await request.put({ url: `/crm/clue/put-pool-remind/batch-extend`, data: { ids, extendDays } })
}

// ======================= 公海回收 - 流转记录 =======================

/** 线索公海流转记录 */
export interface ClueSeaPoolFlowRecordVO {
  id: number
  /** 操作类型：auto_recovery/manual_return/force_recovery/leave_recovery/pool_claim */
  operationType: string
  /** 操作人 */
  operatorName: string
  /** 操作时间 */
  operationTime: Date
  /** 资源类型：clue */
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

/** 获取线索公海流转记录 */
export const getClueSeaPoolFlowRecords = async (clueId: number): Promise<ClueSeaPoolFlowRecordVO[]> => {
  const result = await request.get({ url: `/crm/clue/pool-record-page`, params: { clueId, pageNo: 1, pageSize: 100 } })
  return (result?.list || []).map((item: any) => ({
    id: item.id,
    operationType: mapClueOperateType(item.operateType),
    operatorName: item.fromUserName || '',
    operationTime: item.operateTime,
    resourceType: 'clue',
    resourceId: item.resourceId,
    resourceName: '',
    previousOwnerName: item.fromUserName || '',
    operationReason: item.reason || ''
  }))
}

/** 线索操作类型数字映射为字符串 */
function mapClueOperateType(type: number): string {
  switch (type) {
    case 1: return 'auto_recovery'
    case 2: return 'manual_return'
    case 3: return 'pool_claim'
    case 4: return 'force_recovery'
    case 5: return 'leave_recovery'
    default: return 'unknown'
  }
}

/** 归还线索至公海（带原因） */
export const returnClueToPool = async (clueId: number, reason: string) => {
  return await request.put({ url: `/crm/clue/put-pool`, params: { id: clueId, reason } })
}