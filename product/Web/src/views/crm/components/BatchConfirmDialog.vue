<template>
  <el-dialog v-model="dialogVisible" :title="operationName" width="450">
    <div class="text-center py-20px">
      <Icon icon="ep:warning-filled" color="#e6a23c" size="48" class="mb-15px" />
      <p class="text-16px">
        {{ t('crm.common.batchConfirmMsg', { count: ids.length, operation: operationName }) }}
      </p>
    </div>
    <template #footer>
      <el-button type="primary" @click="handleConfirm">{{ t('common.confirm') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
defineOptions({ name: 'BatchConfirmDialog' })

const { t } = useI18n()

const dialogVisible = ref(false)
const operationName = ref('')
const ids = ref<number[]>([])

const emit = defineEmits<{
  (e: 'confirm', ids: number[]): void
}>()

const open = (selectedIds: number[], operation: string) => {
  ids.value = selectedIds
  operationName.value = operation
  dialogVisible.value = true
}

const handleConfirm = () => {
  emit('confirm', ids.value)
  dialogVisible.value = false
}

defineExpose({ open })
</script>
