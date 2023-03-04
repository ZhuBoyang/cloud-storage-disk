import config from '../api/config.js'

const identifyFileIcon = (file) => {
  const { ext, type } = file
  if (type === 1) {
    return config.iconBaseUrl + 'icons/file/category/directory.svg'
  }
  if (ext === 'doc' || ext === 'docx') {
    return config.iconBaseUrl + 'icons/file/category/docx.svg'
  }
  if (ext === 'ppt' || ext === 'pptx') {
    return config.iconBaseUrl + 'icons/file/category/ppt.svg'
  }
  if (ext === 'xls' || ext === 'xlsx') {
    return config.iconBaseUrl + 'icons/file/category/xls.svg'
  }
  if (ext === 'java') {
    return config.iconBaseUrl + 'icons/file/category/java.svg'
  }
  if (ext === 'zip') {
    return config.iconBaseUrl + 'icons/file/category/zip.svg'
  }
  if (ext === 'mp3') {
    return config.iconBaseUrl + 'icons/file/category/mp3.svg'
  }
  if (ext === 'mp4') {
    return config.iconBaseUrl + 'icons/file/category/mp4.svg'
  }
  if (ext === 'zip') {
    return config.iconBaseUrl + 'icons/file/category/zip.svg'
  }
  if (ext === 'jpg' || ext === 'jpeg' || ext === 'png') {
    return config.iconBaseUrl + 'icons/file/category/jpg.svg'
  }
  return config.iconBaseUrl + 'icons/file/category/document.svg'
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
// 格式化时间 yyyy-MM-dd HH:mm::ss
const formatTime = (timestamp) => {
  if (timestamp === undefined || timestamp === null || timestamp === 0) {
    return '未知'
  }
  const date = new Date(parseInt(timestamp))
  const years = date.getFullYear()
  const months = date.getMonth() + 1
  const days = date.getDate()
  const hours = date.getHours()
  const minutes = date.getMinutes()
  const seconds = date.getSeconds()
  return `${years}-${months < 10 ? '0' + months : months}-${days < 10 ? '0' + days : days} ` +
      `${hours < 10 ? '0' + hours : hours}:` +
      `${minutes < 10 ? '0' + minutes : minutes}:` +
      `${seconds < 10 ? '0' + seconds : seconds}`
}

const common = {
  identifyFileIcon,
  formatSizeInPerson,
  jumpUrl,
  formatTime
}

export default common
