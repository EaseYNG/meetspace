<template>
  <LoadingSpinner v-if="pageLoading" />
  <div v-else class="form-page">
    <el-button text :icon="ArrowLeft" @click="$router.back()" style="margin-bottom: 12px">
      返回
    </el-button>

    <el-card shadow="never" class="form-card">
      <h2 class="form-title">{{ isEdit ? '编辑活动' : '创建活动' }}</h2>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        size="large"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="活动标题" />
        </el-form-item>

        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="活动描述（选填）"
          />
        </el-form-item>

        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="活动地址" />
        </el-form-item>

        <el-form-item label="封面图片">
          <el-input v-model="form.image" placeholder="图片URL（选填）" />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="form.startTime"
                type="datetime"
                placeholder="开始时间"
                value-format="YYYY-MM-DDTHH:mm:ss"
                :disabled-date="disablePast"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="form.endTime"
                type="datetime"
                placeholder="结束时间"
                value-format="YYYY-MM-DDTHH:mm:ss"
                :disabled-date="disablePast"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="报名截止" prop="signupDeadline">
              <el-date-picker
                v-model="form.signupDeadline"
                type="datetime"
                placeholder="报名截止时间"
                value-format="YYYY-MM-DDTHH:mm:ss"
                :disabled-date="disablePast"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="最少人数">
              <el-input-number v-model="form.minParticipants" :min="0" :max="999" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最多人数" prop="maxParticipants">
              <el-input-number v-model="form.maxParticipants" :min="1" :max="999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="submitting">
            {{ isEdit ? '保存修改' : '创建活动' }}
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import dayjs from 'dayjs'
import { ActivityStatus } from '@/types'
import type { ActivityCreateRequest, ActivityUpdateRequest } from '@/types'
import {
  createActivityAPI,
  updateActivityAPI,
  getActivityDetailAPI,
} from '@/api/activity'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)

const formRef = ref<FormInstance>()
const submitting = ref(false)
const pageLoading = ref(false)

const form = reactive<ActivityCreateRequest>({
  title: '',
  description: '',
  address: '',
  image: '',
  startTime: '',
  endTime: '',
  signupDeadline: '',
  minParticipants: 1,
  maxParticipants: 10,
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入活动标题', trigger: 'blur' }],
  address: [{ required: true, message: '请输入活动地址', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  signupDeadline: [{ required: true, message: '请选择报名截止时间', trigger: 'change' }],
}

function disablePast(time: Date) {
  return dayjs(time).isBefore(dayjs(), 'day')
}

onMounted(async () => {
  if (isEdit.value) {
    pageLoading.value = true
    try {
      const res = await getActivityDetailAPI(Number(route.params.id))
      const a = res.data.data
      if (a.status !== ActivityStatus.READY) {
        ElMessage.warning('该活动不可编辑')
        router.replace(`/activity/${a.id}`)
        return
      }
      form.title = a.title
      form.description = a.description ?? ''
      form.address = a.address
      form.image = a.image ?? ''
      form.startTime = a.startTime
      form.endTime = a.endTime
      form.signupDeadline = a.signupDeadline
      form.minParticipants = a.minParticipants
      form.maxParticipants = a.maxParticipants
    } finally {
      pageLoading.value = false
    }
  }
})

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      const updateData: ActivityUpdateRequest = {
        title: form.title,
        description: form.description || undefined,
        address: form.address,
        image: form.image || undefined,
        startTime: form.startTime,
        endTime: form.endTime,
        signupDeadline: form.signupDeadline,
      }
      await updateActivityAPI(Number(route.params.id), updateData)
      ElMessage.success('已更新')
    } else {
      await createActivityAPI(form)
      ElMessage.success('创建成功')
    }
    router.push(isEdit.value ? `/activity/${route.params.id}` : '/')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.form-page {
  max-width: 800px;
  margin: 0 auto;

  .form-card {
    border-radius: 12px;

    .form-title {
      font-size: 20px;
      font-weight: 600;
      margin-bottom: 24px;
    }
  }
}
</style>
