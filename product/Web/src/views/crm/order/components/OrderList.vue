<template>
  <!-- 操作栏 -->
  <el-row justify="end">
    <el-button @click="openForm">
      <Icon class="mr-5px" icon="clarity:contract-line" />
      {{ t('crm.order.createOrder') }}
    </el-button>
  </el-row>

  <!-- 列表 -->
  <ContentWrap class="mt-10px">
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column :label="t('crm.order.name')" fixed="left" align="center" prop="name">
        <template #default="scope">
          <el-link type="primary" :underline="false" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column :label="t('crm.order.no')" align="center" prop="no" />
      <el-table-column :label="t('crm.order.customerName')" align="center" prop="customerName" />
      <el-table-column
        :label="t('crm.order.totalPrice') + '（元）'"
        align="center"
        prop="totalPrice"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        :label="t('crm.order.startTime')"
        align="center"
        prop="startTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column
        :label="t('crm.order.endTime')"
        align="center"
        prop="endTime"
        :formatter="dateFormatter"
        min-width="180"
      />
      <el-table-column align="center" :label="t('common.status')" prop="auditStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.CRM_AUDIT_STATUS" :value="scope.row.auditStatus" />
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

  <!-- 表单弹窗：添加 -->
  <OrderForm ref="formRef" @success="getList" />
</template>
<script setup lang="ts">
import * as OrderApi from '@/api/crm/order'
import OrderForm from './../OrderForm.vue'
import { BizTypeEnum } from '@/api/crm/permission'
import { dateFormatter } from '@/utils/formatTime'
import { DICT_TYPE } from '@/utils/dict'
import { erpPriceTableColumnFormatter } from '@/utils'

defineOptions({ name: 'CrmOrderList' })

const { t } = useI18n() // 国际化
const props = defineProps<{
  bizType: number // 业务类型
  bizId: number // 业务编号
}>()

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  customerId: undefined as unknown // 允许 undefined + number
})

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    // 置空参数
    queryParams.customerId = undefined
    // 执行查询
    let data = { list: [], total: 0 }
    switch (props.bizType) {
      case BizTypeEnum.CRM_CUSTOMER:
        queryParams.customerId = props.bizId
        data = await OrderApi.getOrderPageByCustomer(queryParams)
        break
      case BizTypeEnum.CRM_BUSINESS:
        queryParams.businessId = props.bizId
        data = await OrderApi.getOrderPageByBusiness(queryParams)
        break
      default:
        return
    }
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 添加 */
const formRef = ref()
const openForm = () => {
  formRef.value.open('create')
}

/** 打开订单详情 */
const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'CrmOrderDetail', params: { id } })
}

/** 监听打开的 bizId + bizType，从而加载最新的列表 */
watch(
  () => [props.bizId, props.bizType],
  () => {
    handleQuery()
  },
  { immediate: true, deep: true }
)
</script>
