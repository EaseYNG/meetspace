<template>
  <div class="profile-page">
    <div class="page-header">
      <h1 class="page-title">{{ $t('profile') }}</h1>
    </div>

    <div class="profile-card glass-card" v-if="profile">
      <div class="avatar">{{ profile.nickname?.charAt(0)?.toUpperCase() || 'U' }}</div>
      <h2 class="name">{{ profile.nickname || $t('profile') }}</h2>
      <p class="username">@{{ profile.username }}</p>
    </div>

    <div class="menu-list">
      <div class="menu-item glass-card" @click="$router.push('/settings')">
        <el-icon><Setting /></el-icon>
        <span>{{ $t('settings') }}</span>
        <el-icon class="arrow"><ArrowRight /></el-icon>
      </div>
      <div class="menu-item glass-card" @click="handleLogout">
        <el-icon><SwitchButton /></el-icon>
        <span>{{ $t('logout') }}</span>
        <el-icon class="arrow"><ArrowRight /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getUserProfileApi } from '@/api/user'
import { logoutApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const profile = ref(null)

async function fetchProfile() {
  try {
    const res = await getUserProfileApi()
    profile.value = res.data
    authStore.setUser(res.data)
  } catch {}
}

async function handleLogout() {
  try {
    await logoutApi()
  } catch {}
  authStore.clearUser()
  localStorage.removeItem('token')
  ElMessage.success('已退出登录')
  router.push('/login')
}

onMounted(() => { fetchProfile() })
</script>

<style scoped>
.profile-page {
  padding: 24px 16px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text);
}

.profile-card {
  text-align: center;
  padding: 32px 20px;
  margin-bottom: 24px;
}

.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4caf50, #81c784);
  color: white;
  font-size: 28px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  box-shadow: 0 4px 20px rgba(76, 175, 80, 0.3);
}

.name {
  font-size: 20px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 4px;
}

.username {
  font-size: 14px;
  color: var(--text-secondary);
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  gap: 12px;
}

.menu-item:hover {
  transform: none;
}

.menu-item .el-icon:first-child {
  font-size: 20px;
  color: var(--primary);
}

.menu-item span {
  flex: 1;
  font-size: 15px;
  color: var(--text);
}

.menu-item .arrow {
  color: var(--text-secondary);
  font-size: 14px;
}
</style>
