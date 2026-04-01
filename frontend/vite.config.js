import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    strictPort: true, // 强制使用5173端口,如果被占用则报错
    proxy: {
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/home': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/activity': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
