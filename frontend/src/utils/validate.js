/**
 * 验证邮箱格式
 * @param {string} email - 邮箱地址
 * @returns {boolean} 是否有效
 */
export const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

/**
 * 验证时间顺序（开始时间应该早于结束时间）
 * @param {string|Date} startTime - 开始时间
 * @param {string|Date} endTime - 结束时间
 * @returns {boolean} 是否有效
 */
export const validateTimeOrder = (startTime, endTime) => {
  const start = new Date(startTime)
  const end = new Date(endTime)
  return start < end
}

/**
 * 验证报名截止时间（截止时间应该早于开始时间）
 * @param {string|Date} deadline - 报名截止时间
 * @param {string|Date} startTime - 活动开始时间
 * @returns {boolean} 是否有效
 */
export const validateDeadline = (deadline, startTime) => {
  const dl = new Date(deadline)
  const start = new Date(startTime)
  return dl < start
}

/**
 * 邮箱验证器（用于Element Plus表单）
 * @param {Object} rule - 验证规则
 * @param {string} value - 验证值
 * @param {Function} callback - 回调函数
 */
export const emailValidator = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入邮箱地址'))
  } else if (!validateEmail(value)) {
    callback(new Error('请输入有效的邮箱地址'))
  } else {
    callback()
  }
}

/**
 * 必填验证器（用于Element Plus表单）
 * @param {string} fieldName - 字段名称
 * @returns {Function} 验证函数
 */
export const requiredValidator = (fieldName) => {
  return (rule, value, callback) => {
    if (!value || (typeof value === 'string' && value.trim() === '')) {
      callback(new Error(`请输入${fieldName}`))
    } else {
      callback()
    }
  }
}

/**
 * 时间顺序验证器（用于Element Plus表单）
 * @param {string} startTimeField - 开始时间字段名
 * @param {string} endTimeField - 结束时间字段名
 * @param {Object} form - 表单数据
 * @returns {Function} 验证函数
 */
export const timeOrderValidator = (startTimeField, endTimeField, form) => {
  return (rule, value, callback) => {
    const startTime = form[startTimeField]
    const endTime = form[endTimeField]
    if (startTime && endTime && !validateTimeOrder(startTime, endTime)) {
      callback(new Error('开始时间必须早于结束时间'))
    } else {
      callback()
    }
  }
}
