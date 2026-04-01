<template>
  <div class="empty">
    <el-empty :description="message" />
  </div>
</template>

<script setup>
defineProps({
  message: {
    type: String,
    default: '暂无数据'
  }
})
</script>

<style scoped>
.empty {
  padding: 40px 0;
}
</style>
