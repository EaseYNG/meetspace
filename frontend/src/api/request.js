import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 30000,
  withCredentials: true,
})

request.interceptors.response.use(
  (res) => {
    const data = res.data
    if (data.code !== 200) {
      ElMessage.error(data.msg || 'Request failed')
      if (data.code === 401) {
        router.push('/login')
      }
      return Promise.reject(new Error(data.msg))
    }
    return data
  },
  (err) => {
    if (err.response?.status === 401) {
      router.push('/login')
      return Promise.reject(err)
    }
    ElMessage.error(err.response?.data?.msg || err.message || 'Network error')
    return Promise.reject(err)
  },
)

export default request
