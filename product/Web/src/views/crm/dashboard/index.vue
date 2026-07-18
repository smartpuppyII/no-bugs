<template>
  <ContentWrap title="CRM 工作台">
    <!-- 待办事项卡片 -->
    <TodoWidget />

    <!-- 公海概览统计卡片 -->
    <el-card shadow="hover" class="mt-4">
      <template #header>
        <div class="card-header">
          <span class="title">{{ $t('crm.dashboard.seaPoolOverview') }}</span>
        </div>
      </template>
      <el-row :gutter="16">
        <el-col :span="6">
          <div class="stat-card" @click="$router.push('/crm/customer/pool')">
            <div class="stat-value" style="color: #409EFF">{{ seaPoolStats.totalCount }}</div>
            <div class="stat-label">{{ $t('crm.dashboard.seaPoolTotal') }}</div>
            <div class="stat-detail">
              <span>{{ $t('crm.dashboard.seaPoolTotalClue') }}: {{ seaPoolStats.clueCount }}</span>
              <span class="ml-8px">{{ $t('crm.dashboard.seaPoolTotalCustomer') }}: {{ seaPoolStats.customerCount }}</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-value" style="color: #67C23A">{{ seaPoolStats.weeklyRecoveryCount }}</div>
            <div class="stat-label">{{ $t('crm.dashboard.weeklyRecovery') }}</div>
            <div class="stat-detail">
              <span v-if="seaPoolStats.weeklyRecoveryChange > 0" class="text-green-500">
                {{ $t('crm.dashboard.weeklyRecoveryUp', { change: seaPoolStats.weeklyRecoveryChange }) }}
              </span>
              <span v-else-if="seaPoolStats.weeklyRecoveryChange < 0" class="text-red-500">
                {{ $t('crm.dashboard.weeklyRecoveryDown', { change: seaPoolStats.weeklyRecoveryChange }) }}
              </span>
              <span v-else class="text-gray-400">与上周持平</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-value" style="color: #E6A23C">{{ (seaPoolStats.conversionRate * 100).toFixed(1) }}%</div>
            <div class="stat-label">{{ $t('crm.dashboard.conversionRate') }}</div>
            <div class="stat-detail">
              <span class="text-gray-400">确权成功数 / 总领取数</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" @click="$router.push('/crm/backlog?tab=customerPutPoolRemind')">
            <div class="stat-value" :style="{ color: seaPoolStats.upcomingRecoveryCount > 0 ? '#F56C6C' : '#909399' }">
              {{ seaPoolStats.upcomingRecoveryCount }}
            </div>
            <div class="stat-label">{{ $t('crm.dashboard.upcomingRecovery') }}</div>
            <div class="stat-detail">
              <span class="text-gray-400">未来3天内</span>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 快捷入口 -->
    <el-card shadow="hover" class="mt-4">
      <template #header>
        <span class="title">快捷入口</span>
      </template>
      <el-row :gutter="12">
        <el-col :span="4" v-for="item in quickLinks" :key="item.path">
          <div class="quick-link-item" @click="$router.push(item.path)">
            <Icon :icon="item.icon" :size="24" :color="item.color" />
            <div class="quick-link-label">{{ item.label }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </ContentWrap>
</template>

<script lang="ts" setup>
import TodoWidget from './TodoWidget.vue'
import { ContentWrap } from '@/components/ContentWrap'
import * as SeaPoolApi from '@/api/crm/seapool'
import type { SeaPoolStatisticsVO } from '@/api/crm/seapool'

defineOptions({ name: 'CrmDashboard' })

const quickLinks = [
  { label: '新增客户', icon: 'ep:plus', color: '#409EFF', path: '/crm/customer' },
  { label: '新增线索', icon: 'ep:plus', color: '#67C23A', path: '/crm/clue' },
  { label: '新增商机', icon: 'ep:plus', color: '#E6A23C', path: '/crm/business' },
  { label: '新增合同', icon: 'ep:plus', color: '#F56C6C', path: '/crm/contract' },
  { label: '待办事项', icon: 'ep:list', color: '#909399', path: '/crm/backlog' },
  { label: '客户管理', icon: 'ep:user', color: '#409EFF', path: '/crm/customer' },
]

// ===== 公海概览统计 =====
const seaPoolStats = ref<SeaPoolStatisticsVO>({
  totalCount: 0,
  clueCount: 0,
  customerCount: 0,
  weeklyRecoveryCount: 0,
  weeklyRecoveryChange: 0,
  conversionRate: 0,
  upcomingRecoveryCount: 0
})

const loadSeaPoolStats = async () => {
  try {
    seaPoolStats.value = await SeaPoolApi.getSeaPoolStatistics()
  } catch {
    // 降级处理
  }
}

onMounted(() => loadSeaPoolStats())
</script>

<style lang="scss" scoped>
.title {
  font-size: 16px;
  font-weight: bold;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.quick-link-item {
  text-align: center;
  padding: 16px 8px;
  cursor: pointer;
  border-radius: 8px;
  transition: background-color 0.2s;
  &:hover {
    background-color: var(--el-fill-color-light);
  }
}
.quick-link-label {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 8px;
}
// 公海概览统计卡片
.stat-card {
  text-align: center;
  padding: 12px 8px;
  border-radius: 8px;
  transition: background-color 0.2s;
  cursor: default;
  &[clickable] {
    cursor: pointer;
  }
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  line-height: 1.2;
}
.stat-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}
.stat-detail {
  font-size: 11px;
  color: var(--el-text-color-placeholder);
  margin-top: 6px;
}
</style>