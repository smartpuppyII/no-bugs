import request from '@/config/axios'

export interface CompetitorVO {
  id?: number
  name: string
  advantage?: string
  disadvantage?: string
  productCompare?: string
  website?: string
  remark?: string
  status?: number
  creator?: string
  createTime?: Date
  updateTime?: Date
}

// 创建竞争对手
export const createCompetitor = async (data: CompetitorVO) => {
  return await request.post({ url: '/crm/competitor/create', data })
}

// 更新竞争对手
export const updateCompetitor = async (data: CompetitorVO) => {
  return await request.put({ url: '/crm/competitor/update', data })
}

// 删除竞争对手
export const deleteCompetitor = async (id: number) => {
  return await request.delete({ url: '/crm/competitor/delete?id=' + id })
}

// 获得竞争对手
export const getCompetitor = async (id: number) => {
  return await request.get({ url: '/crm/competitor/get?id=' + id })
}

// 获得竞争对手分页
export const getCompetitorPage = async (params: any) => {
  return await request.get({ url: '/crm/competitor/page', params })
}

// 获得竞争对手精简列表
export const getCompetitorSimpleList = async () => {
  return await request.get({ url: '/crm/competitor/simple-list' })
}
