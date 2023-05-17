import apiConfig from '../api/apiConfig.js'

const identifyFileIcon = (file) => {
  const { ext, type } = file
  if (type === 1) {
    return apiConfig().iconBaseUrl + 'icons/directory.png'
  }
  if (ext === 'doc' || ext === 'docx') {
    return apiConfig().iconBaseUrl + 'icons/docx.png'
  }
  if (ext === 'ppt' || ext === 'pptx') {
    return apiConfig().iconBaseUrl + 'icons/ppt.png'
  }
  if (ext === 'xls' || ext === 'xlsx') {
    return apiConfig().iconBaseUrl + 'icons/xls.png'
  }
  if (ext === 'java') {
    return apiConfig().iconBaseUrl + 'icons/java.png'
  }
  if (ext === 'zip') {
    return apiConfig().iconBaseUrl + 'icons/zip.png'
  }
  if (ext === 'mp3') {
    return apiConfig().iconBaseUrl + 'icons/mp3.png'
  }
  if (ext === 'mp4') {
    return apiConfig().iconBaseUrl + 'icons/mp4.png'
  }
  if (ext === 'zip') {
    return apiConfig().iconBaseUrl + 'icons/zip.png'
  }
  if (ext === 'jpg' || ext === 'jpeg' || ext === 'png') {
    return apiConfig().iconBaseUrl + 'icons/jpg.png'
  }
  return apiConfig().iconBaseUrl + 'icons/document.png'
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

// 读取本地文件函数
const readLocalFile = (fileUrl) => {
  // eslint-disable-next-line no-undef
  const xhr = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject('Microsoft.XMLHTTP')
  const okStatus = document.location.protocol === 'file' ? 0 : 200
  xhr.open('GET', fileUrl, false)
  xhr.overrideMimeType('text/html;charset=utf-8')
  xhr.send(null)
  return xhr.status === okStatus ? xhr.responseText : null
}

const common = {
  identifyFileIcon,
  formatSizeInPerson,
  jumpUrl,
  formatTime,
  readLocalFile
}

export default common
