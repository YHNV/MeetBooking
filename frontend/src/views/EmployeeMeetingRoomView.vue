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
              <el-select v-model="filters.roomType" placeholder="请选择类型" clearable>
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
              <el-select v-model="filters.roomStatus" placeholder="请选择状态" clearable>
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
                <el-button type="primary" @click="handleQuery">
                  <!-- <el-icon><search /></el-icon> -->
                  查询
                </el-button>
                <el-button @click="handleReset">
                  <!-- <el-icon><refresh /></el-icon> -->
                  重置
                </el-button>
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
        :page-sizes="[6, 12, 24]"
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
        <!-- 预约按钮暂时隐藏，等待后续开发 -->
        <!-- <el-button type="primary" @click="handleReservation">预约</el-button> -->
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useApi } from '@/composables/useApi.js'
import { formatDate } from '@/utils/date.js'
//   import { Search, Refresh } from '@element-plus/icons-vue'
import MeetingRoomCard from '@/components/MeetingRoomCard.vue'

const http = useApi()

// 状态管理
const loading = ref(false)
const meetingRoomList = ref([])
const equipmentList = ref([])
const roomEquipmentMap = ref(new Map()) // 存储会议室ID到设备列表的映射

// 对话框状态
const dialogVisible = ref(false)
const currentRoom = ref(null)
const currentRoomEquipments = ref([])

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
  pageSize: 6,
  total: 0,
  pages: 0,
})

// 页面加载时查询数据
onMounted(() => {
  fetchMeetingRoomList()
  fetchEquipmentList()
})

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
    console.error('查询会议室列表失败:', error)
    ElMessage.error('查询失败，请稍后重试')
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
    console.error('获取设备列表失败:', error)
    ElMessage.error('查询失败，请稍后重试')
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
    console.error('获取会议室设备失败:', error)
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
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.mt-3 {
  margin-top: 15px;
}

.empty-state {
  margin: 50px 0;
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
</style>
