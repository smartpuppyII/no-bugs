import request from '@/config/axios'

export const createQuotation = (data: any) => request.post({ url: '/crm/quotation/create', data })
export const getQuotationPage = (params: any) => request.get({ url: '/crm/quotation/page', params })
export const getQuotation = (id: number) => request.get({ url: '/crm/quotation/get?id=' + id })
export const getQuotationProducts = (quotationId: number) => request.get({ url: '/crm/quotation/products?quotationId=' + quotationId })
export const deleteQuotation = (id: number) => request.delete({ url: '/crm/quotation/delete?id=' + id })
