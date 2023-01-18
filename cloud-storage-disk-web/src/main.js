import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
// router
import router from './router'
// arco design
import ArcoVue from '@arco-design/web-vue'
import '@arco-design/web-vue/dist/arco.css'
import common from './utils/commonUtils.js'

const app = createApp(App)
app.use(router)
app.use(ArcoVue, {
  // 用于改变使用组件时的前缀名称
  componentPrefix: 'arco'
})
// 识别文件 icon
app.config.globalProperties.$identifyFileIcon = common.identifyFileIcon
// 将文件大小转为人类可识别数字
app.config.globalProperties.$formatSizeInPerson = common.formatSizeInPerson
// 跳转页面
app.config.globalProperties.$jumpUrl = common.jumpUrl
app.mount('#app')
