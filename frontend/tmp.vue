<template>
  <div class="dashboard-container">
    <!-- 统计卡片区域 -->
    <el-row :gutter="20" class="mb-4">
      <!-- 左上统计卡片：参与会议 + 未读消息 -->
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-grid">
            <div class="stat-item" @click="handleStatClick('joined')">
              <div class="stat-label">参与会议</div>
              <div class="stat-value">{{ joinedMeetingCount }}</div>
              <div class="stat-desc">本月累计</div>
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
            <div class="stat-label">可用会议室</div>
            <div class="stat-value">{{ availableRoomCount }}</div>
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
        <el-card class="quick-book-card" @click="openQuickBooking">
          <div class="quick-book-content">
            <div class="quick-book-icon">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="36" height="36">
                <path
                  fill="currentColor"
                  d="M480 128a32 32 0 0 1 32 32v288h288a32 32 0 1 1 0 64H512v288a32 32 0 1 1-64 0V416H160a32 32 0 0 1 0-64h288V160a32 32 0 0 1 32-32z"
                />
              </svg>
            </div>
            <div class="quick-book-text">快速预约会议室</div>
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
            <el-table-column prop="roomName" label="会议室" width="180" />
            <el-table-column prop="startTime" label="开始时间" width="200" />
            <el-table-column prop="endTime" label="结束时间" width="200" />
            <el-table-column prop="meetingTopic" label="会议主题" />
            <el-table-column prop="reservationStatus" label="状态" width="120">
              <template #default="scope">
                <el-tag
                  :type="
                    scope.row.reservationStatus === 'APPROVED'
                      ? 'success'
                      : scope.row.reservationStatus === 'PENDING'
                        ? 'warning'
                        : 'danger'
                  "
                >
                  {{ formatStatus(scope.row.reservationStatus) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧信息区域：公告/提示 -->
      <el-col :span="8"> ... </el-col>
    </el-row>

    <!-- 快速预约弹窗 -->
    <el-dialog title="快速预约会议室" v-model="showQuickBooking" width="600px">
      <el-form :model="bookingForm" :rules="bookingRules" ref="bookingFormRef">
        <el-form-item label="会议室" prop="roomId">
          <el-select v-model="bookingForm.roomId" placeholder="选择会议室">
            <el-option v-for="room in availableRooms" :key="room.roomId" :label="room.roomName" :value="room.roomId" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期" prop="reservationDate">
          <el-date-picker
            v-model="bookingForm.reservationDate"
            type="date"
            placeholder="选择预约日期"
            :min-date="new Date()"
          />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker v-model="bookingForm.startTime" placeholder="选择开始时间" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker v-model="bookingForm.endTime" placeholder="选择结束时间" />
        </el-form-item>
        <el-form-item label="会议主题" prop="meetingTopic">
          <el-input v-model="bookingForm.meetingTopic" placeholder="请输入会议主题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showQuickBooking = false">取消</el-button>
        <el-button type="primary" @click="submitBooking">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useApi } from '@/composables/useApi'
import { useAccountStore } from '@/stores/account.js'
import { useRouter } from 'vue-router'
// 导入你的公告组件
import AnnouncementCard from '@/components/AnnouncementCard.vue'

// 依赖注入
const http = useApi()
const accountStore = useAccountStore()
const router = useRouter()

// 状态管理
const joinedMeetingCount = ref(0)
const unreadMsgCount = ref(0)
const availableRoomCount = ref(0)
const recentReservations = ref([])
const announcements = ref([])
const availableRooms = ref([])
const loadingReservations = ref(false)
const loadingAnnouncements = ref(false)

// 处理公告数据，添加isRecent属性
const processedAnnouncements = computed(() => {
  return announcements.value.map((ann) => ({
    ...ann,
    isRecent: ann.createTime ? new Date(ann.createTime).getTime() >= Date.now() - 3 * 24 * 60 * 60 * 1000 : false,
  }))
})

// 快速预约相关
const showQuickBooking = ref(false)
const bookingFormRef = ref(null)
const bookingForm = reactive({
  roomId: '',
  reservationDate: '',
  startTime: '',
  endTime: '',
  meetingTopic: '',
  meetingDesc: '',
  attendees: [],
  mentionAll: false,
})
const bookingRules = reactive({
  roomId: [{ required: true, message: '请选择会议室', trigger: 'change' }],
  reservationDate: [{ required: true, message: '请选择预约日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  meetingTopic: [{ required: true, message: '请输入会议主题', trigger: 'blur' }],
})

// 初始化数据
onMounted(() => {
  fetchDashboardData()
  fetchRecentReservations()
  fetchAvailableRooms()
})

// 获取仪表盘统计数据
const fetchDashboardData = async () => {
  try {
    // 获取未读消息数量
    const unreadResponse = await http.post('/notify/getNotReadNum', {
      params: {
        jwtClaim: JSON.stringify({
          accountId: accountStore.accountInfo.accountId,
          isAdmin: accountStore.accountInfo.isAdmin,
        }),
      },
    })
    unreadMsgCount.value = unreadResponse.data.data || 0

    // 获取可用会议室数量（通过查询可用会议室列表并计算长度）
    const roomsResponse = await http.post('/meetingRoom/queryMeetingRooms', {
      roomStatus: 'AVAILABLE',
      pageNum: 1,
      pageSize: 1,
    })
    availableRoomCount.value = roomsResponse.data.data?.total || 0

    // 参与会议数量可以通过查询自己的预约来统计
    const reservationsResponse = await http.post(
      '/res/queryReservations',
      {
        accountId: accountStore.accountInfo.accountId,
        pageNum: 1,
        pageSize: 1,
      },
      {
        params: {
          jwtClaim: JSON.stringify({
            accountId: accountStore.accountInfo.accountId,
            isAdmin: accountStore.accountInfo.isAdmin,
          }),
        },
      },
    )
    joinedMeetingCount.value = reservationsResponse.data.data?.total || 0
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('加载数据失败，请稍后重试')
  }
}

// 获取近期预约
const fetchRecentReservations = async () => {
  loadingReservations.value = true
  try {
    const response = await http.post(
      '/res/queryReservations',
      {
        accountId: accountStore.accountInfo.accountId,
        pageNum: 1,
        pageSize: 5,
      },
      {
        params: {
          jwtClaim: JSON.stringify({
            accountId: accountStore.accountInfo.accountId,
            isAdmin: accountStore.accountInfo.isAdmin,
          }),
        },
      },
    )
    recentReservations.value = response.data.data?.list || []
  } catch (error) {
    console.error('获取预约记录失败:', error)
    ElMessage.error('加载预约记录失败')
  } finally {
    loadingReservations.value = false
  }
}

// 获取可用会议室
const fetchAvailableRooms = async () => {
  try {
    const response = await http.post('/meetingRoom/queryMeetingRooms', {
      roomStatus: 'AVAILABLE',
      pageNum: 1,
      pageSize: 100,
    })
    availableRooms.value = response.data.data?.list || []
  } catch (error) {
    console.error('获取可用会议室失败:', error)
  }
}

// 格式化状态文本
const formatStatus = (status) => {
  const statusMap = {
    APPROVED: '已确认',
    PENDING: '待审核',
    CANCELLED: '已取消',
    REJECTED: '已拒绝',
    EXPIRED: '已过期',
  }
  return statusMap[status] || status
}

// 事件处理
const handleStatClick = (type) => {
  if (type === 'message') {
    // 触发消息面板显示（与HomeView的消息面板联动）
    const event = new CustomEvent('show-notifications', { bubbles: true })
    document.dispatchEvent(event)
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

const openQuickBooking = () => {
  showQuickBooking.value = true
  // 重置表单
  bookingFormRef.value?.resetFields()
}

const submitBooking = async () => {
  try {
    await bookingFormRef.value.validate()
    // 补充预约人信息
    const submitData = {
      ...bookingForm,
      // 从账户信息获取当前用户ID
      accountId: accountStore.accountInfo.accountId,
    }
    await http.post('/res/reservation', submitData, {
      params: {
        jwtClaim: JSON.stringify({
          accountId: accountStore.accountInfo.accountId,
          isAdmin: accountStore.accountInfo.isAdmin,
        }),
      },
    })
    ElMessage.success('预约成功')
    showQuickBooking.value = false
    // 刷新预约列表
    fetchRecentReservations()
  } catch (error) {
    if (error.name === 'Error') {
      ElMessage.error('预约失败: ' + (error.response?.data?.msg || '服务器错误'))
    }
    // 表单验证失败会自动提示，无需额外处理
  }
}
</script>

<style scoped>
/* 样式部分保持不变 */
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 64px);
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}
</style>
