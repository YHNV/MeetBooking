import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 配置根路径别名
import path from 'path'

import Inspect from 'vite-plugin-inspect'

// 配置 element-plus 自动导入
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
// 导入图标库
import Icons from 'unplugin-icons/vite'
import IconsResolver from 'unplugin-icons/resolver'

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
      // 自动导入Vue的API，如ref
      imports: ['vue'],
      resolvers: [
        ElementPlusResolver(),
        // 自动导入图表组件
        IconsResolver({
          prefix: 'Icon',
        }),
      ],
    }),
    Components({
      resolvers: [
        // 自动导入ElementPlus组件
        ElementPlusResolver(),
        // 自动注册图表组件
        IconsResolver({
          enabledCollections: ['ep'],
        }),
      ],
    }),
    Icons({
      autoInstall: true,
    }),
    Inspect(),
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
})
