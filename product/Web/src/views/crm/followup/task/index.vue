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
          <el-form-item :label="t('customer.name')" prop="customerName">
            <el-input
              v-model="queryParams.customerName"
              class="!w-240px"
              clearable
              :placeholder="t('customer.namePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('followUp.type')" prop="type">
            <el-select
              v-model="queryParams.type"
              class="!w-240px"
              clearable
              :placeholder="t('common.pleaseSelect')"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.CRM_FOLLOW_UP_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="" prop="onlyUpcoming">
            <el-checkbox v-model="queryParams.onlyUpcoming">
              {{ t('task.onlyUpcoming') }}
            </el-checkbox>
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
      <el-table-column :label="t('task.customerName')" align="center" prop="customerName" fixed="left" min-width="160">
        <template #default="scope">
          <el-link
            v-if="scope.row.customerId"
            :underline="false"
            type="primary"
            @click="openCustomerDetail(scope.row.customerId)"
          >
            {{ scope.row.customerName }}
          </el-link>
          <span v-else>{{ scope.row.customerName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="t('followUp.content')" align="center" prop="content" min-width="200" />
      <el-table-column align="center" :label="t('followUp.type')" prop="type" min-width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_FOLLOW_UP_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('followUp.contactNextTime')" prop="nextTime" min-width="180">
        <template #default="scope">
          <span :style="{ color: isOverdue(scope.row.nextTime) ? '#F56C6C' : '' }">
            {{ dateFormatter(scope.row, scope.column, scope.row.nextTime) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column :label="t('customer.ownerUserId')" align="center" prop="ownerUserName" min-width="100" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('followUp.createTime')"
        prop="createTime"
        min-width="180"
      />
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

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { FollowUpRecordApi } from '@/api/crm/followup'

defineOptions({ name: 'CrmFollowUpTask' })

const { t } = useI18n('crm') // 国际化
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = ref({
  pageNo: 1,
  pageSize: 10,
  customerName: '',
  type: undefined,
  onlyUpcoming: true
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await FollowUpRecordApi.getFollowUpRecordPage(queryParams.value)
    // 客户端筛选：任务 = 有下次跟进时间的记录
    let filtered = (data.list || []).filter((item: any) => item.nextTime)
    if (queryParams.value.onlyUpcoming) {
      const now = Date.now()
      filtered = filtered.filter((item: any) => new Date(item.nextTime).getTime() >= now)
    }
    list.value = filtered
    total.value = filtered.length
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
    customerName: '',
    type: undefined,
    onlyUpcoming: true
  }
  handleQuery()
}

/** 打开客户详情 */
const { push } = useRouter()
const openCustomerDetail = (id: number) => {
  push({ name: 'CrmCustomerDetail', params: { id } })
}

/** 判断是否逾期 */
const isOverdue = (nextTime: string | Date) => {
  if (!nextTime) return false
  return new Date(nextTime).getTime() < Date.now()
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
