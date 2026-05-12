<template>
  <div class="my-activities">
    <h2 class="page-title">我的活动</h2>

    <el-tabs v-model="activeTab" class="activity-tabs">
      <el-tab-pane label="我参加的" name="participated">
        <LoadingSpinner v-if="loading.participated" />
        <EmptyState v-else-if="!participated.length" text="还没有参加任何活动" />
        <el-row v-else :gutter="16">
          <el-col
            v-for="act in participated"
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
      </el-tab-pane>

      <el-tab-pane label="我报名的" name="signed-up">
        <LoadingSpinner v-if="loading.signedUp" />
        <EmptyState v-else-if="!signedUp.length" text="还没有报名任何活动" />
        <el-row v-else :gutter="16">
          <el-col
            v-for="act in signedUp"
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
      </el-tab-pane>

      <el-tab-pane label="我创建的" name="created">
        <LoadingSpinner v-if="loading.created" />
        <EmptyState v-else-if="!created.length" text="还没有创建任何活动" />
        <el-row v-else :gutter="16">
          <el-col
            v-for="act in created"
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
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import {
  getParticipatedActivitiesAPI,
  getSignedUpActivitiesAPI,
  getCreatedActivitiesAPI,
} from '@/api/user'
import type { ActivityVO } from '@/types'
import ActivityCard from '@/components/activity/ActivityCard.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const activeTab = ref('participated')
const participated = ref<ActivityVO[]>([])
const signedUp = ref<ActivityVO[]>([])
const created = ref<ActivityVO[]>([])

const loading = reactive({
  participated: false,
  signedUp: false,
  created: false,
})

const loaders: Record<string, () => Promise<void>> = {
  participated: async () => {
    loading.participated = true
    try {
      const res = await getParticipatedActivitiesAPI()
      participated.value = res.data.data ?? []
    } finally {
      loading.participated = false
    }
  },
  'signed-up': async () => {
    loading.signedUp = true
    try {
      const res = await getSignedUpActivitiesAPI()
      signedUp.value = res.data.data ?? []
    } finally {
      loading.signedUp = false
    }
  },
  created: async () => {
    loading.created = true
    try {
      const res = await getCreatedActivitiesAPI()
      created.value = res.data.data ?? []
    } finally {
      loading.created = false
    }
  },
}

watch(activeTab, (tab) => {
  loaders[tab]?.()
}, { immediate: true })
</script>

<style scoped lang="scss">
.my-activities {
  .page-title {
    font-size: 22px;
    font-weight: 600;
    margin-bottom: 16px;
  }

  .activity-tabs {
    :deep(.el-tabs__header) {
      margin-bottom: 20px;
    }
  }
}
</style>
