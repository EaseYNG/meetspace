import client from './client'
import type {
  ActivityVO,
  ActivityCreateRequest,
  ActivityUpdateRequest,
  ActivitySearchQuery,
  Result,
} from '@/types'

export function createActivityAPI(data: ActivityCreateRequest) {
  return client.post<Result<null>>('/activities/create', data)
}

export function getActivityDetailAPI(id: number) {
  return client.get<Result<ActivityVO>>(`/activities/${id}`)
}

export function updateActivityAPI(id: number, data: ActivityUpdateRequest) {
  return client.patch<Result<null>>(`/activities/${id}`, data)
}

export function deleteActivityAPI(id: number) {
  return client.delete<Result<null>>(`/activities/${id}`)
}

export function searchActivitiesAPI(data: ActivitySearchQuery) {
  return client.post<Result<ActivityVO[]>>('/activities/search', data)
}

export function signupActivityAPI(id: number) {
  return client.post<Result<null>>(`/activities/${id}/participants`)
}

export function quitActivityAPI(id: number) {
  return client.delete<Result<null>>(`/activities/${id}/participants/me`)
}
