<template>
  <doc-alert title="【客户】客户管理、公海客户" url="https://doc.iocoder.cn/crm/customer/" />
  <doc-alert title="【通用】数据权限" url="https://doc.iocoder.cn/crm/permission/" />

  <ContentWrap>
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="160px"
      v-loading="formLoading"
    >
      <el-card shadow="never">
        <!-- 操作 -->
        <template #header>
          <div class="flex items-center justify-between">
            <CardTitle :title="t('poolConfig.title')" />
            <el-button
              type="primary"
              @click="onSubmit"
              v-hasPermi="['crm:customer-pool-config:update']"
            >
              {{ t('common.save') }}
            </el-button>
          </div>
        </template>
        <!-- 表单 -->
        <el-form-item :label="t('poolConfig.enabled')" prop="enabled">
          <el-radio-group v-model="formData.enabled" @change="changeEnable" class="ml-4">
            <el-radio :value="false" size="large">{{ t('poolConfig.notEnabled') }}</el-radio>
            <el-radio :value="true" size="large">{{ t('poolConfig.enabledText') }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <div v-if="formData.enabled">
          <el-form-item>
            <el-input-number class="mr-2" v-model="formData.contactExpireDays" />
            {{ t('poolConfig.contactExpireDays') }}
            <el-input-number class="mx-2" v-model="formData.dealExpireDays" />
            {{ t('poolConfig.dealExpireDays') }}
          </el-form-item>
          <el-form-item :label="t('poolConfig.notifyEnabled')" prop="notifyEnabled">
            <el-radio-group
              v-model="formData.notifyEnabled"
              @change="changeNotifyEnable"
              class="ml-4"
            >
              <el-radio :value="false" size="large">{{ t('poolConfig.notNotify') }}</el-radio>
              <el-radio :value="true" size="large">{{ t('poolConfig.notify') }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <div v-if="formData.notifyEnabled">
            <el-form-item>
              {{ t('poolConfig.notifyDaysBefore') }} <el-input-number class="mx-2" v-model="formData.notifyDays" /> {{ t('poolConfig.notifyDaysAfter') }}
            </el-form-item>
          </div>

          <!-- 客户等级回收时效 -->
          <el-divider content-position="left">{{ t('poolConfig.levelExpireDaysTitle') }}</el-divider>
          <el-form-item :label="t('poolConfig.levelExpireDays')">
            <div class="flex flex-col gap-3">
              <div class="flex items-center" v-for="level in customerLevels" :key="level.value">
                <span class="w-80px text-right mr-3">{{ t('poolConfig.levelLabel', { level: level.label }) }}</span>
                <el-input-number
                  v-model="levelExpireDaysData[level.value]"
                  :min="1"
                  :max="730"
                  :step="1"
                  controls-position="right"
                />
                <span class="ml-2 text-gray-500">{{ t('poolConfig.day') }}</span>
              </div>
            </div>
            <div class="text-gray-400 text-xs mt-1">
              {{ t('poolConfig.levelExpireDaysTip') }}
            </div>
          </el-form-item>

          <!-- 合同/回款暂停开关 -->
          <el-divider content-position="left">{{ t('poolConfig.pauseSettingsTitle') }}</el-divider>
          <el-form-item :label="t('poolConfig.pauseContractEnabled')" prop="pauseContractEnabled">
            <el-radio-group v-model="formData.pauseContractEnabled" class="ml-4">
              <el-radio :value="false" size="large">{{ t('poolConfig.notPause') }}</el-radio>
              <el-radio :value="true" size="large">{{ t('poolConfig.pause') }}</el-radio>
            </el-radio-group>
            <div class="text-gray-400 text-xs ml-4 mt-1">
              {{ t('poolConfig.pauseContractTip') }}
            </div>
          </el-form-item>
          <el-form-item :label="t('poolConfig.pauseReceivableEnabled')" prop="pauseReceivableEnabled">
            <el-radio-group v-model="formData.pauseReceivableEnabled" class="ml-4">
              <el-radio :value="false" size="large">{{ t('poolConfig.notPause') }}</el-radio>
              <el-radio :value="true" size="large">{{ t('poolConfig.pause') }}</el-radio>
            </el-radio-group>
            <div class="text-gray-400 text-xs ml-4 mt-1">
              {{ t('poolConfig.pauseReceivableTip') }}
            </div>
          </el-form-item>

          <!-- 最大延期次数 -->
          <el-divider content-position="left">{{ t('poolConfig.extendSettingsTitle') }}</el-divider>
          <el-form-item :label="t('poolConfig.extendMaxCount')" prop="extendMaxCount">
            <el-input-number
              v-model="formData.extendMaxCount"
              :min="0"
              :max="10"
              :step="1"
              controls-position="right"
            />
            <span class="ml-2 text-gray-500">{{ t('poolConfig.extendMaxCountUnit') }}</span>
            <div class="text-gray-400 text-xs mt-1">
              {{ t('poolConfig.extendMaxCountTip') }}
            </div>
          </el-form-item>
        </div>
      </el-card>
    </el-form>
  </ContentWrap>
</template>
<script setup lang="ts">
import * as CustomerPoolConfigApi from '@/api/crm/customer/poolConfig'
import { CardTitle } from '@/components/Card'

defineOptions({ name: 'CrmCustomerPoolConfig' })

const message = useMessage() // 消息弹窗
const { t } = useI18n('crm.customer') // 国际化

const formLoading = ref(false)
const formData = ref<CustomerPoolConfigApi.CustomerPoolConfigVO>({
  enabled: false,
  contactExpireDays: undefined,
  dealExpireDays: undefined,
  notifyEnabled: false,
  notifyDays: undefined,
  levelExpireDays: '',
  pauseContractEnabled: false,
  pauseReceivableEnabled: false,
  extendMaxCount: undefined
})
const formRules = reactive({
  enabled: [{ required: true, message: t('poolConfig.enabledRequired'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

// 客户等级列表（仅A/B/C三级，系统不支持D级客户）
const customerLevels = [
  { label: 'A', value: 'A' },
  { label: 'B', value: 'B' },
  { label: 'C', value: 'C' }
]

// 客户等级回收时效数据（双向绑定到独立对象，仅A/B/C三级）
const levelExpireDaysData = reactive<Record<string, number | undefined>>({
  A: 90,
  B: 60,
  C: 30
})

// 监听等级回收时效变化，同步到 formData.levelExpireDays
watch(
  levelExpireDaysData,
  (val) => {
    formData.value.levelExpireDays = JSON.stringify(val)
  },
  { deep: true }
)

/** 解析 levelExpireDays JSON 字符串到 levelExpireDaysData */
const initLevelExpireDays = (json: string) => {
  if (!json) return
  try {
    const parsed = JSON.parse(json)
    Object.keys(parsed).forEach((key) => {
      if (levelExpireDaysData[key] !== undefined) {
        levelExpireDaysData[key] = parsed[key]
      }
    })
  } catch {
    // 解析失败，使用默认值
  }
}

/** 获取配置 */
const getConfig = async () => {
  try {
    formLoading.value = true
    const data = await CustomerPoolConfigApi.getCustomerPoolConfig()
    if (data === null) {
      return
    }
    formData.value = data
    // 初始化等级回收时效
    if (data.levelExpireDays) {
      initLevelExpireDays(data.levelExpireDays)
    }
  } finally {
    formLoading.value = false
  }
}

/** 提交配置 */
const onSubmit = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as CustomerPoolConfigApi.CustomerPoolConfigVO
    await CustomerPoolConfigApi.saveCustomerPoolConfig(data)
    message.success(t('common.updateSuccess'))
    await getConfig()
    formLoading.value = false
  } finally {
    formLoading.value = false
  }
}

/** 更改客户公海规则设置 */
const changeEnable = () => {
  if (!formData.value.enabled) {
    formData.value.contactExpireDays = undefined
    formData.value.dealExpireDays = undefined
    formData.value.notifyEnabled = false
    formData.value.notifyDays = undefined
    formData.value.levelExpireDays = ''
    formData.value.pauseContractEnabled = false
    formData.value.pauseReceivableEnabled = false
    formData.value.extendMaxCount = undefined
  }
}

/** 更改提前提醒设置 */
const changeNotifyEnable = () => {
  if (!formData.value.notifyEnabled) {
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
.w-80px {
  width: 80px;
}
</style>