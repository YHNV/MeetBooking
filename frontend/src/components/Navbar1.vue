<template>
  <el-container>
    <el-header class="header-container">
      <!-- 左侧Logo -->
      <div class="logo">
        <img src="/logo.png" alt="网站Logo" class="logo-img" />
        <!--<el-icon><i class="fas fa-cube"></i></el-icon>-->
        <!--<span class="logo-text">{{ appName }}</span>-->
      </div>

      <!-- 中间菜单 -->
      <el-menu
        mode="horizontal"
        :ellipsis="false"
        :default-active="activeIndex"
        class="center-menu"
        @select="handleSelect"
      >
        <el-menu-item v-for="item in menuItems" :key="item.index" :index="item.index">
          {{ item.name }}
        </el-menu-item>
      </el-menu>

      <!-- 右侧功能区 -->
      <div class="right-tools">
        <!-- 消息通知按钮 -->
        <el-button icon="bell" circle size="small" class="notification-btn" @click="handleNotificationClick">
          <span v-if="unreadCount > 0" class="notification-badge">{{ unreadCount }}</span>
        </el-button>
        <!-- 用户信息 -->
        <el-dropdown placement="bottom-end" @command="handleCommand">
          <div class="user-info">
            <el-avatar :src="userAvatar" class="user-avatar" :alt="userName + '的头像'"></el-avatar>
            <span class="username">{{ userName }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><i class="fas fa-user"></i></el-icon>
                <span>个人中心</span>
              </el-dropdown-item>
              <el-dropdown-item command="logout">
                <el-icon><i class="fas fa-sign-out-alt"></i></el-icon>
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
  </el-container>
</template>

<script setup>
import { defineProps, defineEmits, ref } from 'vue'

// 定义组件属性
const props = defineProps({
  // 应用名称，用于Logo显示
  appName: {
    type: String,
    default: 'MyApp',
  },
  // 导航菜单项
  menuItems: {
    type: Array,
    default: () => [
      { index: '1', name: '首页' },
      { index: '2', name: '产品' },
      { index: '3', name: '服务' },
      { index: '4', name: '关于我们' },
    ],
  },
  // 默认激活的菜单索引
  defaultActive: {
    type: String,
    default: '1',
  },
  // 用户名
  userName: {
    type: String,
    default: '张三',
  },
  // 用户头像URL
  userAvatar: {
    type: String,
    default: 'https://picsum.photos/id/1005/200/200',
  },
  // 未读消息数量
  unreadCount: {
    type: Number,
    default: 3,
  },
})

// 定义组件事件
const emit = defineEmits([
  'menu-select', // 菜单选择事件
  'notification-click', // 通知按钮点击事件
  'profile', // 个人中心事件
  'logout', // 退出登录事件
])

// 当前激活的菜单索引
const activeIndex = ref(props.defaultActive)

// 处理菜单选择
const handleSelect = (key, keyPath) => {
  activeIndex.value = key
  emit('menu-select', key, keyPath)
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'profile') {
    emit('profile')
  } else if (command === 'logout') {
    emit('logout')
  }
}

// 处理通知按钮点击
const handleNotificationClick = () => {
  emit('notification-click')
}
</script>

<style scoped>
/* 基础样式 */
.el-header {
  padding: 0 10%;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-width: 900px;
}

/* 布局容器 */
.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

/* Logo样式 */
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: bold;
  color: #165dff;
}

.logo i {
  font-size: 24px;
}

.logo-img {
  /*width: 32px;*/ /* 根据实际logo大小调整 */
  height: 60px;
  object-fit: contain; /* 保持图片比例 */
}

/* 中间菜单 */
.center-menu {
  flex: 1;
  justify-content: center;
  border-bottom: none;
}

/* 右侧功能区 */
.right-tools {
  display: flex;
  align-items: center;
  gap: 15px;
}

/* 消息通知按钮 */
.notification-btn {
  position: relative;
}

.notification-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background-color: #ff4d4f;
  color: white;
  border-radius: 50%;
  font-size: 12px;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-avatar {
  width: 36px;
  height: 36px;
}

.username {
  font-size: 14px;
  color: #333;
}
</style>
