<template>
  <doc-alert title="【客户】客户管理、公海客户" url="https://doc.iocoder.cn/crm/customer/" />
  <doc-alert title="【通用】数据权限" url="https://doc.iocoder.cn/crm/permission/" />

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
          <el-form-item :label="t('name')" prop="name">
            <el-input
              v-model="queryParams.name"
              class="!w-240px"
              clearable
              :placeholder="t('namePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('mobile')" prop="mobile">
            <el-input
              v-model="queryParams.mobile"
              class="!w-240px"
              clearable
              :placeholder="t('mobilePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('industryId')" prop="industryId">
            <el-select
              v-model="queryParams.industryId"
              class="!w-240px"
              clearable
              :placeholder="t('industryPlaceholder')"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.CRM_CUSTOMER_INDUSTRY)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('level')" prop="level">
            <el-select
              v-model="queryParams.level"
              class="!w-240px"
              clearable
              :placeholder="t('levelPlaceholder')"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.CRM_CUSTOMER_LEVEL)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('source')" prop="source">
            <el-select
              v-model="queryParams.source"
              class="!w-240px"
              clearable
              :placeholder="t('sourcePlaceholder')"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.CRM_CUSTOMER_SOURCE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('inflowMethod')" prop="poolEntryType">
            <el-select
              v-model="queryParams.poolEntryType"
              class="!w-240px"
              clearable
              :placeholder="t('inflowMethod')"
            >
              <el-option :label="t('inflowMethodAuto')" :value="1" />
              <el-option :label="t('inflowMethodReturn')" :value="2" />
              <el-option :label="t('inflowMethodForce')" :value="3" />
              <el-option :label="t('inflowMethodLeave')" :value="4" />
            </el-select>
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
            <el-button @click="resetQuery(undefined)">
              <Icon class="mr-5px" icon="ep:refresh" />
              {{ t('common.reset') }}
            </el-button>
            <el-button
              v-hasPermi="['crm:customer:export']"
              :loading="exportLoading"
              plain
              type="success"
              @click="handleExport"
            >
              <Icon class="mr-5px" icon="ep:download" />
              {{ t('common.export') }}
            </el-button>
            <el-button
              v-hasPermi="['crm:customer:receive']"
              :disabled="selectionList.length === 0"
              plain
              type="primary"
              @click="handleBatchReceive"
            >
              {{ t('batchReceive') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column align="center" :label="t('name')" fixed="left" prop="name" min-width="160">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
          <!-- 冷却中标签 -->
          <el-tag v-if="scope.row.coolingStatus" type="warning" size="small" class="ml-8px">
            {{ t('coolDown') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('tags')" prop="tags" min-width="180">
        <template #default="scope">
          <el-tag
            v-for="tag in scope.row.tags"
            :key="tag.id"
            :color="tag.color"
            class="mr-4px mb-2px"
            size="small"
          >
            {{ tag.name }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('source')" prop="source" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_SOURCE" :value="scope.row.source" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('recoveryCountdown')" prop="poolRemainDays" min-width="120">
        <template #default="scope">
          <span v-if="scope.row.poolRemainDays !== undefined && scope.row.poolRemainDays !== null"
                :class="scope.row.poolRemainDays <= 1 ? 'text-red-500 font-bold' : scope.row.poolRemainDays <= 3 ? 'text-yellow-500' : ''">
            {{ t('daysLater', { day: scope.row.poolRemainDays }) }}
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('returnReason')" prop="putPoolReason" min-width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.putPoolReason === '无法联系'" type="info" size="small">{{ t('returnReasonCannotContact') }}</el-tag>
          <el-tag v-else-if="scope.row.putPoolReason === '无意向'" type="warning" size="small">{{ t('returnReasonNoIntention') }}</el-tag>
          <el-tag v-else-if="scope.row.putPoolReason === '已成交'" type="success" size="small">{{ t('returnReasonDealed') }}</el-tag>
          <el-tag v-else-if="scope.row.putPoolReason === '其他'" size="small">{{ t('returnReasonOther') }}</el-tag>
          <span v-else-if="scope.row.putPoolReason">{{ scope.row.putPoolReason }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('originalOwner')" prop="previousOwnerName" min-width="100">
        <template #default="scope">
          {{ scope.row.previousOwnerName || '-' }}
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('reservationCount')" prop="reservationCount" min-width="90">
        <template #default="scope">
          <el-link v-if="scope.row.reservationCount" :underline="false" type="primary">
            {{ scope.row.reservationCount }}
          </el-link>
          <span v-else>0</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('inflowMethod')" prop="poolEntryType" min-width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.poolEntryType === 1" type="danger" size="small">{{ t('inflowMethodAuto') }}</el-tag>
          <el-tag v-else-if="scope.row.poolEntryType === 2" type="warning" size="small">{{ t('inflowMethodReturn') }}</el-tag>
          <el-tag v-else-if="scope.row.poolEntryType === 3" type="danger" size="small">{{ t('inflowMethodForce') }}</el-tag>
          <el-tag v-else-if="scope.row.poolEntryType === 4" type="info" size="small">{{ t('inflowMethodLeave') }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('inflowTime')"
        prop="poolEntryTime"
        min-width="180"
      />
      <el-table-column :label="t('mobile')" align="center" prop="mobile" min-width="120" />
      <el-table-column :label="t('telephone')" align="center" prop="telephone" min-width="130" />
      <el-table-column :label="t('email')" align="center" prop="email" min-width="180" />
      <el-table-column align="center" :label="t('level')" prop="level" min-width="135">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_LEVEL" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('industryId')" prop="industryId" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_INDUSTRY" :value="scope.row.industryId" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('contactNextTime')"
        prop="contactNextTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('remark')" prop="remark" min-width="200" />
      <el-table-column align="center" :label="t('dealStatus')" prop="dealStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="scope.row.dealStatus" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('lastContactTime')"
        prop="contactLastTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('lastContactContent')" prop="contactLastContent" min-width="200" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('common.updateTime')"
        prop="updateTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('common.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('common.creator')" prop="creatorName" min-width="100" />
      <!-- 操作列：领取 -->
      <el-table-column align="center" :label="t('common.action')" fixed="right" min-width="120">
        <template #default="scope">
          <el-tooltip
            v-if="isClaimLimitReached"
            :content="t('claimLimitReached', { current: dailyClaimCount.dailyCustomerClaimCount, max: dailyClaimCount.dailyCustomerLimit })"
            placement="top"
          >
            <el-button type="primary" disabled size="small">
              {{ t('receive') }}
            </el-button>
          </el-tooltip>
          <el-tooltip
            v-else-if="scope.row.coolingStatus"
            :content="t('coolDown')"
            placement="top"
          >
            <el-button type="primary" disabled size="small">
              {{ t('receive') }}
            </el-button>
          </el-tooltip>
          <el-button
            v-else
            v-hasPermi="['crm:customer:receive']"
            type="primary"
            size="small"
            @click="handleReceiveCustomer(scope.row)"
          >
            {{ t('receive') }}
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
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as CustomerApi from '@/api/crm/customer'
import * as SeaPoolApi from '@/api/crm/seapool'
import type { DailyClaimCountVO } from '@/api/crm/seapool'

defineOptions({ name: 'CrmCustomerPool' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('crm.customer') // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = ref({
  pageNo: 1,
  pageSize: 10,
  name: '',
  mobile: '',
  industryId: undefined,
  level: undefined,
  source: undefined,
  sceneType: undefined,
  pool: true,
  poolEntryType: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const selectionList = ref<any[]>([]) // 选中的行数据

// ===== 公海领取限制 =====
const dailyClaimCount = ref<DailyClaimCountVO>({
  dailyClueClaimCount: 0,
  dailyClueLimit: 0,
  dailyCustomerClaimCount: 0,
  dailyCustomerLimit: 0
})

/** 是否达到领取上限 */
const isClaimLimitReached = computed(() => {
  const { dailyCustomerClaimCount, dailyCustomerLimit } = dailyClaimCount.value
  return dailyCustomerLimit > 0 && dailyCustomerClaimCount >= dailyCustomerLimit
})

/** 获取当日领取统计 */
const loadDailyClaimCount = async () => {
  try {
    dailyClaimCount.value = await SeaPoolApi.getDailyClaimCount()
  } catch {
    // 降级处理：不阻塞列表加载
  }
}

/** 多选操作 */
const handleSelectionChange = (rows: any[]) => {
  selectionList.value = rows
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CustomerApi.getCustomerPage(queryParams.value)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
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
    name: '',
    mobile: '',
    industryId: undefined,
    level: undefined,
    source: undefined,
    sceneType: undefined,
    pool: true,
    poolEntryType: undefined
  }
  handleQuery()
}

/** 打开客户详情 */
const { currentRoute, push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'CrmCustomerDetail', params: { id } })
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await CustomerApi.exportCustomer(queryParams.value)
    download.excel(data, t('poolExportFileName') + '.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 监听路由变化更新列表 */
watch(
  () => currentRoute.value,
  () => {
    getList()
  }
)

/** 单个领取客户 */
const handleReceiveCustomer = async (row: any) => {
  try {
    await message.confirm(t('receiveConfirm', { name: row.name }))
    await CustomerApi.receiveCustomer([row.id])
    message.success(t('receiveSuccess', { name: row.name }))
    // 刷新领取统计和列表
    await loadDailyClaimCount()
    await getList()
  } catch {}
}

/** 批量领取 */
const handleBatchReceive = async () => {
  try {
    const ids = selectionList.value.map((row: any) => row.id)
    await message.confirm(t('batchReceiveConfirm', { count: ids.length }))
    await CustomerApi.receiveCustomer(ids)
    message.success(t('common.batchActionSuccess'))
    await loadDailyClaimCount()
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
  loadDailyClaimCount()
})
</script>