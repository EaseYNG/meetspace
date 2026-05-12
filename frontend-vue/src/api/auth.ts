import client from './client'
import type {
  LoginRequest,
  RegisterRequest,
  Result,
  UserProfileVO,
} from '@/types'

export function loginAPI(data: LoginRequest) {
  return client.post<Result<UserProfileVO>>('/auth/login', data)
}

export function registerAPI(data: RegisterRequest) {
  return client.post<Result<null>>('/auth/register', data)
}

export function logoutAPI() {
  return client.post<Result<null>>('/auth/logout')
}
