import axios from 'axios'
import type { AxiosInstance, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const client: AxiosInstance = axios.create({
  baseURL: '/api/v1',
  timeout: 30000,
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
})

client.interceptors.response.use(
  (res: AxiosResponse) => {
    if (res.data?.code && res.data.code !== 200) {
      ElMessage.error(res.data.msg || '请求失败')
    }
    return res
  },
  (error) => {
    if (error.response?.status === 401) {
      const auth = useAuthStore()
      auth.clear()
    } else {
      ElMessage.error(error.response?.data?.msg || error.message || '网络错误')
    }
    return Promise.reject(error)
  },
)

export default client
