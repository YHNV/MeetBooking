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
        <el-image :src="roomInfo.imageUrl || defaultImage" fit="cover" />
      </div>

      <div class="room-details">
        <div class="detail-item">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="14" height="14">
            <path
              fill="currentColor"
              d="M448 456.064V96a32 32 0 0 1 32-32.064L672 64a32 32 0 0 1 0 64H512v128h160a32 32 0 0 1 0 64H512v128a256 256 0 1 1-64 8.064M512 896a192 192 0 1 0 0-384 192 192 0 0 0 0 384"
            ></path>
          </svg>
          <span>ID: {{ roomInfo.roomId }}</span>
        </div>
        <div class="detail-item">
          <!-- <el-icon><location /></el-icon> -->
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="14" height="14">
            <path
              fill="currentColor"
              d="M800 416a288 288 0 1 0-576 0c0 118.144 94.528 272.128 288 456.576C705.472 688.128 800 534.144 800 416M512 960C277.312 746.688 160 565.312 160 416a352 352 0 0 1 704 0c0 149.312-117.312 330.688-352 544"
            ></path>
            <path
              fill="currentColor"
              d="M512 512a96 96 0 1 0 0-192 96 96 0 0 0 0 192m0 64a160 160 0 1 1 0-320 160 160 0 0 1 0 320"
            ></path>
          </svg>
          <span>{{ roomInfo.location || '未知位置' }}</span>
        </div>
        <div class="detail-item">
          <!-- <el-icon><user /></el-icon> -->
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="14" height="14">
            <path
              fill="currentColor"
              d="M512 512a192 192 0 1 0 0-384 192 192 0 0 0 0 384m0 64a256 256 0 1 1 0-512 256 256 0 0 1 0 512m320 320v-96a96 96 0 0 0-96-96H288a96 96 0 0 0-96 96v96a32 32 0 1 1-64 0v-96a160 160 0 0 1 160-160h448a160 160 0 0 1 160 160v96a32 32 0 1 1-64 0"
            ></path>
          </svg>
          <span>容量: {{ roomInfo.capacity }}人</span>
        </div>
        <div class="detail-item">
          <!-- <el-icon><building /></el-icon> -->
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="14" height="14">
            <path
              fill="currentColor"
              d="M192 128v704h384V128zm-32-64h448a32 32 0 0 1 32 32v768a32 32 0 0 1-32 32H160a32 32 0 0 1-32-32V96a32 32 0 0 1 32-32"
            ></path>
            <path
              fill="currentColor"
              d="M256 256h256v64H256zm0 192h256v64H256zm0 192h256v64H256zm384-128h128v64H640zm0 128h128v64H640zM64 832h896v64H64z"
            ></path>
            <path
              fill="currentColor"
              d="M640 384v448h192V384zm-32-64h256a32 32 0 0 1 32 32v512a32 32 0 0 1-32 32H608a32 32 0 0 1-32-32V352a32 32 0 0 1 32-32"
            ></path>
          </svg>
          <span>类型: {{ roomTypeText }}</span>
        </div>
        <div class="detail-item equipment-list">
          <!-- <el-icon><tools /></el-icon> -->
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="14" height="14">
            <path
              fill="currentColor"
              d="M256 128v698.88l196.032-156.864a96 96 0 0 1 119.936 0L768 826.816V128zm-32-64h576a32 32 0 0 1 32 32v797.44a32 32 0 0 1-51.968 24.96L531.968 720a32 32 0 0 0-39.936 0L243.968 918.4A32 32 0 0 1 192 893.44V96a32 32 0 0 1 32-32"
            ></path>
          </svg>
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
