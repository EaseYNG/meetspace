<template>
  <div class="activity-list">
    <Loading v-if="loading" />
    <Empty v-else-if="activities.length === 0" message="暂无活动" />
    <ActivityCard
      v-else
      v-for="activity in activities"
      :key="activity.id"
      :activity="activity"
      :show-actions="showActions"
      :current-user-id="currentUserId"
      @edit="$emit('edit', $event)"
      @delete="$emit('delete', $event)"
      @signup="$emit('signup', $event)"
    />
  </div>
</template>

<script setup>
import Loading from '@/components/Common/Loading.vue'
import Empty from '@/components/Common/Empty.vue'
import ActivityCard from './ActivityCard.vue'

defineProps({
  activities: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  showActions: {
    type: Boolean,
    default: true
  },
  currentUserId: {
    type: Number,
    default: null
  }
})

defineEmits(['edit', 'delete', 'signup'])
</script>

<style scoped>
.activity-list {
  display: flex;
  flex-direction: column;
}
</style>
