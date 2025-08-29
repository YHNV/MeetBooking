<template>
  <el-card class="meeting-room-card" @click="handleCardClick">
    <div class="card-header">
      <div class="room-name">{{ roomInfo.roomName }}</div>
      <el-tag :type="statusType" class="status-tag">
        {{ statusText }}
      </el-tag>
    </div>

    <div class="card-body">
      <div class="room-image">
        <el-image
          :src="roomInfo.imageUrl || defaultImage"
          fit="cover"
        />
      </div>

      <div class="room-details">
        <div class="detail-item">
          <!-- <el-icon><location /></el-icon> -->
          <span>{{ roomInfo.location || '未知位置' }}</span>
        </div>
        <div class="detail-item">
          <!-- <el-icon><user /></el-icon> -->
          <span>容量: {{ roomInfo.capacity }}人</span>
        </div>
        <div class="detail-item">
          <!-- <el-icon><building /></el-icon> -->
          <span>类型: {{ roomTypeText }}</span>
        </div>
        <div class="detail-item equipment-list">
          <!-- <el-icon><tools /></el-icon> -->
          <div class="equipment-tags">
            <el-tag v-for="eq in equipmentList" :key="eq.equipmentId" size="small" effect="light">
              {{ eq.equipmentName }}
            </el-tag>
            <span v-if="equipmentList.length === 0">无设备</span>
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
//   import { Location, User, Building, Tools } from '@element-plus/icons-vue'

const props = defineProps({
  roomInfo: {
    type: Object,
    required: true,
    default: () => ({}),
  },
  equipmentList: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['cardClick'])

// 处理卡片点击
const handleCardClick = () => {
  emit('cardClick', props.roomInfo)
}

// 格式化会议室类型
const roomTypeText = computed(() => {
  return props.roomInfo.roomType === 'SMALL' ? '小型会议室' : '大型会议室'
})

// 格式化状态文本和样式
const statusText = computed(() => {
  switch (props.roomInfo.roomStatus) {
    case 'AVAILABLE':
      return '可用'
    case 'DISABLED':
      return '禁用'
    case 'MAINTENANCE':
      return '维护中'
    default:
      return '未知'
  }
})

const statusType = computed(() => {
  switch (props.roomInfo.roomStatus) {
    case 'AVAILABLE':
      return 'success'
    case 'DISABLED':
      return 'danger'
    case 'MAINTENANCE':
      return 'warning'
    default:
      return 'info'
  }
})

// 默认图片
const defaultImage = 'https://picsum.photos/seed/meetingroom/400/200'
</script>

<style scoped>
.meeting-room-card {
  height: 100%;
  transition: all 0.3s ease;
  cursor: pointer;
  overflow: hidden;
}

.meeting-room-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.room-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.status-tag {
  font-size: 12px;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.room-image {
  width: 100%;
  height: 160px;
  border-radius: 6px;
  overflow: hidden;
}

.room-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #666;
}

.detail-item el-icon {
  margin-right: 8px;
  color: #67c23a;
  width: 16px;
  height: 16px;
}

.equipment-list {
  flex-wrap: wrap;
  gap: 5px;
}

.equipment-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}
</style>
