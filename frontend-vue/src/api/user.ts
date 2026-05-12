import client from './client'
import type {
  Result,
  UserHomeVO,
  UserProfileVO,
  ProfileUpdateRequest,
  ActivityVO,
} from '@/types'

export function getHomeAPI() {
  return client.get<Result<UserHomeVO>>('/users/me/home')
}

export function getProfileAPI() {
  return client.get<Result<UserProfileVO>>('/users/me/profile')
}

export function updateProfileAPI(data: ProfileUpdateRequest) {
  return client.patch<Result<null>>('/users/me/profile', data)
}

export function getParticipatedActivitiesAPI() {
  return client.get<Result<ActivityVO[]>>('/users/me/activities/participated')
}

export function getCreatedActivitiesAPI() {
  return client.get<Result<ActivityVO[]>>('/users/me/activities/created')
}

export function getSignedUpActivitiesAPI() {
  return client.get<Result<ActivityVO[]>>('/users/me/activities/signed-up')
}
