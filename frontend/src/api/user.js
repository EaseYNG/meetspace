import api from './index'

// 用户登录
export const login = (data) => {
  return api.post('/user/login', data)
}

// 用户注册
export const register = (data) => {
  return api.post('/user/register', data)
}
