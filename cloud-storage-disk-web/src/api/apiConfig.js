const apiConfig = () => {
  const nodeEnv = process.env.VITE_USER_NODE_ENV
  if (nodeEnv === 'development') {
    return {
      apiBaseUrl: 'http://localhost:8100/',
      iconBaseUrl: 'http://localhost:8100/'
    }
  }
  return {
    apiBaseUrl: 'http://localhost:8100/',
    iconBaseUrl: 'http://localhost:8100/'
  }
}

export default apiConfig
