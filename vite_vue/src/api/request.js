import axios from 'axios'
import { getToken, removeToken } from '../utils/auth.js'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

request.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data.code !== 200) {
      ElMessage.error(data.message || '操作失败')
      if (data.code === 401) {
        removeToken()
        window.location.href = '/login'
      }
      return Promise.reject(new Error(data.message))
    }
    return data
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      removeToken()
      window.location.href = '/login'
    }
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  },
)

export default request
