<template>
    <doc-alert title="【客户】客户管理、公海客户" url="https://doc.iocoder.cn/crm/customer/" />
    <doc-alert title="【通用】数据权限" url="https://doc.iocoder.cn/crm/permission/" />

    <ContentWrap>
      <el-alert
        title="客户公海"
        type="info"
        :closable="false"
        description="公海池中的客户为未被认领或已被释放的客户资源。您可以在此领取客户到自己名下。「待办事项-待进入公海的客户」可查看即将到期进入公海的客户。"
        class="mb-15px"
      />
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
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon class="mr-5px" icon="ep:search" />
              搜索
            </el-button>
            <el-button @click="resetQuery(undefined)">
              <Icon class="mr-5px" icon="ep:refresh" />
              重置
            </el-button>
            <el-button
              v-hasPermi="['crm:customer:export']"
              :loading="exportLoading"
              plain
              type="success"
              @click="handleExport"
            >
              <Icon class="mr-5px" icon="ep:download" />
              导出
            </el-button>
            <el-button
              v-hasPermi="['crm:customer:create']"
              plain
              type="primary"
              @click="openForm('create')"
            >
              <Icon class="mr-5px" icon="ep:plus" /> 新增
            </el-button>
            <el-button
              v-hasPermi="['crm:customer:import']"
              plain
              type="success"
              @click="openImportForm"
            >
              <Icon class="mr-5px" icon="ep:upload" /> 导入
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
        :label="$t('common.updateTime')"
        prop="updateTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="$t('common.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="$t('common.creator')" prop="creatorName" min-width="100" />
      <el-table-column label="操作" align="center" fixed="right" width="200">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row.id)">查看</el-button>
          <el-button link type="success" v-hasPermi="['crm:customer:receive']" @click="handleReceive(scope.row)">
            {{ t('receive') }}
          </el-button>
          <el-button link type="danger" v-hasPermi="['crm:customer:delete']" @click="handleDelete(scope.row.id)">
            删除
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

  <!-- 弹窗 -->
  <CustomerForm ref="formRef" @success="getList" />
  <CustomerImportForm ref="importFormRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as CustomerApi from '@/api/crm/customer'
import CustomerForm from '../CustomerForm.vue'
import CustomerImportForm from '../CustomerImportForm.vue'

defineOptions({ name: 'CrmCustomerPool' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('crm.customer') // 国际化
const { t: tg } = useI18n() // 全局国际化
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
  pool: true
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const selectionList = ref<any[]>([]) // 选中的行数据

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
    pool: true
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

/** 新增操作 */
const formRef = ref()
const openForm = (type: string) => { formRef.value.open(type) }

/** 导入操作 */
const importFormRef = ref()
const openImportForm = () => { importFormRef.value.open() }

/** 单条领取 */
const handleReceive = async (row: any) => {
  try {
    await message.confirm(t('receiveConfirm', { name: row.name }))
    await CustomerApi.receiveCustomer([row.id])
    message.success(t('receiveSuccess', { name: row.name }))
    await getList()
  } catch {}
}

/** 批量领取 */
const handleBatchReceive = async () => {
  try {
    const ids = selectionList.value.map((row: any) => row.id)
    await message.confirm(t('batchReceiveConfirm', { count: ids.length }))
    await CustomerApi.receiveCustomer(ids)
    message.success(tg('common.batchActionSuccess'))
    await getList()
  } catch {}
}

/** 单条删除 */
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await CustomerApi.deleteCustomer(id)
    message.success(tg('common.delSuccess'))
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
