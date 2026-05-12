<template>
  <div :data-theme="appStore.isDark ? 'dark' : 'light'">
    <div class="liquid-bg">
      <div class="liquid-blob"></div>
      <div class="liquid-blob"></div>
      <div class="liquid-blob"></div>
    </div>
    <router-view v-slot="{ Component }">
      <transition name="page" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
import { useAppStore } from '@/stores/app'
const appStore = useAppStore()
</script>

<style>
.page-enter-active,
.page-leave-active {
  transition: all 0.25s ease;
}
.page-enter-from {
  opacity: 0;
  transform: translateY(12px);
}
.page-leave-to {
  opacity: 0;
  transform: translateY(-12px);
}
</style>
