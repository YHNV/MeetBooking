<template>
  <div class="body-content">
    <!--定义容器布局-->
    <el-container>
      <!-- 头部容器，用作导航栏 -->
      <el-header class="header-container">
        <!-- 左侧logo -->
        <div class="logo" @click="logo">
          <img src="/logo.png" alt="logo" />
        </div>
        <!-- 中间菜单，使用遍历，使用路由 -->
        <el-menu mode="horizontal" :default-active="activeIndex" class="center-menu" @select="handleSelect" router>
          <el-menu-item v-for="item in menuItems" :key="item.index" :index="item.index" :route="item.route">
            {{ item.name }}
          </el-menu-item>
        </el-menu>
        <!-- 右侧个人信息 -->
        <div class="right-tools">
          <!--消息通知按钮-->
          <el-button circle size="default" class="notification-btn" @click="handleNotificationClick">
            <!--通知icon-->
            <Bell class="notification-icon" />
            <!--消息数量-->
            <span v-if="msgCount > 0" class="notification-badge">{{ msgCount }}</span>
          </el-button>
          <!-- 用户信息，触碰弹窗 -->
          <el-popover placement="bottom" :width="275" trigger="hover" :show-arrow="false">
            <template #reference>
              <div class="avatar-info">
                <!-- 用户头像 -->
                <el-avatar size="default" fit="fill" class="avatar-trigger">
                  <Avatar />
                </el-avatar>
                <!-- 用户名展示 -->
                <span class="emp-name">{{ accountInfo.empName }}</span>
              </div>
            </template>
            <!-- 用户信息卡片内容 -->
            <div class="account-info-card" id="accountCard">
              <div class="card-content">
                <div class="user-info-top">
                  <div>
                    <div class="user-name text-xl font-semibold text-primary">
                      <!-- 显示员工名字和部门信息 -->
                      {{ accountInfo.empName }}
                      <span class="account-type" id="accountType">{{
                        accountInfo.isAdmin ? '管理员' : accountInfo.isManager ? '部门经理' : '部门员工'
                      }}</span>
                    </div>
                    <!-- 显示员工工号 -->
                    <div class="user-id text-md text-secondary" id="empId">
                      {{ accountInfo.empId }}
                    </div>
                  </div>
                </div>

                <!-- 显示最后登陆时间 -->
                <div class="last-login text-sm text-secondary">
                  最后登陆：<span>{{ accountInfo.lastLoginTime }}</span>
                </div>

                <!-- 显示部门以及职位信息 -->
                <div class="dept-position rounded" v-if="accountInfo.isAdmin != true">
                  <div class="dept">
                    <span class="text-sm text-secondary">部门</span>
                    <span class="text-md font-medium text-primary" id="deptName">{{ accountInfo.deptName }}</span>
                  </div>
                  <div class="position">
                    <span class="text-sm text-secondary">职位</span>
                    <span class="text-md font-medium text-primary" id="position">{{ accountInfo.position }}</span>
                  </div>
                </div>

                <!--分割线-->
                <el-divider />

                <ul class="menu-list">
                  <li class="menu-item clickable hover-bg rounded text-lg text-primary" id="profileBtn">
                    <span @click="profile(accountInfo.empId)">个人中心</span>
                    <svg class="menu-icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
                      <path
                        d="M858.5 768c-.2 37.5-30.4 68-68 68h-612c-37.6 0-67.8-30.5-68-68v-512c0-37.5 30.4-68 68-68h612c37.6 0 67.8 30.5 68 68v512zM832 800H192V288h640v512z"
                        fill="currentColor"
                      />
                      <path
                        d="M464 528a48 48 0 1 0 96 0 48 48 0 1 0-96 0zM320 592a32 32 0 1 0 0-64 32 32 0 1 0 0 64zm384 0a32 32 0 1 0 0-64 32 32 0 1 0 0 64z"
                        fill="currentColor"
                      />
                    </svg>
                  </li>
                  <li class="menu-item logout clickable hover-bg rounded text-lg" id="logoutBtn">
                    <span @click="logout(accountInfo.empId)">退出登录</span>
                    <svg class="menu-icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
                      <path
                        d="M864 352H736v-80c0-35.3-28.7-64-64-64h-80c-35.3 0-64 28.7-64 64v80H160c-17.7 0-32 14.3-32 32v448c0 17.7 14.3 32 32 32h704c17.7 0 32-14.3 32-32V384c0-17.7-14.3-32-32-32zM640 352V272h144v80H640zM192 768H160V416h32v352zm256 0H416V416h32v352zm256 0H672V416h32v352z"
                        fill="currentColor"
                      />
                      <path
                        d="M505.4 613.4l-77.2 77.2c-6.2 6.2-16.4 6.2-22.6 0l-45.4-45.4c-6.2-6.2-6.2-16.4 0-22.6l77.2-77.2-77.2-77.2c-6.2-6.2-6.2-16.4 0-22.6l45.4-45.4c6.2-6.2 16.4-6.2 22.6 0l77.2 77.2 77.2-77.2c6.2-6.2 16.4-6.2 22.6 0l45.4 45.4c6.2 6.2 6.2 16.4 0 22.6l-77.2 77.2 77.2 77.2c6.2 6.2 6.2 16.4 0 22.6l-45.4 45.4c-6.2 6.2-16.4 6.2-22.6 0l-77.2-77.2z"
                        fill="currentColor"
                      />
                    </svg>
                  </li>
                </ul>
              </div>
            </div>
          </el-popover>
        </div>
      </el-header>
      <!-- 中间容器，放置路由 -->
      <el-main class="main-container">
        <RouterView />
      </el-main>
    </el-container>

    <!-- 消息通知侧边弹窗 -->
    <!-- 消息通知面板 -->
    <transition name="slide-fade">
      <div v-if="showNotificationPanel" class="notification-panel">
        <!-- 这里放你的消息通知内容 -->
        <div class="notification-header">
          <h3>消息通知</h3>
          <el-button circle size="small" @click="showNotificationPanel = false">
            <Close />
          </el-button>
        </div>
        <div class="notification-content">
          <!-- 消息列表内容 -->
        </div>
      </div>
    </transition>

    <!-- 遮罩层 -->
    <transition name="fade">
      <div v-if="showNotificationPanel" class="notification-overlay" @click="showNotificationPanel = false"></div>
    </transition>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Bell from '@/components/svg/Bell.vue'
import Avatar from '@/components/svg/Avatar.vue'
import avatar from '@/assets/images/avatar.svg'
import { formatDate } from '@/utils/date.js'

import { useRouter } from 'vue-router'
import { useAccountStore } from '@/stores/account.js'
import { useApi } from '@/composables/useApi.js'

const http = useApi()
const router = useRouter()
const accountStore = useAccountStore()

// 定义默认个人信息对象
const accountInfo = ref({
  empId: 0,
  empName: '未登录用户',
  isAdmin: false,
  isManager: true,
  firstLogin: false,
  lastLoginTime: formatDate(new Date()),
  deptName: '未获取',
  position: '未获取',
  token: '未获取',
})

// 获取小菠萝中的账号信息
accountInfo.value = accountStore.accountInfo
// 转换日期格式
accountInfo.value.lastLoginTime = formatDate(accountStore.accountInfo.lastLoginTime)
console.log(accountInfo.value)

// 菜单栏
const menuItems = ref([
  { index: '1', name: '首页', route: '/' },
  { index: '2', name: '会议室列表', route: '/rooms' },
  { index: '3', name: '我的预约', route: '/booking' },
  { index: '4', name: '公告中心', route: '/notice' },
])

// 定义默认激活菜单索引
const activeIndex = ref('1')

// 消息数量
const msgCount = ref(3)

// 处理菜单选择
const handleSelect = (index) => {
  console.log(index)
  // 1. 根据点击的菜单index，在菜单数组中查找对应的菜单项
  // const selectedItem = menuItems.value.find(item => item.index === index)
  // console.log(selectedItem)
  // // 2. 如果找到对应的菜单项
  // if (selectedItem) {
  //   // 3. 使用vue-router的push方法跳转到该菜单项配置的路由
  //   router.push(selectedItem.route)
  // }
}

// logo点击事件
const logo = () => {
  console.log('点击logo')
  router.push('/')
  activeIndex.value = '1'
  console.log(activeIndex.value)
}

const showNotificationPanel = ref(false)

// 消息通知点击事件
const handleNotificationClick = () => {
  // 点击通知按钮弹出侧边消息栏
  console.log('消息数量为：' + msgCount.value)
  showNotificationPanel.value = !showNotificationPanel.value
}

// 个人中心点击事件
const profile = (empId) => {
  router.push('/profile')
  console.log('profile:' + empId)
}

// 退出登录点击事件
const logout = async (empId) => {
  console.log('logout:' + empId)
  // 调用小菠萝退出登录
  // accountStore.logout()

  // ElMessage.success('退出登录成功')
  // router.push('/')

  // 调用后端退出登录接口
  try {
    const response = await http.post('/auth/logout', empId)
    console.log(response)

    if (response.code !== 20003) {
      ElMessage.success(response.msg)
      accountStore.logout()
      await router.push('/login')
      return
    }

    // 退出成功，给一个弹窗
    ElMessage.info(response.msg)
    accountStore.logout()

    await router.push('/')
  } catch (error) {
    console.log('服务器异常' + error)
    ElMessage.error('服务器异常')
    return
  }
}

// 监听路由变化，自动更新选中的菜单
watch(
  // 监听路由路径变化
  () => router.currentRoute.value.path,
  (newPath) => {
    console.log('newPath:' + newPath)
    // 找到对应路径的菜单项index
    const matchedItem = menuItems.value.find((item) => item.route === newPath)
    if (matchedItem) {
      activeIndex.value = matchedItem.index
      console.log(activeIndex.value, matchedItem.index)
    } else {
      activeIndex.value = 0
    }
  },
  { immediate: true }, // 初始加载时就执行一次
)

// 挂载登录检查
onMounted(() => {
  accountStore.init()
  console.log(accountStore.accountInfo)
})
</script>

<style scoped>
.body-content {
  height: 100%;
}

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
  height: 64px;
}

.logo {
  cursor: pointer;
  display: flex;
  align-items: center;
}

/*logo*/
.logo img {
  pointer-events: none; /* 让点击穿透到父元素 */
  height: 50px;
}

/* 中间菜单 */
.center-menu {
  margin-left: -20px;
  flex: 1;
  justify-content: center;
  border-bottom: none !important;
}

.center-menu li {
  font-size: 16px;
  padding: 0 15px !important;
}

/* 右侧功能区 */
.right-tools {
  /*padding: 0 10px;*/
  display: flex;
  align-items: center;
  /*设置子元素之间的间隔为15px*/
  gap: 15px;
}

/* 消息通知按钮 */
.notification-btn {
  position: relative;
  display: flex; /* 启用 Flex 布局 */
  align-items: center; /* 垂直居中 */
  justify-content: center;
}

.notification-icon {
  width: 16px;
  height: 16px;
}

.notification-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background-color: #ff4d4f;
  color: white;
  border-radius: 50%;
  font-size: 10px;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

/* 头像触发样式 */
.avatar-trigger {
  cursor: pointer;
  margin-left: 5px;
  flex-shrink: 0;
}

/* 用户信息卡片 */
.account-info-card {
  padding: 4px;
}

.account-id {
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 20px;
  /*display: block;*/
  padding: 5px 0px;
  border-radius: 15px;
  /*background-color: #ecf5ff;*/
}

/*.account-meta, .emp-dept {
  !*display: flex;
  justify-content: space-between;*!
  font-size: 12px;
  color: #606266;
  margin: 8px 0;
}*/
.account-meta {
  text-align: center;
}

.account-type {
  padding: 8px 0;
  font-weight: bold;
  font-size: 16px;
}

.emp-dept {
  /*display: flex;
  justify-content: space-between;*/
  /*font-size: 12px;*/
  text-align: justify;
  justify-self: center;
  color: #606266;
  margin: 8px 10px;
}

/* .menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  font-size: 16px;
  padding: 5px 10px;
  user-select: none;
} */

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  font-size: 16px;
  padding: 12px 16px;
  margin-bottom: 4px;
  user-select: none;
}

.logout {
  color: #f56c6c;
}

/*消息通知侧边栏*/
/* 消息通知面板样式 */
.notification-panel {
  position: fixed;
  top: 64px; /* 与导航栏高度一致 */
  right: 0;
  width: 400px;
  height: calc(100vh - 64px); /* 减去导航栏高度 */
  background-color: white;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.15);
  z-index: 2001;
  padding: 20px;
  overflow-y: auto;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.notification-content {
  /* 消息内容样式 */
  color: unset;
}

/* 遮罩层样式 */
.notification-overlay {
  position: fixed;
  top: 64px; /* 从导航栏下方开始 */
  left: 0;
  width: 100%;
  height: calc(100vh - 64px); /* 减去导航栏高度 */
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 2000;
}

/* 过渡动画 */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateX(100%);
  opacity: 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* -------------------------------------------------------------------- */
/* 共享样式 - 提取重复定义 */
.clickable {
  cursor: pointer;
  transition: all 0.2s ease;
}

.hover-bg:hover {
  background-color: #f7f8fa;
}

.rounded {
  border-radius: 8px;
}

.text-primary {
  color: #1d2129;
}

.text-secondary {
  color: #86909c;
}

.text-sm {
  font-size: 12px;
}

.text-md {
  font-size: 14px;
}

.text-lg {
  font-size: 15px;
}

.text-xl {
  font-size: 20px;
}

.font-medium {
  font-weight: 500;
}

.font-semibold {
  font-weight: 600;
}

.trigger-wrapper {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 24px;
}

.avatar-trigger {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-weight: 500;
  /* box-shadow: 0 4px 12px rgba(64, 158, 255, 0.25); */
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.avatar-trigger:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.3);
}

.avatar-trigger.active {
  border-color: rgba(64, 158, 255, 0.3);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.account-info-card.show {
  opacity: 1;
  transform: scale(1);
  pointer-events: all;
}

/* 卡片内容区域 */
.card-content {
  /* padding: 50px 24px 20px; */
  padding: 12px 10px;
}

/* 信息布局 */
.user-info-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 8px;
}

.user-id {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
}

.user-id::before {
  content: '工号:';
  margin-right: 6px;
  /* color: #c9cdD4; */
  color: #86909c;
}

.last-login {
  text-align: center;
  margin-bottom: 16px;
}

/* 部门和职位 */
.dept-position {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background-color: #f7f8fa;
  margin-bottom: 24px;
}

.dept,
.position {
  display: flex;
  flex-direction: column;
}

/* 菜单样式 */
.menu-list {
  list-style: none;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 4px;
}

.menu-item.logout {
  color: #f53f3f;
}

.menu-icon {
  width: 20px;
  height: 20px;
  opacity: 0.5;
  transition: opacity 0.2s ease;
}

.menu-item:hover .menu-icon {
  opacity: 0.8;
}

/* 账户类型标签 */
.account-type {
  display: inline-block;
  padding: 3px 10px;
  background-color: rgba(64, 158, 255, 0.1);
  color: #409eff;
  font-size: 12px;
  font-weight: 500;
  border-radius: 12px;
  margin-left: 8px;
}

:deep(.custom-popover) {
  padding: 0;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  border: none;
  overflow: hidden;
}
</style>
