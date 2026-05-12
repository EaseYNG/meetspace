<template>
  <div
    class="activity-card"
    :class="{ 'is-deleted': activity.status === ActivityStatus.DELETED }"
    @click="handleClick"
  >
    <div class="card-image">
      <img
        v-if="activity.image"
        :src="activity.image"
        alt="cover"
        @error="onImgError"
      />
      <div v-else class="img-placeholder">
        <el-icon :size="32" color="#C0C4CC"><Picture /></el-icon>
      </div>
      <div class="card-badge">
        <ActivityStatusBadge :status="activity.status" />
      </div>
    </div>
    <div class="card-body">
      <h3 class="card-title">{{ activity.title }}</h3>
      <p v-if="activity.description" class="card-desc">{{ activity.description }}</p>
      <div class="card-meta">
        <div class="meta-item">
          <el-icon><Clock /></el-icon>
          <span>{{ formatTime(activity.startTime) }} ~ {{ formatTime(activity.endTime) }}</span>
        </div>
        <div class="meta-item">
          <el-icon><User /></el-icon>
          <span>{{ activity.minParticipants }} ~ {{ activity.maxParticipants }} 人</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Picture, Clock, User } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { ActivityStatus } from '@/types'
import type { ActivityVO } from '@/types'
import ActivityStatusBadge from './ActivityStatusBadge.vue'
import { useRouter } from 'vue-router'

const props = defineProps<{ activity: ActivityVO }>()
const router = useRouter()

function formatTime(t: string) {
  return dayjs(t).format('MM-DD HH:mm')
}

function onImgError(e: Event) {
  (e.target as HTMLImageElement).style.display = 'none'
}

function handleClick() {
  if (props.activity.status === ActivityStatus.DELETED) return
  router.push(`/activity/${props.activity.id}`)
}
</script>

<style scoped lang="scss">
@use '../../styles/variables' as *;

.activity-card {
  background: #fff;
  border-radius: $radius;
  overflow: hidden;
  box-shadow: $shadow;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 2px solid transparent;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  }

  &.is-deleted {
    opacity: 0.5;
    cursor: not-allowed;
  }

  .card-image {
    position: relative;
    height: 140px;
    overflow: hidden;
    background: #f0f0f0;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .img-placeholder {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100%;
    }

    .card-badge {
      position: absolute;
      top: 8px;
      right: 8px;
    }
  }

  .card-body {
    padding: 12px;

    .card-title {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .card-desc {
      font-size: 13px;
      color: $text-secondary;
      margin-bottom: 8px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .card-meta {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: $text-secondary;

        .el-icon {
          font-size: 14px;
        }
      }
    }
  }
}
</style>
