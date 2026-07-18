<template>
  <div v-loading="loading">
    <el-table :data="records" :show-overflow-tooltip="true" :stripe="true" class="mt-20px">
      <el-table-column align="center" :label="t('clue.poolFlowRecord.operationType')" prop="operationType" min-width="120">
        <template #default="{ row }">
          <el-tag v-if="row.operationType === 'pool_claim'" type="success" size="small">
            {{ t('clue.poolFlowRecord.operationTypePoolClaim') }}
          </el-tag>
          <el-tag v-else-if="row.operationType === 'auto_recovery'" type="danger" size="small">
            {{ t('clue.poolFlowRecord.operationTypeAutoRecovery') }}
          </el-tag>
          <el-tag v-else-if="row.operationType === 'manual_return'" type="warning" size="small">
            {{ t('clue.poolFlowRecord.operationTypeManualReturn') }}
          </el-tag>
          <el-tag v-else-if="row.operationType === 'force_recovery'" type="danger" size="small">
            {{ t('clue.poolFlowRecord.operationTypeForceRecovery') }}
          </el-tag>
          <el-tag v-else-if="row.operationType === 'leave_recovery'" type="info" size="small">
            {{ t('clue.poolFlowRecord.operationTypeLeaveRecovery') }}
          </el-tag>
          <span v-else>{{ row.operationType }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('clue.poolFlowRecord.operatorName')" prop="operatorName" min-width="100" />
      <el-table-column align="center" :label="t('clue.poolFlowRecord.operationTime')" prop="operationTime" min-width="170">
        <template #default="{ row }">
          {{ formatDate(row.operationTime) }}
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('clue.poolFlowRecord.previousOwner')" prop="previousOwnerName" min-width="120" />
      <el-table-column align="center" :label="t('clue.poolFlowRecord.operationReason')" prop="operationReason" min-width="160" />
    </el-table>
    <el-empty v-if="!loading && records.length === 0" :description="t('common.noData')" />
  </div>
</template>
<script lang="ts" setup>
import * as ClueApi from '@/api/crm/clue'
import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'CluePoolFlowRecordList' })

const { t } = useI18n('crm')

const props = defineProps<{
  clueId: number
}>()

const loading = ref(false)
const records = ref<ClueApi.ClueSeaPoolFlowRecordVO[]>([])

const getRecords = async () => {
  loading.value = true
  try {
    records.value = await ClueApi.getClueSeaPoolFlowRecords(props.clueId)
  } catch {
    records.value = []
  } finally {
    loading.value = false
  }
}

watch(
  () => props.clueId,
  (val) => {
    if (val) {
      getRecords()
    }
  },
  { immediate: true }
)
</script>