import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

const instance = axios.create({
  baseURL: `${window.location.protocol}//${window.location.hostname}:8080/`,
  // baseURL: '/api',
  withCredentials: true,
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json', // 默认所有请求都是 json 格式
  },
})

// 请求拦截器（自动添加 Token）
instance.interceptors.request.use(
  (config) => {
    const accountStore = localStorage.getItem('accountStore')
    console.log('请求拦截小菠萝获取：' + accountStore)
    if (accountStore) {
      try {
        const accountData = JSON.parse(accountStore)
        const token = accountData.accountInfo.token // 假设格式是 "Bearer xxxxxx"
        console.log('请求拦截Token获取：' + token)
        if (token) {
          config.headers.Authorization = token // 直接赋值
        }
      } catch (error) {
        console.error('解析 accountStore 失败:', error)
      }
    }
    return config
  },
  (error) => Promise.reject(error),
)

// 响应拦截器
instance.interceptors.response.use(
  // TODO 判断一下，如果返回的是401，弹出msg，令牌失效，清除小菠萝，跳转到登录
  (response) => {
    // 检查 data.code 是否为 401，?.先检查data是否存在
    if (response.data?.code === 401) {
      // 1. 提示错误信息
      const message = response.data?.msg || '登录已过期，请重新登录'
      ElMessage.error(message) // 替换成你的 UI 提示库

      // 2. 清除 Token（如 localStorage、Vuex/Pinia）
      localStorage.removeItem('accountStore') // 或你的存储方式

      // 3. 跳转到登录页
      router.push('/login') // 替换为你的登录路由

      // 4. 阻止后续处理（返回一个 rejected Promise）
      return Promise.reject(new Error('401 未授权，已跳转登录页'))
    }

    // 正常情况，直接返回 data
    return response.data
  }, // 直接返回 data
  (error) => Promise.reject(error),
)

export function useApi() {
  return {
    get: (url, config = {}) => instance.get(url, config),
    post: (url, data, config = {}) => instance.post(url, data, config),
  }
}
