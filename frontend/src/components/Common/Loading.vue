<template>
  <div class="loading">
    <el-icon class="is-loading" :size="40">
      <Loading />
    </el-icon>
    <p v-if="text">{{ text }}</p>
  </div>
</template>

<script setup>
import { Loading } from '@element-plus/icons-vue'

defineProps({
  text: {
    type: String,
    default: '加载中...'
  }
})
</script>

<style scoped>
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #909399;
}

.loading p {
  margin-top: 15px;
  font-size: 14px;
}
</style>
