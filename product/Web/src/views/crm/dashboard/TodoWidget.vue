<template>
  <el-card shadow="hover" class="todo-widget">
    <template #header>
      <div class="card-header">
        <span class="title">待办事项</span>
        <el-button text type="primary" @click="$router.push('/crm/backlog')">查看全部</el-button>
      </div>
    </template>
    <el-row :gutter="12">
      <el-col :span="8" v-for="item in todoItems" :key="item.key">
        <div class="todo-item" @click="$router.push(item.route)">
          <div class="todo-count" :style="{ color: item.color }">{{ item.count }}</div>
          <div class="todo-label">{{ item.label }}</div>
        </div>
      </el-col>
    </el-row>
  </el-card>
</template>

<script lang="ts" setup>
import * as DashboardApi from '@/api/crm/dashboard'
import type { TodoSummaryVO } from '@/api/crm/dashboard'

defineOptions({ name: 'CrmTodoWidget' })

const summary = ref<TodoSummaryVO>({
  todayContactCount: 0,
  clueFollowCount: 0,
  customerFollowCount: 0,
  customerPutPoolRemindCount: 0,
  contractAuditCount: 0,
  contractRemindCount: 0,
  receivablePlanRemindCount: 0,
  receivableAuditCount: 0,
  expiredFollowCount: 0
})

const todoItems = computed(() => [
  { key: 'todayContact', label: '今日需联系', count: summary.value.todayContactCount, color: '#E6A23C', route: '/crm/backlog?tab=customerTodayContact' },
  { key: 'expiredFollow', label: '逾期未跟进', count: summary.value.expiredFollowCount, color: '#F56C6C', route: '/crm/backlog?tab=customerFollow' },
  { key: 'clueFollow', label: '待跟进线索', count: summary.value.clueFollowCount, color: '#409EFF', route: '/crm/backlog?tab=clueFollow' },
  { key: 'contractRemind', label: '即将到期合同', count: summary.value.contractRemindCount, color: '#E6A23C', route: '/crm/backlog?tab=contractRemind' },
  { key: 'receivablePlan', label: '待回款计划', count: summary.value.receivablePlanRemindCount, color: '#909399', route: '/crm/backlog?tab=receivablePlanRemind' },
  { key: 'contractAudit', label: '待审核合同', count: summary.value.contractAuditCount, color: '#67C23A', route: '/crm/backlog?tab=contractAudit' },
  { key: 'poolRemind', label: '待入公海', count: summary.value.customerPutPoolRemindCount, color: '#909399', route: '/crm/backlog?tab=customerPutPoolRemind' },
  { key: 'receivableAudit', label: '待审核回款', count: summary.value.receivableAuditCount, color: '#67C23A', route: '/crm/backlog?tab=receivableAudit' },
  { key: 'customerFollow', label: '待跟进客户', count: summary.value.customerFollowCount, color: '#409EFF', route: '/crm/backlog?tab=customerFollow' },
])

const loadData = async () => {
  try {
    summary.value = await DashboardApi.getTodoSummary()
  } catch (e) {
    console.error('Failed to load todo summary', e)
  }
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.todo-widget {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title {
  font-size: 16px;
  font-weight: bold;
}
.todo-item {
  text-align: center;
  padding: 12px 8px;
  cursor: pointer;
  border-radius: 8px;
  transition: background-color 0.2s;
  &:hover {
    background-color: var(--el-fill-color-light);
  }
}
.todo-count {
  font-size: 24px;
  font-weight: bold;
  line-height: 1.2;
}
.todo-label {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}
</style>
