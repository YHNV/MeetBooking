import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

// 导入 element-plus 样式，防止自动导入样式缺失
import 'element-plus/dist/index.css'

// 创建 app 实例
const app = createApp(App)

app.mount('#app') // 挂载 app
