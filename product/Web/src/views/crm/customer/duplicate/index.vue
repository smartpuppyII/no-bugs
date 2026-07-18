<template>
  <ContentWrap title="重复客户管理">
    <template #search>
      <el-form :inline="true">
        <el-form-item label="检查名称">
          <el-switch v-model="checkName" />
        </el-form-item>
        <el-form-item label="检查手机号">
          <el-switch v-model="checkMobile" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询重复</el-button>
        </el-form-item>
      </el-form>
    </template>

    <div v-loading="loading">
      <el-alert
        v-if="!loading && duplicateGroups.length === 0"
        title="未发现重复客户"
        type="success"
        :closable="false"
        show-icon
      />
      <el-card
        v-for="(group, index) in duplicateGroups"
        :key="index"
        style="margin-bottom: 16px"
      >
        <template #header>
          <span>重复组 {{ index + 1 }}（共 {{ group.length }} 个客户）</span>
          <el-button
            type="danger"
            size="small"
            style="float: right"
            @click="handleMerge(group)"
          >
            合并为同一客户
          </el-button>
        </template>
        <el-table :data="group" border>
          <el-table-column label="客户名称" prop="name" />
          <el-table-column label="手机号" prop="mobile" />
          <el-table-column label="电话" prop="telephone" />
          <el-table-column label="邮箱" prop="email" />
          <el-table-column label="负责人" prop="ownerUserName" />
          <el-table-column label="创建时间" prop="createTime" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button
                type="primary"
                size="small"
                link
                @click="setTarget(group, row)"
              >
                {{ targetIdMap[index] === row.id ? '已选为目标' : '设为目标' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </ContentWrap>
</template>

<script lang="ts" setup>
import * as CustomerApi from '@/api/crm/customer'
import { ContentWrap } from '@/components/ContentWrap'

const message = useMessage()

const loading = ref(false)
const checkName = ref(true)
const checkMobile = ref(true)
const duplicateGroups = ref<any[]>([])
const targetIdMap = ref<Record<number, number>>({})

const loadData = async () => {
  loading.value = true
  duplicateGroups.value = []
  targetIdMap.value = {}
  try {
    const res = await CustomerApi.getDuplicateCustomerList(checkName.value, checkMobile.value)
    if (res && Array.isArray(res)) {
      duplicateGroups.value = res
    }
  } catch (e) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

const setTarget = (group: any[], row: any) => {
  const index = duplicateGroups.value.indexOf(group)
  targetIdMap.value[index] = row.id
  message.success(`已将「${row.name}」设为目标客户，合并后其他客户将被删除`)
}

const handleMerge = async (group: any[]) => {
  const index = duplicateGroups.value.indexOf(group)
  const targetId = targetIdMap.value[index]
  if (!targetId) {
    message.warning('请先选择一个目标客户')
    return
  }

  const sourceIds = group.filter((item: any) => item.id !== targetId).map((item: any) => item.id)
  if (sourceIds.length === 0) {
    message.warning('没有需要合并的客户')
    return
  }

  try {
    await message.confirm(
      `确认将 ${sourceIds.length} 个客户合并到「${group.find((i: any) => i.id === targetId)?.name}」？合并后源客户将被删除，其联系人和商机将转移到目标客户。`,
      '确认合并',
      { confirmButtonText: '确认合并', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    return
  }

  for (const sourceId of sourceIds) {
    try {
      await CustomerApi.mergeCustomer(sourceId, targetId)
    } catch (e) {
      message.error(`合并客户 ${sourceId} 失败`)
      return
    }
  }
  message.success('合并成功')
  await loadData()
}

onMounted(() => {
  loadData()
})
</script>
