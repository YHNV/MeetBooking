<template>
  <div class="app-container">
    <el-card class="demo-card">
      <template #header>
        <div class="card-header">
          <span>ElementPlus 暗黑模式演示</span>
          <el-switch
            v-model="isDark"
            active-text="暗黑模式"
            inactive-text="明亮模式"
            @update:model-value="handleDarkModeChange"
          />
        </div>
      </template>

      <div class="demo-content">
        <el-button type="primary" class="mb-4">主要按钮</el-button>
        <el-button type="success" class="mb-4">成功按钮</el-button>
        <el-button type="warning" class="mb-4">警告按钮</el-button>
        <el-button type="danger" class="mb-4">危险按钮</el-button>

        <el-input v-model="inputValue" placeholder="请输入内容" class="mb-4" />

        <el-select v-model="selectedValue" placeholder="请选择" class="mb-4">
          <el-option label="选项1" value="1" />
          <el-option label="选项2" value="2" />
          <el-option label="选项3" value="3" />
        </el-select>

        <el-table :data="tableData" border style="width: 100%">
          <el-table-column prop="date" label="日期" width="180" />
          <el-table-column prop="name" label="姓名" width="180" />
          <el-table-column prop="address" label="地址" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

// 状态管理 - 不依赖@vueuse/core，手动实现暗黑模式切换
const isDark = ref(false)
const inputValue = ref('')
const selectedValue = ref('')

// 表格数据
const tableData = ref([
  {
    date: '2023-05-01',
    name: '张三',
    address: '北京市朝阳区',
  },
  {
    date: '2023-05-02',
    name: '李四',
    address: '上海市浦东新区',
  },
  {
    date: '2023-05-03',
    name: '王五',
    address: '广州市天河区',
  },
])

// 处理暗黑模式切换
const handleDarkModeChange = (value) => {
  isDark.value = value
  // 更新DOM上的dark类
  if (value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
  // 保存用户偏好到本地存储
  localStorage.setItem('dark-mode', value ? 'dark' : 'light')
}

// 初始化时检查用户偏好
onMounted(() => {
  const savedMode = localStorage.getItem('dark-mode')
  if (savedMode) {
    isDark.value = savedMode === 'dark'
    handleDarkModeChange(isDark.value)
  } else {
    // 默认为跟随系统
    const systemDark = window.matchMedia('(prefers-color-scheme: dark)').matches
    isDark.value = systemDark
    handleDarkModeChange(systemDark)
  }
})
</script>

<style scoped>
.app-container {
  padding: 20px;
  min-height: 100vh;
  transition: background-color 0.3s ease;
}

/* 暗黑模式下的背景色调整 */
:deep(.dark) .app-container {
  background-color: #141414;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.demo-card {
  max-width: 800px;
  margin: 0 auto;
}

.demo-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.mb-4 {
  margin-bottom: 16px !important;
}
</style>
