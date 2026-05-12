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
              <p class="stat-value">{{ homeData?.ongoingActivities.length ?? 0 }}</p>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <p class="stat-label">推荐活动</p>
              <p class="stat-value">{{ homeData?.recommendedActivities.length ?? 0 }}</p>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <p class="stat-label">状态</p>
              <p class="stat-value" style="color: #4CAF50">已登录</p>
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
import type { UserHomeVO } from '@/types'
import ActivityCard from '@/components/activity/ActivityCard.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const loading = ref(true)
const homeData = ref<UserHomeVO | null>(null)

onMounted(async () => {
  try {
    const res = await getHomeAPI()
    homeData.value = res.data.data
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.home {
  .section {
    margin-bottom: 32px;

    .section-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 8px;
    }

    .section-title {
      font-size: 20px;
      font-weight: 600;
      margin-bottom: 16px;
    }

    .section-more {
      font-size: 14px;
      color: #4CAF50;

      &:hover {
        text-decoration: underline;
      }
    }

    .stat-card {
      text-align: center;

      .stat-label {
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;
      }

      .stat-value {
        font-size: 28px;
        font-weight: 700;
        color: #4CAF50;
      }
    }
  }
}
</style>
