import request from '@/config/axios'

export interface VisitPlanVO {
  id?: number; customerId: number; customerName?: string
  visitTime: string; purpose?: string; address?: string; status?: number; ownerUserId?: number
}
export interface VisitRecordVO {
  id?: number; planId: number; checkInLocation?: string
  latitude?: string; longitude?: string; content?: string; customerFeedback?: string; nextStep?: string
}

export const createPlan = (data: VisitPlanVO) => request.post({ url: '/crm/visit/plan/create', data })
export const updatePlan = (data: VisitPlanVO) => request.put({ url: '/crm/visit/plan/update', data })
export const deletePlan = (id: number) => request.delete({ url: '/crm/visit/plan/delete?id=' + id })
export const getPlanPage = (params: any) => request.get({ url: '/crm/visit/plan/page', params })
export const getTodayPlans = () => request.get({ url: '/crm/visit/plan/today' })

export const checkIn = (data: VisitRecordVO) => request.post({ url: '/crm/visit/record/check-in', data })
export const report = (data: VisitRecordVO) => request.put({ url: '/crm/visit/record/report', data })
export const getRecordPage = (params: any) => request.get({ url: '/crm/visit/record/page', params })
export const getRecordCount = () => request.get({ url: '/crm/visit/record/count' })
