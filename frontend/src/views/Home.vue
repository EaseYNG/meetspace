<template>
  <Layout>
    <div class="home">
      <div class="welcome-section">
        <h1>欢迎回来，{{ username }}！</h1>
        <p class="subtitle">MeetSpace - 您的活动管理平台</p>
      </div>

      <div class="quick-actions">
        <el-button type="primary" size="large" @click="goToCreateActivity">
          <el-icon><Plus /></el-icon>
          创建活动
        </el-button>
        <el-button type="success" size="large" @click="goToActivities">
          <el-icon><List /></el-icon>
          我的活动
        </el-button>
      </div>

      <el-divider />

      <div class="recent-activities">
        <h2>最近创建的活动</h2>
        <ActivityList
          :activities="recentCreatedActivities"
          :loading="loading"
          :current-user-id="currentUserId"
          @edit="handleEditActivity"
          @delete="handleDeleteActivity"
          @signup="handleSignupActivity"
        />
        <el-button
          v-if="recentCreatedActivities.length > 0"
          type="primary"
          link
          @click="goToActivities"
        >
          查看全部
        </el-button>
      </div>

      <el-divider />

      <div class="recent-activities">
        <h2>最近参与的活动</h2>
        <ActivityList
          :activities="recentParticipatedActivities"
          :loading="loading"
          :show-actions="false"
          :current-user-id="currentUserId"
        />
        <el-button
          v-if="recentParticipatedActivities.length > 0"
          type="primary"
          link
          @click="goToActivities"
        >
          查看全部
        </el-button>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, List } from '@element-plus/icons-vue'
import Layout from '@/components/Layout/index.vue'
import ActivityList from '@/components/Activity/ActivityList.vue'
import { getCreatedActivities, getParticipatedActivities } from '@/api/activity'

const router = useRouter()
const username = ref('')
const currentUserId = ref(null)
const loading = ref(false)
const recentCreatedActivities = ref([])
const recentParticipatedActivities = ref([])

onMounted(async () => {
  // 获取用户信息
  const user = localStorage.getItem('user')
  if (user) {
    const userData = JSON.parse(user)
    username.value = userData.username || '用户'
    currentUserId.value = userData.id || null
  }

  // 加载活动数据
  await loadActivities()
})

const loadActivities = async () => {
  loading.value = true
  try {
    // 获取创建的活动
    const createdRes = await getCreatedActivities()
    recentCreatedActivities.value = (createdRes.data || []).slice(0, 3)

    // 获取参与的活动
    if (currentUserId.value) {
      const participatedRes = await getParticipatedActivities(currentUserId.value)
      recentParticipatedActivities.value = (participatedRes.data || []).slice(0, 3)
    }
  } catch (error) {
    console.error('加载活动失败:', error)
  } finally {
    loading.value = false
  }
}

const goToCreateActivity = () => {
  router.push('/activities/create')
}

const goToActivities = () => {
  router.push('/activities')
}

const handleEditActivity = (activity) => {
  router.push(`/activities/${activity.id}/edit`)
}

const handleDeleteActivity = async (activity) => {
  ElMessage.success('活动删除成功')
  await loadActivities()
}

const handleSignupActivity = async (activity) => {
  ElMessage.success('报名成功')
  await loadActivities()
}
</script>

<style scoped>
.home {
  padding: 40px 0;
}

.welcome-section {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-section h1 {
  font-size: 36px;
  color: #303133;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 16px;
  color: #909399;
}

.quick-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 40px;
}

.quick-actions .el-button {
  min-width: 150px;
}

.recent-activities {
  margin-bottom: 40px;
}

.recent-activities h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 20px;
}
</style>
