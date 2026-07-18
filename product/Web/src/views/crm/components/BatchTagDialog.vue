<template>
  <el-dialog v-model="dialogVisible" :title="t('crm.common.batchTagTitle')" width="500">
    <div class="mb-15px">
      <el-alert :title="t('crm.common.selectedCount', { count: customerIds.length })" type="info" :closable="false" />
    </div>
    <el-form label-width="80px">
      <el-form-item :label="t('crm.common.tags')">
        <el-select v-model="selectedTagIds" class="!w-full" multiple clearable :placeholder="t('crm.common.tagPlaceholder')">
          <el-option
            v-for="tag in tagList"
            :key="tag.id"
            :label="tag.name"
            :value="tag.id"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="selectedTagIds.length === 0" type="primary" @click="handleConfirm">
        {{ t('common.confirm') }}
      </el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import * as TagApi from '@/api/crm/tag'

defineOptions({ name: 'BatchTagDialog' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const selectedTagIds = ref<number[]>([])
const customerIds = ref<number[]>([])
const tagList = ref<TagApi.TagVO[]>([])

const emit = defineEmits<{
  (e: 'confirm', data: { tagIds: number[]; customerIds: number[] }): void
}>()

const open = async (selectedCustomerIds: number[]) => {
  customerIds.value = selectedCustomerIds
  selectedTagIds.value = []
  dialogVisible.value = true
  tagList.value = await TagApi.getAllTags()
}

const handleConfirm = () => {
  if (selectedTagIds.value.length === 0) {
    message.warning(t('crm.common.pleaseSelectTag'))
    return
  }
  emit('confirm', { tagIds: selectedTagIds.value, customerIds: customerIds.value })
  dialogVisible.value = false
}

defineExpose({ open })
</script>
