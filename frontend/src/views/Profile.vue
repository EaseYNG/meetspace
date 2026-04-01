<template>
  <Layout>
    <div class="profile">
      <el-card>
        <template #header>
          <div class="card-header">
            <h2>个人中心</h2>
            <el-button
              v-if="!isEditing"
              type="primary"
              @click="handleEdit"
            >
              编辑
            </el-button>
            <template v-else>
              <el-button @click="handleCancel">取消</el-button>
              <el-button type="primary" @click="handleSave" :loading="saving">
                保存
              </el-button>
            </template>
          </div>
        </template>

        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="100px"
          :disabled="!isEditing"
        >
          <el-form-item label="用户名">
            <el-input v-model="formData.username" disabled />
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input v-model="formData.email" />
          </el-form-item>

          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="formData.nickname" />
          </el-form-item>

          <el-form-item label="电话" prop="phone">
            <el-input v-model="formData.phone" />
          </el-form-item>

          <el-form-item label="个人简介" prop="bio">
            <el-input
              v-model="formData.bio"
              type="textarea"
              :rows="4"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </Layout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import Layout from '@/components/Layout/index.vue'
import { getProfile, setProfile } from '@/api/profile'
import { emailValidator } from '@/utils/validate'

const formRef = ref(null)
const isEditing = ref(false)
const saving = ref(false)
const formData = reactive({
  username: '',
  email: '',
  nickname: '',
  phone: '',
  bio: ''
})

// 验证规则
const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { validator: emailValidator, trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
  ]
}

onMounted(async () => {
  await loadProfile()
})

const loadProfile = async () => {
  try {
    const res = await getProfile()
    if (res.data) {
      Object.assign(formData, res.data)
    }
  } catch (error) {
    console.error('加载用户资料失败:', error)
  }
}

const handleEdit = () => {
  isEditing.value = true
}

const handleCancel = () => {
  isEditing.value = false
  loadProfile()
}

const handleSave = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        await setProfile(formData)
        ElMessage.success('保存成功')
        isEditing.value = false

        // 更新localStorage中的用户信息
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        Object.assign(user, formData)
        localStorage.setItem('user', JSON.stringify(user))
      } catch (error) {
        console.error('保存失败:', error)
      } finally {
        saving.value = false
      }
    }
  })
}
</script>

<style scoped>
.profile {
  max-width: 800px;
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
