const apiPort = 8100

const apiConfig = () => {
  const envMode = import.meta.env.MODE
  if (envMode === 'dev') {
    return { apiBaseUrl: `http://localhost:${apiPort}/`, iconBaseUrl: `http://localhost:${apiPort}/` }
  }
  if (envMode === 'prod') {
    return { apiBaseUrl: `${window.origin}/pan/`, iconBaseUrl: `${window.origin}/pan/` }
  }
  return { apiBaseUrl: '', iconBaseUrl: '' }
}

export default apiConfig
