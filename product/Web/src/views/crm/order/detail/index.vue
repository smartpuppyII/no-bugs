<!-- 订单详情页面组件-->
<template>
  <OrderDetailsHeader v-loading="loading" :order="order">
    <el-button v-if="permissionListRef?.validateWrite" @click="openForm('update', order.id)">
      {{ t('common.edit') }}
    </el-button>
    <el-button v-if="permissionListRef?.validateOwnerUser" type="primary" @click="transferOrder">
      {{ t('crm.customer.transfer') }}
    </el-button>
  </OrderDetailsHeader>
  <el-col>
    <el-tabs>
      <el-tab-pane :label="t('crm.order.followUpTab')">
        <FollowUpList :biz-id="order.id" :biz-type="BizTypeEnum.CRM_CONTRACT" />
      </el-tab-pane>
      <el-tab-pane :label="t('crm.order.basicInfoTab')">
        <OrderDetailsInfo :order="order" />
      </el-tab-pane>
      <el-tab-pane :label="t('crm.order.teamMemberTab')">
        <PermissionList
          ref="permissionListRef"
          :biz-id="order.id!"
          :biz-type="BizTypeEnum.CRM_CONTRACT"
          :show-action="true"
          @quit-team="close"
        />
      </el-tab-pane>
      <el-tab-pane :label="t('crm.order.operateLogTab')">
        <OperateLogV2 :log-list="logList" />
      </el-tab-pane>
    </el-tabs>
  </el-col>

  <!-- 表单弹窗：添加/修改 -->
  <OrderForm ref="formRef" @success="getOrderData" />
  <CrmTransferForm ref="transferFormRef" :biz-type="BizTypeEnum.CRM_CONTRACT" @success="close" />
</template>
<script lang="ts" setup>
import { useTagsViewStore } from '@/store/modules/tagsView'
import { OperateLogVO } from '@/api/system/operatelog'
import * as OrderApi from '@/api/crm/order'
import OrderDetailsInfo from './OrderDetailsInfo.vue'
import OrderDetailsHeader from './OrderDetailsHeader.vue'
import { BizTypeEnum } from '@/api/crm/permission'
import { getOperateLogPage } from '@/api/crm/operateLog'
import OrderForm from '@/views/crm/order/OrderForm.vue'
import CrmTransferForm from '@/views/crm/permission/components/TransferForm.vue'
import PermissionList from '@/views/crm/permission/components/PermissionList.vue'
import FollowUpList from '@/views/crm/followup/index.vue'

defineOptions({ name: 'CrmOrderDetail' })

const { t } = useI18n() // 国际化
const props = defineProps<{ id?: number }>()

const route = useRoute()
const message = useMessage()
const orderId = ref(0) // 编号
const loading = ref(true) // 加载中
const order = ref<OrderApi.OrderVO>({} as OrderApi.OrderVO) // 详情
const permissionListRef = ref<InstanceType<typeof PermissionList>>() // 团队成员列表 Ref

/** 编辑 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 获取详情 */
const getOrderData = async () => {
  loading.value = true
  try {
    order.value = await OrderApi.getOrder(orderId.value)
    await getOperateLog(orderId.value)
  } finally {
    loading.value = false
  }
}

/** 获取操作日志 */
const logList = ref<OperateLogVO[]>([]) // 操作日志列表
const getOperateLog = async (orderId: number) => {
  if (!orderId) {
    return
  }
  const data = await getOperateLogPage({
    bizType: BizTypeEnum.CRM_CONTRACT,
    bizId: orderId
  })
  logList.value = data.list
}

/** 转移 */
const transferFormRef = ref<InstanceType<typeof CrmTransferForm>>() // 订单转移表单 ref
const transferOrder = () => {
  transferFormRef.value?.open(order.value.id)
}

/** 关闭 */
const { delView } = useTagsViewStore() // 视图操作
const { currentRoute } = useRouter() // 路由
const close = () => {
  delView(unref(currentRoute))
}

/** 初始化 */
onMounted(async () => {
  const id = props.id || route.params.id
  if (!id) {
    message.warning(t('crm.order.paramError'))
    close()
    return
  }
  orderId.value = id as unknown as number
  await getOrderData()
})
</script>
