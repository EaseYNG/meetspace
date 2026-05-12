import request from './request'

export function getActivityDetail(id) {
  return request.get(`/activities/${id}`)
}

export function createActivityApi(data) {
  return request.post('/activities/create', data)
}

export function updateActivityApi(id, data) {
  return request.patch(`/activities/${id}`, data)
}

export function deleteActivityApi(id) {
  return request.delete(`/activities/${id}`)
}

export function searchActivitiesApi(data) {
  return request.post('/activities/search', data)
}

export function signupActivityApi(id) {
  return request.post(`/activities/${id}/participants`)
}

export function quitActivityApi(id) {
  return request.delete(`/activities/${id}/participants/me`)
}

export function getParticipatedActivitiesApi() {
  return request.get('/users/me/activities/participated')
}

export function getCreatedActivitiesApi() {
  return request.get('/users/me/activities/created')
}

export function getSignedUpActivitiesApi() {
  return request.get('/users/me/activities/signed-up')
}
