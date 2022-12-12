import axios from "axios";

const service = axios.create({
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
}

const url = {}

const methods = {
  get: 'get',
  post: 'post',
  put: 'put'
}

const req = function (url, method, data, headers) {
  if (methods.post === method) {
    return service.post(url, data)
  } else if (methods.get === method) {
    return service.get(url, {
      params: data
    })
  }
}

const http = {
  url,
  methods,
  req,
}

export default http
