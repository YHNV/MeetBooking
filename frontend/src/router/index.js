import { createWebHistory, createRouter } from 'vue-router'

import TestView from '@/views/TestView.vue'
import LoginView from '@/views/LoginView.vue'

const routes = [
  {
    path: '/login',
    component: LoginView,
  },
  {
    path: '/test',
    component: TestView,
  },
  // {
  //     path: '/',
  //     redirect: '/login',
  // },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
