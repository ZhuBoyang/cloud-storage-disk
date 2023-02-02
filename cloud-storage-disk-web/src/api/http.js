import axios from 'axios'
import config from './config.js'
import { Notification } from '@arco-design/web-vue'

const service = axios.create({
  baseURL: config.apiBaseUrl,
  // timeout: 20000,
  headers: {
    'content-type': 'application/json;charset=utf-8'
  }
})

// 对响应进行解析
service.interceptors.response.use(config => {
  const response = config.data
  if (response.code === 200) {
    if (response.message !== '') {
      Notification.success({
        title: response.message,
        content: ''
      })
    }
    return response.data
  } else if (response.code >= 10000) {
    Notification.error({
      title: response.message,
      content: ''
    })
    return Promise.reject(response)
  }
})

// 对请求进行封装
service.interceptors.request.use(config => {
  const userJson = localStorage.getItem('user')
  if (userJson !== undefined && userJson !== null && userJson !== '') {
    const user = JSON.parse(userJson)
    const { sessionId } = user
    config.headers.authorization = sessionId
  }
  return config
}, error => {
  return Promise.reject(error)
})

const modules = {
  file: 'file/', // 文件相关
  fileBlock: 'file_block/', // 文件块相关
  user: 'user/', // 用户相关
  systemSpace: 'space/' // 系统空间
}

const url = {
  file: {
    mkdir: modules.file + 'mkdir', // 新建文件夹
    batchDelete: modules.file + 'batch_delete', // 批量删除文件
    batchMove: modules.file + 'batch_move', // 批量移动文件及文件夹
    batchCopy: modules.file + 'batch_copy', // 批量复制文件及文件夹
    rename: modules.file + 'rename', // 文件重命名
    playUrl: modules.file + 'play_url', // 获取文件播放地址
    breads: modules.file + 'breads', // 查询文件面包屑导航
    list: modules.file + 'list', // 文件列表
    dirBreads: modules.file + 'dir_breads', // 查询文件夹的面包屑导航数据
    dirs: modules.file + 'dirs' // 查询目录下次一级的所有文件夹
  },
  fileBlock: {
    checkExist: modules.fileBlock + 'check_exist', // 检查文件块是否已入库
    uploadBlocks: modules.fileBlock + 'upload', // 上传文件块
    mergeFile: modules.fileBlock + 'merge' // 文件合并
  },
  user: {
    register: modules.user + 'register', // 用户注册
    login: modules.user + 'login', // 用户登录
    logout: modules.user + 'logout', // 用户退出登录
    reset: modules.user + 'reset' // 重置用户账户密码
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

const http = {
  url,
  methods,
  req
}

export default http
