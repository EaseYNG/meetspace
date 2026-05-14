<template>
  <div class="home">
    <LoadingSpinner v-if="loading" text="加载中..." />
    <template v-else>
      <section class="section">
        <h2 class="section-title">欢迎回来，{{ homeData?.profile.nickname }}</h2>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <p class="stat-label">进行中的活动</p>
              <p class="stat-value">{{ homeData?.ongoingActivities?.length ?? 0 }}</p>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <p class="stat-label">推荐活动</p>
              <p class="stat-value">{{ homeData?.recommendedActivities?.length ?? 0 }}</p>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <p class="stat-label">状态</p>
              <p class="stat-value">已登录</p>
            </el-card>
          </el-col>
        </el-row>
      </section>

      <section class="section">
        <div class="section-header">
          <h2 class="section-title">推荐活动</h2>
          <router-link to="/explore" class="section-more">查看更多</router-link>
        </div>
        <EmptyState v-if="!homeData?.recommendedActivities?.length" text="暂无推荐活动" />
        <el-row v-else :gutter="16">
          <el-col
            v-for="act in homeData.recommendedActivities"
            :key="act.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
            style="margin-bottom: 16px"
          >
            <ActivityCard :activity="act" />
          </el-col>
        </el-row>
      </section>

      <section class="section">
        <h2 class="section-title">进行中的活动</h2>
        <EmptyState v-if="!homeData?.ongoingActivities?.length" text="暂无进行中的活动" />
        <el-row v-else :gutter="16">
          <el-col
            v-for="act in homeData.ongoingActivities"
            :key="act.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
            style="margin-bottom: 16px"
          >
            <ActivityCard :activity="act" />
          </el-col>
        </el-row>
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getHomeAPI } from '@/api/user'
import { useActivityActions } from '@/composables/useActivityActions'
import type { UserHomeVO } from '@/types'
import ActivityCard from '@/components/activity/ActivityCard.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const loading = ref(true)
const homeData = ref<UserHomeVO | null>(null)
const { ensureLoaded, markParticipant } = useActivityActions()

onMounted(async () => {
  try {
    await ensureLoaded()
    const res = await getHomeAPI()
    const data = res.data.data
    if (data) {
      // 同步参与状态到 composable
      for (const act of data.ongoingActivities ?? []) {
        markParticipant(act.id)
      }
      for (const act of data.recommendedActivities ?? []) {
        if (act.isParticipant) markParticipant(act.id)
      }
    }
    homeData.value = data ?? null
  } catch {
    homeData.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
@use '../styles/variables' as *;

.home {
  .section {
    margin-bottom: 36px;

    .section-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 12px;
    }

    .section-title {
      font-size: 20px;
      font-weight: 700;
      margin-bottom: 16px;
      color: $text;
      letter-spacing: -0.2px;
    }

    .section-more {
      font-size: 14px;
      color: $primary;
      font-weight: 500;
      transition: color $transition-fast;

      &:hover {
        color: $primary-dark;
      }
    }

    .stat-card {
      text-align: center;
      border-radius: $radius;
      border: 1px solid $border-light;
      transition: transform $transition, box-shadow $transition;

      &:hover {
        transform: translateY(-2px);
        box-shadow: $shadow-md;
      }

      .stat-label {
        font-size: 13px;
        color: $text-muted;
        margin-bottom: 8px;
        font-weight: 500;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }

      .stat-value {
        font-size: 32px;
        font-weight: 800;
        background: linear-gradient(135deg, $primary, $accent);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }
  }
}
</style>
