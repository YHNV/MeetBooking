import { createApp } from 'vue'
import router from './router'
import App from './App.vue'

// 导入全局CSS
import './assets/style.css'

// 导入 element-plus 样式，防止自动导入样式缺失
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'

// 创建 app 实例
const app = createApp(App)

app.use(router)

app.mount('#app') // 挂载 app
