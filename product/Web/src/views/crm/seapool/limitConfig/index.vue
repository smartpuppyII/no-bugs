<template>
  <doc-alert title="【客户】客户管理、公海客户" url="https://doc.iocoder.cn/crm/customer/" />
  <doc-alert title="【通用】数据权限" url="https://doc.iocoder.cn/crm/permission/" />

  <ContentWrap>
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="200px"
      v-loading="formLoading"
    >
      <el-card shadow="never">
        <template #header>
          <div class="flex items-center justify-between">
            <CardTitle :title="t('seaPoolLimitConfig.title')" />
            <el-button
              type="primary"
              @click="onSubmit"
              v-hasPermi="['crm:sea-pool-limit-config:update']"
            >
              {{ t('common.save') }}
            </el-button>
          </div>
        </template>

        <!-- 每日领取上限 -->
        <el-divider content-position="left">{{ t('seaPoolLimitConfig.dailyLimitTitle') }}</el-divider>
        <el-form-item :label="t('seaPoolLimitConfig.dailyClueLimit')" prop="dailyClueLimit">
          <el-input-number
            v-model="formData.dailyClueLimit"
            :min="0"
            :max="100"
            :step="1"
            controls-position="right"
          />
          <span class="ml-2 text-gray-500">{{ t('seaPoolLimitConfig.dailyClueLimitUnit') }}</span>
          <div class="text-gray-400 text-xs mt-1">
            {{ t('seaPoolLimitConfig.dailyClueLimitTip') }}
          </div>
        </el-form-item>
        <el-form-item :label="t('seaPoolLimitConfig.dailyCustomerLimit')" prop="dailyCustomerLimit">
          <el-input-number
            v-model="formData.dailyCustomerLimit"
            :min="0"
            :max="50"
            :step="1"
            controls-position="right"
          />
          <span class="ml-2 text-gray-500">{{ t('seaPoolLimitConfig.dailyCustomerLimitUnit') }}</span>
          <div class="text-gray-400 text-xs mt-1">
            {{ t('seaPoolLimitConfig.dailyCustomerLimitTip') }}
          </div>
        </el-form-item>

        <!-- 领取保护与冷却 -->
        <el-divider content-position="left">{{ t('seaPoolLimitConfig.protectAndCoolingTitle') }}</el-divider>
        <el-form-item :label="t('seaPoolLimitConfig.coolingDays')" prop="coolingDays">
          <el-input-number
            v-model="formData.coolingDays"
            :min="0"
            :max="90"
            :step="1"
            controls-position="right"
          />
          <span class="ml-2 text-gray-500">{{ t('seaPoolLimitConfig.coolingDaysUnit') }}</span>
          <div class="text-gray-400 text-xs mt-1">
            {{ t('seaPoolLimitConfig.coolingDaysTip') }}
          </div>
        </el-form-item>
        <el-form-item :label="t('seaPoolLimitConfig.protectHours')" prop="protectHours">
          <el-input-number
            v-model="formData.protectHours"
            :min="0"
            :max="168"
            :step="1"
            controls-position="right"
          />
          <span class="ml-2 text-gray-500">{{ t('seaPoolLimitConfig.protectHoursUnit') }}</span>
          <div class="text-gray-400 text-xs mt-1">
            {{ t('seaPoolLimitConfig.protectHoursTip') }}
          </div>
        </el-form-item>
      </el-card>
    </el-form>
  </ContentWrap>
</template>
<script setup lang="ts">
import * as SeaPoolLimitConfigApi from '@/api/crm/seapool/claimLimitConfig'
import { CardTitle } from '@/components/Card'

defineOptions({ name: 'CrmSeaPoolLimitConfig' })

const message = useMessage()
const { t } = useI18n('crm')

const formLoading = ref(false)
const formData = ref<SeaPoolLimitConfigApi.SeaPoolLimitConfigVO>({
  dailyClueLimit: 10,
  dailyCustomerLimit: 5,
  coolingDays: 7,
  protectHours: 24
})
const formRules = reactive({
  dailyClueLimit: [{ required: true, message: '请输入每日线索领取上限', trigger: 'blur' }],
  dailyCustomerLimit: [{ required: true, message: '请输入每日客户领取上限', trigger: 'blur' }],
  coolingDays: [{ required: true, message: '请输入冷却天数', trigger: 'blur' }],
  protectHours: [{ required: true, message: '请输入保护期时长', trigger: 'blur' }]
})
const formRef = ref()

/** 获取配置 */
const getConfig = async () => {
  try {
    formLoading.value = true
    const data = await SeaPoolLimitConfigApi.getSeaPoolLimitConfig()
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
    const data = formData.value as SeaPoolLimitConfigApi.SeaPoolLimitConfigVO
    await SeaPoolLimitConfigApi.saveSeaPoolLimitConfig(data)
    message.success(t('common.updateSuccess'))
    await getConfig()
  } finally {
    formLoading.value = false
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