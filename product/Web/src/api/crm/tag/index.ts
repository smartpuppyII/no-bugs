import request from '@/config/axios'

export interface TagVO {
  id: number
  name: string
  color: string
  groupName: string
  sortOrder: number
  createTime: Date
}

// 创建标签
export const createTag = async (data: TagVO) => {
  return await request.post({ url: '/crm/tag/create', data })
}

// 更新标签
export const updateTag = async (data: TagVO) => {
  return await request.put({ url: '/crm/tag/update', data })
}

// 删除标签
export const deleteTag = async (id: number) => {
  return await request.delete({ url: '/crm/tag/delete?id=' + id })
}

// 获得标签分页
export const getTagPage = async (params: any) => {
  return await request.get({ url: '/crm/tag/page', params })
}

// 获得所有标签
export const getAllTags = async () => {
  return await request.get({ url: '/crm/tag/list-all' })
}

// 按组获得标签
export const getTagsByGroup = async (groupName: string) => {
  return await request.get({ url: '/crm/tag/list-by-group', params: { groupName } })
}

// ======= 客户标签操作 =======

// 获得客户标签
export const getCustomerTags = async (customerId: number) => {
  return await request.get({ url: '/crm/customer/tags', params: { customerId } })
}

// 为客户添加标签
export const addCustomerTags = async (customerId: number, tagIds: number[]) => {
  return await request.post({ url: '/crm/customer/tag', params: { customerId, tagIds: tagIds.join(',') } })
}

// 移除客户标签
export const removeCustomerTag = async (customerId: number, tagId: number) => {
  return await request.delete({ url: '/crm/customer/tag', params: { customerId, tagId } })
}

// 批量添加客户标签
export const batchAddCustomerTag = async (customerIds: number[], tagIds: number[]) => {
  return await request.post({ url: '/crm/customer/batch-tag', params: { customerIds: customerIds.join(','), tagIds: tagIds.join(',') } })
}
