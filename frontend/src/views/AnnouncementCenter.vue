<template>
  <div class="announcement-center">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>公告中心</h1>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="handleFilter"
            class="date-filter"
          />
        </el-col>
        <el-col :span="16">
          <el-input
            v-model="filters.title"
            placeholder="搜索公告标题..."
            clearable
            @keyup.enter="handleFilter"
            class="search-input"
          />
        </el-col>
      </el-row>
    </div>

    <!-- 公告卡片容器 -->
    <div class="announcement-grid" v-loading="loading" element-loading-text="加载公告中...">
      <!-- 公告卡片 -->
      <AnnouncementCard
        v-for="announcement in filteredAnnouncements"
        :key="announcement.announcementId"
        :announcement="announcement"
      />

      <!-- 空状态 -->
      <div class="empty-state" v-if="!loading && filteredAnnouncements.length === 0">
        <div class="empty-icon">📋</div>
        <p>暂无符合条件的公告</p>
      </div>
    </div>

    <!-- 分页控件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[6, 12, 24]"
        :total="totalFiltered"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useApi } from '@/composables/useApi.js'
import AnnouncementCard from '@/components/AnnouncementCard.vue'

const http = useApi()

// 状态管理
const loading = ref(false)
const announcementList = ref([]) // 存储所有公告数据

// 筛选条件
const filters = reactive({
  title: '',
  dateRange: [],
})

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 6,
  total: 0,
})

// 获取公告列表
const fetchAnnouncements = async () => {
  try {
    loading.value = true

    // 仅使用getAnnouncement接口
    const response = await http.post('/ann/getAnnouncement', {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    })

    if (response.code === 2001) {
      announcementList.value = response.data.list || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '获取公告失败')
    }
  } catch (error) {
    console.error('获取公告失败:', error)
    ElMessage.error('获取公告失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 过滤后的公告列表
const filteredAnnouncements = computed(() => {
  // 应用筛选条件
  let filtered = announcementList.value.filter((item) => {
    // 标题筛选
    if (filters.title && !item.title.toLowerCase().includes(filters.title.toLowerCase())) {
      return false
    }
    // 日期范围筛选
    if (filters.dateRange && filters.dateRange.length === 2) {
      const createTime = new Date(item.createTime)
      const startDate = new Date(filters.dateRange[0])
      const endDate = new Date(filters.dateRange[1])
      // 确保时间在范围内（包含起止日期）
      if (createTime < startDate || createTime > endDate) {
        return false
      }
    }
    return true
  })

  return filtered
})

// 过滤后的总条数
const totalFiltered = computed(() => {
  return filteredAnnouncements.value.length
})

// 处理筛选
const handleFilter = () => {
  pagination.pageNum = 1 // 重置到第一页
}

// 处理页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  fetchAnnouncements()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  fetchAnnouncements()
}

// 页面加载时获取数据
onMounted(() => {
  fetchAnnouncements()
})
</script>

<style scoped>
.announcement-center {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.page-header h1 {
  font-size: 28px;
  margin-bottom: 8px;
  color: #333;
  font-weight: 600;
}

.page-header p {
  color: #666;
  font-size: 16px;
}

.filter-section {
  margin-bottom: 30px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.search-input {
  width: 100%;
}

.status-select {
  width: 100%;
}

.announcement-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 30px;
  margin-bottom: 30px;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  color: #999;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.date-filter {
  width: 100%;
}
</style>
