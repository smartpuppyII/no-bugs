<template>
  <ContentWrap title="拜访管理">
    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="拜访计划" name="plan">
        <el-form :inline="true" :model="planQuery" class="-mb-15px">
          <el-form-item label="客户名称"><el-input v-model="planQuery.customerName" placeholder="请输入" clearable /></el-form-item>
          <el-form-item label="状态">
            <el-select v-model="planQuery.status" clearable placeholder="全部">
              <el-option label="待拜访" :value="0" /><el-option label="已签到" :value="1" />
              <el-option label="已完成" :value="2" /><el-option label="已取消" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadPlans">搜索</el-button>
            <el-button @click="dialogVisible = true">新增计划</el-button>
          </el-form-item>
        </el-form>
        <el-table :data="planList" v-loading="planLoading" stripe>
          <el-table-column prop="customerName" label="客户" min-width="120" />
          <el-table-column prop="visitTime" label="拜访时间" width="160" />
          <el-table-column prop="purpose" label="拜访目的" min-width="150" show-overflow-tooltip />
          <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="['warning','primary','success','info'][row.status]" size="small">
                {{ ['待拜访','已签到','已完成','已取消'][row.status] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button v-if="row.status === 0" text type="primary" size="small" @click="openCheckIn(row)">签到</el-button>
              <el-button v-if="row.status === 1" text type="success" size="small" @click="openReport(row)">写报告</el-button>
              <el-button text type="danger" size="small" @click="handleDeletePlan(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <Pagination v-model:limit="planQuery.pageSize" v-model:page="planQuery.pageNo" :total="planTotal" @pagination="loadPlans" />
      </el-tab-pane>

      <el-tab-pane label="拜访记录" name="record">
        <el-table :data="recordList" v-loading="recLoading" stripe>
          <el-table-column prop="customerName" label="客户" min-width="120" />
          <el-table-column prop="checkInTime" label="签到时间" width="160" />
          <el-table-column prop="checkInLocation" label="签到地点" min-width="150" />
          <el-table-column prop="content" label="报告内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="customerFeedback" label="客户反馈" min-width="150" show-overflow-tooltip />
          <el-table-column prop="nextStep" label="下一步" min-width="150" show-overflow-tooltip />
        </el-table>
        <Pagination v-model:limit="recQuery.pageSize" v-model:page="recQuery.pageNo" :total="recTotal" @pagination="loadRecords" />
      </el-tab-pane>
    </el-tabs>

    <!-- 新增计划弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增拜访计划" width="500px">
      <el-form :model="planForm" label-width="80px">
        <el-form-item label="客户" required>
          <el-select
            v-model="planForm.customerId"
            filterable
            remote
            reserve-keyword
            :remote-method="searchCustomers"
            :loading="customerSearchLoading"
            placeholder="请搜索并选择客户"
            class="w-1/1"
            @change="onCustomerSelect"
          >
            <el-option v-for="c in customerOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="拜访时间" required>
          <el-date-picker v-model="planForm.visitTime" type="datetime" placeholder="选择时间" value-format="YYYY-MM-DD HH:mm:ss" class="w-1/1" />
        </el-form-item>
        <el-form-item label="目的"><el-input v-model="planForm.purpose" placeholder="拜访目的" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="planForm.address" placeholder="拜访地址" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="handleCreatePlan">创建</el-button><el-button @click="dialogVisible = false">取消</el-button></template>
    </el-dialog>

    <!-- 签到弹窗 -->
    <el-dialog v-model="checkInVisible" title="拜访签到" width="400px">
      <el-form :model="checkInForm" label-width="80px">
        <el-form-item label="签到地点"><el-input v-model="checkInForm.checkInLocation" placeholder="当前位置" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="handleCheckIn">签到</el-button></template>
    </el-dialog>

    <!-- 报告弹窗 -->
    <el-dialog v-model="reportVisible" title="拜访报告" width="500px">
      <el-form :model="reportForm" label-width="80px">
        <el-form-item label="拜访内容"><el-input v-model="reportForm.content" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="客户反馈"><el-input v-model="reportForm.customerFeedback" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="下一步"><el-input v-model="reportForm.nextStep" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="handleReport">提交</el-button></template>
    </el-dialog>
  </ContentWrap>
</template>

<script lang="ts" setup>
import * as VisitApi from '@/api/crm/visit'
import { getCustomerPage } from '@/api/crm/customer'
import { ContentWrap } from '@/components/ContentWrap'

defineOptions({ name: 'CrmVisitPlan' })
const message = useMessage()
const activeTab = ref('plan')

const planQuery = reactive({ pageNo: 1, pageSize: 10, customerName: '', status: undefined as any })
const planList = ref<any[]>([]); const planTotal = ref(0); const planLoading = ref(false)
const recQuery = reactive({ pageNo: 1, pageSize: 10 })
const recordList = ref<any[]>([]); const recTotal = ref(0); const recLoading = ref(false)

const dialogVisible = ref(false); const checkInVisible = ref(false); const reportVisible = ref(false)
const currentPlanId = ref(0)

const customerSearchLoading = ref(false); const customerOptions = ref<any[]>([])
const planForm = reactive({ customerId: undefined as any, customerName: '', visitTime: '', purpose: '', address: '' })
const checkInForm = reactive({ planId: 0, checkInLocation: '' })
const reportForm = reactive({ id: 0, planId: 0, content: '', customerFeedback: '', nextStep: '' })

const searchCustomers = async (query: string) => {
  customerSearchLoading.value = true
  try { const r = await getCustomerPage({ pageNo: 1, pageSize: 30, name: query }); customerOptions.value = r.list || [] }
  finally { customerSearchLoading.value = false }
}
const onCustomerSelect = (id: number) => {
  const c = customerOptions.value.find((x: any) => x.id === id)
  if (c) planForm.customerName = c.name
}

const loadPlans = async () => { planLoading.value = true; try { const r = await VisitApi.getPlanPage(planQuery); planList.value = r.list || []; planTotal.value = r.total || 0 } finally { planLoading.value = false } }
const loadRecords = async () => { recLoading.value = true; try { const r = await VisitApi.getRecordPage(recQuery); recordList.value = r.list || []; recTotal.value = r.total || 0 } finally { recLoading.value = false } }

const handleCreatePlan = async () => {
  if (!planForm.customerId || !planForm.visitTime) { message.warning('请选择客户和拜访时间'); return }
  await VisitApi.createPlan(planForm as any); message.success('创建成功'); dialogVisible.value = false
  planForm.customerId = undefined; planForm.customerName = ''; planForm.visitTime = ''; planForm.purpose = ''; planForm.address = ''; loadPlans()
}
const openCheckIn = (row: any) => { currentPlanId.value = row.id; checkInForm.planId = row.id; checkInForm.checkInLocation = ''; checkInVisible.value = true }
const handleCheckIn = async () => { await VisitApi.checkIn(checkInForm as any); message.success('签到成功'); checkInVisible.value = false; loadPlans() }
const openReport = (row: any) => { currentPlanId.value = row.id; reportForm.id = 0; reportForm.planId = row.id; reportForm.content = ''; reportForm.customerFeedback = ''; reportForm.nextStep = ''; reportVisible.value = true }
const handleReport = async () => { await VisitApi.report(reportForm as any); message.success('报告提交成功'); reportVisible.value = false; loadPlans(); loadRecords() }
const handleDeletePlan = async (id: number) => { try { await message.confirm('确认删除？'); await VisitApi.deletePlan(id); message.success('删除成功'); loadPlans() } catch { /* cancelled */ } }

const handleTabChange = () => { if (activeTab.value === 'record') loadRecords() }

onMounted(() => loadPlans())
</script>
