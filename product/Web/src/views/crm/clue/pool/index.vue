<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('clue.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              class="!w-240px"
              clearable
              :placeholder="t('clue.namePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon class="mr-5px" icon="ep:search" />
              {{ t('common.search') }}
            </el-button>
            <el-button @click="resetQuery">
              <Icon class="mr-5px" icon="ep:refresh" />
              {{ t('common.reset') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
      <el-table-column :label="t('clue.name')" align="center" prop="name" fixed="left" min-width="160">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column :label="t('customer.mobile')" align="center" prop="mobile" min-width="120" />
      <el-table-column :label="t('customer.telephone')" align="center" prop="telephone" min-width="130" />
      <el-table-column align="center" :label="t('customer.industryId')" prop="industryId" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_INDUSTRY" :value="scope.row.industryId" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('customer.level')" prop="level" min-width="135">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_LEVEL" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('customer.source')" prop="source" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_SOURCE" :value="scope.row.source" />
        </template>
      </el-table-column>
      <!-- 回收倒计时 -->
      <el-table-column align="center" :label="t('clue.recoveryCountdown')" prop="recoveryCountdown" min-width="140">
        <template #default="scope">
          <span
            v-if="scope.row.recoveryCountdown !== null && scope.row.recoveryCountdown !== undefined"
            :class="{ 'text-red-500 font-bold': scope.row.recoveryCountdown <= 1 }"
          >
            {{ scope.row.recoveryCountdown }}{{ t('customer.dayUnit') }}后回收
          </span>
          <span v-else class="text-gray-400">--</span>
        </template>
      </el-table-column>
      <!-- 归还原因 -->
      <el-table-column align="center" :label="t('clue.returnReason')" prop="returnReason" min-width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.returnReason" size="small" type="info">
            {{ scope.row.returnReason }}
          </el-tag>
          <span v-else class="text-gray-400">--</span>
        </template>
      </el-table-column>
      <!-- 原归属销售 -->
      <el-table-column align="center" :label="t('clue.originalOwner')" prop="originalOwnerName" min-width="120">
        <template #default="scope">
          <span v-if="scope.row.originalOwnerName">{{ scope.row.originalOwnerName }}</span>
          <span v-else class="text-gray-400">--</span>
        </template>
      </el-table-column>
      <!-- 预约人数 -->
      <el-table-column align="center" :label="t('clue.reservationCount')" prop="reservationCount" min-width="100">
        <template #default="scope">
          <el-link
            v-if="scope.row.reservationCount > 0"
            :underline="false"
            type="primary"
            @click="handleViewReservations(scope.row)"
          >
            {{ scope.row.reservationCount }}人
          </el-link>
          <span v-else class="text-gray-400">0人</span>
        </template>
      </el-table-column>
      <!-- 流入方式 -->
      <el-table-column align="center" :label="t('clue.inflowMethod')" prop="inflowMethod" min-width="110">
        <template #default="scope">
          <el-tag v-if="scope.row.inflowMethod === 'auto'" size="small" type="warning">
            {{ t('clue.inflowMethodAuto') }}
          </el-tag>
          <el-tag v-else-if="scope.row.inflowMethod === 'return'" size="small" type="info">
            {{ t('clue.inflowMethodReturn') }}
          </el-tag>
          <el-tag v-else-if="scope.row.inflowMethod === 'force'" size="small" type="danger">
            {{ t('clue.inflowMethodForce') }}
          </el-tag>
          <el-tag v-else-if="scope.row.inflowMethod === 'leave'" size="small" type="danger">
            {{ t('clue.inflowMethodLeave') }}
          </el-tag>
          <span v-else class="text-gray-400">--</span>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('clue.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column :label="t('common.action')" align="center" min-width="150" fixed="right">
        <template #default="scope">
          <el-tooltip
            v-if="isReceiveLimitReached && !isCoolDown(scope.row)"
            :content="t('clue.receiveLimitReached', { current: receiveLimitInfo.current, max: receiveLimitInfo.max })"
            placement="top"
          >
            <el-button link type="primary" disabled>
              {{ t('clue.receive') }}
            </el-button>
          </el-tooltip>
          <el-tooltip
            v-else-if="isCoolDown(scope.row)"
            :content="t('clue.coolDown')"
            placement="top"
          >
            <el-button link type="primary" disabled>
              {{ t('clue.receive') }}
            </el-button>
          </el-tooltip>
          <el-button
            v-else
            link
            type="primary"
            @click="handleReceive(scope.row)"
          >
            {{ t('clue.receive') }}
          </el-button>
          <el-button link type="primary" @click="handleDistributeForm(scope.row.id)">
            {{ t('customer.assign') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：分配 -->
  <ClueDistributeForm ref="distributeFormRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as ClueApi from '@/api/crm/clue'
import ClueDistributeForm from './ClueDistributeForm.vue'

defineOptions({ name: 'CrmCluePool' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('crm') // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = ref({
  pageNo: 1,
  pageSize: 10,
  name: ''
})
const queryFormRef = ref() // 搜索的表单

// 领取上限信息
const receiveLimitInfo = ref({
  max: 10, // 默认每日上限
  current: 0 // 当前已领取数
})

// 是否达到领取上限
const isReceiveLimitReached = computed(() => {
  return receiveLimitInfo.value.current >= receiveLimitInfo.value.max
})

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ClueApi.getCluePoolPage(queryParams.value)
    list.value = data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

/** 获取领取上限信息 */
const getReceiveLimit = async () => {
  try {
    const data = await ClueApi.getClueReceiveLimit()
    if (data) {
      receiveLimitInfo.value = {
        max: data.max || 10,
        current: data.current || 0
      }
    }
  } catch {
    // 获取失败使用默认值
  }
}

/** 判断是否处于冷却期 */
const isCoolDown = (row: any) => {
  return row.coolDown === true
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  queryParams.value = {
    pageNo: 1,
    pageSize: 10,
    name: ''
  }
  handleQuery()
}

/** 打开线索详情 */
const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'CrmClueDetail', params: { id } })
}

/** 领取操作 */
const handleReceive = async (row: any) => {
  // 领取上限校验
  if (isReceiveLimitReached.value) {
    message.warning(
      t('clue.receiveLimitReached', {
        current: receiveLimitInfo.value.current,
        max: receiveLimitInfo.value.max
      })
    )
    return
  }
  try {
    await message.confirm(t('clue.receiveConfirm', { name: row.name }))
    await ClueApi.receiveClue([row.id])
    message.success(t('clue.receiveSuccess', { name: row.name }))
    // 领取成功后刷新上限信息
    await getReceiveLimit()
    await getList()
  } catch {}
}

/** 查看预约人列表 */
const handleViewReservations = (row: any) => {
  // 跳转到详情页查看预约人信息，或打开弹窗
  push({ name: 'CrmClueDetail', params: { id: row.id }, query: { tab: 'reservations' } })
}

/** 分配操作 */
const distributeFormRef = ref<InstanceType<typeof ClueDistributeForm>>()
const handleDistributeForm = (id: number) => {
  distributeFormRef.value?.open(id)
}

/** 初始化 **/
onMounted(() => {
  getList()
  getReceiveLimit()
})
</script>

<style lang="scss" scoped>
.text-red-500 {
  color: #ef4444;
}
.text-gray-400 {
  color: #9ca3af;
}
</style>