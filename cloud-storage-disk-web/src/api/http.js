import axios from "axios";
import config from "./config.js";

const service = axios.create({
  baseURL: config.api_base_url,
  timeout: 8000,
  headers: {
    'content-type': 'application/json;charset=utf-8'
  }
})

service.interceptors.response.use(config => {
  const response = config.data
  if (response.code === 200) {
    if (response.msg !== '') {
      console.log(response.msg)
    }
    return response.data
  } else if (response.code >= 10000) {
    return Promise.reject(response)
  }
})

const modules = {
  block: 'file_block/'
}

const url = {
  uploadBlocks: modules.block + 'upload'
}

const methods = {
  get: 'get',
  post: 'post',
  put: 'put',
  form: 'form'
}

const req = function (url, method, data, headers) {
  if (headers === undefined || headers === null) {
    headers = {'content-type': 'application/json;charset=utf-8'}
  }
  if (methods.post === method) {
    return service.post(url, data, {headers})
  } else if (methods.get === method) {
    return service.get(url, {
      params: data
    }, {headers})
  } else if (methods.form === method) {
    return service.post(url, data, {headers})
  }
}

const http = {
  url,
  methods,
  req,
}

export default http
