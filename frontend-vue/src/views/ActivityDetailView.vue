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
        <!-- READY + 未报名 → 报名 -->
        <el-button
          v-if="showSignup"
          type="primary"
          :loading="actionLoading"
          @click="handleSignup"
        >
          我要报名
        </el-button>

        <!-- READY/CLOSED + 已报名 → 退出 -->
        <el-button
          v-if="showQuit"
          type="warning"
          :loading="actionLoading"
          @click="handleQuit"
        >
          退出活动
        </el-button>

        <!-- CLOSED + 未报名 → 禁用 -->
        <el-button
          v-if="showClosedHint"
          type="info"
          disabled
        >
          报名已截止
        </el-button>

        <!-- OVER/DELETED → 禁用 -->
        <el-button
          v-if="showOverHint"
          type="info"
          disabled
        >
          活动已结束
        </el-button>

        <!-- READY 状态才能编辑 -->
        <el-button
          v-if="activity.status === ActivityStatus.READY"
          type="primary"
          plain
          @click="$router.push(`/activity/${activity.id}/edit`)"
        >
          编辑活动
        </el-button>

        <!-- 非 DELETED 才能删除 -->
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
import { getActivityDetailAPI, signupActivityAPI, quitActivityAPI, deleteActivityAPI } from '@/api/activity'
import ActivityStatusBadge from '@/components/activity/ActivityStatusBadge.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const actionLoading = ref(false)
const activity = ref<ActivityVO | null>(null)
const amParticipant = ref(false)

const activityId = computed(() => Number(route.params.id))

// ---- 按钮可见性 ----
const showSignup = computed(() =>
  activity.value?.status === ActivityStatus.READY && !amParticipant.value
)
const showQuit = computed(() =>
  (activity.value?.status === ActivityStatus.READY
    || activity.value?.status === ActivityStatus.CLOSED)
  && amParticipant.value
)
const showClosedHint = computed(() =>
  activity.value?.status === ActivityStatus.CLOSED && !amParticipant.value
)
const showOverHint = computed(() =>
  activity.value?.status === ActivityStatus.OVER
  || activity.value?.status === ActivityStatus.DELETED
)

async function loadActivity() {
  loading.value = true
  try {
    const res = await getActivityDetailAPI(activityId.value)
    activity.value = res.data.data
  } catch {
    activity.value = null
  } finally {
    loading.value = false
  }
}

async function handleSignup() {
  actionLoading.value = true
  try {
    await signupActivityAPI(activityId.value)
    ElMessage.success('报名成功')
    amParticipant.value = true
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
    await quitActivityAPI(activityId.value)
    ElMessage.success('已退出活动')
    amParticipant.value = false
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
      height: 240px;
      border-radius: $radius;
      overflow: hidden;
      background: #f0f0f0;
      margin-bottom: 16px;

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

      .detail-badge {
        position: absolute;
        top: 12px;
        right: 12px;
      }
    }
  }

  .detail-body {
    border-radius: $radius;

    .detail-title {
      font-size: 24px;
      font-weight: 700;
      margin-bottom: 8px;
    }

    .detail-desc {
      font-size: 14px;
      color: $text-secondary;
      margin-bottom: 24px;
    }

    .detail-info {
      margin-bottom: 24px;
    }

    .detail-actions {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
    }
  }
}
</style>
