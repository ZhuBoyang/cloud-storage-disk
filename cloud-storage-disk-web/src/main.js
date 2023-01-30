import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
// router
import router from './router'
// arco design
import ArcoVue from '@arco-design/web-vue'
import '@arco-design/web-vue/dist/arco.css'
import common from './utils/commonUtils.js'
import commonType from './utils/typeUtil.js'

const app = createApp(App)
app.use(router)
app.use(ArcoVue, {
  // 用于改变使用组件时的前缀名称
  componentPrefix: 'arco'
})
// 格式化时间
app.config.globalProperties.$common = common
// 检测文件是否是视频
app.config.globalProperties.$type = commonType
app.mount('#app')
