<template>
  <div class="login-container">
    <div class="login-box">
      <h2>登录</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            type="text"
            id="username"
            v-model="form.username"
            placeholder="请输入用户名"
            required
          />
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input
            type="password"
            id="password"
            v-model="form.password"
            placeholder="请输入密码"
            required
          />
        </div>
        <div class="error-message" v-if="errorMsg">{{ errorMsg }}</div>
        <button type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      <div class="footer">
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/user'

const router = useRouter()
const form = ref({
  username: '',
  password: ''
})
const loading = ref(false)
const errorMsg = ref('')

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    errorMsg.value = '请填写用户名和密码'
    return
  }

  loading.value = true
  errorMsg.value = ''

  try {
    const res = await login(form.value)
    console.log('登录响应:', res)

    if (res.code === 'SUCCESS') {
      // 后端返回的data就是token字符串
      const token = res.data
      localStorage.setItem('token', token)

      // 解析token获取用户信息(或者需要额外调用获取用户信息的接口)
      // 这里暂时存储用户名,后续可以通过token获取完整用户信息
      localStorage.setItem('user', JSON.stringify({
        username: form.value.username,
        id: null // 后续可以通过API获取
      }))

      alert('登录成功！')
      // 登录成功后跳转到主页
      router.push('/home')
    } else {
      errorMsg.value = res.msg || '登录失败'
    }
  } catch (error) {
    console.error('登录错误:', error)
    errorMsg.value = error.response?.data?.msg || error.message || '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 28px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.error-message {
  color: #e74c3c;
  font-size: 14px;
  margin-bottom: 15px;
  text-align: center;
}

button {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: opacity 0.3s;
}

button:hover:not(:disabled) {
  opacity: 0.9;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.footer a {
  color: #667eea;
  text-decoration: none;
  margin-left: 5px;
}

.footer a:hover {
  text-decoration: underline;
}
</style>
