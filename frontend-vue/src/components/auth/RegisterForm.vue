<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-width="0"
    size="large"
    @submit.prevent="handleRegister"
  >
    <el-form-item prop="nickname">
      <el-input v-model="form.nickname" placeholder="昵称" :prefix-icon="User" />
    </el-form-item>
    <el-form-item prop="username">
      <el-input v-model="form.username" placeholder="用户名" :prefix-icon="UserFilled" />
    </el-form-item>
    <el-form-item prop="password">
      <el-input
        v-model="form.password"
        type="password"
        placeholder="密码（至少6位）"
        show-password
        :prefix-icon="Lock"
      />
    </el-form-item>
    <el-form-item>
      <el-button
        type="primary"
        native-type="submit"
        :loading="loading"
        style="width: 100%"
      >
        注册
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { User, UserFilled, Lock } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { RegisterRequest } from '@/types'

const form = reactive<RegisterRequest>({
  nickname: '',
  username: '',
  password: '',
})

const rules: FormRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 32, message: '用户名长度3-32位', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' },
  ],
}

const formRef = ref<FormInstance>()
const loading = ref(false)
const emit = defineEmits<{
  register: [data: RegisterRequest, done: () => void]
}>()

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  emit('register', { ...form }, () => { loading.value = false })
}
</script>
