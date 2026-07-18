<template>
  <div v-loading="loading">
    <div class="flex items-start justify-between">
      <div>
        <!-- 左上：线索基本信息 -->
        <el-col>
          <el-row>
            <span class="text-xl font-bold">{{ clue.name }}</span>
          </el-row>
        </el-col>
      </div>
      <div>
        <!-- 右上：按钮 -->
        <slot></slot>
      </div>
    </div>
  </div>
  <ContentWrap class="mt-10px">
    <el-descriptions :column="5" direction="vertical">
      <el-descriptions-item :label="t('customer.source')">
        <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_SOURCE" :value="clue.source" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('customer.mobile')"> {{ clue.mobile }} </el-descriptions-item>
      <el-descriptions-item :label="t('clue.ownerUserName')">
        {{ clue.ownerUserName }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('clue.createTime')">
        {{ formatDate(clue.createTime) }}
      </el-descriptions-item>
      <!-- 回收倒计时 -->
      <el-descriptions-item v-if="clue.poolRemainDays !== undefined && clue.poolRemainDays !== null" :label="t('clue.recoveryCountdown')">
        <template v-if="clue.recoveryPaused">
          <el-tag type="warning" effect="plain">
            {{ t('clue.recoveryPaused') }}
            <el-tooltip v-if="clue.pauseReason" :content="clue.pauseReason" placement="top">
              <Icon icon="ep:question-filled" class="ml-4px" />
            </el-tooltip>
          </el-tag>
        </template>
        <template v-else>
          <span :class="countdownClass">
            {{ t('clue.daysLater', { day: clue.poolRemainDays }) }}
          </span>
        </template>
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import { computed } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import * as ClueApi from '@/api/crm/clue'
import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'CrmClueDetailsHeader' })

const { t } = useI18n('crm') // 国际化

const props = defineProps<{
  clue: ClueApi.ClueVO // 线索信息
  loading: boolean // 加载中
}>()

/** 回收倒计时样式 */
const countdownClass = computed(() => {
  const days = props.clue?.poolRemainDays
  if (days === undefined || days === null) return ''
  if (days <= 1) return 'text-red-500 font-bold'
  if (days <= 3) return 'text-yellow-500 font-bold'
  return 'text-gray-600'
})
</script>