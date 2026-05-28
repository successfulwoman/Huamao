import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import compression from 'vite-plugin-compression'

export default defineConfig({
  plugins: [
    vue(),
    // 生产构建时生成 .gz 预压缩文件（配合 nginx gzip_static）
    compression({
      algorithm: 'gzip',
      ext: '.gz',
      threshold: 1024,  // 大于 1KB 才压缩
      deleteOriginFile: false,
    }),
    // 生产构建时生成 .br 预压缩文件（配合 nginx brotli_static）
    compression({
      algorithm: 'brotliCompress',
      ext: '.br',
      threshold: 1024,
      deleteOriginFile: false,
    }),
  ],

  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },

  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  },

  build: {
    // 目标现代浏览器，减少 polyfill 体积
    target: 'es2020',

    // 启用 CSS 代码分割
    cssCodeSplit: true,

    // chunk 大小警告阈值
    chunkSizeWarningLimit: 500,

    rollupOptions: {
      output: {
        // 手动 chunk 分割，提升缓存命中率
        manualChunks: {
          // Vue 核心单独分包
          'vendor-vue': ['vue', 'vue-router'],

          // Element Plus 单独分包（最重的依赖）
          'vendor-element': ['element-plus'],
          'vendor-element-icons': ['@element-plus/icons-vue'],

          // Axios 单独分包
          'vendor-axios': ['axios'],
        },
      },
    },

    // 小资源内联为 base64（默认 4KB，适当提高到 8KB）
    assetsInlineLimit: 8192,
  },
})
