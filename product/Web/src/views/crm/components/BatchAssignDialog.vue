<template>
  <el-dialog v-model="dialogVisible" :title="t('crm.common.batchAssignTitle')" width="500">
    <div class="mb-15px">
      <el-alert :title="t('crm.common.selectedCount', { count: ids.length })" type="info" :closable="false" />
    </div>
    <el-form label-width="80px">
      <el-form-item :label="t('crm.common.assignToUser')">
        <el-select v-model="selectedUserId" class="!w-full" clearable :placeholder="t('crm.common.assignToUserPlaceholder')">
          <el-option
            v-for="user in userList"
            :key="user.id"
            :label="user.nickname"
            :value="user.id"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="!selectedUserId" type="primary" @click="handleConfirm">
        {{ t('common.confirm') }}
      </el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import * as UserApi from '@/api/system/user'

defineOptions({ name: 'BatchAssignDialog' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const selectedUserId = ref<number>()
const ids = ref<number[]>([])
const userList = ref<UserApi.UserVO[]>([])

const emit = defineEmits<{
  (e: 'confirm', data: { userId: number; ids: number[] }): void
}>()

const open = async (selectedIds: number[]) => {
  ids.value = selectedIds
  selectedUserId.value = undefined
  dialogVisible.value = true
  userList.value = await UserApi.getSimpleUserList()
}

const handleConfirm = () => {
  if (!selectedUserId.value) {
    message.warning(t('crm.common.pleaseSelectUser'))
    return
  }
  emit('confirm', { userId: selectedUserId.value, ids: ids.value })
  dialogVisible.value = false
}

defineExpose({ open })
</script>
