import api from './index'

/**
 * 获取用户资料
 * @returns {Promise} 用户资料数据
 */
export const getProfile = () => {
  return api.get('/home/profile')
}

/**
 * 设置用户资料
 * @param {Object} data - 用户资料数据
 * @returns {Promise} 更新后的用户资料数据
 */
export const setProfile = (data) => {
  return api.post('/home/profile', data)
}
