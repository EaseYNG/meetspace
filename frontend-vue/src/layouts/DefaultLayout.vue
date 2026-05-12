<template>
  <el-container class="layout">
    <el-header class="layout-header">
      <div class="header-inner">
        <router-link to="/" class="logo">MeetSpace</router-link>
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
          <el-button text type="primary" @click="handleLogout">退出</el-button>
        </div>
      </div>
    </el-header>
    <el-main class="layout-main">
      <div class="page-container">
        <router-view />
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
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 100;

  .header-inner {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    height: 60px;
    gap: 24px;

    .logo {
      font-size: 20px;
      font-weight: 700;
      color: $primary;
    }

    .nav-menu {
      flex: 1;
      border-bottom: none !important;
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 8px;

      .user-name {
        font-size: 14px;
        color: $text-secondary;
      }
    }
  }
}

.layout-main {
  background: $bg;
  min-height: calc(100vh - 60px);
  padding: 24px 16px;

  .page-container {
    max-width: 1200px;
    margin: 0 auto;
  }
}
</style>
