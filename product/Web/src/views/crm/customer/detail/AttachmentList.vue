<template>
  <!-- 附件列表：聚合该客户下所有跟进记录中的附件和图片 -->
  <ContentWrap>
    <!-- 图片区域 -->
    <div v-if="images.length > 0" class="mb-20px">
      <h4 class="mb-10px">图片 ({{ images.length }})</h4>
      <div class="flex flex-wrap gap-10px">
        <el-image
          v-for="(url, index) in images"
          :key="'img-' + index"
          :src="url"
          :preview-src-list="images"
          :initial-index="index"
          class="w-100px h-100px"
          fit="cover"
          preview-teleported
        />
      </div>
    </div>

    <!-- 附件区域 -->
    <div v-if="files.length > 0">
      <h4 class="mb-10px">附件 ({{ files.length }})</h4>
      <el-table :data="fileList" :show-overflow-tooltip="true" :stripe="true">
        <el-table-column label="文件名" prop="name" show-overflow-tooltip />
        <el-table-column label="来源" prop="source" min-width="180" show-overflow-tooltip />
        <el-table-column label="上传时间" prop="time" min-width="170" />
        <el-table-column align="center" label="操作" width="100">
          <template #default="scope">
            <el-link :href="scope.row.url" type="primary" target="_blank" download>
              下载
            </el-link>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-empty v-if="images.length === 0 && files.length === 0" description="暂无附件" />
  </ContentWrap>
</template>
<script setup lang="ts">
import { FollowUpRecordApi } from '@/api/crm/followup'
import { BizTypeEnum } from '@/api/crm/permission'
import { dateFormatter } from '@/utils/formatTime'

defineOptions({ name: 'CrmCustomerAttachmentList' })

const props = defineProps<{
  customerId: number
}>()

const loading = ref(true)
const images = ref<string[]>([])
const files = ref<string[]>([])
const fileList = ref<{ name: string; url: string; source: string; time: string }[]>([])

const formatTime = (val: any) => (val ? dateFormatter(null, null, val) : '')

const getFileName = (url: string) => {
  const parts = url.split('/')
  return parts[parts.length - 1] || url
}

const getList = async () => {
  loading.value = true
  try {
    const data = await FollowUpRecordApi.getFollowUpRecordPage({
      pageNo: 1,
      pageSize: 200,
      bizType: BizTypeEnum.CRM_CUSTOMER,
      bizId: props.customerId
    })
    const records = data?.list || []

    const imgList: string[] = []
    const fileItems: { name: string; url: string; source: string; time: string }[] = []

    records.forEach((record: any) => {
      const source = `跟进记录 - ${record.creatorName || ''} (${formatTime(record.createTime) || ''})`
      // 图片
      if (record.picUrls) {
        record.picUrls.forEach((url: string) => {
          imgList.push(url)
        })
      }
      // 附件
      if (record.fileUrls) {
        record.fileUrls.forEach((url: string) => {
          fileItems.push({
            name: getFileName(url),
            url,
            source,
            time: formatTime(record.createTime) || ''
          })
        })
      }
    })

    images.value = imgList
    fileList.value = fileItems
    files.value = fileItems.map((f) => f.name)
  } catch {
    // 接口不可用时静默忽略
    images.value = []
    fileList.value = []
    files.value = []
  } finally {
    loading.value = false
  }
}

watch(
  () => props.customerId,
  () => {
    getList()
  },
  { immediate: true }
)
</script>
