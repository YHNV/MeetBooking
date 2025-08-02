import { createWebHistory, createRouter } from 'vue-router'

import TestView from '@/views/TestView.vue'
import LoginView from '@/views/LoginView.vue'
import DarkAndLight from '@/views/DarkAndLight.vue'

const routes = [
  {
    path: '/',
    component: LoginView,
  },
  {
    path: '/login',
    component: LoginView,
  },
  {
    path: '/test',
    component: TestView,
  },
  {
    path: '/dk',
    component: DarkAndLight,
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
