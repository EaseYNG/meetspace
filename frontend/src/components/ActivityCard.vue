<template>
  <div class="activity-card glass-card" @click="$router.push(`/activity/${activity.id}`)">
    <div v-if="activity.image" class="card-image">
      <img :src="activity.image" :alt="activity.title" />
      <span class="status-tag" :class="activity.status.toLowerCase()">{{ statusText }}</span>
    </div>
    <div class="card-body" :class="{ 'no-image': !activity.image }">
      <div class="card-header">
        <h3 class="card-title">{{ activity.title }}</h3>
        <span v-if="!activity.image" class="status-badge" :class="activity.status.toLowerCase()">{{ statusText }}</span>
      </div>
      <p v-if="activity.description" class="card-desc">{{ activity.description }}</p>
      <div class="card-meta">
        <span class="meta-item">
          <el-icon><Clock /></el-icon>
          {{ formatTime(activity.startTime) }}
        </span>
        <span class="meta-item">
          <el-icon><Location /></el-icon>
          {{ activity.address }}
        </span>
        <span class="meta-item">
          <el-icon><User /></el-icon>
          {{ activity.minParticipants }}-{{ activity.maxParticipants }}{{ $t('people') }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  activity: { type: Object, required: true },
})

const statusText = computed(() => {
  const map = { READY: 'statusReady', CLOSED: 'statusClosed', OVER: 'statusOver', DELETED: 'statusDeleted' }
  return props.activity.status ? map[props.activity.status] || props.activity.status : 'statusReady'
})

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
</script>

<style scoped>
.activity-card {
  overflow: hidden;
  cursor: pointer;
  margin-bottom: 16px;
}

.card-image {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.activity-card:hover .card-image img {
  transform: scale(1.05);
}

.status-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  backdrop-filter: blur(12px);
  background: rgba(0, 0, 0, 0.4);
  color: white;
}

.status-tag.ready { background: rgba(76, 175, 80, 0.85); }
.status-tag.closed { background: rgba(255, 152, 0, 0.85); }
.status-tag.over, .status-tag.deleted { background: rgba(158, 158, 158, 0.85); }

.card-body {
  padding: 16px 20px 20px;
}

.card-body.no-image {
  padding-top: 20px;
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 8px;
}

.card-title {
  font-size: 17px;
  font-weight: 600;
  line-height: 1.4;
  color: var(--text);
}

.status-badge {
  flex-shrink: 0;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.status-badge.ready { background: rgba(76, 175, 80, 0.15); color: #4caf50; }
.status-badge.closed { background: rgba(255, 152, 0, 0.15); color: #ff9800; }
.status-badge.over, .status-badge.deleted { background: rgba(158, 158, 158, 0.15); color: #9e9e9e; }

.card-desc {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
}

.meta-item .el-icon {
  font-size: 14px;
}
</style>
