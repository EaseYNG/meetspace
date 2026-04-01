<template>
  <Layout>
    <div class="activity-detail" v-if="activity">
      <el-card>
        <template #header>
          <div class="card-header">
            <h2>{{ activity.title }}</h2>
            <el-tag :type="getStatusTagType(activity.status)" size="large">
              {{ formatStatus(activity.status) }}
            </el-tag>
          </div>
        </template>

        <div class="detail-content">
          <div class="info-section">
            <h3>活动信息</h3>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="活动描述">
                {{ activity.description }}
              </el-descriptions-item>
              <el-descriptions-item label="活动地点">
                {{ activity.location }}
              </el-descriptions-item>
              <el-descriptions-item label="开始时间">
                {{ formatDateTime(activity.startTime) }}
              </el-descriptions-item>
              <el-descriptions-item label="结束时间">
                {{ formatDateTime(activity.endTime) }}
              </el-descriptions-item>
              <el-descriptions-item label="报名截止时间">
                {{ formatDateTime(activity.signupDeadline) }}
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="image-section" v-if="activity.imageUrl">
            <h3>活动图片</h3>
            <el-image
              :src="activity.imageUrl"
              fit="cover"
              style="width: 100%; max-height: 400px;"
            />
          </div>
        </div>

        <template #footer>
          <div class="actions">
            <el-button
              v-if="isCreator"
              type="primary"
              @click="handleEdit"
            >
              编辑
            </el-button>
            <el-button
              v-if="isCreator"
              type="danger"
              @click="handleDelete"
            >
              删除
            </el-button>
            <el-button
              v-else-if="!isParticipated"
              type="success"
              @click="handleSignup"
            >
              报名参加
            </el-button>
            <el-tag v-else type="success" size="large">已报名</el-tag>
            <el-button @click="goBack">返回</el-button>
          </div>
        </template>
      </el-card>

      <Confirm
        v-model:visible="deleteDialogVisible"
        title="确认删除"
        message="确定要删除这个活动吗？此操作不可恢复。"
        @confirm="confirmDelete"
      />
    </div>

    <Loading v-else text="加载中..." />
  </Layout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Layout from '@/components/Layout/index.vue'
import Loading from '@/components/Common/Loading.vue'
import Confirm from '@/components/Common/Confirm.vue'
import { getActivityDetail, deleteActivity, signupActivity } from '@/api/activity'
import { formatDateTime, formatStatus, getStatusTagType } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const activity = ref(null)
const loading = ref(false)
const deleteDialogVisible = ref(false)
const currentUserId = ref(null)

const isCreator = computed(() => {
  return currentUserId.value && activity.value?.creatorId === currentUserId.value
})

const isParticipated = computed(() => {
  return activity.value?.isParticipated || false
})

onMounted(async () => {
  // 获取当前用户ID
  const user = localStorage.getItem('user')
  if (user) {
    currentUserId.value = JSON.parse(user).id || null
  }

  // 加载活动详情
  await loadActivityDetail()
})

const loadActivityDetail = async () => {
  loading.value = true
  try {
    const activityId = route.params.id
    const res = await getActivityDetail(activityId)
    activity.value = res.data
  } catch (error) {
    console.error('加载活动详情失败:', error)
    ElMessage.error('加载活动详情失败')
    router.push('/activities')
  } finally {
    loading.value = false
  }
}

const handleEdit = () => {
  router.push(`/activities/${activity.value.id}/edit`)
}

const handleDelete = () => {
  deleteDialogVisible.value = true
}

const confirmDelete = async () => {
  try {
    await deleteActivity(activity.value.id)
    ElMessage.success('删除成功')
    router.push('/activities')
  } catch (error) {
    console.error('删除失败:', error)
  }
}

const handleSignup = async () => {
  try {
    await signupActivity(activity.value.id)
    ElMessage.success('报名成功')
    await loadActivityDetail()
  } catch (error) {
    console.error('报名失败:', error)
  }
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.activity-detail {
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
  font-size: 24px;
  color: #303133;
}

.detail-content {
  margin-bottom: 20px;
}

.info-section, .image-section {
  margin-bottom: 30px;
}

.info-section h3, .image-section h3 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 15px;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
