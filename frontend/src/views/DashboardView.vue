<template>
  <div class="dashboard-container">
    <!-- 统计卡片区域 -->
    <el-row :gutter="20" class="mb-8">
      <!-- 左上统计卡片：参与会议 + 未读消息 -->
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-grid">
            <div class="stat-item" @click="handleStatClick('joined')">
              <div class="stat-label">我的会议</div>
              <div class="stat-value">{{ joinedMeetingCount }}</div>
              <div class="stat-desc">会议待办</div>
            </div>
            <div class="stat-item" @click="handleStatClick('message')">
              <div class="stat-label">未读消息</div>
              <div class="stat-value">{{ unreadMsgCount }}</div>
              <div class="stat-desc">点击查看</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右上卡片：可用会议室数量 -->
      <el-col :span="8">
        <el-card class="stat-card available-rooms-card" @click="goToRoomList">
          <div class="stat-single">
            <div class="stat-label">全部会议室</div>
            <div class="stat-value">{{ roomCount }}</div>
            <div class="stat-desc">点击查看全部</div>
          </div>
          <div class="room-icon">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="48" height="48">
              <path
                fill="currentColor"
                d="M832 64H192c-17.7 0-32 14.3-32 32v832c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V96c0-17.7-14.3-32-32-32zm-260 728H300c-4.4 0-8-3.6-8-8v-40c0-4.4 3.6-8 8-8h272c4.4 0 8 3.6 8 8v40c0 4.4-3.6 8-8 8zm0-128H300c-4.4 0-8-3.6-8-8v-40c0-4.4 3.6-8 8-8h272c4.4 0 8 3.6 8 8v40c0 4.4-3.6 8-8 8zm0-128H300c-4.4 0-8-3.6-8-8v-40c0-4.4 3.6-8 8-8h272c4.4 0 8 3.6 8 8v40c0 4.4-3.6 8-8 8z"
              />
            </svg>
          </div>
        </el-card>
      </el-col>

      <!-- 快速预约入口 -->
      <el-col :span="8">
        <el-card class="stat-card" @click="goToRoomList">
          <div class="stat-single">
            <div class="stat-label">快速预约</div>
            <div class="stat-value">会议室</div>
            <div class="stat-desc">点击前往预约</div>
          </div>
          <div class="reserve-icon">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="48" height="48">
              <path
                fill="currentColor"
                d="M480 128a32 32 0 0 1 32 32v288h288a32 32 0 1 1 0 64H512v288a32 32 0 1 1-64 0V416H160a32 32 0 0 1 0-64h288V160a32 32 0 0 1 32-32z"
              />
            </svg>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 已预约会议列表 -->
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="reservation-list-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">近期预约会议</span>
              <el-button size="small" text @click="viewAllReservations">查看全部</el-button>
            </div>
          </template>

          <el-table :data="recentReservations" border stripe :loading="loadingReservations" empty-text="暂无预约记录">
            <el-table-column prop="roomId" label="会议室ID" width="100" />
            <el-table-column prop="meetingTopic" label="会议主题" />
            <el-table-column prop="reservationDate" label="预约日期" width="120" />
            <el-table-column prop="startTime" label="开始时间" width="120" />
            <el-table-column prop="endTime" label="结束时间" width="120" />
            <el-table-column prop="reservationStatus" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.reservationStatus)">
                  {{ formatStatus(scope.row) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button
                  size="small"
                  type="warning"
                  @click="handleCancel(scope.row)"
                  :disabled="!canCancel(scope.row.reservationStatus)"
                >
                  取消
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧信息区域：公告/提示 -->
      <el-col :span="8">
        <el-card class="announcement-card-container">
          <template #header>
            <div class="card-header">
              <span class="card-title">最新公告</span>
              <el-button size="small" text @click="goToAnnouncementCenter">查看全部</el-button>
            </div>
          </template>
          <div class="announcement-list" v-loading="loadingAnnouncements">
            <!-- 使用AnnouncementCard组件 -->
            <AnnouncementCard
              v-for="announce in processedAnnouncements"
              :key="announce.announcementId"
              :announcement="announce"
              @click="goToAnnouncementCenter"
            />

            <div v-if="!loadingAnnouncements && announcements.length === 0" class="empty-announce">
              <div class="empty-icon">📋</div>
              <p>暂无公告</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 取消预约弹窗 -->
    <el-dialog title="取消预约" v-model="cancelDialogVisible" width="400px">
      <p>确定要取消此预约吗？</p>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCancel">确认取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, inject } from 'vue'
import { useApi } from '@/composables/useApi'
import { useAccountStore } from '@/stores/account.js'
import { useRouter } from 'vue-router'

// 公告组件
import AnnouncementCard from '@/components/AnnouncementCard.vue'

// 依赖注入
const http = useApi()
const accountStore = useAccountStore()
const router = useRouter()

// 状态管理
const joinedMeetingCount = ref(0)
const unreadMsgCount = computed(() => notification?.msgCount.value || 0)
const roomCount = ref(0)
const recentReservations = ref([])
const announcements = ref([])
const loadingReservations = ref(false)
const loadingAnnouncements = ref(false)

// 处理公告数据，添加isRecent属性
const processedAnnouncements = computed(() => {
  return announcements.value.map((ann) => ({
    ...ann,
    isRecent: ann.createTime ? new Date(ann.createTime).getTime() >= Date.now() - 3 * 24 * 60 * 60 * 1000 : false,
  }))
})

// 取消预约相关状态
const cancelDialogVisible = ref(false)
const cancelFormRef = ref(null)
const cancelForm = reactive({
  reservationId: null,
})

// 格式化状态显示
const formatStatus = (row) => {
  const statusMap = {
    PENDING: '待处理',
    APPROVED: '已批准',
    REJECTED: '已拒绝',
    CANCELLED: '已取消',
    EXPIRED: '已过期',
  }
  return statusMap[row.reservationStatus] || row.reservationStatus
}

// 获取状态标签的类型
const getStatusTagType = (status) => {
  const typeMap = {
    PENDING: 'info', // 待处理 - 蓝色
    APPROVED: 'success', // 已批准 - 绿色
    REJECTED: 'danger', // 已拒绝 - 红色
    CANCELLED: 'warning', // 已取消 - 橙色
    EXPIRED: 'gray', // 已过期 - 灰色
  }
  return typeMap[status] || 'default'
}

// 判断是否可以取消预约
const canCancel = (status) => {
  // 只有待处理和已批准的预约可以取消
  return ['PENDING', 'APPROVED'].includes(status)
}

// 初始化数据
onMounted(() => {
  fetchRecentReservations()
  fetchAnnouncements()
  fetchRooms()
})

// 注入Home提供的消息状态
const notification = inject('notification')

// 获取近期预约
const fetchRecentReservations = async () => {
  // 获取当前登录员工ID
  const accountId = accountStore.accountInfo.accountId

  try {
    loadingReservations.value = true

    const requestData = {
      pageNum: 1,
      pageSize: 10,
      accountId, // 固定查询当前员工的预约
    }

    const response = await http.post('/res/queryReservations', requestData)
    if (response.code === 2001) {
      joinedMeetingCount.value = response.data.total
      recentReservations.value = response.data.list || []
    } else {
      ElMessage.error(response.msg || '预约信息查询失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '查询失败，请稍后重试')
    } else {
      console.error('查询预约列表失败:', error)
      ElMessage.error('查询失败，请稍后重试')
    }
  } finally {
    loadingReservations.value = false
  }
}

// 获取公告
const fetchAnnouncements = async () => {
  loadingAnnouncements.value = true
  try {
    const response = await http.post('/ann/getAnnouncement', {
      pageNum: 1,
      pageSize: 3,
    })
    if (response.code === 2001) {
      announcements.value = response.data.list || []
    } else {
      ElMessage.error(response.msg || '获取公告失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '获取公告失败，请稍后重试')
    } else {
      console.error('获取公告失败:', error)
      ElMessage.error('获取公告失败，请稍后重试')
    }
  } finally {
    loadingAnnouncements.value = false
  }
}

// 获取可用会议室
const fetchRooms = async () => {
  try {
    const response = await http.post('/meetingRoom/queryMeetingRooms', {
      roomStatus: 'AVAILABLE',
      pageNum: 1,
      pageSize: 10,
    })

    if (response.code === 2001) {
      roomCount.value = response.data.total
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '获取可用会议室失败，请稍后重试')
    } else {
      console.error('获取可用会议室失败:', error)
      ElMessage.error('获取可用会议室失败，请稍后重试')
    }
  }
}

// 事件处理
const handleStatClick = (type) => {
  if (type === 'message') {
    // 调用Home提供的方法显示消息面板
    notification?.toggleNotifications()
  } else if (type === 'joined') {
    router.push('/reservation')
  }
}

const goToRoomList = () => {
  router.push('/rooms')
}

const viewAllReservations = () => {
  router.push('/reservation')
}

// 前往公告中心
const goToAnnouncementCenter = () => {
  router.push('/notice')
}

// 处理取消操作
const handleCancel = (row) => {
  // 重置表单
  cancelForm.reservationId = row.reservationId
  cancelFormRef.value?.resetFields()

  // 显示取消原因对话框
  cancelDialogVisible.value = true
}

// 确认取消
const confirmCancel = async () => {
  cancelReservation()
}

// 取消预约
const cancelReservation = async () => {
  try {
    loadingReservations.value = true

    // 构建请求数据
    const requestData = {
      reservationId: cancelForm.reservationId,
      reservationStatus: 'CANCELLED',
      accountId: accountStore.accountInfo.accountId,
    }

    const response = await http.post('/res/updateReservation', requestData, {
      params: {
        jwtClaim: JSON.stringify({
          accountId: accountStore.accountInfo.accountId,
          isAdmin: accountStore.accountInfo.isAdmin,
        }),
      },
    })

    if (response.code === 2001 && response.data) {
      ElMessage.success('取消预约成功')
      cancelDialogVisible.value = false
      fetchRecentReservations() // 刷新列表
    } else {
      ElMessage.error(response.msg || '取消预约失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '操作失败，请稍后重试')
    } else {
      console.error('取消预约失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    }
  } finally {
    loadingReservations.value = false
  }
}
</script>

<style scoped>
.dashboard-container {
  min-height: calc(100vh - 64px);
  padding: 24px;
  max-width: 1500px;
  margin: 0 auto;
}

/* 统计卡片样式 */
.stat-card {
  height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-card .reserve-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  color: #409eff;
}

.stat-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  padding: 16px 0;
}

.stat-single {
  padding: 16px;
  text-align: center;
}

.stat-item {
  text-align: center;
  padding: 16px 8px;
  border-radius: 8px;
  background-color: #f0f7ff;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 4px;
}

.stat-desc {
  font-size: 12px;
  color: #909399;
}

/* 可用会议室卡片 */
.available-rooms-card {
  position: relative;
  overflow: hidden;
}

.room-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  color: #1890ff;
  opacity: 0.2;
}

/* 快速预约卡片 */
.quick-book-card {
  background: linear-gradient(135deg, #409eff 0%, #69b1ff 100%);
  color: white;
  border: none;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.quick-book-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.quick-book-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px 0;
}

.quick-book-icon {
  margin-bottom: 16px;
}

.quick-book-text {
  font-size: 18px;
  font-weight: 500;
}

/* 列表卡片样式 */
.reservation-list-card {
  height: 100%;
}

.announcement-card-container {
  height: 100%;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}

/* 公告列表样式 */
.announcement-list {
  padding: 8px 0;
  max-height: 500px;
  overflow-y: auto;
}

.empty-announce {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.empty-icon {
  font-size: 36px;
  margin-bottom: 12px;
}

.mb-8 {
  margin-bottom: 2rem;
}
</style>
