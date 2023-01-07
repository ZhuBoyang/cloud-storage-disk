import axios from 'axios'
import config from './config.js'

const service = axios.create({
  baseURL: config.apiBaseUrl,
  timeout: 8000,
  headers: {
    'content-type': 'application/json;charset=utf-8'
  }
})

service.interceptors.response.use(config => {
  const response = config.data
  if (response.code === 200) {
    if (response.message !== '') {
      console.log(response.message)
    }
    return response.data
  } else if (response.code >= 10000) {
    return Promise.reject(response)
  }
})

const modules = {
  file: 'file/',
  fileBlock: 'file_block/'
}

const url = {
  file: {
    mkdir: modules.file + 'mkdir', // 新建文件夹
    breads: modules.file + 'breads', // 查询文件面包屑导航
    list: modules.file + 'list' // 文件列表
  },
  fileBlock: {
    checkExist: modules.fileBlock + 'check_exist', // 检查文件块是否已入库
    uploadBlocks: modules.fileBlock + 'upload', // 上传文件块
    mergeFile: modules.fileBlock + 'merge' // 文件合并
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
