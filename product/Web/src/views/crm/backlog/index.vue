<template>
  <doc-alert title="【通用】跟进记录、待办事项" url="https://doc.iocoder.cn/crm/follow-up/" />

  <!-- 顶部操作栏：快捷跳转 -->
  <ContentWrap class="mb-10px">
    <div class="flex items-center gap-2 flex-wrap">
      <span class="text-sm font-bold mr-2">快捷入口：</span>
      <el-button size="small" @click="getCount">
        <Icon icon="ep:refresh" class="mr-1" /> 刷新数据
      </el-button>
      <el-button size="small" type="primary" @click="push({name:'CrmClue'})">
        线索管理
      </el-button>
      <el-button size="small" type="primary" @click="push({name:'CrmCustomer'})">
        客户管理
      </el-button>
      <el-button size="small" type="primary" @click="push({name:'CrmContract'})">
        合同管理
      </el-button>
      <el-button size="small" type="primary" @click="push({name:'CrmReceivable'})">
        回款管理
      </el-button>
      <el-button size="small" type="success" @click="push({path:'/crm/clue-pool'})">
        公共线索池
      </el-button>
    </div>
  </ContentWrap>

  <el-row :gutter="20">
    <el-col :span="4" class="min-w-[200px]">
      <div class="side-item-list">
        <div
          v-for="(item, index) in leftSides"
          :key="index"
          :class="leftMenu == item.menu ? 'side-item-select' : 'side-item-default'"
          class="side-item"
          @click="sideClick(item)"
        >
          {{ item.name }}
          <el-badge v-if="item.count > 0" :max="99" :value="item.count" />
        </div>
      </div>
    </el-col>
    <el-col :span="20" :xs="24">
      <CustomerTodayContactList v-if="leftMenu === 'customerTodayContact'" />
      <ClueFollowList v-if="leftMenu === 'clueFollow'" />
      <ContractAuditList v-if="leftMenu === 'contractAudit'" />
      <ReceivableAuditList v-if="leftMenu === 'receivableAudit'" />
      <ContractRemindList v-if="leftMenu === 'contractRemind'" />
      <CustomerFollowList v-if="leftMenu === 'customerFollow'" />
      <CustomerPutPoolRemindList v-if="leftMenu === 'customerPutPoolRemind'" />
      <ReceivablePlanRemindList v-if="leftMenu === 'receivablePlanRemind'" />
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import CustomerFollowList from './components/CustomerFollowList.vue'
import CustomerTodayContactList from './components/CustomerTodayContactList.vue'
import CustomerPutPoolRemindList from './components/CustomerPutPoolRemindList.vue'
import ClueFollowList from './components/ClueFollowList.vue'
import ContractAuditList from './components/ContractAuditList.vue'
import ContractRemindList from './components/ContractRemindList.vue'
import ReceivablePlanRemindList from './components/ReceivablePlanRemindList.vue'
import ReceivableAuditList from './components/ReceivableAuditList.vue'
import * as CustomerApi from '@/api/crm/customer'
import * as ClueApi from '@/api/crm/clue'
import * as ContractApi from '@/api/crm/contract'
import * as ReceivableApi from '@/api/crm/receivable'
import * as ReceivablePlanApi from '@/api/crm/receivable/plan'
import { useBadgeRefreshListener, emitBadgeRefresh } from '@/hooks/web/useCrmBadgeEvent'

defineOptions({ name: 'CrmBacklog' })

const { t } = useI18n('crm') // 国际化
const { push } = useRouter()

const leftMenu = ref('customerTodayContact')

const clueFollowCount = ref(0)
const customerFollowCount = ref(0)
const customerPutPoolRemindCount = ref(0)
const customerTodayContactCount = ref(0)
const contractAuditCount = ref(0)
const contractRemindCount = ref(0)
const receivableAuditCount = ref(0)
const receivablePlanRemindCount = ref(0)

const leftSides = computed(() => [
  {
    name: t('backlog.customerTodayContact'),
    menu: 'customerTodayContact',
    count: customerTodayContactCount.value
  },
  {
    name: t('backlog.clueFollow'),
    menu: 'clueFollow',
    count: clueFollowCount.value
  },
  {
    name: t('backlog.customerFollow'),
    menu: 'customerFollow',
    count: customerFollowCount.value
  },
  {
    name: t('backlog.customerPutPoolRemind'),
    menu: 'customerPutPoolRemind',
    count: customerPutPoolRemindCount.value
  },
  {
    name: t('backlog.contractAudit'),
    menu: 'contractAudit',
    count: contractAuditCount.value
  },
  {
    name: t('backlog.receivableAudit'),
    menu: 'receivableAudit',
    count: receivableAuditCount.value
  },
  {
    name: t('backlog.receivablePlanRemind'),
    menu: 'receivablePlanRemind',
    count: receivablePlanRemindCount.value
  },
  {
    name: t('backlog.contractRemind'),
    menu: 'contractRemind',
    count: contractRemindCount.value
  }
])

/** 侧边点击 */
const sideClick = (item: any) => {
  leftMenu.value = item.menu
}

const getCount = () => {
  CustomerApi.getTodayContactCustomerCount().then(
    (count) => (customerTodayContactCount.value = count)
  )
  CustomerApi.getPutPoolRemindCustomerCount().then(
    (count) => (customerPutPoolRemindCount.value = count)
  )
  CustomerApi.getFollowCustomerCount().then((count) => (customerFollowCount.value = count))
  ClueApi.getFollowClueCount().then((count) => (clueFollowCount.value = count))
  ContractApi.getAuditContractCount().then((count) => (contractAuditCount.value = count))
  ContractApi.getRemindContractCount().then((count) => (contractRemindCount.value = count))
  ReceivableApi.getAuditReceivableCount().then((count) => (receivableAuditCount.value = count))
  ReceivablePlanApi.getReceivablePlanRemindCount().then(
    (count) => (receivablePlanRemindCount.value = count)
  )
}

/** 刷新所有计数并通知菜单红点更新 */
const refreshAllCounts = async () => {
  await Promise.all([
    CustomerApi.getTodayContactCustomerCount().then(
      (count) => (customerTodayContactCount.value = count)
    ),
    CustomerApi.getPutPoolRemindCustomerCount().then(
      (count) => (customerPutPoolRemindCount.value = count)
    ),
    CustomerApi.getFollowCustomerCount().then((count) => (customerFollowCount.value = count)),
    ClueApi.getFollowClueCount().then((count) => (clueFollowCount.value = count)),
    ContractApi.getAuditContractCount().then((count) => (contractAuditCount.value = count)),
    ContractApi.getRemindContractCount().then((count) => (contractRemindCount.value = count)),
    ReceivableApi.getAuditReceivableCount().then((count) => (receivableAuditCount.value = count)),
    ReceivablePlanApi.getReceivablePlanRemindCount().then(
      (count) => (receivablePlanRemindCount.value = count)
    )
  ])
  // 通知菜单红点同步刷新
  emitBadgeRefresh()
}

// 提供给子组件调用的刷新函数
provide('refreshBacklogCount', refreshAllCounts)

// 监听外部触发的 badge 刷新事件（如从其他页面操作后返回）
useBadgeRefreshListener(() => {
  getCount()
})

/** 激活时 */
onActivated(async () => {
  getCount()
})

/** 初始化 */
onMounted(async () => {
  getCount()
})
</script>

<style lang="scss" scoped>
.side-item-list {
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 1;
  font-size: 14px;
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  border-radius: 5px;

  .side-item {
    position: relative;
    height: 50px;
    padding: 0 20px;
    line-height: 50px;
    cursor: pointer;
  }
}

.side-item-default {
  color: var(--el-text-color-primary);
  border-right: 2px solid transparent;
}

.side-item-select {
  color: var(--el-color-primary);
  background-color: var(--el-color-primary-light-9);
  border-right: 2px solid var(--el-color-primary);
}

.el-badge :deep(.el-badge__content) {
  top: 0;
  border: none;
}

.el-badge {
  position: absolute;
  top: 0;
  right: 15px;
}
</style>