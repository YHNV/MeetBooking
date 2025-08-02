import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 配置根路径别名
import path from 'path'

// 配置 element-plus 自动导入
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig({
  server: {
    allowedHosts: true, // 允许所有域名访问（比如 ngrok）
    // proxy: {
    //   // 配置代理规则，例如以'/api'开头的请求都转发到后端
    //   '/api': {
    //     target: 'http://localhost:8080', // 后端接口地址
    //     changeOrigin: true, // 允许跨域
    //     // 去掉请求路径中的'/api'前缀
    //     rewrite: (path) => path.replace(/^\/api/, '')
    //   }
    // }
  },
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    }
  }
})
