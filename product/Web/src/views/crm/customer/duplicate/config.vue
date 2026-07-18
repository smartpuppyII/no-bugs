<template>
  <ContentWrap>
    <el-form :model="formData" label-width="120px">
      <el-card header="查重规则配置">
        <el-form-item label="检查客户名称">
          <el-switch v-model="formData.checkName" />
        </el-form-item>
        <el-form-item label="检查手机号">
          <el-switch v-model="formData.checkMobile" />
        </el-form-item>
        <el-form-item label="检查邮箱">
          <el-switch v-model="formData.checkEmail" />
        </el-form-item>
        <el-form-item label="检查微信">
          <el-switch v-model="formData.checkWechat" />
        </el-form-item>
        <el-form-item label="查重范围">
          <el-radio-group v-model="formData.checkScope">
            <el-radio value="ALL">全集团</el-radio>
            <el-radio value="DEPT">本部门</el-radio>
            <el-radio value="SELF">仅自己</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="saveConfig">保存配置</el-button>
        </el-form-item>
      </el-card>
    </el-form>
  </ContentWrap>
</template>

<script lang="ts" setup>
import * as CustomerApi from '@/api/crm/customer'
import { ContentWrap } from '@/components/ContentWrap'

const message = useMessage()

const loading = ref(false)
const formData = reactive({
  checkName: true,
  checkMobile: true,
  checkEmail: false,
  checkWechat: false,
  checkScope: 'ALL'
})

const loadConfig = async () => {
  try {
    const res = await CustomerApi.getDuplicateConfig()
    if (res) {
      formData.checkName = res.checkName ?? true
      formData.checkMobile = res.checkMobile ?? true
      formData.checkEmail = res.checkEmail ?? false
      formData.checkWechat = res.checkWechat ?? false
      formData.checkScope = res.checkScope ?? 'ALL'
    }
  } catch (e) {
    // 使用默认值
  }
}

const saveConfig = async () => {
  loading.value = true
  try {
    await CustomerApi.saveDuplicateConfig({ ...formData })
    message.success('配置保存成功')
  } catch (e) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadConfig()
})
</script>
