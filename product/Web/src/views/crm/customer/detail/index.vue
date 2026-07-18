<template>
  <CustomerDetailsHeader :customer="customer" :loading="loading">
    <!-- 回收倒计时状态栏 -->
    <div v-if="customer.ownerUserId && customer.poolRemainDays !== undefined && customer.poolRemainDays !== null" class="recovery-status-bar">
      <span v-if="customer.recoveryPaused" class="recovery-paused-tag">
        <Icon icon="ep:pause" :size="14" />
        {{ t('recoveryPaused') }}
        <el-tooltip v-if="customer.pauseReason" :content="customer.pauseReason" placement="top">
          <Icon icon="ep:question-filled" :size="14" class="ml-4px" />
        </el-tooltip>
      </span>
      <span v-else class="recovery-countdown" :class="customer.poolRemainDays <= 3 ? 'text-red-500 font-bold' : ''">
        <Icon icon="ep:clock" :size="14" />
        {{ t('recoveryCountdownStatus') }}：{{ t('daysLater', { day: customer.poolRemainDays }) }}
      </span>
    </div>
    <el-button
      v-if="permissionListRef?.validateWrite"
      v-hasPermi="['crm:customer:update']"
      type="primary"
      @click="openForm"
    >
      {{ t('customer.edit') }}
    </el-button>
    <el-button v-if="permissionListRef?.validateOwnerUser" type="primary" @click="transfer">
      {{ t('customer.transfer') }}
    </el-button>
    <el-button v-if="permissionListRef?.validateWrite" @click="handleUpdateDealStatus">
      {{ t('customer.changeDealStatus') }}
    </el-button>
    <el-button
      v-if="customer.lockStatus && permissionListRef?.validateOwnerUser"
      @click="handleUnlock"
    >
      {{ t('customer.unlock') }}
    </el-button>
    <el-button
      v-if="!customer.lockStatus && permissionListRef?.validateOwnerUser"
      @click="handleLock"
    >
      {{ t('customer.lock') }}
    </el-button>
    <el-button v-if="!customer.ownerUserId" type="primary" @click="handleReceive"> {{ t('customer.receive') }}</el-button>
    <el-button v-if="!customer.ownerUserId" type="primary" @click="handleDistributeForm">
      {{ t('customer.assign') }}
    </el-button>
    <!-- 归还公海按钮 -->
    <el-button
      v-if="customer.ownerUserId && permissionListRef?.validateOwnerUser"
      type="warning"
      @click="handleReturnToPool"
    >
      {{ t('returnToPool') }}
    </el-button>
    <el-button
      v-if="customer.ownerUserId && permissionListRef?.validateOwnerUser"
      @click="handlePutPool"
    >
      {{ t('customer.putPool') }}
    </el-button>
  </CustomerDetailsHeader>
  <el-row class="mb-15px" v-if="customer.tags || allTags.length > 0">
    <el-col :span="24">
      <div class="tag-section">
        <span class="tag-section-label">{{ t('customer.tags') }}：</span>
        <el-tag
          v-for="tag in customer.tags"
          :key="tag.id"
          :color="tag.color"
          class="mr-8px mb-4px"
          closable
          size="default"
          @close="handleRemoveTag(tag.id)"
        >
          {{ tag.name }}
        </el-tag>
        <el-dropdown
          v-if="permissionListRef?.validateWrite"
          trigger="click"
          @command="handleAddTag"
        >
          <el-button size="small" type="primary" link>
            <Icon icon="ep:plus" /> {{ t('customer.addTag') }}
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="tag in availableTags"
                :key="tag.id"
                :command="tag.id"
              >
                <el-tag :color="tag.color" size="small">{{ tag.name }}</el-tag>
              </el-dropdown-item>
              <el-dropdown-item v-if="availableTags.length === 0" disabled>
                {{ t('customer.noMoreTags') }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-col>
  </el-row>
  <el-col>
    <el-tabs>
      <el-tab-pane :label="t('customer.followUpTab')">
        <FollowUpList :biz-id="customerId" :biz-type="BizTypeEnum.CRM_CUSTOMER" />
      </el-tab-pane>
      <el-tab-pane :label="t('customer.basicInfoTab')">
        <CustomerDetailsInfo :customer="customer" />
      </el-tab-pane>
      <el-tab-pane :label="t('customer.contactTab')" lazy>
        <ContactList
          :biz-id="customer.id!"
          :customer-id="customer.id!"
          :biz-type="BizTypeEnum.CRM_CUSTOMER"
        />
      </el-tab-pane>
      <el-tab-pane :label="t('customer.teamMemberTab')">
        <PermissionList
          ref="permissionListRef"
          :biz-id="customer.id!"
          :biz-type="BizTypeEnum.CRM_CUSTOMER"
          :show-action="!permissionListRef?.isPool || false"
          @quit-team="close"
        />
      </el-tab-pane>
      <el-tab-pane :label="t('customer.businessTab')" lazy>
        <BusinessList
          :biz-id="customer.id!"
          :customer-id="customer.id!"
          :biz-type="BizTypeEnum.CRM_CUSTOMER"
        />
      </el-tab-pane>
      <el-tab-pane :label="t('customer.contractTab')" lazy>
        <ContractList :biz-id="customer.id!" :biz-type="BizTypeEnum.CRM_CUSTOMER" />
      </el-tab-pane>
      <el-tab-pane :label="t('customer.receivableTab')" lazy>
        <ReceivablePlanList :customer-id="customer.id!" @create-receivable="createReceivable" />
        <ReceivableList ref="receivableListRef" :customer-id="customer.id!" />
      </el-tab-pane>
      <el-tab-pane :label="t('customer.orderTab')" lazy>
        <OrderList :biz-type="BizTypeEnum.CRM_CUSTOMER" :biz-id="customer.id!" />
      </el-tab-pane>
      <el-tab-pane :label="t('customer.taskTab')" lazy>
        <TaskList :customer-id="customer.id!" />
      </el-tab-pane>
      <el-tab-pane :label="t('customer.attachmentTab')" lazy>
        <AttachmentList :customer-id="customer.id!" />
      </el-tab-pane>
      <!-- 公海流转记录 Tab -->
      <el-tab-pane :label="t('poolFlowRecordTab')">
        <SeaPoolFlowRecord :customer-id="customerId" />
      </el-tab-pane>
      <el-tab-pane :label="t('customer.operateLogTab')">
        <OperateLogV2 :log-list="logList" />
      </el-tab-pane>
    </el-tabs>
  </el-col>

  <!-- 表单弹窗：添加/修改 -->
  <CustomerForm ref="formRef" @success="getCustomer" />
  <CustomerDistributeForm ref="distributeForm" @success="getCustomer" />
  <CrmTransferForm ref="transferFormRef" :biz-type="BizTypeEnum.CRM_CUSTOMER" @success="close" />

  <!-- 归还公海原因弹窗 -->
  <el-dialog v-model="returnToPoolDialogVisible" :title="t('returnToPool')" width="480px">
    <el-form ref="returnToPoolFormRef" :model="returnToPoolForm" :rules="returnToPoolRules" label-width="auto">
      <el-form-item :label="t('returnToPoolReason')" prop="reason">
        <el-select v-model="returnToPoolForm.reason" class="w-1/1" :placeholder="t('returnToPoolReasonPlaceholder')">
          <el-option :label="t('returnReasonCannotContact')" value="无法联系" />
          <el-option :label="t('returnReasonNoIntention')" value="无意向" />
          <el-option :label="t('returnReasonDealed')" value="已成交" />
          <el-option :label="t('returnReasonOther')" value="其他" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="returnToPoolLoading" type="primary" @click="confirmReturnToPool">
        {{ t('common.confirm') }}
      </el-button>
      <el-button @click="returnToPoolDialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </el-dialog>
</template>
<script lang="ts" setup>
import { useTagsViewStore } from '@/store/modules/tagsView'
import * as CustomerApi from '@/api/crm/customer'
import * as SeaPoolApi from '@/api/crm/seapool'
import * as TagApi from '@/api/crm/tag'
import CustomerForm from '@/views/crm/customer/CustomerForm.vue'
import CustomerDetailsInfo from './CustomerDetailsInfo.vue' // 客户明细 - 详细信息
import CustomerDetailsHeader from './CustomerDetailsHeader.vue' // 客户明细 - 头部
import SeaPoolFlowRecord from './SeaPoolFlowRecord.vue' // 公海流转记录
import ContactList from '@/views/crm/contact/components/ContactList.vue' // 联系人列表
import ContractList from '@/views/crm/contract/components/ContractList.vue' // 合同列表
import BusinessList from '@/views/crm/business/components/BusinessList.vue' // 商机列表
import ReceivableList from '@/views/crm/receivable/components/ReceivableList.vue' // 回款列表
import ReceivablePlanList from '@/views/crm/receivable/plan/components/ReceivablePlanList.vue' // 回款计划列表
import PermissionList from '@/views/crm/permission/components/PermissionList.vue' // 团队成员列表（权限）
import CrmTransferForm from '@/views/crm/permission/components/TransferForm.vue'
import FollowUpList from '@/views/crm/followup/index.vue'
import OrderList from '@/views/crm/order/components/OrderList.vue' // 订单列表
import TaskList from './TaskList.vue' // 任务列表
import AttachmentList from './AttachmentList.vue' // 附件列表
import { BizTypeEnum } from '@/api/crm/permission'
import type { OperateLogVO } from '@/api/system/operatelog'
import { getOperateLogPage } from '@/api/crm/operateLog'
import CustomerDistributeForm from '@/views/crm/customer/pool/CustomerDistributeForm.vue'

defineOptions({ name: 'CrmCustomerDetail' })

const customerId = ref(0) // 客户编号
const loading = ref(true) // 加载中
const message = useMessage() // 消息弹窗
const { t } = useI18n('crm') // 国际化
const { delView } = useTagsViewStore() // 视图操作
const { push, currentRoute } = useRouter() // 路由

const permissionListRef = ref<InstanceType<typeof PermissionList>>() // 团队成员列表 Ref

/** 获取详情 */
const customer = ref<CustomerApi.CustomerVO>({} as CustomerApi.CustomerVO) // 客户详情
const getCustomer = async () => {
  loading.value = true
  try {
    customer.value = await CustomerApi.getCustomer(customerId.value)
    await getOperateLog()
    await loadCustomerTags()
  } finally {
    loading.value = false
  }
}

/** 标签相关 */
const allTags = ref<TagApi.TagVO[]>([]) // 所有标签
const customerTags = ref<TagApi.TagVO[]>([]) // 当前客户标签

// 可添加的标签（未被打标的）
const availableTags = computed(() => {
  const existingIds = new Set((customer.value.tags || []).map((t: TagApi.TagVO) => t.id))
  return allTags.value.filter((t) => !existingIds.has(t.id))
})

const loadCustomerTags = async () => {
  try {
    customerTags.value = await TagApi.getCustomerTags(customerId.value)
    customer.value.tags = customerTags.value
  } catch {
    customer.value.tags = []
  }
}

const loadAllTags = async () => {
  try {
    allTags.value = await TagApi.getAllTags()
  } catch {
    allTags.value = []
  }
}

const handleRemoveTag = async (tagId: number) => {
  try {
    await TagApi.removeCustomerTag(customerId.value, tagId)
    message.success(t('common.delSuccess'))
    await loadCustomerTags()
  } catch {}
}

const handleAddTag = async (tagId: number) => {
  try {
    await TagApi.addCustomerTags(customerId.value, [tagId])
    message.success(t('common.createSuccess'))
    await loadCustomerTags()
  } catch {}
}

/** 编辑客户 */
const formRef = ref<InstanceType<typeof CustomerForm>>() // 客户表单 Ref
const openForm = () => {
  formRef.value?.open('update', customerId.value)
}

/** 更新成交状态操作 */
const handleUpdateDealStatus = async () => {
  const dealStatus = !customer.value.dealStatus
  try {
    // 更新状态的二次确认
    await message.confirm(t('customer.updateDealStatusConfirm', { status: dealStatus ? t('customer.dealStatusYes') : t('customer.dealStatusNo') }))
    // 发起更新
    await CustomerApi.updateCustomerDealStatus(customerId.value, dealStatus)
    message.success(t('customer.updateDealStatusSuccess'))
    // 刷新数据
    await getCustomer()
  } catch {}
}

/** 客户转移 */
const transferFormRef = ref<InstanceType<typeof CrmTransferForm>>() // 客户转移表单 ref
const transfer = () => {
  transferFormRef.value?.open(customerId.value)
}

/** 锁定客户 */
const handleLock = async () => {
  await message.confirm(t('customer.lockConfirm', { name: customer.value.name }))
  await CustomerApi.lockCustomer(unref(customerId.value), true)
  message.success(t('customer.lockSuccess', { name: customer.value.name }))
  await getCustomer()
}

/** 解锁客户 */
const handleUnlock = async () => {
  await message.confirm(t('customer.unlockConfirm', { name: customer.value.name }))
  await CustomerApi.lockCustomer(unref(customerId.value), false)
  message.success(t('customer.unlockSuccess', { name: customer.value.name }))
  await getCustomer()
}

/** 领取客户 */
const handleReceive = async () => {
  await message.confirm(t('customer.receiveConfirm', { name: customer.value.name }))
  await CustomerApi.receiveCustomer([unref(customerId.value)])
  message.success(t('customer.receiveSuccess', { name: customer.value.name }))
  await getCustomer()
}

/** 分配客户 */
const distributeForm = ref<InstanceType<typeof CustomerDistributeForm>>() // 分配客户表单 Ref
const handleDistributeForm = async () => {
  distributeForm.value?.open(customerId.value)
}

/** 客户放入公海 */
const handlePutPool = async () => {
  await message.confirm(t('customer.putPoolConfirm', { name: customer.value.name }))
  await CustomerApi.putCustomerPool(unref(customerId.value))
  message.success(t('customer.putPoolSuccess', { name: customer.value.name }))
  // 加载
  close()
}

// ===== 归还公海 =====
const returnToPoolDialogVisible = ref(false)
const returnToPoolLoading = ref(false)
const returnToPoolFormRef = ref()
const returnToPoolForm = ref({
  reason: ''
})
const returnToPoolRules = {
  reason: [{ required: true, message: '请选择归还原因', trigger: 'change' }]
}

/** 打开归还公海弹窗 */
const handleReturnToPool = () => {
  returnToPoolForm.value.reason = ''
  returnToPoolDialogVisible.value = true
}

/** 确认归还公海 */
const confirmReturnToPool = async () => {
  if (!returnToPoolFormRef.value) return
  const valid = await returnToPoolFormRef.value.validate()
  if (!valid) return
  returnToPoolLoading.value = true
  try {
    await message.confirm(t('customer.returnToPoolConfirm', { name: customer.value.name }))
    await SeaPoolApi.returnCustomerToPool(unref(customerId.value), returnToPoolForm.value.reason)
    message.success(t('customer.returnToPoolSuccess'))
    returnToPoolDialogVisible.value = false
    close()
  } catch {
  } finally {
    returnToPoolLoading.value = false
  }
}

/** 获取操作日志 */
const logList = ref<OperateLogVO[]>([]) // 操作日志列表
const getOperateLog = async () => {
  if (!customerId.value) {
    return
  }
  const data = await getOperateLogPage({
    bizType: BizTypeEnum.CRM_CUSTOMER,
    bizId: customerId.value
  })
  logList.value = data.list
}

/** 从回款计划创建回款 */
const receivableListRef = ref<InstanceType<typeof ReceivableList>>() // 回款列表 Ref
const createReceivable = (planData: any) => {
  receivableListRef.value?.createReceivable(planData)
}

const close = () => {
  delView(unref(currentRoute))
  push({ name: 'CrmCustomer' })
}

/** 初始化 */
const { params } = useRoute()
onMounted(() => {
  if (!params.id) {
    message.warning(t('customer.paramError'))
    close()
    return
  }
  customerId.value = params.id as unknown as number
  loadAllTags()
  getCustomer()
})
</script>

<style lang="scss" scoped>
.recovery-status-bar {
  display: inline-flex;
  align-items: center;
  margin-right: 12px;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 13px;
  background-color: var(--el-fill-color-light);
}

.recovery-countdown {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--el-text-color-regular);
}

.recovery-paused-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--el-color-warning);
  font-weight: 500;
}
</style>