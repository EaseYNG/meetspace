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
        <el-icon :size="32" color="#A0AEC0"><Picture /></el-icon>
      </div>
      <div class="img-overlay"></div>
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
      <div class="card-actions" @click.stop>
        <el-button
          v-if="canSignup(activity)"
          type="primary"
          size="small"
          :loading="actionLoading"
          @click="handleSignup"
        >
          报名
        </el-button>
        <el-button
          v-if="canQuit(activity)"
          type="warning"
          size="small"
          :loading="actionLoading"
          @click="handleQuit"
        >
          退出
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Picture, Clock, User } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { ActivityStatus } from '@/types'
import type { ActivityVO } from '@/types'
import { useActivityActions } from '@/composables/useActivityActions'
import ActivityStatusBadge from './ActivityStatusBadge.vue'
import { useRouter } from 'vue-router'

const props = defineProps<{
  activity: ActivityVO
}>()

const emit = defineEmits<{
  signup: [id: number]
  quit: [id: number]
}>()

const router = useRouter()
const { canSignup, canQuit, signup, quit, ensureLoaded } = useActivityActions()
const actionLoading = ref(false)

onMounted(() => ensureLoaded())

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

async function handleSignup() {
  actionLoading.value = true
  try {
    await signup(props.activity.id)
    emit('signup', props.activity.id)
  } finally {
    actionLoading.value = false
  }
}

async function handleQuit() {
  actionLoading.value = true
  try {
    await quit(props.activity.id)
    emit('quit', props.activity.id)
  } finally {
    actionLoading.value = false
  }
}
</script>

<style scoped lang="scss">
@use '../../styles/variables' as *;

.activity-card {
  background: $bg-card;
  border-radius: $radius;
  overflow: hidden;
  box-shadow: $shadow;
  cursor: pointer;
  transition: transform $transition, box-shadow $transition;
  border: 1px solid $border-light;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-lg;

    .card-image img {
      transform: scale(1.06);
    }

    .card-image .img-overlay {
      opacity: 1;
    }
  }

  &:active {
    transform: translateY(-1px) scale(0.99);
  }

  &.is-deleted {
    opacity: 0.45;
    cursor: not-allowed;
    filter: grayscale(0.6);

    &:hover {
      transform: none;
      box-shadow: $shadow;
    }
  }

  .card-image {
    position: relative;
    height: 148px;
    overflow: hidden;
    background: linear-gradient(135deg, #E8F5E9 0%, #E3F2FD 100%);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform $transition-slow;
    }

    .img-overlay {
      position: absolute;
      inset: 0;
      background: linear-gradient(to top, rgba(0,0,0,0.35) 0%, transparent 50%);
      opacity: 0;
      transition: opacity $transition;
      pointer-events: none;
    }

    .img-placeholder {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100%;
      background: linear-gradient(135deg, #E8F5E9 0%, #E3F2FD 100%);
    }

    .card-badge {
      position: absolute;
      top: 10px;
      right: 10px;
      z-index: 2;
    }
  }

  .card-body {
    padding: 14px 16px 16px;

    .card-title {
      font-size: 15px;
      font-weight: 600;
      margin-bottom: 6px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      color: $text;
    }

    .card-desc {
      font-size: 13px;
      color: $text-secondary;
      margin-bottom: 10px;
      line-height: 1.5;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .card-meta {
      display: flex;
      flex-direction: column;
      gap: 5px;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 5px;
        font-size: 12px;
        color: $text-secondary;

        .el-icon {
          font-size: 14px;
          color: $text-muted;
        }
      }
    }

    .card-actions {
      margin-top: 12px;
      display: flex;
      gap: 8px;
    }
  }
}
</style>
