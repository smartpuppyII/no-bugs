<template>
  <el-dialog
    v-model="visible"
    title="重复客户检测"
    width="700px"
    :close-on-click-modal="false"
  >
    <div v-loading="checking">
      <el-alert
        v-if="!checking && duplicates.length === 0"
        title="未发现重复客户，可以继续创建"
        type="success"
        :closable="false"
        show-icon
      />
      <div v-if="duplicates.length > 0">
        <el-alert
          title="发现以下可能重复的客户，请确认是否继续创建"
          type="warning"
          :closable="false"
          show-icon
          style="margin-bottom: 16px"
        />
        <el-table :data="duplicates" border>
          <el-table-column label="匹配字段" prop="matchField" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.matchField === 'name'" type="primary">名称</el-tag>
              <el-tag v-else-if="row.matchField === 'mobile'" type="success">手机号</el-tag>
              <el-tag v-else-if="row.matchField === 'email'" type="warning">邮箱</el-tag>
              <el-tag v-else-if="row.matchField === 'wechat'" type="info">微信</el-tag>
              <el-tag v-else>{{ row.matchField }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="匹配值" prop="matchValue" />
          <el-table-column label="已有客户名称">
            <template #default="{ row }">
              {{ row.customer?.name || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="已有客户手机号">
            <template #default="{ row }">
              {{ row.customer?.mobile || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="负责人">
            <template #default="{ row }">
              {{ row.customer?.ownerUserId || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button
                type="primary"
                size="small"
                link
                @click="viewCustomer(row.customer?.id)"
              >
                查看已有客户
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <template #footer>
      <el-button @click="handleContinue">仍然新建</el-button>
      <el-button type="primary" @click="visible = false">取消创建</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import * as CustomerApi from '@/api/crm/customer'

const props = defineProps<{
  modelValue: boolean
  customerData: {
    name?: string
    mobile?: string
    email?: string
    wechat?: string
  }
}>()

const emit = defineEmits(['update:modelValue', 'continue', 'cancel'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const checking = ref(false)
const duplicates = ref<any[]>([])

watch(visible, async (val) => {
  if (val && props.customerData) {
    await doCheck()
  }
})

const doCheck = async () => {
  checking.value = true
  duplicates.value = []
  try {
    const res = await CustomerApi.checkDuplicateCustomer({
      name: props.customerData.name,
      mobile: props.customerData.mobile,
      email: props.customerData.email,
      wechat: props.customerData.wechat
    })
    if (res && Array.isArray(res)) {
      duplicates.value = res
    }
    if (duplicates.value.length === 0) {
      // 无重复，自动继续
      setTimeout(() => {
        emit('continue')
        visible.value = false
      }, 500)
    }
  } catch (e) {
    // 查重失败不阻断流程
    emit('continue')
    visible.value = false
  } finally {
    checking.value = false
  }
}

const viewCustomer = (id: number) => {
  if (id) {
    window.open(`/crm/customer/detail/${id}`, '_blank')
  }
}

const handleContinue = () => {
  emit('continue')
  visible.value = false
}
</script>
