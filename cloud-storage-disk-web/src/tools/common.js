import apiConfig from '../api/apiConfig.js'
import { Notification } from '@arco-design/web-vue'
import md5 from 'js-md5'
import type from './type.js'

const identifyFileAvatar = (file) => {
  console.log('file', file)
  const { ext, type, thumbnail } = file
  if (thumbnail !== undefined && thumbnail !== '' && thumbnail !== null) {
    return apiConfig().apiBaseUrl + thumbnail
  }
  if (type === 1) {
    return apiConfig().iconBaseUrl + 'file/directory.png'
  }
  if (ext === 'doc' || ext === 'docx') {
    return apiConfig().iconBaseUrl + 'file/docx.png'
  }
  if (ext === 'ppt' || ext === 'pptx') {
    return apiConfig().iconBaseUrl + 'file/ppt.png'
  }
  if (ext === 'xls' || ext === 'xlsx') {
    return apiConfig().iconBaseUrl + 'file/xls.png'
  }
  if (ext === 'java') {
    return apiConfig().iconBaseUrl + 'file/java.png'
  }
  if (ext === 'zip') {
    return apiConfig().iconBaseUrl + 'file/zip.png'
  }
  if (ext === 'mp3') {
    return apiConfig().iconBaseUrl + 'file/mp3.png'
  }
  if (ext === 'mp4') {
    return apiConfig().iconBaseUrl + 'file/mp4.png'
  }
  if (ext === 'zip') {
    return apiConfig().iconBaseUrl + 'file/zip.png'
  }
  if (ext === 'jpg' || ext === 'jpeg' || ext === 'png') {
    return apiConfig().iconBaseUrl + 'file/jpg.png'
  }
  return apiConfig().iconBaseUrl + 'file/document.png'
}
// 格式化文件大小至人类可识别格式
const formatSizeInPerson = (size) => {
  if (size === undefined || size === null || size === 0) {
    return '0KB'
  }
  const units = ['B', 'KB', 'MB', 'GB', 'TB', 'PB']
  const mod = 1024.0
  let i = 0
  while (size >= mod) {
    size /= mod
    i++
  }
  return formatNum(size, 1) + units[i]
}
// 格式化数字
const formatNum = (size, n) => {
  const sizeStr = size.toString()
  if (sizeStr.lastIndexOf('.') > -1) {
    return sizeStr.substring(0, sizeStr.toString().indexOf('.') + 1 + n)
  }
  return sizeStr
}
// 页面跳转
const jumpUrl = (url, router) => {
  router.push(url)
}
// 解析时间
const analysisDateTime = timestamp => {
  if (timestamp === undefined || timestamp === null || timestamp === 0 || typeof timestamp === 'boolean') {
    return '未知'
  }
  const date = new Date(typeof timestamp === 'number' ? timestamp : timestamp.indexOf('-') > 0 ? timestamp : parseInt(timestamp))
  return {
    years: date.getFullYear(),
    months: date.getMonth() + 1,
    days: date.getDate(),
    hours: date.getHours(),
    minutes: date.getMinutes(),
    seconds: date.getSeconds()
  }
}
// 格式化时间 yyyy-MM-dd HH:mm::ss
const formatDateTime = (timestamp) => {
  const { years, months, days, hours, minutes, seconds } = analysisDateTime(timestamp)
  return `${years}-${months < 10 ? '0' + months : months}-${days < 10 ? '0' + days : days} ` +
    `${hours < 10 ? '0' + hours : hours}:` +
    `${minutes < 10 ? '0' + minutes : minutes}:` +
    `${seconds < 10 ? '0' + seconds : seconds}`
}

// 格式化时间 yyyy-MM-dd
const formatDate = (timestamp) => {
  const { years, months, days } = analysisDateTime(timestamp)
  return `${years}-${months < 10 ? '0' + months : months}-${days < 10 ? '0' + days : days}`
}

const notify = {
  warning: msg => { Notification.warning({ title: '警告', content: msg }) },
  success: msg => { Notification.success({ title: '操作成功', content: msg }) },
  error: msg => { Notification.error({ title: '操作失败', content: msg }) }
}

// 重写浏览器地址中的 query 参数
const setUrlQuery = (router, key, value, defaultValue) => {
  const { query, path } = router.currentRoute.value
  const routerQuery = {}
  for (const key in query) {
    routerQuery[key] = query[key]
  }
  if (query[key] === undefined || query[key] !== defaultValue) {
    routerQuery[key] = value
    router.push({
      path,
      query: routerQuery
    })
  }
}

// 密码加密
const encrypt = (str, count) => {
  if (count === undefined || count === null || count < 1) {
    return md5(str)
  }
  for (let i = 0; i < count; i++) {
    str = md5(str)
  }
  return count + str
}

// 将数字转换为时间（将秒数转为 时:分:秒）
const formatNumberToTime = time => {
  if (!type.isNumber(time)) {
    return ''
  }
  if (typeof time === 'string') {
    time = parseInt(time)
  }
  let hour = Math.floor(time / 60 / 60 % 24)
  let minute = Math.floor(time / 60 % 60)
  let second = Math.floor(time % 60)
  hour = hour < 10 ? '0' + hour : hour
  minute = minute < 10 ? '0' + minute : minute
  second = second < 10 ? '0' + second : second
  return `${hour}:${minute}:${second}`
}

const common = {
  identifyFileAvatar,
  formatSizeInPerson,
  jumpUrl,
  formatDateTime,
  formatDate,
  notify,
  setUrlQuery,
  encrypt,
  formatNumberToTime
}

export default common
