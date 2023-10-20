import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
// router
import router from './router'
// arco design
import ArcoVue from '@arco-design/web-vue'
import '@arco-design/web-vue/dist/arco.css'
import common from './tools/common.js'
import commonType from './tools/type.js'

console.log(
  ' ________  ________  ___  ________           ________  ________  ___  ___       ___  ________   ________     \n' +
  '|\\   __  \\|\\   __  \\|\\  \\|\\   ___  \\        |\\   __  \\|\\   __  \\|\\  \\|\\  \\     |\\  \\|\\   ___  \\|\\   ____\\    \n' +
  '\\ \\  \\|\\  \\ \\  \\|\\  \\ \\  \\ \\  \\\\ \\  \\       \\ \\  \\|\\ /\\ \\  \\|\\  \\ \\  \\ \\  \\    \\ \\  \\ \\  \\\\ \\  \\ \\  \\___|    \n' +
  ' \\ \\   _  _\\ \\   __  \\ \\  \\ \\  \\\\ \\  \\       \\ \\   __  \\ \\  \\\\\\  \\ \\  \\ \\  \\    \\ \\  \\ \\  \\\\ \\  \\ \\  \\  ___  \n' +
  '  \\ \\  \\\\  \\\\ \\  \\ \\  \\ \\  \\ \\  \\\\ \\  \\       \\ \\  \\|\\  \\ \\  \\\\\\  \\ \\  \\ \\  \\____\\ \\  \\ \\  \\\\ \\  \\ \\  \\|\\  \\ \n' +
  '   \\ \\__\\\\ _\\\\ \\__\\ \\__\\ \\__\\ \\__\\\\ \\__\\       \\ \\_______\\ \\_______\\ \\__\\ \\_______\\ \\__\\ \\__\\\\ \\__\\ \\_______\\\n' +
  '    \\|__|\\|__|\\|__|\\|__|\\|__|\\|__| \\|__|        \\|_______|\\|_______|\\|__|\\|_______|\\|__|\\|__| \\|__|\\|_______|\n' +
  '\n\n'
)
console.log(
  '%c %s %c %s %c %s',
  'border: 1px solid white;border-radius: 5px;padding: 2px 5px;color: white;background-color: #040404;',
  'author：洋洋的煮雨',
  'border: 1px solid white;border-radius: 5px;padding: 2px 5px;color: white;background-color: #040404;',
  'current version: v0.3.1',
  'border: 1px solid white;border-radius: 5px;padding: 2px 5px;color: white;background-color: #040404;',
  'email: zhuboyang1996@foxmail.com'
)

const app = createApp(App)
app.use(router)
app.use(ArcoVue, {
  // 用于改变使用组件时的前缀名称
  componentPrefix: 'arco'
})
// 格式化时间
app.config.globalProperties.common = common
// 检测文件是否是视频
app.config.globalProperties.type = commonType
app.mount('#app')
