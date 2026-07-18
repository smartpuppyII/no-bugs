<template>
  <ContentWrap>
    <el-table v-loading="loading" :data="flowRecords" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column align="center" :label="t('poolFlowRecord.operationType')" prop="operationType" min-width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.operationType === 'pool_claim'" type="success" size="small">
            {{ t('poolFlowRecord.operationTypePoolClaim') }}
          </el-tag>
          <el-tag v-else-if="scope.row.operationType === 'auto_recovery'" type="danger" size="small">
            {{ t('poolFlowRecord.operationTypeAutoRecovery') }}
          </el-tag>
          <el-tag v-else-if="scope.row.operationType === 'manual_return'" type="warning" size="small">
            {{ t('poolFlowRecord.operationTypeManualReturn') }}
          </el-tag>
          <el-tag v-else-if="scope.row.operationType === 'force_recovery'" type="danger" size="small">
            {{ t('poolFlowRecord.operationTypeForceRecovery') }}
          </el-tag>
          <el-tag v-else-if="scope.row.operationType === 'leave_recovery'" type="info" size="small">
            {{ t('poolFlowRecord.operationTypeLeaveRecovery') }}
          </el-tag>
          <el-tag v-else size="small">{{ scope.row.operationType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('poolFlowRecord.operatorName')" prop="operatorName" min-width="100" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('poolFlowRecord.operationTime')"
        prop="operationTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('poolFlowRecord.previousOwner')" prop="previousOwnerName" min-width="120" />
      <el-table-column align="center" :label="t('poolFlowRecord.operationReason')" prop="operationReason" min-width="160">
        <template #default="scope">
          {{ scope.row.operationReason || '-' }}
        </template>
      </el-table-column>
    </el-table>
    <div v-if="!loading && flowRecords.length === 0" class="text-center text-gray-400 py-8">
      {{ t('backlog.noData') }}
    </div>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as SeaPoolApi from '@/api/crm/seapool'
import type { SeaPoolFlowRecordVO } from '@/api/crm/seapool'

defineOptions({ name: 'CrmSeaPoolFlowRecord' })

const props = defineProps<{
  customerId: number
}>()

const { t } = useI18n('crm.customer') // 国际化
const loading = ref(false)
const flowRecords = ref<SeaPoolFlowRecordVO[]>([])

const loadFlowRecords = async () => {
  if (!props.customerId) return
  loading.value = true
  try {
    flowRecords.value = await SeaPoolApi.getCustomerSeaPoolFlowRecords(props.customerId)
  } catch {
    flowRecords.value = []
  } finally {
    loading.value = false
  }
}

watch(() => props.customerId, () => {
  loadFlowRecords()
})

onMounted(() => {
  loadFlowRecords()
})
</script>

<style lang="scss" scoped></style>