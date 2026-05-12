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
