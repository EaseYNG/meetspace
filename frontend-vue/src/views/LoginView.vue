<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1 class="auth-title">MeetSpace</h1>
      <p class="auth-subtitle">登录到您的账户</p>
      <LoginForm @login="handleLogin" />
      <div class="auth-footer">
        还没有账户？
        <router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { loginAPI } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import type { LoginRequest } from '@/types'
import LoginForm from '@/components/auth/LoginForm.vue'

const router = useRouter()
const auth = useAuthStore()

async function handleLogin(data: LoginRequest, done: () => void) {
  try {
    const res = await loginAPI(data)
    const profile = res.data.data
    auth.setProfile(profile)
    ElMessage.success('登录成功')
    router.push('/')
  } catch {
    done()
  }
}
</script>

<style scoped lang="scss">
.auth-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #e8f5e9 0%, #f1f8e9 100%);
}

.auth-card {
  width: 400px;
  padding: 40px 32px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.auth-title {
  text-align: center;
  font-size: 28px;
  color: #4CAF50;
  margin-bottom: 4px;
}

.auth-subtitle {
  text-align: center;
  font-size: 14px;
  color: #909399;
  margin-bottom: 24px;
}

.auth-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #909399;

  a {
    color: #4CAF50;
    font-weight: 500;

    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
