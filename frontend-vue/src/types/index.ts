export enum ActivityStatus {
  READY = 'READY',
  CLOSED = 'CLOSED',
  DELETED = 'DELETED',
  OVER = 'OVER',
}

export interface ActivityVO {
  id: number
  title: string
  description: string | null
  address: string
  image: string | null
  startTime: string
  endTime: string
  signupDeadline: string
  status: ActivityStatus
  minParticipants: number
  maxParticipants: number
  latitude: number | null
  longitude: number | null
}

export interface UserProfileVO {
  id: number
  nickname: string
  username: string
  age: number | null
  gender: string | null
  email: string | null
  firstname: string | null
  lastname: string | null
}

export interface UserHomeVO {
  profile: UserProfileVO
  ongoingActivities: ActivityVO[]
  recommendedActivities: ActivityVO[]
}

export interface ActivityCreateRequest {
  title: string
  startTime: string
  endTime: string
  signupDeadline: string
  address: string
  minParticipants?: number
  maxParticipants?: number
  image?: string
  description?: string
}

export interface ActivityUpdateRequest {
  title?: string
  startTime?: string
  endTime?: string
  signupDeadline?: string
  address?: string
  image?: string
  description?: string
}

export interface ActivitySearchQuery {
  startTime?: string
  endTime?: string
  longitude?: number
  latitude?: number
  radiusKm?: number
  minParticipants?: number
  maxParticipants?: number
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  nickname: string
  username: string
  password: string
}

export interface ProfileUpdateRequest {
  age?: number
  gender?: string
  email?: string
  firstname?: string
  lastname?: string
}

export interface Result<T> {
  code: number
  msg: string
  data: T
}
