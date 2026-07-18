<template>
  <ContentWrap title="竞争对手管理">
    <el-form :inline="true" :model="queryParams" class="-mb-15px">
      <el-form-item label="名称">
        <el-input v-model="queryParams.name" placeholder="请输入竞品名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" />重置</el-button>
      </el-form-item>
    </el-form>

    <el-card shadow="never" class="mt-4">
      <template #header>
        <span>竞品列表</span>
        <el-button type="primary" size="small" style="float: right" @click="openCreate">新增竞品</el-button>
      </template>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="name" label="竞品名称" min-width="150" />
        <el-table-column prop="advantage" label="优势" min-width="200" show-overflow-tooltip />
        <el-table-column prop="disadvantage" label="劣势" min-width="200" show-overflow-tooltip />
        <el-table-column prop="website" label="官网" min-width="150" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
              {{ row.status === 0 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button text type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo" :total="total" @pagination="loadList" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑竞品' : '新增竞品'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="如：XX科技有限公司" />
        </el-form-item>
        <el-form-item label="优势">
          <el-input v-model="form.advantage" type="textarea" :rows="2" placeholder="如：价格便宜、响应快" />
        </el-form-item>
        <el-form-item label="劣势">
          <el-input v-model="form.disadvantage" type="textarea" :rows="2" placeholder="如：售后服务差、产品不稳定" />
        </el-form-item>
        <el-form-item label="产品对比">
          <el-input v-model="form.productCompare" type="textarea" :rows="2" placeholder="产品功能对比说明" />
        </el-form-item>
        <el-form-item label="官网">
          <el-input v-model="form.website" placeholder="https://" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="其他备注信息" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">正常</el-radio>
            <el-radio :value="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="handleSave">{{ isEdit ? '更新' : '创建' }}</el-button>
        <el-button @click="dialogVisible = false">取消</el-button>
      </template>
    </el-dialog>
  </ContentWrap>
</template>

<script lang="ts" setup>
import * as CompetitorApi from '@/api/crm/competitor'
import type { CompetitorVO } from '@/api/crm/competitor'
import { ContentWrap } from '@/components/ContentWrap'

defineOptions({ name: 'CrmCompetitor' })
const message = useMessage()
const loading = ref(false); const total = ref(0); const list = ref<CompetitorVO[]>([])
const isEdit = ref(false); const dialogVisible = ref(false)
const queryParams = reactive({ pageNo: 1, pageSize: 10, name: '' })

const form = reactive<CompetitorVO>({ name: '', advantage: '', disadvantage: '', productCompare: '', website: '', remark: '', status: 0 })

const loadList = async () => { loading.value = true; try { const r = await CompetitorApi.getCompetitorPage(queryParams); list.value = r.list || []; total.value = r.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryParams.pageNo = 1; loadList() }
const resetQuery = () => { queryParams.name = ''; handleQuery() }

const openCreate = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }
const openEdit = (row: CompetitorVO) => { isEdit.value = true; Object.assign(form, { ...row }); dialogVisible.value = true }

const resetForm = () => { form.id = undefined; form.name = ''; form.advantage = ''; form.disadvantage = ''; form.productCompare = ''; form.website = ''; form.remark = ''; form.status = 0 }

const handleSave = async () => {
  if (!form.name) { message.warning('请输入竞品名称'); return }
  try {
    if (isEdit.value) { await CompetitorApi.updateCompetitor({ ...form }); message.success('更新成功') }
    else { await CompetitorApi.createCompetitor({ ...form }); message.success('创建成功') }
    dialogVisible.value = false; loadList()
  } catch (e) { /* handled */ }
}

const handleDelete = async (id: number) => {
  try { await message.confirm('确认删除该竞争对手？'); await CompetitorApi.deleteCompetitor(id); message.success('删除成功'); loadList() } catch (e) { /* cancelled */ }
}

onMounted(() => loadList())
</script>
