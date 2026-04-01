<template>
  <Layout>
    <div class="activity-edit" v-if="activity">
      <el-card>
        <template #header>
          <h2>编辑活动</h2>
        </template>

        <ActivityForm
          ref="formRef"
          mode="edit"
          :initial-data="activity"
          :loading="submitting"
          @submit="handleSubmit"
          @cancel="handleCancel"
        />
      </el-card>
    </div>

    <Loading v-else text="加载中..." />
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Layout from '@/components/Layout/index.vue'
import Loading from '@/components/Common/Loading.vue'
import ActivityForm from '@/components/Activity/ActivityForm.vue'
import { getActivityDetail, updateActivity } from '@/api/activity'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const activity = ref(null)
const submitting = ref(false)

onMounted(async () => {
  await loadActivityDetail()
})

const loadActivityDetail = async () => {
  try {
    const activityId = route.params.id
    const res = await getActivityDetail(activityId)
    activity.value = res.data
  } catch (error) {
    console.error('加载活动详情失败:', error)
    ElMessage.error('加载活动详情失败')
    router.push('/activities')
  }
}

const handleSubmit = async (formData) => {
  submitting.value = true
  try {
    await updateActivity(activity.value.id, formData)
    ElMessage.success('活动更新成功')
    router.push('/activities')
  } catch (error) {
    console.error('更新活动失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  router.push('/activities')
}
</script>

<style scoped>
.activity-edit {
  max-width: 900px;
  margin: 0 auto;
}

.activity-edit h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}
</style>
