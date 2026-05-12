<template>
  <div class="main-layout">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
    <div class="bottom-nav">
      <div
        v-for="tab in tabs"
        :key="tab.name"
        class="nav-item"
        :class="{ active: currentTab === tab.name }"
        @click="switchTab(tab.name)"
      >
        <el-icon :size="22">
          <component :is="tab.icon" />
        </el-icon>
        <span class="nav-label">{{ $t(tab.label) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'

const router = useRouter()
const route = useRoute()
const appStore = useAppStore()

const tabs = [
  { name: 'explore', icon: 'Compass', label: 'explore' },
  { name: 'activities', icon: 'Calendar', label: 'activities' },
  { name: 'agent', icon: 'MagicStick', label: 'agent' },
  { name: 'profile', icon: 'User', label: 'profile' },
]

const currentTab = computed(() => route.name?.toLowerCase() || 'explore')

function switchTab(name) {
  router.push({ name: name.charAt(0).toUpperCase() + name.slice(1) })
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  padding-bottom: 80px;
}

.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  background: var(--surface);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-top: 1px solid var(--surface-border);
  padding: 8px 0;
  padding-bottom: max(8px, env(safe-area-inset-bottom));
  z-index: 100;
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  cursor: pointer;
  padding: 4px 0;
  color: var(--text-secondary);
  transition: all 0.2s ease;
  position: relative;
}

.nav-item.active {
  color: var(--primary);
}

.nav-item.active::before {
  content: '';
  position: absolute;
  top: -1px;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 3px;
  background: var(--primary);
  border-radius: 0 0 4px 4px;
}

.nav-label {
  font-size: 11px;
  font-weight: 500;
}
</style>
