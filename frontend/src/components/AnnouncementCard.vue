<template>
  <div class="announcement-card" :class="{ disabled: !announcement.isActive }">
    <!-- 顶部渐变装饰条 -->
    <div class="card-gradient-bar"></div>

    <!-- 卡片头部 -->
    <div class="card-header">
      <h3 class="card-title">{{ announcement.title }}</h3>
      <span class="status-badge" :class="isRecent ? 'active' : 'inactive'">
        {{ isRecent ? '最新' : '历史' }}
      </span>
    </div>

    <!-- 卡片内容 -->
    <div class="card-content">
      <p class="announcement-text">
        {{ truncatedContent }}
        <span v-if="announcement.content.length > 150" class="read-more" @click="toggleExpand">
          {{ isExpanded ? '收起' : '阅读更多' }}
        </span>
      </p>
    </div>

    <!-- 卡片底部 -->
    <div class="card-footer">
      <div class="meta-info">
        <span class="author">发布人: {{ announcement.senderId }}</span>
        <span class="date">{{ formatDate(announcement.createTime) }}</span>
      </div>
      <div class="card-corner"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { formatDate } from '@/utils/date.js'

const props = defineProps({
  announcement: {
    type: Object,
    required: true,
    default: () => ({
      announcementId: '',
      title: '',
      content: '',
      senderId: '',
      createTime: '',
      isActive: true,
    }),
  },
})

const isRecent = computed(() => {
  if (!props.announcement.createTime) return false
  // 解析公告日期（兼容ISO格式）
  const announcementDate = new Date(props.announcement.createTime).getTime()
  // 计算3天前的时间戳（毫秒）
  const threeDaysAgo = Date.now() - 3 * 24 * 60 * 60 * 1000
  // 判断是否在最近3天内
  return announcementDate >= threeDaysAgo
})

const isExpanded = ref(false)

// 处理内容展开/收起
const toggleExpand = () => {
  isExpanded.value = !isExpanded.value
}

// 根据展开状态显示不同内容长度
const truncatedContent = computed(() => {
  if (isExpanded.value) {
    return props.announcement.content
  }
  return props.announcement.content.length > 150
    ? props.announcement.content.slice(0, 150) + '...'
    : props.announcement.content
})
</script>

<style scoped>
.announcement-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  position: relative;
  transition: all 0.3s ease;
  border: 1px solid #e6f4ff; /* 整体浅蓝色边框 */
  overflow: hidden; /* 确保渐变条不超出卡片 */
  margin-bottom: 20px;
}

/* 顶部渐变装饰条 */
.card-gradient-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #99d5ff, #66b2ff);
}

.announcement-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(153, 213, 255, 0.2);
  border-color: #bde0fe;
}

.announcement-card.disabled {
  border-color: #e0e0e0;
  opacity: 0.7;
}

.announcement-card.disabled .card-gradient-bar {
  background: linear-gradient(90deg, #cccccc, #999999);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
  padding-top: 5px; /* 给顶部渐变条留出空间 */
}

.card-title {
  font-size: 20px;
  margin: 0;
  color: #333;
  font-weight: 600;
  line-height: 1.3;
}

.status-badge {
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background-color: #e6f4ff;
  color: #1e88e5;
}

.status-badge.inactive {
  background-color: #f5f5f5;
  color: #666;
}

.card-content {
  margin-bottom: 20px;
}

.announcement-text {
  margin: 0;
  color: #555;
  line-height: 1.6;
  font-size: 16px;
}

.read-more {
  color: #1e88e5;
  cursor: pointer;
  font-weight: 500;
  margin-left: 5px;
}

.read-more:hover {
  text-decoration: underline;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px dashed #e6f4ff;
}

.meta-info {
  font-size: 13px;
  color: #888;
}

.author {
  margin-right: 15px;
}

.date {
  white-space: nowrap;
}

.card-corner {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 0 0 20px 20px;
  border-color: transparent transparent #f0f7ff transparent; /* 角落装饰改为浅蓝 */
  border-radius: 0 0 0 4px;
}
</style>
