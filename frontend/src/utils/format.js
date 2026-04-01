/**
 * 格式化日期时间
 * @param {string|Date} datetime - 日期时间
 * @param {string} format - 格式化字符串，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的日期时间字符串
 */
export const formatDateTime = (datetime, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!datetime) return ''

  const date = new Date(datetime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化日期
 * @param {string|Date} date - 日期
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (date) => {
  return formatDateTime(date, 'YYYY-MM-DD')
}

/**
 * 格式化时间
 * @param {string|Date} time - 时间
 * @returns {string} 格式化后的时间字符串
 */
export const formatTime = (time) => {
  return formatDateTime(time, 'HH:mm:ss')
}

/**
 * 格式化活动状态
 * @param {string} status - 活动状态
 * @returns {string} 中文状态名称
 */
export const formatStatus = (status) => {
  const statusMap = {
    'PENDING': '待开始',
    'ONGOING': '进行中',
    'COMPLETED': '已结束',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

/**
 * 格式化活动状态对应的标签类型
 * @param {string} status - 活动状态
 * @returns {string} Element Plus标签类型
 */
export const getStatusTagType = (status) => {
  const typeMap = {
    'PENDING': 'info',
    'ONGOING': 'success',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}
