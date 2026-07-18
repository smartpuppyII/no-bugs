<template>
  <ContentWrap title="报价单管理">
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="no" label="报价单号" width="160" />
      <el-table-column prop="customerName" label="客户" min-width="120" />
      <el-table-column prop="businessName" label="商机" min-width="150" />
      <el-table-column prop="totalPrice" label="报价金额" width="120" />
      <el-table-column label="审批状态" width="100">
        <template #default="{ row }">
          <el-tag :type="['info','warning','success','danger'][row.auditStatus]" size="small">
            {{ ['草稿','审批中','通过','拒绝'][row.auditStatus] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="openDetail(row)">查看</el-button>
          <el-button text type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination v-model:limit="query.pageSize" v-model:page="query.pageNo" :total="total" @pagination="load" />

    <el-dialog v-model="detailVisible" title="报价单详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="报价单号">{{ detail.no }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ detail.customerName }}</el-descriptions-item>
        <el-descriptions-item label="商机">{{ detail.businessName }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">{{ ['草稿','审批中','通过','拒绝'][detail.auditStatus||0] }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-divider>产品明细</el-divider>
      <el-table :data="products" stripe>
        <el-table-column prop="productName" label="产品" /><el-table-column prop="productNo" label="编号" />
        <el-table-column prop="productUnit" label="单位" /><el-table-column prop="productPrice" label="单价" />
        <el-table-column prop="count" label="数量" /><el-table-column prop="totalPrice" label="小计" />
      </el-table>
    </el-dialog>
  </ContentWrap>
</template>

<script lang="ts" setup>
import * as QuotationApi from '@/api/crm/quotation'
import { ContentWrap } from '@/components/ContentWrap'
defineOptions({ name: 'CrmQuotation' })
const message = useMessage(); const loading = ref(false); const total = ref(0)
const list = ref<any[]>([]); const query = reactive({ pageNo: 1, pageSize: 10 })
const detailVisible = ref(false); const detail = ref<any>({}); const products = ref<any[]>([])

const load = async () => { loading.value = true; try { const r = await QuotationApi.getQuotationPage(query); list.value = r.list || []; total.value = r.total || 0 } finally { loading.value = false } }
const openDetail = async (row: any) => { detail.value = row; detailVisible.value = true; products.value = await QuotationApi.getQuotationProducts(row.id) || [] }
const handleDelete = async (id: number) => { try { await message.confirm('确认删除？'); await QuotationApi.deleteQuotation(id); message.success('删除成功'); load() } catch { /* cancelled */ } }
onMounted(() => load())
</script>
