<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    width="400px"
    :close-on-click-modal="false"
  >
    <p>{{ message }}</p>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '确认'
  },
  message: {
    type: String,
    default: '确定要执行此操作吗？'
  },
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['confirm', 'cancel', 'update:visible'])

const dialogVisible = ref(false)

watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
})

watch(dialogVisible, (newVal) => {
  emit('update:visible', newVal)
})

const handleConfirm = () => {
  emit('confirm')
  dialogVisible.value = false
}

const handleCancel = () => {
  emit('cancel')
  dialogVisible.value = false
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

p {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}
</style>
