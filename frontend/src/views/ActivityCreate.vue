<template>
  <Layout>
    <div class="activity-create">
      <el-card>
        <template #header>
          <h2>创建活动</h2>
        </template>

        <ActivityForm
          mode="create"
          :loading="submitting"
          @submit="handleSubmit"
          @cancel="handleCancel"
        />
      </el-card>
    </div>
  </Layout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Layout from '@/components/Layout/index.vue'
import ActivityForm from '@/components/Activity/ActivityForm.vue'
import { createActivity } from '@/api/activity'

const router = useRouter()
const submitting = ref(false)

const handleSubmit = async (formData) => {
  submitting.value = true
  try {
    await createActivity(formData)
    ElMessage.success('活动创建成功')
    router.push('/activities')
  } catch (error) {
    console.error('创建活动失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  router.push('/activities')
}
</script>

<style scoped>
.activity-create {
  max-width: 900px;
  margin: 0 auto;
}

.activity-create h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}
</style>
