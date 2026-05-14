<template>
  <div class="loading-wrap">
    <div class="spinner"></div>
    <p v-if="text" class="loading-text">{{ text }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{ text?: string }>()
</script>

<style scoped lang="scss">
@use '../../styles/variables' as *;

.loading-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 72px 0;

  .spinner {
    width: 36px;
    height: 36px;
    border: 3px solid $border;
    border-top-color: $primary;
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
  }

  .loading-text {
    margin-top: 16px;
    font-size: 14px;
    color: $text-muted;
    font-weight: 500;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
