<!-- 待进入公海的客户 -->
<template>
  <ContentWrap>
    <div class="pb-5 text-xl">{{ t('backlog.customerPutPoolRemind') }}</div>
    <!-- 搜索工作区 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item :label="t('backlog.sceneType')" prop="sceneType">
        <el-select
          v-model="queryParams.sceneType"
          class="!w-240px"
          :placeholder="t('backlog.sceneType')"
          @change="handleQuery"
        >
          <el-option
            v-for="(option, index) in SCENE_TYPES"
            :label="option.label"
            :value="option.value"
            :key="index"
          />
        </el-select>
      </el-form-item>
    </el-form>
  </ContentWrap>
  <ContentWrap>
    <!-- 批量操作按钮 -->
    <div class="mb-10px">
      <el-button
        :disabled="selectionList.length === 0"
        plain
        type="primary"
        @click="handleBatchExtension"
      >
        {{ t('backlog.batchExtension') }}
      </el-button>
    </div>
    <el-table
      v-loading="loading"
      :data="list"
      :show-overflow-tooltip="true"
      :stripe="true"
      :table-layout="'auto'"
      @selection-change="handleSelectionChange"
    >
      <!-- 多选列 -->
      <el-table-column type="selection" width="55" />
      <el-table-column align="center" :label="t('customer.name')" fixed="left" prop="name" min-width="160">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('customer.source')" prop="source" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_SOURCE" :value="scope.row.source" />
        </template>
      </el-table-column>
      <el-table-column :label="t('customer.mobile')" align="center" prop="mobile" min-width="120" />
      <el-table-column :label="t('customer.telephone')" align="center" prop="telephone" min-width="130" />
      <el-table-column :label="t('customer.email')" align="center" prop="email" min-width="180" />
      <el-table-column align="center" :label="t('customer.level')" prop="level" min-width="135">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_LEVEL" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('customer.industryId')" prop="industryId" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_INDUSTRY" :value="scope.row.industryId" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('customer.contactNextTime')"
        prop="contactNextTime"
        min-width="180"
      />
      <!-- 剩余天数 -->
      <el-table-column align="center" :label="t('backlog.remainingDays')" prop="remainingDays" min-width="120">
        <template #default="scope">
          <span
            :class="{
              'text-red-500 font-bold': scope.row.remainingDays <= 1,
              'text-yellow-500 font-bold': scope.row.remainingDays > 1 && scope.row.remainingDays <= 3,
              'text-green-500': scope.row.remainingDays > 3
            }"
          >
            {{ scope.row.remainingDays }}{{ t('customer.dayUnit') }}
          </span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('customer.remark')" prop="remark" min-width="200" />
      <el-table-column align="center" :label="t('customer.lockStatus')" prop="lockStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="scope.row.lockStatus" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('customer.dealStatus')" prop="dealStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="scope.row.dealStatus" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('customer.lastContactTime')"
        prop="contactLastTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('customer.lastContactContent')" prop="contactLastContent" min-width="200" />
      <el-table-column :label="t('customer.detailAddress')" align="center" prop="detailAddress" min-width="180" />
      <el-table-column align="center" :label="t('customer.poolDay')" prop="poolDay" min-width="140">
        <template #default="scope"> {{ scope.row.poolDay }} {{ t('customer.dayUnit') }}</template>
      </el-table-column>
      <el-table-column align="center" :label="t('customer.ownerUserId')" prop="ownerUserName" min-width="100" />
      <el-table-column align="center" :label="t('customer.ownerUserDeptName')" prop="ownerUserDeptName" min-width="100" />
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
      <!-- 操作列 -->
      <el-table-column align="center" :label="t('common.action')" min-width="180" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            :disabled="scope.row.extensionCount >= 3"
            @click="handleExtension(scope.row)"
          >
            {{ t('backlog.extension') }}
          </el-button>
          <el-button link type="primary" @click="handleFollowUp(scope.row)">
            {{ t('backlog.followUp') }}
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

  <!-- 延期弹窗 -->
  <el-dialog
    v-model="extensionDialogVisible"
    :title="t('backlog.extensionDays')"
    width="400px"
    append-to-body
  >
    <el-radio-group v-model="extensionDays" class="flex flex-col gap-3">
      <el-radio :value="7">{{ t('backlog.extensionDays7') }}</el-radio>
      <el-radio :value="15">{{ t('backlog.extensionDays15') }}</el-radio>
      <el-radio :value="30">{{ t('backlog.extensionDays30') }}</el-radio>
    </el-radio-group>
    <template #footer>
      <el-button @click="extensionDialogVisible = false">{{ t('common.cancel') }}</el-button>
      <el-button type="primary" :loading="extensionLoading" @click="confirmExtension">
        {{ t('common.confirm') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import * as CustomerApi from '@/api/crm/customer'
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { SCENE_TYPES } from './common'

defineOptions({ name: 'CrmCustomerPutPoolRemindList' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('crm') // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = ref({
  pageNo: 1,
  pageSize: 10,
  sceneType: 1, // 我负责的
  pool: true // 固定 公海参数为 true
})
const queryFormRef = ref() // 搜索的表单

// 多选
const selectionList = ref<any[]>([])

/** 多选操作 */
const handleSelectionChange = (rows: any[]) => {
  selectionList.value = rows
}

// 延期弹窗
const extensionDialogVisible = ref(false)
const extensionDays = ref(7)
const extensionLoading = ref(false)
const currentExtensionRow = ref<any>(null) // 当前单个延期的行
const isBatchExtension = ref(false) // 是否批量延期

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CustomerApi.getPutPoolRemindCustomerPage(queryParams.value)
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

/** 打开客户详情 */
const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'CrmCustomerDetail', params: { id } })
}

/** 一键延期：打开延期弹窗 */
const handleExtension = (row: any) => {
  if (row.extensionCount >= 3) {
    message.warning(t('backlog.extensionMaxReached'))
    return
  }
  currentExtensionRow.value = row
  isBatchExtension.value = false
  extensionDays.value = 7
  extensionDialogVisible.value = true
}

/** 立即跟进：跳转至客户详情页跟进记录Tab */
const handleFollowUp = (row: any) => {
  push({ name: 'CrmCustomerDetail', params: { id: row.id }, query: { tab: 'followUp' } })
}

/** 批量延期 */
const handleBatchExtension = () => {
  if (selectionList.value.length === 0) {
    message.warning('请先选择需要延期的客户')
    return
  }
  currentExtensionRow.value = null
  isBatchExtension.value = true
  extensionDays.value = 7
  extensionDialogVisible.value = true
}

/** 确认延期 */
const confirmExtension = async () => {
  try {
    extensionLoading.value = true
    if (isBatchExtension.value) {
      const ids = selectionList.value.map((row: any) => row.id)
      await message.confirm(t('backlog.extensionConfirm', { day: extensionDays.value }))
      await CustomerApi.batchExtendPutPoolRemind(ids, extensionDays.value)
      message.success(t('backlog.extensionSuccess'))
      selectionList.value = []
    } else {
      const row = currentExtensionRow.value
      await message.confirm(t('backlog.extensionConfirm', { day: extensionDays.value }))
      await CustomerApi.extendPutPoolRemind(row.id, extensionDays.value)
      message.success(t('backlog.extensionSuccess'))
    }
    extensionDialogVisible.value = false
    await getList()
  } catch {
    // 用户取消或请求失败
  } finally {
    extensionLoading.value = false
  }
}

/** 激活时 */
onActivated(async () => {
  await getList()
})

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.text-red-500 {
  color: #ef4444;
}
.text-yellow-500 {
  color: #eab308;
}
.text-green-500 {
  color: #22c55e;
}
</style>