const TOKEN_KEY = 'token'

/**
 * 检查用户是否已登录
 * @returns {boolean} 是否已登录
 */
export const isAuthenticated = () => {
  const token = localStorage.getItem(TOKEN_KEY)
  return !!token
}

/**
 * 获取Token
 * @returns {string|null} Token字符串
 */
export const getToken = () => {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 设置Token
 * @param {string} token - Token字符串
 */
export const setToken = (token) => {
  localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 清除Token
 */
export const clearToken = () => {
  localStorage.removeItem(TOKEN_KEY)
}
