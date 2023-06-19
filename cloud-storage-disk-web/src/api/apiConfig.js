const apiConfig = () => {
  const nodeEnv = process.env.VITE_USER_NODE_ENV
  if (nodeEnv === 'development') {
    return {
      apiBaseUrl: 'http://localhost:8100/',
      iconBaseUrl: 'http://localhost:8100/'
    }
  }
  return {
    apiBaseUrl: 'http://panapi.yangcloud.online/',
    iconBaseUrl: 'http://panapi.yangcloud.online/'
  }
}

export default apiConfig
