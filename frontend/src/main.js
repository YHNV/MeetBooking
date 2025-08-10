import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import { useAccountStore } from '@/stores/account.js'

// 导入全局CSS
import './assets/style.css'

// 导入 element-plus 样式，防止自动导入样式缺失
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'

// 创建 app 实例
const app = createApp(App)
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

app.use(pinia)
app.use(router)

// 初始化用户信息（从localStore加载）
const accountStore = useAccountStore()
// accountStore.logout()
accountStore.init()

app.mount('#app') // 挂载 app
