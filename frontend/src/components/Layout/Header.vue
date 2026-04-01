<template>
  <header class="header">
    <div class="header-container">
      <div class="logo">
        <router-link to="/home">MeetSpace</router-link>
      </div>
      <nav class="nav">
        <router-link to="/home" class="nav-link" active-class="active">首页</router-link>
        <router-link to="/profile" class="nav-link" active-class="active">个人中心</router-link>
        <router-link to="/activities" class="nav-link" active-class="active">我的活动</router-link>
      </nav>
      <div class="user-info">
        <span class="username">{{ username }}</span>
        <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { clearToken } from '@/utils/auth'

const router = useRouter()
const username = ref('')

onMounted(() => {
  // 从localStorage获取用户名
  const user = localStorage.getItem('user')
  if (user) {
    username.value = JSON.parse(user).username || '用户'
  }
})

const handleLogout = () => {
  clearToken()
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.header {
  background-color: #409eff;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo a {
  font-size: 24px;
  font-weight: bold;
  color: white;
  text-decoration: none;
}

.nav {
  display: flex;
  gap: 30px;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-size: 16px;
  transition: opacity 0.3s;
}

.nav-link:hover {
  opacity: 0.8;
}

.nav-link.active {
  font-weight: bold;
  border-bottom: 2px solid white;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  font-size: 14px;
}
</style>
