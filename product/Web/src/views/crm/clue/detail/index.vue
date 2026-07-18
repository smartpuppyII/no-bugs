<template>
  <ClueDetailsHeader :clue="clue" :loading="loading">
    <el-button
      v-if="permissionListRef?.validateWrite"
      v-hasPermi="['crm:clue:update']"
      type="primary"
      @click="openForm"
    >
      {{ t('common.edit') }}
    </el-button>
    <el-button v-if="permissionListRef?.validateOwnerUser" type="primary" @click="transfer">
      {{ t('customer.transfer') }}
    </el-button>
    <el-button
      v-if="permissionListRef?.validateOwnerUser && !clue.transformStatus"
      type="success"
      @click="handleTransform"
    >
      {{ t('clue.transform') }}
    </el-button>
    <el-button v-if="clue.transformStatus" disabled type="success">{{ t('clue.transformStatusYes') }}</el-button>
    <!-- 归还公海按钮 -->
    <el-button
      v-if="permissionListRef?.validateOwnerUser"
      v-hasPermi="['crm:clue:update']"
      type="warning"
      @click="handleReturnToPool"
    >
      {{ t('clue.returnToPool') }}
    </el-button>
  </ClueDetailsHeader>
  <el-col>
    <el-tabs>
      <el-tab-pane :label="t('clue.followUpTab')">
        <FollowUpList :biz-id="clueId" :biz-type="BizTypeEnum.CRM_CLUE" />
      </el-tab-pane>
      <el-tab-pane :label="t('clue.basicInfoTab')">
        <ClueDetailsInfo :clue="clue" />
      </el-tab-pane>
      <el-tab-pane :label="t('clue.teamMemberTab')">
        <PermissionList
          ref="permissionListRef"
          :biz-id="clue.id!"
          :biz-type="BizTypeEnum.CRM_CLUE"
          :show-action="true"
          @quit-team="close"
        />
      </el-tab-pane>
      <el-tab-pane :label="t('clue.poolFlowRecordTab')">
        <CluePoolFlowRecordList :clue-id="clueId" />
      </el-tab-pane>
      <el-tab-pane :label="t('clue.operateLogTab')">
        <OperateLogV2 :log-list="logList" />
      </el-tab-pane>
    </el-tabs>
  </el-col>

  <!-- 表单弹窗：添加/修改 -->
  <ClueForm ref="formRef" @success="getClue" />
  <CrmTransferForm ref="transferFormRef" :biz-type="BizTypeEnum.CRM_CLUE" @success="close" />

  <!-- 归还公海弹窗 -->
  <el-dialog v-model="returnPoolDialogVisible" :title="t('clue.returnToPool')" width="480px" :close-on-click-modal="false">
    <el-form ref="returnPoolFormRef" :model="returnPoolForm" :rules="returnPoolRules" label-width="100px">
      <el-form-item :label="t('clue.returnToPoolReason')" prop="reason">
        <el-select v-model="returnPoolForm.reason" :placeholder="t('clue.returnToPoolReasonPlaceholder')" class="!w-full">
          <el-option :label="t('clue.returnReasonCannotContact')" value="cannot_contact" />
          <el-option :label="t('clue.returnReasonNoIntention')" value="no_intention" />
          <el-option :label="t('clue.returnReasonDealed')" value="dealed" />
          <el-option :label="t('clue.returnReasonOther')" value="other" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="returnPoolDialogVisible = false">{{ t('common.cancel') }}</el-button>
      <el-button type="primary" :loading="returnPoolLoading" @click="confirmReturnToPool">
        {{ t('common.confirm') }}
      </el-button>
    </template>
  </el-dialog>
</template>
<script lang="ts" setup>
import { useTagsViewStore } from '@/store/modules/tagsView'
import * as ClueApi from '@/api/crm/clue'
import ClueForm from '@/views/crm/clue/ClueForm.vue'
import ClueDetailsHeader from './ClueDetailsHeader.vue' // 线索明细 - 头部
import ClueDetailsInfo from './ClueDetailsInfo.vue' // 线索明细 - 详细信息
import PermissionList from '@/views/crm/permission/components/PermissionList.vue' // 团队成员列表（权限）
import CrmTransferForm from '@/views/crm/permission/components/TransferForm.vue'
import FollowUpList from '@/views/crm/followup/index.vue'
import CluePoolFlowRecordList from './CluePoolFlowRecordList.vue' // 公海流转记录
import { BizTypeEnum } from '@/api/crm/permission'
import type { OperateLogVO } from '@/api/system/operatelog'
import { getOperateLogPage } from '@/api/crm/operateLog'

defineOptions({ name: 'CrmClueDetail' })

const { t } = useI18n('crm') // 国际化

const clueId = ref(0) // 线索编号
const loading = ref(true) // 加载中
const message = useMessage() // 消息弹窗
const { delView } = useTagsViewStore() // 视图操作
const { currentRoute } = useRouter() // 路由

const permissionListRef = ref<InstanceType<typeof PermissionList>>() // 团队成员列表 Ref

/** 获取详情 */
const clue = ref<ClueApi.ClueVO>({} as ClueApi.ClueVO) // 线索详情
const getClue = async () => {
  loading.value = true
  try {
    clue.value = await ClueApi.getClue(clueId.value)
    await getOperateLog()
  } finally {
    loading.value = false
  }
}

/** 编辑线索 */
const formRef = ref<InstanceType<typeof ClueForm>>() // 线索表单 Ref
const openForm = () => {
  formRef.value?.open('update', clueId.value)
}

/** 线索转移 */
const transferFormRef = ref<InstanceType<typeof CrmTransferForm>>() // 线索转移表单 ref
const transfer = () => {
  transferFormRef.value?.open(clueId.value)
}

/** 转化为客户 */
const handleTransform = async () => {
  await message.confirm(t('clue.transformConfirm', { name: clue.value.name }))
  await ClueApi.transformClue(clueId.value)
  message.success(t('clue.transformSuccess'))
  await getClue()
}

/** 归还公海 */
const returnPoolDialogVisible = ref(false)
const returnPoolLoading = ref(false)
const returnPoolFormRef = ref()
const returnPoolForm = reactive({
  reason: ''
})
const returnPoolRules = {
  reason: [{ required: true, message: t('clue.returnToPoolReasonRequired'), trigger: 'change' }]
}
const handleReturnToPool = () => {
  returnPoolForm.reason = ''
  returnPoolDialogVisible.value = true
}
const confirmReturnToPool = async () => {
  const valid = await returnPoolFormRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    await message.confirm(t('clue.returnToPoolConfirm', { name: clue.value.name }))
    returnPoolLoading.value = true
    await ClueApi.returnClueToPool(clueId.value, returnPoolForm.reason)
    message.success(t('clue.returnToPoolSuccess'))
    returnPoolDialogVisible.value = false
    close()
  } catch {
  } finally {
    returnPoolLoading.value = false
  }
}

/** 获取操作日志 */
const logList = ref<OperateLogVO[]>([]) // 操作日志列表
const getOperateLog = async () => {
  const data = await getOperateLogPage({
    bizType: BizTypeEnum.CRM_CLUE,
    bizId: clueId.value
  })
  logList.value = data.list
}

const close = () => {
  delView(unref(currentRoute))
}

/** 初始化 */
const { params } = useRoute()
onMounted(() => {
  if (!params.id) {
    message.warning(t('clue.paramError'))
    close()
    return
  }
  clueId.value = params.id as unknown as number
  getClue()
})
</script>