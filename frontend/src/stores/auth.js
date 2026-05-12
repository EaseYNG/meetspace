import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)

  const isLoggedIn = computed(() => user.value !== null)
  const userId = computed(() => user.value?.id ?? null)
  const nickname = computed(() => user.value?.nickname ?? '')
  const username = computed(() => user.value?.username ?? '')

  function setUser(userData) {
    user.value = userData
  }

  function clearUser() {
    user.value = null
  }

  return { user, isLoggedIn, userId, nickname, username, setUser, clearUser }
})
