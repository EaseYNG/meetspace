<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-header">
        <div class="logo">{{ $t('register') }}</div>
        <p class="subtitle">{{ $t('appName') }}</p>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        size="large"
        class="auth-form"
        @keyup.enter="handleRegister"
      >
        <el-form-item :label="$t('nickname')" prop="nickname">
          <el-input v-model="form.nickname" :placeholder="$t('nickname')" class="glass-input" />
        </el-form-item>
        <el-form-item :label="$t('username')" prop="username">
          <el-input v-model="form.username" :placeholder="$t('username')" class="glass-input" />
        </el-form-item>
        <el-form-item :label="$t('password')" prop="password">
          <el-input v-model="form.password" type="password" show-password :placeholder="$t('password')" class="glass-input" />
        </el-form-item>
        <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="handleRegister">
          {{ $t('register') }}
        </el-button>
      </el-form>
      <div class="auth-footer">
        {{ $t('hasAccount') }}
        <router-link to="/login" class="link">{{ $t('login') }}</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { registerApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  nickname: '',
  username: '',
  password: '',
})

const rules = {
  nickname: [{ required: true, message: ' ', trigger: 'blur' }],
  username: [
    { required: true, message: ' ', trigger: 'blur' },
    { min: 3, max: 32, message: ' ', trigger: 'blur' },
  ],
  password: [
    { required: true, message: ' ', trigger: 'blur' },
    { min: 6, max: 128, message: ' ', trigger: 'blur' },
  ],
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await registerApi(form)
    ElMessage.success(res.msg || '注册成功')
    router.push('/login')
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
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #4caf50, #81c784);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
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
