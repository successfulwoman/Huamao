
/**
 * 应用入口文件
 * 初始化Vue应用并注册全局指令和插件
 */

import { createApp } from 'vue'
import App from './App.vue' // 根组件
import router from './router' // 路由配置
import './style.css' // 全局样式
import './assets/styles/variables.css' // 全局变量
import { lazyLoad } from './directives/lazyLoad' // 图片懒加载指令
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 创建Vue应用实例
const app = createApp(App)

// 注册插件
app.use(router)
app.use(ElementPlus)

// 注册全局指令
app.directive('lazy', lazyLoad)

// 注册全局图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 挂载应用
app.mount('#app')
