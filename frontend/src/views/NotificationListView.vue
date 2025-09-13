<template>
  <div class="notifications-management-page">
    <el-card>
      <!-- 通知表格 -->
      <el-table :data="notificationList" border stripe v-loading="loading" element-loading-text="加载中...">
        <el-table-column prop="notificationId" label="通知ID" width="100" />
        <el-table-column prop="senderId" label="发送者ID" width="100" />
        <el-table-column prop="receiverId" label="接收者ID" width="100" />
        <!-- <el-table-column prop="notificationType" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.notificationType)">
              {{ formatType(scope.row.notificationType) }}
            </el-tag>
          </template>
        </el-table-column> -->
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" min-width="200">
          <template #default="scope">
            <el-tooltip
              :content="scope.row.content"
              placement="top"
              effect="light"
              :disabled="scope.row.content.length <= 20"
            >
              <div class="content-cell">
                {{ scope.row.content.length > 20 ? scope.row.content.slice(0, 40) + '...' : scope.row.content }}
              </div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="isRead" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isRead ? 'success' : 'warning'">
              {{ scope.row.isRead ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
          :formatter="(row) => formatDate(row.createTime)"
        />
      </el-table>

      <!-- 分页控件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAccountStore } from '@/stores/account.js'
import { useApi } from '@/composables/useApi.js'
import { formatDate } from '@/utils/date.js'

const http = useApi()
const router = useRouter()
const accountStore = useAccountStore()

// 状态管理
const loading = ref(false)
const notificationList = ref([])

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  pages: 0,
})

// 判断是否为管理员
const isAdmin = computed(() => accountStore.accountInfo.isAdmin)

// 页面加载时查询数据
onMounted(() => {
  if (!isAdmin.value) {
    // 不是管理员，进行拦截
    ElMessage.warning('您没有权限访问此页面')
    router.replace('/') // 重定向到首页
  } else {
    fetchNotificationsList()
  }
})

// 查询通知列表（仅分页，无筛选）
const fetchNotificationsList = async () => {
  try {
    loading.value = true

    // 构建请求体数据（仅包含分页参数）
    const requestData = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    }

    // 调用API查询
    const response = await http.post('/notify/getNotifications', requestData)

    // 如果 code 正常，则显示数据
    if (response.code === 2001) {
      notificationList.value = response.data.list || []
      // console.log(notificationList.value)
      pagination.total = response.data.total || 0
      pagination.pages = response.data.pages || 0
    } else {
      ElMessage.error(response.msg || '通知信息查询失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '查询失败，请稍后重试')
    } else {
      console.error('查询通知列表失败:', error)
      ElMessage.error('查询失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 处理页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchNotificationsList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  fetchNotificationsList()
}

// 格式化通知类型显示
const formatType = (type) => {
  const typeMap = {
    MENTION: '提及',
    SYSTEM: '系统',
    ADMIN: '管理员',
    APPROVAL: '审批',
    REJECTION: '拒绝',
  }
  return typeMap[type] || type
}

// 获取类型标签的样式
const getTypeTagType = (type) => {
  const typeMap = {
    MENTION: 'primary',
    SYSTEM: 'info',
    ADMIN: 'warning',
    APPROVAL: 'success',
    REJECTION: 'danger',
  }
  return typeMap[type] || 'default'
}
</script>

<style scoped>
.notifications-management-page {
  padding: 24px;
  max-width: 1500px;
  margin: 0 auto;
}

.pagination-container {
  margin-top: 20px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
  text-align: right;
}

.el-table {
  border-radius: 6px;
  overflow: hidden;
}

.el-table-column {
  min-width: 80px;
}

.content-cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
