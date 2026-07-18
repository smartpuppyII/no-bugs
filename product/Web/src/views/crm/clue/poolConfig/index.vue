<template>
  <doc-alert title="【线索】线索管理、公海线索" url="https://doc.iocoder.cn/crm/clue/" />

  <ContentWrap>
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="180px"
      v-loading="formLoading"
    >
      <el-card shadow="never">
        <template #header>
          <div class="flex items-center justify-between">
            <CardTitle :title="t('cluePoolConfig.title')" />
            <el-button
              type="primary"
              @click="onSubmit"
              v-hasPermi="['crm:clue-pool-config:update']"
            >
              {{ t('common.save') }}
            </el-button>
          </div>
        </template>

        <!-- 是否启用 -->
        <el-form-item :label="t('cluePoolConfig.enabled')" prop="enabled">
          <el-radio-group v-model="formData.enabled" @change="changeEnable" class="ml-4">
            <el-radio :value="false" size="large">{{ t('cluePoolConfig.notEnabled') }}</el-radio>
            <el-radio :value="true" size="large">{{ t('cluePoolConfig.enabledText') }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <div v-if="formData.enabled">
          <!-- 线索自动回收时效 -->
          <el-form-item :label="t('cluePoolConfig.clueExpireDays')" prop="clueExpireDays">
            <el-input-number
              v-model="formData.clueExpireDays"
              :min="1"
              :max="90"
              :step="1"
              controls-position="right"
            />
            <span class="ml-2 text-gray-500">{{ t('cluePoolConfig.clueExpireDaysUnit') }}</span>
          </el-form-item>

          <!-- 跟进记录重置倒计时 -->
          <el-form-item :label="t('cluePoolConfig.followUpResetEnabled')" prop="followUpResetEnabled">
            <el-radio-group v-model="formData.followUpResetEnabled" class="ml-4">
              <el-radio :value="false" size="large">{{ t('cluePoolConfig.notReset') }}</el-radio>
              <el-radio :value="true" size="large">{{ t('cluePoolConfig.reset') }}</el-radio>
            </el-radio-group>
            <div class="text-gray-400 text-xs ml-4 mt-1">
              {{ t('cluePoolConfig.followUpResetTip') }}
            </div>
          </el-form-item>

          <!-- A类重点线索豁免 -->
          <el-form-item :label="t('cluePoolConfig.aLevelExemptEnabled')" prop="aLevelExemptEnabled">
            <el-radio-group v-model="formData.aLevelExemptEnabled" class="ml-4">
              <el-radio :value="false" size="large">{{ t('cluePoolConfig.notExempt') }}</el-radio>
              <el-radio :value="true" size="large">{{ t('cluePoolConfig.exempt') }}</el-radio>
            </el-radio-group>
            <div class="text-gray-400 text-xs ml-4 mt-1">
              {{ t('cluePoolConfig.aLevelExemptTip') }}
            </div>
          </el-form-item>

          <!-- 预热提醒天数 -->
          <el-form-item :label="t('cluePoolConfig.notifyDays')" prop="notifyDays">
            <el-input-number
              v-model="formData.notifyDays"
              :min="1"
              :max="7"
              :step="1"
              controls-position="right"
            />
            <span class="ml-2 text-gray-500">{{ t('cluePoolConfig.notifyDaysUnit') }}</span>
            <div class="text-gray-400 text-xs ml-4 mt-1">
              {{ t('cluePoolConfig.notifyDaysTip') }}
            </div>
          </el-form-item>
        </div>
      </el-card>
    </el-form>
  </ContentWrap>
</template>
<script setup lang="ts">
import * as CluePoolConfigApi from '@/api/crm/clue/poolConfig'
import { CardTitle } from '@/components/Card'

defineOptions({ name: 'CrmCluePoolConfig' })

const message = useMessage()
const { t } = useI18n('crm.clue')

const formLoading = ref(false)
const formData = ref<CluePoolConfigApi.CluePoolConfigVO>({
  enabled: true,
  clueExpireDays: 7,
  followUpResetEnabled: true,
  aLevelExemptEnabled: true,
  notifyDays: 3
})
const formRules = reactive({
  enabled: [{ required: true, message: '请选择是否启用线索公海', trigger: 'blur' }],
  clueExpireDays: [{ required: true, message: '请输入线索回收时效', trigger: 'blur' }],
  notifyDays: [{ required: true, message: '请输入提前提醒天数', trigger: 'blur' }]
})
const formRef = ref()

/** 获取配置 */
const getConfig = async () => {
  try {
    formLoading.value = true
    const data = await CluePoolConfigApi.getCluePoolConfig()
    if (data === null) {
      return
    }
    formData.value = data
  } finally {
    formLoading.value = false
  }
}

/** 提交配置 */
const onSubmit = async () => {
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  formLoading.value = true
  try {
    const data = formData.value as CluePoolConfigApi.CluePoolConfigVO
    await CluePoolConfigApi.saveCluePoolConfig(data)
    message.success(t('common.updateSuccess'))
    await getConfig()
  } finally {
    formLoading.value = false
  }
}

/** 切换启用状态 */
const changeEnable = () => {
  if (!formData.value.enabled) {
    formData.value.clueExpireDays = undefined
    formData.value.followUpResetEnabled = undefined
    formData.value.aLevelExemptEnabled = undefined
    formData.value.notifyDays = undefined
  }
}

onMounted(() => {
  getConfig()
})
</script>
<style scoped lang="scss">
.text-gray-400 {
  color: #9ca3af;
}
.text-gray-500 {
  color: #6b7280;
}
.text-xs {
  font-size: 12px;
}
</style>