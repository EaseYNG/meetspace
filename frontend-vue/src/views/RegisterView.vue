<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1 class="auth-title">MeetSpace</h1>
      <p class="auth-subtitle">创建新账户</p>
      <RegisterForm @register="handleRegister" />
      <div class="auth-footer">
        已有账户？
        <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { registerAPI } from '@/api/auth'
import type { RegisterRequest } from '@/types'
import RegisterForm from '@/components/auth/RegisterForm.vue'

const router = useRouter()

async function handleRegister(data: RegisterRequest, done: () => void) {
  try {
    await registerAPI(data)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    done()
  }
}
</script>

<style scoped lang="scss">
@use '../styles/variables' as *;

.auth-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #ECFDF5 0%, #EFF6FF 100%);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    width: 600px;
    height: 600px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba($primary, 0.08) 0%, transparent 70%);
    top: -200px;
    right: -200px;
    pointer-events: none;
  }

  &::after {
    content: '';
    position: absolute;
    width: 400px;
    height: 400px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba($accent, 0.06) 0%, transparent 70%);
    bottom: -100px;
    left: -100px;
    pointer-events: none;
  }
}

.auth-card {
  width: 420px;
  padding: 44px 36px;
  background: $bg-glass;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: $radius-xl;
  box-shadow: $shadow-xl;
  border: 1px solid rgba(255, 255, 255, 0.6);
  position: relative;
  z-index: 1;
}

.auth-title {
  text-align: center;
  font-size: 30px;
  font-weight: 800;
  background: linear-gradient(135deg, $primary, $accent);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 4px;
  letter-spacing: -0.5px;
}

.auth-subtitle {
  text-align: center;
  font-size: 14px;
  color: $text-muted;
  margin-bottom: 28px;
}

.auth-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: $text-secondary;

  a {
    color: $primary;
    font-weight: 600;
    transition: color $transition-fast;

    &:hover {
      color: $primary-dark;
    }
  }
}
</style>
