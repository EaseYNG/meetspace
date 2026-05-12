<template>
  <div class="loading-wrap">
    <el-icon class="is-loading" :size="32" color="#4CAF50">
      <Loading />
    </el-icon>
    <p v-if="text" class="loading-text">{{ text }}</p>
  </div>
</template>

<script setup lang="ts">
import { Loading } from '@element-plus/icons-vue'

defineProps<{ text?: string }>()
</script>

<style scoped>
.loading-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 0;
}

.loading-text {
  margin-top: 12px;
  font-size: 14px;
  color: #909399;
}
</style>
