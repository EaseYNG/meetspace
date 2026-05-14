<template>
  <LoadingSpinner v-if="loading" text="加载中..." />
  <div v-else-if="!activity" class="detail-error">
    <EmptyState text="活动不存在或已被删除" />
  </div>
  <div v-else class="detail">
    <el-button text :icon="ArrowLeft" @click="$router.back()" style="margin-bottom: 12px">
      返回
    </el-button>

    <div class="detail-header">
      <div class="detail-image">
        <img v-if="activity.image" :src="activity.image" alt="cover" />
        <div v-else class="img-placeholder">
          <el-icon :size="48" color="#C0C4CC"><Picture /></el-icon>
        </div>
        <div class="detail-badge">
          <ActivityStatusBadge :status="activity.status" />
        </div>
      </div>
    </div>

    <el-card shadow="never" class="detail-body">
      <h1 class="detail-title">{{ activity.title }}</h1>
      <p v-if="activity.description" class="detail-desc">{{ activity.description }}</p>

      <el-descriptions :column="1" border class="detail-info">
        <el-descriptions-item label="地址">{{ activity.address }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ fmt(activity.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ fmt(activity.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="报名截止">{{ fmt(activity.signupDeadline) }}</el-descriptions-item>
        <el-descriptions-item label="人数范围">
          {{ activity.minParticipants }} ~ {{ activity.maxParticipants }} 人
        </el-descriptions-item>
      </el-descriptions>

      <div class="detail-actions">
        <el-button
          v-if="canSignup(activity)"
          type="primary"
          :loading="actionLoading"
          @click="handleSignup"
        >
          我要报名
        </el-button>

        <el-button
          v-if="canQuit(activity)"
          type="warning"
          :loading="actionLoading"
          @click="handleQuit"
        >
          退出活动
        </el-button>

        <el-button
          v-if="showClosedHint"
          type="info"
          disabled
        >
          报名已截止
        </el-button>

        <el-button
          v-if="showOverHint"
          type="info"
          disabled
        >
          活动已结束
        </el-button>

        <el-button
          v-if="activity.status === ActivityStatus.READY"
          type="primary"
          plain
          @click="$router.push(`/activity/${activity.id}/edit`)"
        >
          编辑活动
        </el-button>

        <el-button
          v-if="activity.status !== ActivityStatus.DELETED"
          type="danger"
          plain
          @click="handleDelete"
        >
          删除活动
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Picture } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { ActivityStatus } from '@/types'
import type { ActivityVO } from '@/types'
import { getActivityDetailAPI, deleteActivityAPI } from '@/api/activity'
import { useActivityActions } from '@/composables/useActivityActions'
import ActivityStatusBadge from '@/components/activity/ActivityStatusBadge.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const actionLoading = ref(false)
const activity = ref<ActivityVO | null>(null)
const { canSignup, canQuit, signup, quit, ensureLoaded, markParticipant } = useActivityActions()

const activityId = computed(() => Number(route.params.id))

const showClosedHint = computed(() => {
  if (!activity.value) return false
  return activity.value.status === ActivityStatus.CLOSED && !canQuit(activity.value)
})

const showOverHint = computed(() => {
  if (!activity.value) return false
  return (
    activity.value.status === ActivityStatus.OVER ||
    activity.value.status === ActivityStatus.DELETED
  )
})

async function loadActivity() {
  loading.value = true
  try {
    await ensureLoaded()
    const res = await getActivityDetailAPI(activityId.value)
    const data = res.data.data
    if (data) {
      // 后端详情接口已设置 isParticipant，同步到 composable
      if (data.isParticipant) {
        markParticipant(data.id)
      }
    }
    activity.value = data ?? null
  } catch {
    activity.value = null
  } finally {
    loading.value = false
  }
}

async function handleSignup() {
  actionLoading.value = true
  try {
    await signup(activityId.value)
    if (activity.value) activity.value.isParticipant = true
  } finally {
    actionLoading.value = false
  }
}

async function handleQuit() {
  try {
    await ElMessageBox.confirm('确定要退出该活动吗？', '退出确认')
  } catch {
    return
  }
  actionLoading.value = true
  try {
    await quit(activityId.value)
    if (activity.value) activity.value.isParticipant = false
  } finally {
    actionLoading.value = false
  }
}

async function handleDelete() {
  try {
    await ElMessageBox.confirm('确定要删除该活动吗？此操作不可撤销。', '删除确认', {
      confirmButtonText: '删除',
      confirmButtonClass: 'el-button--danger',
      type: 'warning',
    })
  } catch {
    return
  }
  try {
    await deleteActivityAPI(activityId.value)
    ElMessage.success('已删除')
    router.push('/')
  } catch {
    // handled by interceptor
  }
}

function fmt(t: string) {
  return dayjs(t).format('YYYY-MM-DD HH:mm')
}

onMounted(loadActivity)
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.detail {
  max-width: 720px;
  margin: 0 auto;

  .detail-error {
    padding: 64px 0;
  }

  .detail-header {
    .detail-image {
      position: relative;
      height: 260px;
      border-radius: $radius-lg;
      overflow: hidden;
      background: linear-gradient(135deg, #E8F5E9 0%, #E3F2FD 100%);
      margin-bottom: 20px;
      box-shadow: $shadow-md;

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

      &::after {
        content: '';
        position: absolute;
        inset: 0;
        background: linear-gradient(to top, rgba(0,0,0,0.3) 0%, transparent 40%);
        pointer-events: none;
      }

      .detail-badge {
        position: absolute;
        top: 14px;
        right: 14px;
        z-index: 2;
      }
    }
  }

  .detail-body {
    border-radius: $radius-lg;
    border: 1px solid $border-light;
    box-shadow: $shadow;

    :deep(.el-descriptions__label) {
      font-weight: 500;
      color: $text-secondary;
    }

    .detail-title {
      font-size: 26px;
      font-weight: 700;
      margin-bottom: 10px;
      color: $text;
      letter-spacing: -0.3px;
    }

    .detail-desc {
      font-size: 15px;
      color: $text-secondary;
      margin-bottom: 24px;
      line-height: 1.7;
    }

    .detail-info {
      margin-bottom: 28px;
    }

    .detail-actions {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
    }
  }
}
</style>
