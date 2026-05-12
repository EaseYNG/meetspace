<template>
  <router-view />
</template>

<script setup lang="ts">
import { watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

watch(
  () => auth.profile,
  (profile, prev) => {
    if (prev !== null && profile === null) {
      router.push('/login')
    }
  },
)
</script>
