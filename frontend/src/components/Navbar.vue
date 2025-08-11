<template>
  <div class="body-content">
    <!--定义容器布局-->
    <el-container>
      <!-- 头部容器，用作导航栏 -->
      <el-header class="header-container">
        <!-- 左侧logo -->
        <div class="logo">
          <img src="/logo.png" alt="logo"/>
        </div>
        <!-- 中间菜单，使用遍历 -->
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
          <el-button circle size="default" class="notification-btn" @click="handleNotificationClick">
            <Bell class="notification-icon"/>
            <span v-if="msgCount > 0" class="notification-badge">{{ msgCount }}</span>
          </el-button>
          <!-- 用户信息 -->
          <!-- 用户头像弹出框 -->
          <el-popover placement="bottom" :width="250" trigger="hover" :show-arrow="false">
            <template #reference>
              <div class="avatar-info">
                <!-- 用户头像 -->
                <el-avatar size="default" fit="fill" class="avatar-trigger">
                  <Avatar/>
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
                <div class="login-time">最后登录时间：<br/>{{ accountInfo.lastLoginTime }}</div>
                <div class="account-type">
                  {{ accountInfo.isAdmin ? '管理员' : (accountInfo.isManager ? '部门经理' : '部门员工') }}
                </div>
              </div>
              <div class="emp-dept" v-if="accountInfo.isAdmin != true">
                <span class="emp-dept">{{ accountInfo.deptName }}</span>
                <span class="emp-position">{{ accountInfo.position }}</span>
              </div>
              <!--分割线-->
              <el-divider/>
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
  </div>
</template>

<script setup>
import {ref} from 'vue'
import Bell from '@/components/svg/Bell.vue'
import Avatar from '@/components/svg/Avatar.vue'
import avatar from '@/assets/images/avatar.svg'
import {formatDate} from "@/utils/date.js";

import {useRouter} from 'vue-router'
import {useAccountStore} from '@/stores/account.js'
import {useApi} from "@/composables/useApi.js";

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

accountInfo.value = accountStore.accountInfo
console.log(accountInfo.value)

// 菜单栏
const menuItems = ref([
  {index: '1', name: '首页', route: '/'},
  {index: '2', name: '会议室列表', route: '/rooms'},
  {index: '3', name: '我的预约', route: '/booking'},
  {index: '4', name: '公告中心', route: '/notice'},
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

// 消息通知点击事件
const handleNotificationClick = () => {
  // 点击通知按钮弹出侧边消息栏
  console.log('消息数量为：' + msgCount.value)
}

// 个人中心点击事件
const profile = (empId) => {
  console.log('profile:' + empId)
}

// 退出登录点击事件
const logout = async (empId) => {
  console.log('logout:' + empId)

  // 调用后端退出登录接口
  try {
    const response = useApi().post('/auth/logout', accountInfo.value.empId)
    console.log(response)

    if (response.code !== 20003) {
      ElMessage.error('退出登录失败')
      return
    }

    // 退出成功，给一个弹窗
    ElMessage.success('退出登录成功')
    router.push('/')
  } catch (error) {
    console.log('服务器异常' + error)
    ElMessage.error('服务器异常')
    return
  }
}

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
  display: flex;
  align-items: center;
}

/*logo*/
.logo img {
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
</style>
