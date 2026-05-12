<template>
  <el-form ref="formRef" :model="form" label-width="80px" @submit.prevent="handleSubmit">
    <el-form-item label="年龄">
      <el-input-number v-model="form.age" :min="0" :max="150" />
    </el-form-item>
    <el-form-item label="性别">
      <el-select v-model="form.gender" placeholder="请选择" clearable>
        <el-option label="男" value="Male" />
        <el-option label="女" value="Female" />
        <el-option label="其他" value="Other" />
      </el-select>
    </el-form-item>
    <el-form-item label="邮箱">
      <el-input v-model="form.email" placeholder="邮箱" />
    </el-form-item>
    <el-form-item label="名">
      <el-input v-model="form.firstname" placeholder="名" />
    </el-form-item>
    <el-form-item label="姓">
      <el-input v-model="form.lastname" placeholder="姓" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" native-type="submit" :loading="loading">保存</el-button>
      <el-button @click="emit('cancel')">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type { FormInstance } from 'element-plus'
import type { UserProfileVO, ProfileUpdateRequest } from '@/types'

const props = defineProps<{ profile: UserProfileVO }>()
const emit = defineEmits<{
  submit: [data: ProfileUpdateRequest, done: () => void]
  cancel: []
}>()

const form = reactive<ProfileUpdateRequest>({
  age: undefined,
  gender: undefined,
  email: undefined,
  firstname: undefined,
  lastname: undefined,
})

watch(
  () => props.profile,
  (p) => {
    form.age = p.age ?? undefined
    form.gender = p.gender ?? undefined
    form.email = p.email ?? undefined
    form.firstname = p.firstname ?? undefined
    form.lastname = p.lastname ?? undefined
  },
  { immediate: true },
)

const formRef = ref<FormInstance>()
const loading = ref(false)

async function handleSubmit() {
  loading.value = true
  emit('submit', { ...form }, () => { loading.value = false })
}
</script>
