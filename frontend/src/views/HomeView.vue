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
        <el-menu
          mode="horizontal"
          :default-active="activeIndex"
          class="center-menu"
          @select="handleSelect"
          router
        >
          <el-menu-item
            v-for="item in menuItems"
            :key="item.index"
            :index="item.index"
            :route="item.route"
          >
            {{ item.name }}
          </el-menu-item>
        </el-menu>
        <!-- 右侧个人信息 -->
        <div class="right-tools">
          <!--消息通知按钮-->
          <el-button
            circle
            size="default"
            class="notification-btn"
            @click="handleNotificationClick"
          >
            <!--通知icon-->
            <Bell class="notification-icon" />
            <!--消息数量-->
            <span v-if="msgCount > 0" class="notification-badge">{{ msgCount }}</span>
          </el-button>
          <!-- 用户信息，触碰弹窗 -->
          <el-popover placement="bottom" :width="250" trigger="hover" :show-arrow="false">
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
            <div class="account-info-card">
              <!--绑定对象获取信息-->
              <div class="account-id">
                <strong>工号：{{ accountInfo.empId }}</strong>
              </div>
              <div class="account-meta">
                <div class="login-time">最后登录时间：<br />{{ accountInfo.lastLoginTime }}</div>
                <!--判断账号类型-->
                <div class="account-type">
                  {{
                    accountInfo.isAdmin ? '管理员' : accountInfo.isManager ? '部门经理' : '部门员工'
                  }}
                </div>
              </div>
              <div class="emp-dept" v-if="accountInfo.isAdmin != true">
                <span class="emp-dept">{{ accountInfo.deptName }}</span>
                <span class="emp-position">{{ accountInfo.position }}</span>
              </div>
              <!--分割线-->
              <el-divider />
              <!-- 菜单选项 -->
              <div class="menu-item">
                <span class="personal" @click="profile(accountInfo.empId)">个人中心</span>
                <span class="logout" @click="logout(accountInfo.empId)">退出登录</span>
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
      <div
        v-if="showNotificationPanel"
        class="notification-overlay"
        @click="showNotificationPanel = false"
      ></div>
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
  padding: 12px;
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

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  font-size: 16px;
  padding: 5px 10px;
  user-select: none;
}

.personal:hover {
  color: #87c8ff;
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
</style>
