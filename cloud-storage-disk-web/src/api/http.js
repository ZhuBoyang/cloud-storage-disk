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

const url = {
  email: {
    register: modules.email + 'register' // 发送注册账户的邮件
  },
  user: {
    register: modules.user + 'register', // 用户注册
    login: modules.user + 'login', // 用户登录
    logout: modules.user + 'logout', // 用户退出登录
    reset: modules.user + 'reset', // 重置用户账户密码
    info: modules.user + 'info' // 获取当前已登录用户的信息
  },
  file: {
    breads: modules.file + 'breads', // 查询文件面包屑导航
    pager: modules.file + 'pager', // 文件列表
    checkExist: modules.file + 'check_exist', // 检查文件块是否已入库
    uploadBlocks: modules.file + 'upload', // 上传文件块
    mergeFile: modules.file + 'merge', // 文件合并
    mkdir: modules.file + 'mkdir', // 新建文件夹
    batchDelete: modules.file + 'batch_delete', // 批量删除文件
    move: modules.file + 'move', // 批量移动文件及文件夹
    copy: modules.file + 'copy', // 批量复制文件及文件夹
    rename: modules.file + 'rename', // 文件重命名
    playUrl: modules.file + 'play_url', // 获取文件播放地址
    dirBreads: modules.file + 'dir_breads', // 查询文件夹的面包屑导航数据
    dirs: modules.file + 'dirs', // 查询目录下次一级的所有文件夹
    download: modules.file + 'download/' // 文件下载
  },
  systemSpace: {
    diskInfo: modules.systemSpace + 'disk' // 系统磁盘空间使用量
  }
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

const reqUrl = {
  email: {
    sendRegCoder: email => req(url.email.register, methods.post, { email })
  },
  user: {
    register: obj => req(url.user.register, methods.post, obj),
    login: obj => req(url.user.login, methods.post, obj),
    logout: () => req(url.user.logout, methods.post),
    info: () => req(url.user.info, methods.get)
  },
  file: {
    mkdir: obj => req(url.file.mkdir, methods.post, obj),
    move: obj => req(url.file.move, methods.post, obj),
    copy: obj => req(url.file.copy, methods.post, obj),
    rename: obj => req(url.file.rename, methods.post, obj),
    breads: obj => req(url.file.breads, methods.post, obj),
    pager: obj => req(url.file.pager, methods.get, obj),
    dirs: obj => req(url.file.dirs, methods.post, obj)
  }
}

const http = {
  url,
  methods,
  req,
  reqUrl
}

export default http
