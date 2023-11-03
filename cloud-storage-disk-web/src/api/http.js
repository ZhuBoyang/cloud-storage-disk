import axios from 'axios'
import apiConfig from './apiConfig.js'
import common from '../tools/common.js'

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
      common.notify.success(message)
    }
    return response.data
  } else if (code >= 10000) {
    if (code === 11000) {
      common.notify.error(message)
      localStorage.removeItem('t')
      localStorage.removeItem('id')
      const { protocol, host } = window.location
      window.location.href = `${protocol}//${host}/login`
    } else {
      common.notify.error(message)
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

const modules = {
  email: 'email/', // 邮件
  file: 'file/', // 文件相关
  user: 'user/', // 用户相关
  systemSpace: 'space/' // 系统空间
}

const url = {
  file: {
    checkExist: modules.file + 'check_exist', // 检查文件块是否已入库
    uploadBlocks: modules.file + 'upload', // 上传文件块
    mergeFile: modules.file + 'merge', // 文件合并
    simpleUpload: modules.file + 'simple_upload', // 上传简单文件
    download: modules.file + 'download/', // 文件下载
    playUrl: modules.file + 'play_url' // 获取媒体文件的播放地址
  }
}

const reqUrl = {
  user: {
    hasInitialed: obj => req(modules.user + 'has_initial', methods.post, obj),
    initial: obj => req(modules.user + 'initial', methods.post, obj),
    login: obj => req(modules.user + 'login', methods.post, obj),
    logout: () => req(modules.user + 'logout', methods.post),
    update: obj => req(modules.user + 'update', methods.post, obj),
    passwordUpdate: obj => req(modules.user + 'password_update', methods.post, obj),
    reset: obj => req(modules.user + 'reset', methods.post, obj), // 重置用户账户密码
    info: () => req(modules.user + 'info', methods.get)
  },
  file: {
    typeSupports: () => req(modules.file + 'type_supports', methods.get), // 查询各类文件支持的格式后缀
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
    videos: obj => req(modules.file + 'videos', methods.post, obj), // 查询目录下所有的视频文件
    audios: obj => req(modules.file + 'audios', methods.post, obj), // 查询目录下所有的音频文件
    document: obj => req(modules.file + 'document', methods.post, obj) // 查询目录下所有的文档文件
  }
}

const http = {
  url,
  methods,
  req,
  reqUrl
}

export default http
