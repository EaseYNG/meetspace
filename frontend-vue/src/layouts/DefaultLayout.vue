<template>
  <el-container class="layout">
    <el-header class="layout-header">
      <div class="header-inner">
        <router-link to="/" class="logo">
          <span class="logo-icon">M</span>
          <span class="logo-text">MeetSpace</span>
        </router-link>
        <el-menu
          :default-active="route.path"
          mode="horizontal"
          :ellipsis="false"
          class="nav-menu"
          router
        >
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/explore">发现</el-menu-item>
          <el-menu-item index="/activity/create">创建活动</el-menu-item>
          <el-menu-item index="/my-activities">我的活动</el-menu-item>
          <el-menu-item index="/profile">个人中心</el-menu-item>
        </el-menu>
        <div class="header-right">
          <span class="user-name">{{ auth.profile?.nickname }}</span>
          <el-button class="logout-btn" text @click="handleLogout">退出</el-button>
        </div>
      </div>
    </el-header>
    <el-main class="layout-main">
      <div class="page-container">
        <router-view v-slot="{ Component }">
          <transition name="page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { logoutAPI } from '@/api/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

async function handleLogout() {
  try {
    await logoutAPI()
  } catch {
    // ignore
  }
  auth.clear()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped lang="scss">
@use '../styles/variables' as *;

.layout {
  min-height: 100vh;
}

.layout-header {
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(16px) saturate(180%);
  -webkit-backdrop-filter: blur(16px) saturate(180%);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);

  .header-inner {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    height: 60px;
    gap: 24px;

    .logo {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 20px;
      font-weight: 700;
      color: $text;
      text-decoration: none;
      transition: opacity $transition-fast;

      &:hover {
        opacity: 0.8;
      }

      .logo-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 34px;
        height: 34px;
        background: linear-gradient(135deg, $primary, $accent);
        color: #fff;
        border-radius: $radius-sm;
        font-size: 18px;
        font-weight: 800;
      }

      .logo-text {
        background: linear-gradient(135deg, $primary, $accent);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }

    .nav-menu {
      flex: 1;
      border-bottom: none !important;
      background: transparent;

      :deep(.el-menu-item) {
        font-weight: 500;
        color: $text-secondary;
        transition: color $transition-fast;
        border-bottom-color: transparent;

        &:hover {
          color: $primary !important;
          background: transparent !important;
        }

        &.is-active {
          color: $primary !important;
          font-weight: 600;
        }
      }
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 12px;

      .user-name {
        font-size: 14px;
        color: $text-secondary;
        font-weight: 500;
      }

      .logout-btn {
        color: $text-muted;
        font-size: 13px;
        transition: color $transition-fast;

        &:hover {
          color: $danger;
        }
      }
    }
  }
}

.layout-main {
  background: $bg;
  min-height: calc(100vh - 60px);
  padding: 28px 16px;

  .page-container {
    max-width: 1200px;
    margin: 0 auto;
  }
}
</style>
