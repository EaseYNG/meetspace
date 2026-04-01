<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="rules"
    label-width="120px"
    size="large"
  >
    <el-form-item label="活动标题" prop="title">
      <el-input
        v-model="formData.title"
        placeholder="请输入活动标题"
        maxlength="100"
        show-word-limit
      />
    </el-form-item>

    <el-form-item label="活动描述" prop="description">
      <el-input
        v-model="formData.description"
        type="textarea"
        :rows="4"
        placeholder="请输入活动描述"
        maxlength="500"
        show-word-limit
      />
    </el-form-item>

    <el-form-item label="活动地点" prop="location">
      <el-input
        v-model="formData.location"
        placeholder="请输入活动地点"
        maxlength="200"
      />
    </el-form-item>

    <el-form-item label="开始时间" prop="startTime">
      <el-date-picker
        v-model="formData.startTime"
        type="datetime"
        placeholder="选择开始时间"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="YYYY-MM-DDTHH:mm:ss"
        :disabled-date="disabledDate"
      />
    </el-form-item>

    <el-form-item label="结束时间" prop="endTime">
      <el-date-picker
        v-model="formData.endTime"
        type="datetime"
        placeholder="选择结束时间"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="YYYY-MM-DDTHH:mm:ss"
        :disabled-date="disabledDate"
      />
    </el-form-item>

    <el-form-item label="报名截止时间" prop="signupDeadline">
      <el-date-picker
        v-model="formData.signupDeadline"
        type="datetime"
        placeholder="选择报名截止时间"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="YYYY-MM-DDTHH:mm:ss"
        :disabled-date="disabledDate"
      />
    </el-form-item>

    <el-form-item label="活动图片" prop="imageUrl">
      <el-input
        v-model="formData.imageUrl"
        placeholder="请输入图片URL"
      />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="handleSubmit" :loading="loading">
        {{ mode === 'create' ? '创建活动' : '保存修改' }}
      </el-button>
      <el-button @click="handleCancel">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { validateTimeOrder, validateDeadline } from '@/utils/validate'

const props = defineProps({
  mode: {
    type: String,
    default: 'create',
    validator: (value) => ['create', 'edit'].includes(value)
  },
  initialData: {
    type: Object,
    default: () => ({})
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit', 'cancel'])

const formRef = ref(null)
const formData = reactive({
  title: '',
  description: '',
  location: '',
  startTime: '',
  endTime: '',
  signupDeadline: '',
  imageUrl: ''
})

// 验证规则
const rules = {
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入活动描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度在 10 到 500 个字符', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入活动地点', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (formData.startTime && value) {
          if (!validateTimeOrder(formData.startTime, value)) {
            callback(new Error('结束时间必须晚于开始时间'))
            return
          }
        }
        callback()
      },
      trigger: 'change'
    }
  ],
  signupDeadline: [
    { required: true, message: '请选择报名截止时间', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (formData.startTime && value) {
          if (!validateDeadline(value, formData.startTime)) {
            callback(new Error('报名截止时间必须早于开始时间'))
            return
          }
        }
        callback()
      },
      trigger: 'change'
    }
  ]
}

// 禁用今天之前的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 表单提交
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate((valid) => {
    if (valid) {
      emit('submit', { ...formData })
    }
  })
}

// 取消
const handleCancel = () => {
  emit('cancel')
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(formData, {
    title: '',
    description: '',
    location: '',
    startTime: '',
    endTime: '',
    signupDeadline: '',
    imageUrl: ''
  })
}

// 初始化表单数据
onMounted(() => {
  if (props.mode === 'edit' && props.initialData) {
    Object.assign(formData, props.initialData)
  }
})

// 暴露方法
defineExpose({
  resetForm
})
</script>

<style scoped>
.el-form {
  max-width: 800px;
}
</style>
