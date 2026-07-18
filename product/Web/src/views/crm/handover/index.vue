<template>
  <ContentWrap :title="$t('crm.handover.title')">
    <!-- Step 1: 选择离职人员 -->
    <el-card shadow="never" class="mb-4">
      <template #header><span>{{ $t('crm.handover.step1') }}</span></template>
      <el-form :inline="true">
        <el-form-item :label="$t('crm.handover.departingUser')">
          <el-select
            v-model="fromUserId"
            filterable
            :placeholder="$t('crm.handover.selectUserPlaceholder')"
          >
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="`${user.nickname}（${user.deptName || ''}）`"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="previewLoading" :disabled="!fromUserId" @click="handlePreview">
            <Icon icon="ep:search" class="mr-1" />{{ $t('common.search') }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Step 2: 预览数据 & 选择接收人 -->
    <el-card shadow="never" class="mb-4" v-if="previewData">
      <template #header><span>{{ $t('crm.handover.step2') }}（{{ $t('crm.handover.totalCount', { count: previewData.totalCount }) }}）</span></template>
      <el-table :data="bizTypeRows" stripe>
        <el-table-column prop="label" :label="$t('crm.handover.dataType')" width="120" />
        <el-table-column prop="count" :label="$t('crm.handover.dataCount')" width="100" />
        <el-table-column :label="$t('crm.handover.receiver')" width="250">
          <template #default="scope">
            <el-select
              v-model="receivers[scope.row.key]"
              filterable
              :placeholder="$t('crm.handover.receiverPlaceholder')"
              clearable
            >
              <el-option v-for="user in userList" :key="user.id" :label="user.nickname" :value="user.id"
                :disabled="user.id === fromUserId" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="scope">
            <el-button text type="primary" size="small" @click="showDetail(scope.row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-form class="mt-4">
        <el-form-item :label="$t('crm.handover.remark')">
          <el-input v-model="remark" type="textarea" :rows="2" :placeholder="$t('crm.handover.remarkPlaceholder')" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="execLoading" @click="handleExecute">{{ $t('crm.handover.confirmExecute') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 明细查看弹窗 -->
    <el-dialog v-model="detailVisible" :title="detailTitle" width="800px">
      <el-table :data="detailList" stripe max-height="400">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="mobile" label="手机号" width="130" />
        <el-table-column label="负责人" width="100">
          <template #default="{ row }">
            {{ row.ownerUserName || row.ownerUserId || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
      </el-table>
    </el-dialog>

    <!-- 转移日志 -->
    <el-card shadow="never">
      <template #header><span>{{ $t('crm.handover.transferLog') }}</span></template>
      <el-table :data="logList" v-loading="logLoading" stripe>
        <el-table-column prop="bizName" :label="$t('crm.handover.bizName')" />
        <el-table-column prop="bizType" :label="$t('crm.handover.bizType')" width="100">
          <template #default="scope">
            <el-tag size="small">{{ bizTypeMap[scope.row.bizType] || scope.row.bizType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fromUserId" :label="$t('crm.handover.fromUser')" width="80" />
        <el-table-column prop="toUserId" :label="$t('crm.handover.toUser')" width="80" />
        <el-table-column prop="transferType" :label="$t('crm.handover.transferType')" width="100">
          <template #default="scope">
            <el-tag size="small" :type="scope.row.transferType === 2 ? 'warning' : ''">
              {{ scope.row.transferType === 2 ? $t('crm.handover.departureTransfer') : scope.row.transferType === 1 ? $t('crm.handover.manualTransfer') : $t('crm.handover.systemAuto') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" :label="$t('crm.handover.remarkCol')" />
        <el-table-column prop="createTime" :label="$t('crm.handover.createTime')" width="160" />
      </el-table>
    </el-card>
  </ContentWrap>
</template>

<script lang="ts" setup>
import * as HandoverApi from '@/api/crm/handover'
import { getSimpleUserList } from '@/api/system/user'
import { ContentWrap } from '@/components/ContentWrap'

defineOptions({ name: 'CrmHandoverManagement' })

const { t } = useI18n()
const message = useMessage()

const fromUserId = ref<number | null>(null)
const previewLoading = ref(false)
const execLoading = ref(false)
const logLoading = ref(false)
const previewData = ref<any>(null)
const remark = ref('')
const logList = ref<any[]>([])
const userList = ref<any[]>([])

const receivers = reactive<Record<string, number | null>>({})

const bizTypeMap: Record<number, string> = {
  1: '线索', 2: '客户', 3: '联系人', 4: '商机', 5: '合同', 8: '回款计划', 9: '订单'
}

const bizTypeRows = computed(() => {
  if (!previewData.value) return []
  return [
    { key: 'customerToUserId', label: t('crm.handover.customerData'), count: previewData.value.customerData?.count || 0 },
    { key: 'clueToUserId', label: t('crm.handover.clueData'), count: previewData.value.clueData?.count || 0 },
    { key: 'businessToUserId', label: t('crm.handover.businessData'), count: previewData.value.businessData?.count || 0 },
    { key: 'contractToUserId', label: t('crm.handover.contractData'), count: previewData.value.contractData?.count || 0 },
    { key: 'contactToUserId', label: t('crm.handover.contactData'), count: previewData.value.contactData?.count || 0 },
    { key: 'receivablePlanToUserId', label: t('crm.handover.receivablePlanData'), count: previewData.value.receivablePlanData?.count || 0 },
  ]
})

const handlePreview = async () => {
  if (!fromUserId.value) {
    message.warning(t('crm.handover.selectUserPlaceholder'))
    return
  }
  previewLoading.value = true
  try {
    previewData.value = await HandoverApi.previewHandover(fromUserId.value)
  } finally {
    previewLoading.value = false
  }
}

const handleExecute = async () => {
  if (!fromUserId.value) return
  try {
    await message.confirm(t('crm.handover.confirmMessage'))
    execLoading.value = true
    const data: any = { fromUserId: fromUserId.value, remark: remark.value }
    for (const key of Object.keys(receivers)) {
      if (receivers[key]) data[key] = receivers[key]
    }
    await HandoverApi.executeHandover(data)
    message.success(t('crm.handover.executeSuccess'))
    previewData.value = null
    loadLogs()
  } catch (e) {
    // cancelled
  } finally {
    execLoading.value = false
  }
}

const detailVisible = ref(false)
const detailTitle = ref('')
const detailList = ref<any[]>([])

const showDetail = (row: any) => {
  const group = row.key === 'customerToUserId' ? previewData.value?.customerData
    : row.key === 'clueToUserId' ? previewData.value?.clueData
    : row.key === 'businessToUserId' ? previewData.value?.businessData
    : row.key === 'contractToUserId' ? previewData.value?.contractData
    : row.key === 'contactToUserId' ? previewData.value?.contactData
    : row.key === 'receivablePlanToUserId' ? previewData.value?.receivablePlanData
    : null
  detailTitle.value = `${row.label}明细`
  detailList.value = group?.detailList || []
  detailVisible.value = true
}

const loadLogs = async () => {
  logLoading.value = true
  try {
    const res = await HandoverApi.getTransferLogPage({ pageNo: 1, pageSize: 50 })
    logList.value = res.list || []
  } finally {
    logLoading.value = false
  }
}

const loadUsers = async () => {
  try {
    userList.value = await getSimpleUserList()
  } catch (e) {
    // fallback
  }
}

onMounted(() => {
  loadUsers()
  loadLogs()
})
</script>
