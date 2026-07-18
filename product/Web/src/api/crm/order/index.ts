import request from '@/config/axios'
import { TransferReqVO } from '@/api/crm/permission'

export interface OrderVO {
  id: number
  name: string
  no: string
  customerId: number
  customerName?: string
  businessId: number
  businessName: string
  contactLastTime: Date
  ownerUserId: number
  ownerUserName?: string
  ownerUserDeptName?: string
  processInstanceId: number
  auditStatus: number
  orderDate: Date
  startTime: Date
  endTime: Date
  orderType: number
  paymentMethod: number
  totalProductPrice: number
  discountPercent: number
  totalPrice: number
  totalReceivablePrice: number
  signContactId: number
  signContactName?: string
  signUserId: number
  signUserName: string
  remark: string
  createTime?: Date
  creator: string
  creatorName: string
  updateTime?: Date
  products?: [
    {
      id: number
      productId: number
      productName: string
      productNo: string
      productUnit: number
      productPrice: number
      orderPrice: number
      count: number
      totalPrice: number
    }
  ]
}

// 查询 CRM 订单列表
export const getOrderPage = async (params) => {
  return await request.get({ url: `/crm/order/page`, params })
}

// 查询 CRM 订单列表，基于指定客户
export const getOrderPageByCustomer = async (params: any) => {
  return await request.get({ url: `/crm/order/page-by-customer`, params })
}

// 查询 CRM 订单列表，基于指定商机
export const getOrderPageByBusiness = async (params: any) => {
  return await request.get({ url: `/crm/order/page-by-business`, params })
}

// 查询 CRM 订单详情
export const getOrder = async (id: number) => {
  return await request.get({ url: `/crm/order/get?id=` + id })
}

// 查询 CRM 订单下拉列表
export const getOrderSimpleList = async (customerId: number) => {
  return await request.get({
    url: `/crm/order/simple-list?customerId=${customerId}`
  })
}

// 新增 CRM 订单
export const createOrder = async (data: OrderVO) => {
  return await request.post({ url: `/crm/order/create`, data })
}

// 修改 CRM 订单
export const updateOrder = async (data: OrderVO) => {
  return await request.put({ url: `/crm/order/update`, data })
}

// 删除 CRM 订单
export const deleteOrder = async (id: number) => {
  return await request.delete({ url: `/crm/order/delete?id=` + id })
}

// 导出 CRM 订单 Excel
export const exportOrder = async (params) => {
  return await request.download({ url: `/crm/order/export-excel`, params })
}

// 提交审核
export const submitOrder = async (id: number) => {
  return await request.put({ url: `/crm/order/submit?id=${id}` })
}

// 订单转移
export const transferOrder = async (data: TransferReqVO) => {
  return await request.put({ url: '/crm/order/transfer', data })
}
