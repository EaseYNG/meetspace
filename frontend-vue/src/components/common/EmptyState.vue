<template>
  <div class="empty-wrap">
    <el-icon :size="48" color="#C0C4CC"><FolderDelete /></el-icon>
    <p class="empty-text">{{ text || '暂无数据' }}</p>
  </div>
</template>

<script setup lang="ts">
import { FolderDelete } from '@element-plus/icons-vue'

defineProps<{ text?: string }>()
</script>

<style scoped>
.empty-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 0;
}

.empty-text {
  margin-top: 12px;
  font-size: 14px;
  color: #909399;
}
</style>
