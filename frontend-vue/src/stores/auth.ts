import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserProfileVO } from '@/types'
import { getProfileAPI } from '@/api/user'

export const useAuthStore = defineStore('auth', () => {
  const profile = ref<UserProfileVO | null>(null)
  const loaded = ref(false)

  const isLoggedIn = () => profile.value !== null

  async function checkSession() {
    try {
      const res = await getProfileAPI()
      profile.value = res.data.data
    } catch {
      profile.value = null
    } finally {
      loaded.value = true
    }
  }

  function setProfile(p: UserProfileVO) {
    profile.value = p
  }

  function clear() {
    profile.value = null
  }

  return { profile, loaded, isLoggedIn, checkSession, setProfile, clear }
})
