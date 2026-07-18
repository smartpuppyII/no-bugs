import request from '@/config/axios'

export const saveTarget = (data: any) => request.post({ url: '/crm/target/save', data })
export const getTargetList = (targetType: string, targetId: number) =>
  request.get({ url: '/crm/target/list', params: { targetType, targetId } })
export const deleteTarget = (id: number) => request.delete({ url: '/crm/target/delete?id=' + id })
