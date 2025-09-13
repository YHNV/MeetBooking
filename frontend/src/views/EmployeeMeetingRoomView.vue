<template>
  <div class="meeting-room-view">
    <el-card class="filter-card">
      <div class="filter-container">
        <el-row :gutter="20">
          <!-- 会议室名称筛选 -->
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">会议室名称</label>
              <el-input
                v-model="filters.roomName"
                placeholder="请输入会议室名称"
                clearable
                @keyup.enter="handleQuery"
              />
            </div>
          </el-col>

          <!-- 会议室类型筛选 -->
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">会议室类型</label>
              <el-select v-model="filters.roomType" placeholder="请选择类型" clearable @change="handleQuery">
                <el-option label="小型会议室" value="SMALL" />
                <el-option label="大型会议室" value="LARGE" />
              </el-select>
            </div>
          </el-col>

          <!-- 容量范围筛选 -->
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">容量范围</label>
              <el-row :gutter="5">
                <el-col :span="11">
                  <el-input v-model="filters.minCapacity" type="number" placeholder="最小" />
                </el-col>
                <el-col :span="2" class="flex items-center justify-center">-</el-col>
                <el-col :span="11">
                  <el-input v-model="filters.maxCapacity" type="number" placeholder="最大" />
                </el-col>
              </el-row>
            </div>
          </el-col>

          <!-- 会议室状态筛选 -->
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">状态</label>
              <el-select v-model="filters.roomStatus" placeholder="请选择状态" clearable @change="handleQuery">
                <el-option label="可用" value="AVAILABLE" />
                <el-option label="禁用" value="DISABLED" />
                <el-option label="维护中" value="MAINTENANCE" />
              </el-select>
            </div>
          </el-col>
        </el-row>

        <!-- 设备筛选和操作按钮 -->
        <el-row :gutter="20" class="mt-3">
          <el-col :span="18">
            <div class="filter-item">
              <label class="filter-label">设备筛选</label>
              <el-select
                v-model="filters.equipmentIdList"
                placeholder="请选择设备"
                multiple
                collapse-tags
                collapse-tags-tooltip
                :max-collapse-tags="7"
              >
                <el-option
                  v-for="eq in equipmentList"
                  :key="eq.equipmentId"
                  :label="eq.equipmentName"
                  :value="eq.equipmentId"
                />
              </el-select>
            </div>
          </el-col>

          <!-- 操作按钮 -->
          <el-col :span="6">
            <div class="filter-item">
              <label class="filter-label">&nbsp;</label>
              <div class="button-group">
                <el-button type="primary" @click="handleQuery"> 查询 </el-button>
                <el-button @click="handleReset"> 重置 </el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 会议室卡片列表 -->
    <div class="card-container" v-loading="loading" element-loading-text="加载中...">
      <el-row :gutter="20">
        <el-col :span="6" v-for="room in meetingRoomList" :key="room.roomId" class="card-col">
          <MeetingRoomCard
            :room-info="room"
            :equipment-list="getEquipmentList(room.roomId)"
            @card-click="handleCardClick"
          />
          <!-- 预约按钮 -->
          <div class="reserve-btn-container">
            <el-button
              type="primary"
              size="small"
              @click="handleReserveClick(room)"
              :disabled="room.roomStatus !== 'AVAILABLE' || (room.roomType === 'LARGE' && !accountInfo.isManager)"
            >
              预约
            </el-button>
          </div>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <div class="empty-state" v-if="meetingRoomList.length === 0 && !loading">
        <el-empty description="没有找到符合条件的会议室" />
      </div>
    </div>

    <!-- 分页控件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[8, 12, 20]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 会议室详情弹窗 -->
    <el-dialog :title="currentRoom?.roomName || '会议室详情'" v-model="dialogVisible" width="600px">
      <div v-if="currentRoom" class="room-detail-dialog">
        <div class="detail-image">
          <el-image
            :src="currentRoom.imageUrl || defaultImage"
            fit="cover"
            :preview-src-list="currentRoom.imageUrl ? [currentRoom.imageUrl] : []"
          />
        </div>

        <div class="detail-info">
          <el-descriptions column="1" border>
            <el-descriptions-item label="会议室ID">{{ currentRoom.roomId }}</el-descriptions-item>
            <el-descriptions-item label="名称">{{ currentRoom.roomName }}</el-descriptions-item>
            <el-descriptions-item label="类型">{{
              currentRoom.roomType === 'SMALL' ? '小型会议室' : '大型会议室'
            }}</el-descriptions-item>
            <el-descriptions-item label="容量">{{ currentRoom.capacity }}人</el-descriptions-item>
            <el-descriptions-item label="位置">{{ currentRoom.location }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(currentRoom.roomStatus)">
                {{ getStatusText(currentRoom.roomStatus) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="设备">
              <div class="equipment-tags">
                <el-tag v-for="eq in currentRoomEquipments" :key="eq.equipmentId" size="small">
                  {{ eq.equipmentName }}
                </el-tag>
                <span v-if="currentRoomEquipments.length === 0">无设备</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDate(currentRoom.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 预约弹窗 -->
    <el-dialog title="预约会议室" v-model="reserveDialogVisible" width="500px" :before-close="handleReserveDialogClose">
      <div v-if="selectedRoom" class="reserve-form">
        <el-form ref="reserveFormRef" :model="reserveForm" label-width="100px" :rules="bookRules">
          <el-form-item label="会议室" prop="roomId">
            <el-input v-model="selectedRoom.roomName" disabled />
          </el-form-item>

          <el-form-item label="预约日期" prop="reservationDate">
            <el-select v-model="reserveForm.reservationDate" placeholder="请选择预约日期" @change="handleDateChange">
              <el-option v-for="date in availableDates" :key="date" :label="formatDate(date)" :value="date" />
            </el-select>
          </el-form-item>

          <el-form-item label="开始时间" prop="startTime">
            <el-select v-model="reserveForm.startTime" placeholder="请选择开始时间" @change="handleStartTimeChange">
              <el-option
                v-for="(slot, index) in availableTimeSlots"
                :key="slot.startTime"
                :label="slot.startTime"
                :value="slot.startTime"
                :disabled="isSlotDisabled(slot.startTime, index)"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="结束时间" prop="endTime">
            <el-select v-model="reserveForm.endTime" placeholder="请选择结束时间" :disabled="!reserveForm.startTime">
              <el-option
                v-for="(slot, index) in availableTimeSlots"
                :key="slot.endTime"
                :label="slot.endTime"
                :value="slot.endTime"
                :disabled="isEndTimeDisabled(slot.endTime, index)"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="会议主题" prop="meetingTopic">
            <el-input v-model="reserveForm.meetingTopic" placeholder="请输入会议主题" maxlength="127" />
          </el-form-item>

          <el-form-item label="会议描述" prop="meetingDesc">
            <el-input
              v-model="reserveForm.meetingDesc"
              placeholder="请输入会议描述"
              type="textarea"
              :rows="3"
              maxlength="2000"
            />
          </el-form-item>

          <el-form-item label="参会人员" prop="attendees" :disabled="reserveForm.mentionAll">
            <el-select
              v-model="reserveForm.attendees"
              placeholder="请选择参会人员"
              multiple
              filterable
              collapse-tags
              collapse-tags-tooltip
            >
              <el-option v-for="emp in deptEmployees" :key="emp.empId" :label="emp.empName" :value="emp.empId" />
            </el-select>
            <div class="form-hint">只能选择本部门员工</div>
          </el-form-item>

          <el-form-item label="提及所有人" prop="mentionAll">
            <el-switch v-model="reserveForm.mentionAll" :disabled="!accountInfo?.isManager" />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="handleReserveDialogClose()">取消</el-button>
        <el-button type="primary" @click="submitReservation">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useApi } from '@/composables/useApi.js'
import { useAccountStore } from '@/stores/account.js'
import MeetingRoomCard from '@/components/MeetingRoomCard.vue'

const http = useApi()
const accountStore = useAccountStore()

// 状态管理
const loading = ref(false)
const meetingRoomList = ref([])
const equipmentList = ref([])
const roomEquipmentMap = ref(new Map()) // 存储会议室ID到设备列表的映射

// 对话框状态
const dialogVisible = ref(false)
const currentRoom = ref(null)
const currentRoomEquipments = ref([])

// 员工信息相关
const accountInfo = computed(() => accountStore.accountInfo)
const deptEmployees = ref([])
const accountId = accountInfo.value.accountId

// 筛选条件
const filters = reactive({
  roomName: '',
  roomType: '',
  minCapacity: '',
  maxCapacity: '',
  location: '',
  roomStatus: '',
  equipmentIdList: [],
})

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 8,
  total: 0,
  pages: 0,
})

const reserveFormRef = ref(null)

// 预约相关状态
const reserveDialogVisible = ref(false)
const selectedRoom = ref(null)
const availableDates = ref([])
const availableTimeSlots = ref([])
const reserveForm = reactive({
  roomId: null,
  reservationDate: '',
  startTime: '',
  endTime: '',
  meetingTopic: '',
  meetingDesc: '',
  attendees: [],
  mentionAll: false,
})

// 页面加载时查询数据
onMounted(() => {
  fetchMeetingRoomList()
  fetchEquipmentList()
  fetchDeptEmployees()
})

const bookRules = reactive({
  reservationDate: [{ required: true, message: '请选择预约日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  meetingTopic: [
    { required: true, message: '请输入会议主题', trigger: 'blur' },
    { max: 127, message: '会议主题不能超过127个字符', trigger: 'blur' },
  ],
  meetingDesc: [{ max: 2000, message: '会议描述不能超过2000个字符', trigger: 'blur' }],
})

const formatDate = (dateString) => {
  if (!dateString) return ''
  // 只保留日期部分并格式化
  return new Date(dateString)
    .toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    })
    .replace(/\//g, '-')
}

// 获取同部门员工列表
const fetchDeptEmployees = async () => {
  try {
    const response = await http.post('/emp/getSimpleDeptEmp')
    if (response.code === 2001) {
      deptEmployees.value = (response.data || []).filter((emp) => emp.empId !== accountId)
    } else {
      ElMessage.error(response.msg || '获取部门员工列表失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '获取部门员工列表失败，请稍后重试')
    } else {
      console.error('获取部门员工列表失败:', error)
      ElMessage.error('获取部门员工列表失败，请稍后重试')
    }
  }
}

// 查询会议室列表
const fetchMeetingRoomList = async () => {
  try {
    loading.value = true

    // 构建请求体数据
    const requestData = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...(filters.roomName && { roomName: filters.roomName }),
      ...(filters.roomType && { roomType: filters.roomType }),
      ...(filters.minCapacity && { minCapacity: Number(filters.minCapacity) }),
      ...(filters.maxCapacity && { maxCapacity: Number(filters.maxCapacity) }),
      ...(filters.location && { location: filters.location }),
      ...(filters.roomStatus && { roomStatus: filters.roomStatus }),
      ...(filters.equipmentIdList.length > 0 && { equipmentIdList: filters.equipmentIdList }),
    }

    const response = await http.post('/meetingRoom/queryMeetingRooms', requestData)

    if (response.code === 2001) {
      meetingRoomList.value = response.data.list || []
      pagination.total = response.data.total || 0
      pagination.pages = response.data.pages || 0

      // 批量获取所有会议室的设备信息
      await Promise.all(meetingRoomList.value.map((room) => fetchRoomEquipment(room.roomId)))
    } else {
      ElMessage.error(response.msg || '会议室信息查询失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '查询失败，请稍后重试')
    } else {
      console.error('查询会议室列表失败:', error)
      ElMessage.error('查询失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 获取设备列表
const fetchEquipmentList = async () => {
  try {
    const response = await http.post('/equip/getSimpleEquipmentList')
    if (response.code === 2001) {
      equipmentList.value = response.data || []
    } else {
      ElMessage.error(response.msg || '设备列表查询失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '查询失败，请稍后重试')
    } else {
      console.error('获取设备列表失败:', error)
      ElMessage.error('查询失败，请稍后重试')
    }
  }
}

// 获取指定会议室的设备
const fetchRoomEquipment = async (roomId) => {
  try {
    const response = await http.post('/meetingRoom/getRoomEquipmentList', roomId)
    if (response.code === 2001) {
      roomEquipmentMap.value.set(roomId, response.data || [])
      return true
    } else {
      ElMessage.error(response.msg || '获取会议室设备失败')
      return false
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '获取会议室设备失败，请稍后重试')
    } else {
      console.error('获取会议室设备失败:', error)
      ElMessage.error('获取会议室设备失败，请稍后重试')
    }
    return false
  }
}

// 根据会议室ID获取设备列表
const getEquipmentList = (roomId) => {
  return roomEquipmentMap.value.get(roomId) || []
}

// 处理查询
const handleQuery = () => {
  pagination.pageNum = 1
  fetchMeetingRoomList()
}

// 处理重置
const handleReset = () => {
  Object.keys(filters).forEach((key) => {
    if (key === 'equipmentIdList') {
      filters[key] = []
    } else {
      filters[key] = ''
    }
  })
  pagination.pageNum = 1
  pagination.pageSize = 6
  fetchMeetingRoomList()
}

// 处理页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchMeetingRoomList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  fetchMeetingRoomList()
}

// 卡片点击事件
const handleCardClick = async (room) => {
  currentRoom.value = room
  currentRoomEquipments.value = getEquipmentList(room.roomId)
  dialogVisible.value = true
}

// 处理预约按钮点击
const handleReserveClick = async (room) => {
  if (room.roomStatus !== 'AVAILABLE') {
    ElMessage.warning('只有可用状态的会议室可以预约')
    return
  }

  // 非经理不能预约大型会议室
  if (!accountInfo.value?.isManager && room.roomType === 'LARGE') {
    ElMessage.warning('只有经理可以预约大型会议室')
    return
  }

  selectedRoom.value = room
  reserveForm.roomId = room.roomId
  reserveDialogVisible.value = true

  // 获取可用日期
  await fetchAvailableDates(room.roomId)
}

// 获取可用日期列表
const fetchAvailableDates = async (roomId) => {
  try {
    const response = await http.post('/roomAvail/getRoomAvailDateList', roomId)
    if (response.code === 2001) {
      availableDates.value = response.data.dates || []
    } else {
      ElMessage.error(response.msg || '获取可用日期失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '获取可用日期失败，请稍后重试')
    } else {
      console.error('获取可用日期失败:', error)
      ElMessage.error('获取可用日期失败，请稍后重试')
    }
  }
}

// 表单重置函数
const resetReservationForm = () => {
  reserveForm.roomId = null
  reserveForm.reservationDate = ''
  reserveForm.startTime = ''
  reserveForm.endTime = ''
  reserveForm.meetingTopic = ''
  reserveForm.meetingDesc = ''
  reserveForm.attendees = []
  reserveForm.mentionAll = false
  availableDates.value = []
  availableTimeSlots.value = []
  selectedRoom.value = null
}

// 处理日期变更
const handleDateChange = async (date) => {
  if (!date || !reserveForm.roomId) return

  reserveForm.startTime = ''
  reserveForm.endTime = ''
  availableTimeSlots.value = []

  try {
    const response = await http.post('/roomAvail/getRoomAvailTimeSlotList', {
      roomId: reserveForm.roomId,
      date: date,
    })
    if (response.code === 2001) {
      // console.log(response.data)
      availableTimeSlots.value = response.data.timeSlotList || []
    } else {
      ElMessage.error(response.msg || '获取可用时间段失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '获取可用时间段失败，请稍后重试')
    } else {
      console.error('获取可用时间段失败:', error)
      ElMessage.error('获取可用时间段失败，请稍后重试')
    }
  }
}

// 处理开始时间变更
const handleStartTimeChange = () => {
  if (!reserveForm.startTime) return

  // 找到选中的开始时间段
  const selectedSlot = availableTimeSlots.value.find((slot) => slot.startTime === reserveForm.startTime)

  if (selectedSlot) {
    // 结束时间默认设为当前时间段的结束时间
    reserveForm.endTime = selectedSlot.endTime
  } else {
    reserveForm.endTime = ''
  }
}

// 添加新的时间段禁用判断方法
const isSlotDisabled = (startTime, currentIndex) => {
  // 检查当前时间段是否与前一个时间段连续
  if (currentIndex > 0) {
    const prevSlot = availableTimeSlots.value[currentIndex - 1]
    // 如果与前一个时间段不连续，则该时间段不能作为开始时间
    return prevSlot.endTime !== startTime
  }
  return false
}

// 更新结束时间禁用判断方法
const isEndTimeDisabled = (endTime, currentIndex) => {
  if (!reserveForm.startTime) return true

  // 找到开始时间所在的时间段索引
  const startIndex = availableTimeSlots.value.findIndex((slot) => slot.startTime === reserveForm.startTime)

  if (startIndex === -1) return true

  // 结束时间必须在开始时间之后
  if (currentIndex < startIndex) return true

  // 检查时间段是否连续
  for (let i = startIndex; i < currentIndex; i++) {
    const current = availableTimeSlots.value[i]
    const next = availableTimeSlots.value[i + 1]
    if (current.endTime !== next.startTime) {
      return true // 时间段不连续，禁用
    }
  }

  return false
}

// 提交预约
const submitReservation = async () => {
  try {
    // 验证经理是否使用了@全体功能
    if (!accountInfo.value?.isManager && reserveForm.mentionAll) {
      ElMessage.warning('只有经理可以使用@全体功能')
      return
    }

    // @全体优先级高于邀请
    if (reserveForm.mentionAll && reserveForm.attendees.length > 0) {
      reserveForm.attendees = [] // 清空
    }

    // 先进行表单验证
    await reserveFormRef.value.validate()

    const response = await http.post('/res/reservation', reserveForm)

    if (response.code === 2001) {
      ElMessage.success('预约成功')
      reserveDialogVisible.value = false
      // 可以刷新列表或做其他后续处理
    } else {
      ElMessage.error(response.msg || '预约失败')
    }
  } catch (error) {
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '预约失败，请稍后重试')
    } else {
      console.error('提交预约失败:', error)
      ElMessage.error('预约失败，请稍后重试')
    }
  } finally {
    // 重置预约信息
    resetReservationForm()
  }
}

// 关闭预约弹窗时重置表单
const handleReserveDialogClose = () => {
  resetReservationForm()
  reserveDialogVisible.value = false
}

// 状态格式化工具函数
const getStatusText = (status) => {
  switch (status) {
    case 'AVAILABLE':
      return '可用'
    case 'DISABLED':
      return '禁用'
    case 'MAINTENANCE':
      return '维护中'
    default:
      return status
  }
}

const getStatusType = (status) => {
  switch (status) {
    case 'AVAILABLE':
      return 'success'
    case 'DISABLED':
      return 'danger'
    case 'MAINTENANCE':
      return 'warning'
    default:
      return 'info'
  }
}

// 默认图片
const defaultImage = 'https://picsum.photos/seed/meetingroom/600/300'
</script>

<style scoped>
.meeting-room-view {
  padding: 24px;
  max-width: 1500px;
  margin: 0 auto;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-container {
  margin-bottom: 10px;
}

.filter-item {
  display: flex;
  align-items: center;
}

.filter-label {
  width: 80px;
  text-align: right;
  margin-right: 10px;
  font-size: 14px;
  white-space: nowrap;
  line-height: 32px;

  color: #606266;
}

.button-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.card-container {
  margin: 20px 0;
}

.card-col {
  margin-bottom: 48px;
  display: flex;
  flex-direction: column;
}

.pagination-container {
  margin: 20px 0;
  margin-top: 20px;
  /* border-top: 1px solid #f0f0f0; */
  text-align: right;
}

.mt-3 {
  margin-top: 15px;
}

.empty-state {
  margin: 80px 0;
  text-align: center;
}

.room-detail-dialog {
  padding-top: 10px;
}

.detail-image {
  width: 100%;
  height: 250px;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 20px;
}

.equipment-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

/* 预约按钮样式 */
.reserve-btn-container {
  text-align: center;
  margin-top: 10px;
  flex-shrink: 0;
  width: 100%;
}

.reserve-btn-container .el-button {
  width: 100%;
}

/* 预约表单样式 */
.reserve-form {
  margin-top: 10px;
}

.form-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
