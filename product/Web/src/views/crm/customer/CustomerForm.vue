<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="auto"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('name')" prop="name">
            <el-input
              v-model="formData.name"
              :placeholder="t('namePlaceholder')"
              @blur="checkDupInline"
            />
            <div v-if="dupWarnings.length > 0" class="mt-1">
              <el-alert
                v-for="(w, i) in dupWarnings"
                :key="i"
                :title="w"
                type="warning"
                :closable="false"
                show-icon
              />
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('source')" prop="source">
            <el-select v-model="formData.source" :placeholder="t('sourcePlaceholder')" class="w-1/1">
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
        <el-col :span="12">
          <el-form-item :label="t('mobile')" prop="mobile">
            <el-input
              v-model="formData.mobile"
              :placeholder="t('mobilePlaceholder')"
              @blur="checkDupInline"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('ownerUserId')" prop="ownerUserId">
            <el-select
              v-model="formData.ownerUserId"
              :disabled="formType !== 'create'"
              class="w-1/1"
            >
              <el-option
                v-for="item in userOptions"
                :key="item.id"
                :label="item.nickname"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('telephone')" prop="telephone">
            <el-input v-model="formData.telephone" :placeholder="t('telephonePlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('email')" prop="email">
            <el-input v-model="formData.email" :placeholder="t('emailPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('wechat')" prop="wechat">
            <el-input v-model="formData.wechat" :placeholder="t('wechatPlaceholder')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('qq')" prop="qq">
            <el-input v-model="formData.qq" :placeholder="t('qqPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('industryId')" prop="industryId">
            <el-select v-model="formData.industryId" :placeholder="t('industryPlaceholder')" class="w-1/1">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.CRM_CUSTOMER_INDUSTRY)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('level')" prop="level">
            <el-select v-model="formData.level" :placeholder="t('levelPlaceholder')" class="w-1/1">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.CRM_CUSTOMER_LEVEL)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('areaId')" prop="areaId">
            <el-cascader
              v-model="formData.areaId"
              :options="areaList"
              :props="defaultProps"
              class="w-1/1"
              clearable
              filterable
              :placeholder="t('areaPlaceholder')"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('detailAddress')" prop="detailAddress">
            <el-input v-model="formData.detailAddress" :placeholder="t('detailAddressPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="t('contactNextTime')" prop="contactNextTime">
            <el-date-picker
              v-model="formData.contactNextTime"
              :placeholder="t('contactNextTimePlaceholder')"
              type="datetime"
              value-format="x"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('remark')" prop="remark">
            <el-input type="textarea" v-model="formData.remark" :placeholder="t('remarkPlaceholder')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item :label="t('tags')" prop="tagIds">
            <el-select
              v-model="selectedTagIds"
              class="w-1/1"
              clearable
              filterable
              multiple
              :placeholder="t('tagsPlaceholder')"
            >
              <el-option
                v-for="tag in allTags"
                :key="tag.id"
                :label="tag.name"
                :value="tag.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <DuplicateCheckDialog
      v-model="showDedupDialog"
      :customer-data="dedupCheckData"
      @continue="doSubmitForm"
    />
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">{{ t('common.confirm') }}</el-button>
      <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as CustomerApi from '@/api/crm/customer'
import * as TagApi from '@/api/crm/tag'
import * as AreaApi from '@/api/system/area'
import { defaultProps } from '@/utils/tree'
import * as UserApi from '@/api/system/user'
import { useUserStore } from '@/store/modules/user'
import DuplicateCheckDialog from './components/DuplicateCheckDialog.vue'

const { t } = useI18n('crm.customer') // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const areaList = ref([]) // 地区列表
const userOptions = ref<UserApi.UserVO[]>([]) // 用户列表
const allTags = ref<TagApi.TagVO[]>([]) // 所有标签
const selectedTagIds = ref<number[]>([]) // 选中的标签ID列表
const showDedupDialog = ref(false) // 查重弹窗
const dedupCheckData = ref<{ name?: string; mobile?: string; email?: string; wechat?: string }>({}) // 查重数据
const dupWarnings = ref<string[]>([]) // 实时查重的警告信息
const checkingDup = ref(false) // 是否正在检查重复
const formData = ref({
  id: undefined,
  name: undefined,
  contactNextTime: undefined,
  ownerUserId: 0,
  mobile: undefined,
  telephone: undefined,
  qq: undefined,
  wechat: undefined,
  email: undefined,
  areaId: undefined,
  detailAddress: undefined,
  industryId: undefined,
  level: undefined,
  source: undefined,
  remark: undefined
})
const formRules = reactive({
  name: [{ required: true, message: t('nameRequired'), trigger: 'blur' }],
  ownerUserId: [{ required: true, message: t('ownerUserRequired'), trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await CustomerApi.getCustomer(id)
    } finally {
      formLoading.value = false
    }
  }
  // 获得地区列表
  areaList.value = await AreaApi.getAreaTree()
  // 获得用户列表
  userOptions.value = await UserApi.getSimpleUserList()
  // 获得所有标签
  allTags.value = await TagApi.getAllTags()
  // 加载已有标签
  if (id) {
    try {
      const tags = await TagApi.getCustomerTags(id)
      selectedTagIds.value = (tags || []).map((t: TagApi.TagVO) => t.id)
    } catch {
      selectedTagIds.value = []
    }
  }
  // 默认新建时选中自己
  if (formType.value === 'create') {
    formData.value.ownerUserId = useUserStore().getUser.id
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 输入框失焦时实时检查重复 */
const checkDupInline = async () => {
  // 仅在新建模式下检查
  if (formType.value !== 'create') return
  // 至少要有名称或手机号
  if (!formData.value.name && !formData.value.mobile) {
    dupWarnings.value = []
    return
  }
  checkingDup.value = true
  dupWarnings.value = []
  try {
    const res = await CustomerApi.checkDuplicateCustomer({
      name: formData.value.name,
      mobile: formData.value.mobile,
      email: formData.value.email,
      wechat: formData.value.wechat
    })
    if (res && Array.isArray(res) && res.length > 0) {
      dupWarnings.value = res.map((item: any) => {
        const fieldMap: Record<string, string> = { name: '客户名称', mobile: '手机号', email: '邮箱', wechat: '微信' }
        const fieldName = fieldMap[item.matchField] || item.matchField
        return `「${fieldName}」为「${item.matchValue}」已存在（已有客户：${item.customer?.name || '-'}），请确认是否重复创建`
      })
    }
  } catch {
    dupWarnings.value = []
  } finally {
    checkingDup.value = false
  }
}

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return

  // 新建时先进行查重检查
  if (formType.value === 'create') {
    dedupCheckData.value = {
      name: formData.value.name,
      mobile: formData.value.mobile,
      email: formData.value.email,
      wechat: formData.value.wechat
    }
    showDedupDialog.value = true
    return
  }

  // 修改时直接提交
  await doSubmitForm()
}

/** 实际提交表单（查重通过后调用） */
const doSubmitForm = async () => {
  formLoading.value = true
  try {
    const data = formData.value as unknown as CustomerApi.CustomerVO
    if (formType.value === 'create') {
      const customerId = await CustomerApi.createCustomer(data)
      message.success(t('common.createSuccess'))
      // 保存标签
      if (selectedTagIds.value.length > 0) {
        await TagApi.addCustomerTags(customerId, selectedTagIds.value)
      }
    } else {
      await CustomerApi.updateCustomer(data)
      message.success(t('common.updateSuccess'))
      // 更新标签：先清空再添加
      const currentTags = await TagApi.getCustomerTags(data.id!)
      const currentTagIds = (currentTags || []).map((t: TagApi.TagVO) => t.id)
      // 需要删除的标签
      for (const tagId of currentTagIds) {
        if (!selectedTagIds.value.includes(tagId)) {
          await TagApi.removeCustomerTag(data.id!, tagId)
        }
      }
      // 需要添加的标签
      for (const tagId of selectedTagIds.value) {
        if (!currentTagIds.includes(tagId)) {
          await TagApi.addCustomerTags(data.id!, [tagId])
        }
      }
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    name: undefined,
    contactNextTime: undefined,
    ownerUserId: 0,
    mobile: undefined,
    telephone: undefined,
    qq: undefined,
    wechat: undefined,
    email: undefined,
    areaId: undefined,
    detailAddress: undefined,
    industryId: undefined,
    level: undefined,
    source: undefined,
    remark: undefined
  }
  formRef.value?.resetFields()
  selectedTagIds.value = []
  dupWarnings.value = []
}
</script>
