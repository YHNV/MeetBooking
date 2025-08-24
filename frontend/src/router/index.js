import { computed } from 'vue'
import { createWebHistory, createRouter } from 'vue-router'

import TestView from '@/views/TestView.vue'
import LoginView from '@/views/LoginView.vue'
import DarkAndLight from '@/views/DarkAndLight.vue'
import { useAccountStore } from '@/stores/account.js'
import Navbar1 from '@/components/Navbar1.vue'
import Navbar from '@/components/Navbar.vue'
import HomeView from '@/views/HomeView.vue'
import EmployeeManageView from '@/views/EmployeeManageView.vue'
import ProfileView from '@/views/ProfileView.vue'

const routes = [
  {
    path: '/',
    component: HomeView,
    children: [
      { path: 'rooms', component: TestView },
      { path: 'booking', component: Navbar },
      { path: 'notice', component: DarkAndLight },
      { path: 'profile', component: ProfileView },
    ],
    meta: { requiresAuth: true },
  },
  {
    path: '/login',
    component: LoginView,
    meta: { requiresAuth: false },
  },
  {
    path: '/test',
    component: TestView,
    meta: { requiresAuth: true },
  },
  {
    path: '/dk',
    component: DarkAndLight,
    meta: { requiresAuth: true },
  },
  {
    path: '/navbar',
    component: Navbar,
    meta: { requiresAuth: false },
  },
  {
    path: '/navbar1',
    component: Navbar1,
    meta: { requiresAuth: false },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 获取账户存储实例
  const accountStore = useAccountStore()

  const isLoggedIn = computed(() => accountStore.isLoggedIn)
  console.log(accountStore.isLoggedIn.valueOf())
  // ref不能直接获取值，需要.value
  console.log('获取是否登录：' + isLoggedIn.value)

  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    // 检查用户是否已登录
    if (isLoggedIn.value) {
      console.log('已登录')
      // 已登录，继续访问
      next()
    } else {
      console.log('未登录')
      // 未登录，重定向到登录页，并记录当前路径以便登录后返回
      ElMessage.warning('请登录')
      next({
        path: '/login',
        // query: { redirect: to.fullPath }, // 存储要访问的路径
      })
    }
  } else {
    // 不需要登录的页面直接访问
    next()
  }
})

export default router
