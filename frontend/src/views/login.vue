<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-header">
        <div class="logo">MeetSpace</div>
        <p class="subtitle">{{ $t('appName') }}</p>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        size="large"
        class="auth-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item :label="$t('username')" prop="username">
          <el-input v-model="form.username" :placeholder="$t('username')" class="glass-input" />
        </el-form-item>
        <el-form-item :label="$t('password')" prop="password">
          <el-input v-model="form.password" type="password" show-password :placeholder="$t('password')" class="glass-input" />
        </el-form-item>
        <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="handleLogin">
          {{ $t('login') }}
        </el-button>
      </el-form>
      <div class="auth-footer">
        {{ $t('noAccount') }}
        <router-link to="/register" class="link">{{ $t('register') }}</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { loginApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: ' ', trigger: 'blur' }],
  password: [{ required: true, message: ' ', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await loginApi(form)
    authStore.setUser(res.data)
    localStorage.setItem('token', '1')
    ElMessage.success(res.msg || '登录成功')
    router.push('/explore')
  } catch {
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.auth-container {
  width: 400px;
  max-width: 100%;
}

.auth-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  font-size: 42px;
  font-weight: 700;
  background: linear-gradient(135deg, #4caf50, #81c784);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -1px;
}

.subtitle {
  color: var(--text-secondary);
  margin-top: 8px;
  font-size: 14px;
}

.auth-form {
  background: var(--surface);
  backdrop-filter: blur(var(--blur));
  -webkit-backdrop-filter: blur(var(--blur));
  border: 1px solid var(--surface-border);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  padding: 32px;
}

.submit-btn {
  width: 100%;
  margin-top: 8px;
  height: 48px;
  font-size: 16px;
  border-radius: var(--radius-sm);
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
  color: var(--text-secondary);
  font-size: 14px;
}

.link {
  color: var(--primary);
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
}

.link:hover {
  text-decoration: underline;
}
</style>
