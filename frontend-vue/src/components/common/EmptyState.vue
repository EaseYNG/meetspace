<template>
  <div class="empty-wrap">
    <div class="empty-icon">
      <el-icon :size="36" color="#9CA3AF"><FolderDelete /></el-icon>
    </div>
    <p class="empty-text">{{ text || '暂无数据' }}</p>
  </div>
</template>

<script setup lang="ts">
import { FolderDelete } from '@element-plus/icons-vue'

defineProps<{ text?: string }>()
</script>

<style scoped lang="scss">
@use '../../styles/variables' as *;

.empty-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 72px 0;

  .empty-icon {
    width: 72px;
    height: 72px;
    border-radius: 50%;
    background: $border-light;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;
  }

  .empty-text {
    font-size: 14px;
    color: $text-muted;
    font-weight: 500;
  }
}
</style>
