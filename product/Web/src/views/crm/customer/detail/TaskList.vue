<template>
  <!-- 任务列表：基于跟进记录中设置了下次联系时间的记录 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="跟进时间"
        prop="createTime"
        min-width="170"
      />
      <el-table-column align="center" label="跟进人员" prop="creatorName" />
      <el-table-column align="center" label="跟进类型" prop="type">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_FOLLOW_UP_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="任务内容" prop="content" min-width="200" show-overflow-tooltip />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="下次联系时间"
        prop="nextTime"
        min-width="170"
      >
        <template #default="scope">
          <el-tag
            :type="isOverdue(scope.row.nextTime) ? 'danger' : 'warning'"
            size="small"
          >
            {{ formatTime(scope.row.nextTime) }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>
<script setup lang="ts">
import { FollowUpRecordApi } from '@/api/crm/followup'
import { BizTypeEnum } from '@/api/crm/permission'
import { dateFormatter } from '@/utils/formatTime'
import { DICT_TYPE } from '@/utils/dict'

defineOptions({ name: 'CrmCustomerTaskList' })

const props = defineProps<{
  customerId: number
}>()

const loading = ref(true)
const total = ref(0)
const list = ref([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10
})

const getList = async () => {
  loading.value = true
  try {
    const data = await FollowUpRecordApi.getFollowUpRecordPage({
      ...queryParams,
      bizType: BizTypeEnum.CRM_CUSTOMER,
      bizId: props.customerId
    })
    // 只展示设置了下次联系时间的记录（即任务）
    list.value = (data?.list || []).filter((item: any) => item.nextTime)
    total.value = list.value.length
  } catch {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const formatTime = (val: any) => (val ? dateFormatter(null, null, val) : '')

const isOverdue = (nextTime: string) => {
  if (!nextTime) return false
  return new Date(nextTime) < new Date()
}

watch(
  () => props.customerId,
  () => {
    queryParams.pageNo = 1
    getList()
  },
  { immediate: true }
)
</script>
