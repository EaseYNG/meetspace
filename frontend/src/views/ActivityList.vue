<template>
  <Layout>
    <div class="activity-list-page">
      <el-card>
        <template #header>
          <div class="card-header">
            <h2>我的活动</h2>
            <el-button type="primary" @click="goToCreate">
              <el-icon><Plus /></el-icon>
              创建活动
            </el-button>
          </div>
        </template>

        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="我创建的" name="created">
            <ActivityList
              :activities="createdActivities"
              :loading="loading"
              :current-user-id="currentUserId"
              @edit="handleEdit"
              @delete="handleDelete"
              @signup="handleSignup"
            />
          </el-tab-pane>

          <el-tab-pane label="我参与的" name="participated">
            <ActivityList
              :activities="participatedActivities"
              :loading="loading"
              :show-actions="false"
              :current-user-id="currentUserId"
            />
          </el-tab-pane>
        </el-tabs>
      </el-card>

      <Confirm
        v-model:visible="deleteDialogVisible"
        title="确认删除"
        message="确定要删除这个活动吗？此操作不可恢复。"
        @confirm="confirmDelete"
      />
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import Layout from '@/components/Layout/index.vue'
import ActivityList from '@/components/Activity/ActivityList.vue'
import Confirm from '@/components/Common/Confirm.vue'
import { getCreatedActivities, getParticipatedActivities, deleteActivity, signupActivity } from '@/api/activity'

const router = useRouter()
const activeTab = ref('created')
const loading = ref(false)
const createdActivities = ref([])
const participatedActivities = ref([])
const currentUserId = ref(null)
const deleteDialogVisible = ref(false)
const activityToDelete = ref(null)

onMounted(async () => {
  // 获取当前用户ID
  const user = localStorage.getItem('user')
  if (user) {
    currentUserId.value = JSON.parse(user).id || null
  }

  // 加载活动数据
  await loadActivities()
})

const loadActivities = async () => {
  loading.value = true
  try {
    if (activeTab.value === 'created') {
      const res = await getCreatedActivities()
      createdActivities.value = res.data || []
    } else {
      const res = await getParticipatedActivities(currentUserId.value)
      participatedActivities.value = res.data || []
    }
  } catch (error) {
    console.error('加载活动失败:', error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  loadActivities()
}

const goToCreate = () => {
  router.push('/activities/create')
}

const handleEdit = (activity) => {
  router.push(`/activities/${activity.id}/edit`)
}

const handleDelete = (activity) => {
  activityToDelete.value = activity
  deleteDialogVisible.value = true
}

const confirmDelete = async () => {
  if (!activityToDelete.value) return

  try {
    await deleteActivity(activityToDelete.value.id)
    ElMessage.success('删除成功')
    await loadActivities()
  } catch (error) {
    console.error('删除失败:', error)
  }

  activityToDelete.value = null
}

const handleSignup = async (activity) => {
  try {
    await signupActivity(activity.id)
    ElMessage.success('报名成功')
    await loadActivities()
  } catch (error) {
    console.error('报名失败:', error)
  }
}
</script>

<style scoped>
.activity-list-page {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}
</style>
