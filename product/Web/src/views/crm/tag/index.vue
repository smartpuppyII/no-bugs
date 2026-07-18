<template>
  <ContentWrap title="标签管理">
    <!-- 推荐标签样例 -->
    <el-card shadow="never" class="mb-4">
      <template #header>
        <span>推荐标签样例</span>
        <el-tag size="small" type="info" class="ml-2">点击下方标签可一键添加</el-tag>
      </template>
      <div v-for="group in presetGroups" :key="group.name" class="mb-3">
        <div class="text-sm text-gray-500 mb-2">{{ group.name }}</div>
        <div class="flex flex-wrap gap-2">
          <el-tag
            v-for="tag in group.tags"
            :key="tag.name"
            :color="tag.color"
            effect="dark"
            size="large"
            class="cursor-pointer preset-tag"
            @click="addPreset(tag)"
          >
            + {{ tag.name }}
          </el-tag>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="never">
          <template #header><span>标签列表</span></template>
          <el-table :data="tagList" v-loading="loading" stripe>
            <el-table-column prop="name" label="标签名称" />
            <el-table-column prop="groupName" label="所属分组" />
            <el-table-column prop="color" label="颜色">
              <template #default="scope">
                <el-tag :color="scope.row.color" effect="dark" size="small">{{ scope.row.color }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="sortOrder" label="排序" width="80" />
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button text type="primary" size="small" @click="editTag(scope.row)">编辑</el-button>
                <el-button text type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header><span>{{ isEdit ? '编辑标签' : '新建标签' }}</span></template>
          <el-form :model="form" label-width="80px">
            <el-form-item label="标签名称">
              <el-input v-model="form.name" placeholder="请输入标签名称" />
            </el-form-item>
            <el-form-item label="标签颜色">
              <el-color-picker v-model="form.color" />
            </el-form-item>
            <el-form-item label="所属分组">
              <el-input v-model="form.groupName" placeholder="如：客户特征" />
            </el-form-item>
            <el-form-item label="排序">
              <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave">{{ isEdit ? '更新' : '创建' }}</el-button>
              <el-button v-if="isEdit" @click="resetForm">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </ContentWrap>
</template>

<script lang="ts" setup>
import * as TagApi from '@/api/crm/tag'
import type { TagVO } from '@/api/crm/tag'
import { ContentWrap } from '@/components/ContentWrap'

defineOptions({ name: 'CrmTagManagement' })

const { t } = useI18n('crm')
const message = useMessage()

const loading = ref(false)
const tagList = ref<TagVO[]>([])
const isEdit = ref(false)

// 推荐标签样例
const presetGroups = [
  {
    name: '客户特征',
    tags: [
      { name: '高意向', color: '#67C23A', groupName: '客户特征' },
      { name: '中意向', color: '#E6A23C', groupName: '客户特征' },
      { name: '低意向', color: '#F56C6C', groupName: '客户特征' },
      { name: '老客户', color: '#67C23A', groupName: '客户特征' },
      { name: '新客户', color: '#409EFF', groupName: '客户特征' },
      { name: 'VIP客户', color: '#E6A23C', groupName: '客户特征' }
    ]
  },
  {
    name: '风险类型',
    tags: [
      { name: '价格敏感', color: '#E6A23C', groupName: '风险类型' },
      { name: '决策周期长', color: '#909399', groupName: '风险类型' },
      { name: '竞品对比中', color: '#F56C6C', groupName: '风险类型' }
    ]
  },
  {
    name: '决策角色',
    tags: [
      { name: '决策者', color: '#409EFF', groupName: '决策角色' },
      { name: '影响者', color: '#409EFF', groupName: '决策角色' },
      { name: '守门人', color: '#409EFF', groupName: '决策角色' }
    ]
  }
]

const addPreset = (tag: { name: string; color: string; groupName: string }) => {
  form.name = tag.name
  form.color = tag.color
  form.groupName = tag.groupName
  form.sortOrder = 0
  isEdit.value = false
}

const form = reactive<TagVO>({
  id: 0,
  name: '',
  color: '#409EFF',
  groupName: '默认',
  sortOrder: 0,
  createTime: new Date()
})

const loadTags = async () => {
  loading.value = true
  try {
    tagList.value = await TagApi.getAllTags()
  } finally {
    loading.value = false
  }
}

const editTag = (tag: TagVO) => {
  isEdit.value = true
  Object.assign(form, tag)
}

const resetForm = () => {
  isEdit.value = false
  form.id = 0
  form.name = ''
  form.color = '#409EFF'
  form.groupName = '默认'
  form.sortOrder = 0
}

const handleSave = async () => {
  if (!form.name) {
    message.warning('请输入标签名称')
    return
  }
  try {
    if (isEdit.value) {
      await TagApi.updateTag(form)
      message.success('更新成功')
    } else {
      await TagApi.createTag(form)
      message.success('创建成功')
    }
    resetForm()
    loadTags()
  } catch (e) {
    console.error(e)
  }
}

const handleDelete = async (id: number) => {
  try {
    await message.confirm('确认删除该标签？')
    await TagApi.deleteTag(id)
    message.success('删除成功')
    loadTags()
  } catch (e) {
    // cancelled
  }
}

onMounted(() => loadTags())
</script>

<style lang="scss" scoped>
.preset-tag {
  transition: transform 0.15s, opacity 0.15s;
  &:hover {
    transform: scale(1.08);
    opacity: 0.85;
  }
}
</style>
