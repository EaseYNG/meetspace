import request from './request'

export function getUserHomeApi() {
  return request.get('/users/me/home')
}

export function getUserProfileApi() {
  return request.get('/users/me/profile')
}

export function updateUserProfileApi(data) {
  return request.put('/users/me/profile', data)
}
