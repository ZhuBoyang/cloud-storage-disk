import axios from 'axios'
import apiConfig from './apiConfig.js'
import { Notification } from '@arco-design/web-vue'

const service = axios.create({
  baseURL: apiConfig().apiBaseUrl,
  headers: {
    'content-type': 'application/json;charset=utf-8'
  }
})

// 对响应进行解析
service.interceptors.response.use(config => {
  const response = config.data
  const { code, message } = response
  if (code === 200) {
    if (message !== '') {
      Notification.success({
        title: message,
        content: ''
      })
    }
    return response.data
  } else if (code >= 10000) {
    if (code === 11000) {
      Notification.error({
        title: message,
        content: ''
      })
      const { protocol, host } = window.location
      window.location.href = `${protocol}//${host}/login`
    } else {
      Notification.error({
        title: message,
        content: ''
      })
      return Promise.reject(response)
    }
  }
})

// 对请求进行封装
service.interceptors.request.use(config => {
  const token = localStorage.getItem('t')
  if (token !== undefined && token !== null && token !== '') {
    config.headers.authorization = token
  }
  return config
}, error => {
  return Promise.reject(error)
})

const modules = {
  email: 'email/', // 邮件
  file: 'file/', // 文件相关
  user: 'user/', // 用户相关
  systemSpace: 'space/' // 系统空间
}

const methods = {
  get: 'get',
  post: 'post',
  put: 'put',
  form: 'form'
}

const req = function (url, method, data, headers) {
  let config = {}
  if (headers === undefined || headers === null) {
    headers = { 'content-type': 'application/json;charset=utf-8' }
  }
  if (data === undefined) {
    data = {}
  }
  config = { headers }
  if (methods.post === method) {
    return service.post(url, data)
  } else if (methods.get === method) {
    return service.get(url, {
      params: data
    })
  } else if (methods.form === method) {
    return service.post(url, data, config)
  }
}

const url = {
  file: {
    checkExist: modules.file + 'check_exist', // 检查文件块是否已入库
    uploadBlocks: modules.file + 'upload', // 上传文件块
    mergeFile: modules.file + 'merge', // 文件合并
    download: modules.file + 'download/' // 文件下载
  }
}

const reqUrl = {
  email: {
    sendRegCoder: email => req(modules.email + 'register', methods.post, { email })
  },
  user: {
    register: obj => req(modules.user + 'register', methods.post, obj),
    login: obj => req(modules.user + 'login', methods.post, obj),
    logout: () => req(modules.user + 'logout', methods.post),
    reset: obj => req(modules.user + 'reset', methods.post, obj), // 重置用户账户密码
    info: () => req(modules.user + 'info', methods.get)
  },
  file: {
    mkdir: obj => req(modules.file + 'mkdir', methods.post, obj),
    remove: obj => req(modules.file + 'remove', methods.post, obj), // 批量删除文件
    move: obj => req(modules.file + 'move', methods.post, obj),
    copy: obj => req(modules.file + 'copy', methods.post, obj),
    rollback: obj => req(modules.file + 'rollback', methods.post, obj),
    rename: obj => req(modules.file + 'rename', methods.post, obj),
    checkSize: obj => req(modules.file + 'check_size', methods.post, obj), // 检测用户空间剩余大小是否充足
    breads: obj => req(modules.file + 'breads', methods.post, obj),
    pager: obj => req(modules.file + 'pager', methods.get, obj),
    dirs: obj => req(modules.file + 'dirs', methods.post, obj),
    trash: obj => req(modules.file + 'trash', methods.post, obj), // 查询回收站的文件列表
    playUrl: obj => req(modules.file + 'play_url', methods.post, obj) // 获取文件播放地址
  }
}

const http = {
  url,
  methods,
  req,
  reqUrl
}

export default http
