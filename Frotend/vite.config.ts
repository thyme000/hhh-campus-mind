import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    server: {
        port: 3000, // 队友原有的端口配置，不动
        proxy: {
            '/api': {
                target: 'http://localhost:8080', // 只改这一行！指向Java后端的地址
                changeOrigin: true // 保持不变
            }
        }
    }
})