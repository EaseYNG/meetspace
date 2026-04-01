import api from './index'

/**
 * 创建活动
 * @param {Object} data - 活动数据
 * @returns {Promise} 创建的活动数据
 */
export const createActivity = (data) => {
  return api.post('/activity/create', data)
}

/**
 * 更新活动
 * @param {number} id - 活动ID
 * @param {Object} data - 更新的活动数据
 * @returns {Promise} 更新后的活动数据
 */
export const updateActivity = (id, data) => {
  return api.patch(`/activity/update/${id}`, data)
}

/**
 * 删除活动
 * @param {number} id - 活动ID
 * @returns {Promise} 删除结果
 */
export const deleteActivity = (id) => {
  return api.delete(`/activity/delete/${id}`)
}

/**
 * 报名参加活动
 * @param {number} id - 活动ID
 * @returns {Promise} 报名结果
 */
export const signupActivity = (id) => {
  return api.get(`/activity/signup/${id}`)
}

/**
 * 获取用户参与的活动列表
 * @param {number} id - 用户ID
 * @returns {Promise} 活动列表
 */
export const getParticipatedActivities = (id) => {
  return api.get(`/activity/participated/${id}`)
}

/**
 * 获取用户创建的活动列表
 * @returns {Promise} 活动列表
 */
export const getCreatedActivities = () => {
  return api.get('/activity/created')
}

/**
 * 获取活动详情
 * @param {number} id - 活动ID
 * @returns {Promise} 活动详情数据
 */
export const getActivityDetail = (id) => {
  return api.get(`/activity/${id}`)
}
