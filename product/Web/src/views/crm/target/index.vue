<template>
  <ContentWrap title="销售目标管理">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <span>目标看板</span>
            <el-button type="primary" size="small" style="float: right" @click="dialogVisible = true">设定目标</el-button>
          </template>
          <el-table :data="targetList" stripe>
            <el-table-column prop="periodValue" label="周期" width="120" />
            <el-table-column prop="contractAmount" label="合同金额目标(元)" min-width="150" />
            <el-table-column prop="receivableAmount" label="回款金额目标(元)" min-width="150" />
            <el-table-column prop="newCustomerCount" label="新客数目标" min-width="120" />
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button text type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header><span>目标达成</span></template>
          <div v-for="t in targetList" :key="t.id" class="mb-4">
            <div class="text-sm text-gray-500 mb-1">{{ t.periodValue }}</div>
            <div class="mb-2">
              <span class="text-xs">合同金额</span>
              <el-progress :percentage="t.contractAmount > 0 ? Math.min(100, Math.round((actualData.contractAmount || 0) / t.contractAmount * 100)) : 0" />
            </div>
            <div>
              <span class="text-xs">新客数</span>
              <el-progress :percentage="t.newCustomerCount > 0 ? Math.min(100, Math.round((actualData.newCustomerCount || 0) / t.newCustomerCount * 100)) : 0" status="success" />
            </div>
          </div>
          <div v-if="targetList.length === 0" class="text-gray-400 text-center py-8">暂无目标数据</div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="设定销售目标" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="目标对象" required>
          <el-select v-model="form.targetType" @change="loadTargets">
            <el-option label="个人" value="user" /><el-option label="部门" value="dept" />
          </el-select>
        </el-form-item>
        <el-form-item label="周期类型" required>
          <el-radio-group v-model="form.periodType"><el-radio value="month">月度</el-radio><el-radio value="quarter">季度</el-radio><el-radio value="year">年度</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="周期值" required><el-input v-model="form.periodValue" placeholder="如：2025-07" /></el-form-item>
        <el-form-item label="合同金额目标"><el-input-number v-model="form.contractAmount" :min="0" :precision="2" class="w-1/1" /></el-form-item>
        <el-form-item label="回款金额目标"><el-input-number v-model="form.receivableAmount" :min="0" :precision="2" class="w-1/1" /></el-form-item>
        <el-form-item label="新客数目标"><el-input-number v-model="form.newCustomerCount" :min="0" class="w-1/1" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </ContentWrap>
</template>

<script lang="ts" setup>
import * as TargetApi from '@/api/crm/target'
import { ContentWrap } from '@/components/ContentWrap'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'CrmTarget' })
const message = useMessage(); const userStore = useUserStore()
const targetList = ref<any[]>([]); const dialogVisible = ref(false)
const actualData = reactive({ contractAmount: 0, newCustomerCount: 0 })

const form = reactive({ targetType: 'user', periodType: 'month', periodValue: '', contractAmount: 0, receivableAmount: 0, newCustomerCount: 0 })

const loadTargets = async () => {
  try {
    const targetId = userStore.getUser.id; const targetName = userStore.getUser.nickname
    targetList.value = await TargetApi.getTargetList(form.targetType, targetId) || []
  } catch { targetList.value = [] }
}

const handleSave = async () => {
  if (!form.periodValue) { message.warning('请输入周期值'); return }
  await TargetApi.saveTarget({
    targetType: form.targetType, targetId: userStore.getUser.id,
    targetName: userStore.getUser.nickname,
    periodType: form.periodType, periodValue: form.periodValue,
    contractAmount: form.contractAmount, receivableAmount: form.receivableAmount,
    newCustomerCount: form.newCustomerCount
  })
  message.success('保存成功'); dialogVisible.value = false; loadTargets()
}

const handleDelete = async (id: number) => {
  try { await message.confirm('确认删除？'); await TargetApi.deleteTarget(id); message.success('删除成功'); loadTargets() } catch { /* cancelled */ }
}

onMounted(() => loadTargets())
</script>
