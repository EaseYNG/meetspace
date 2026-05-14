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
            <ActivityCard
              :activity="act"
              @quit="(id: number) => removeFromList(participated, id)"
            />
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
            <ActivityCard
              :activity="act"
              @quit="(id: number) => removeFromList(signedUp, id)"
            />
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
            <ActivityCard
              :activity="act"
              @quit="(id: number) => removeFromList(created, id)"
            />
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
import { useActivityActions } from '@/composables/useActivityActions'
import type { ActivityVO } from '@/types'
import ActivityCard from '@/components/activity/ActivityCard.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const activeTab = ref('participated')
const participated = ref<ActivityVO[]>([])
const signedUp = ref<ActivityVO[]>([])
const created = ref<ActivityVO[]>([])
const { ensureLoaded, markParticipant } = useActivityActions()

const loading = reactive({
  participated: false,
  signedUp: false,
  created: false,
})

const loaders: Record<string, () => Promise<void>> = {
  participated: async () => {
    loading.participated = true
    try {
      await ensureLoaded()
      const res = await getParticipatedActivitiesAPI()
      const list = res.data.data ?? []
      for (const act of list) {
        if (act.isParticipant) markParticipant(act.id)
      }
      participated.value = list
    } finally {
      loading.participated = false
    }
  },
  'signed-up': async () => {
    loading.signedUp = true
    try {
      await ensureLoaded()
      const res = await getSignedUpActivitiesAPI()
      const list = res.data.data ?? []
      for (const act of list) {
        if (act.isParticipant) markParticipant(act.id)
      }
      signedUp.value = list
    } finally {
      loading.signedUp = false
    }
  },
  created: async () => {
    loading.created = true
    try {
      await ensureLoaded()
      const res = await getCreatedActivitiesAPI()
      const list = res.data.data ?? []
      for (const act of list) {
        if (act.isParticipant) markParticipant(act.id)
      }
      created.value = list
    } finally {
      loading.created = false
    }
  },
}

function removeFromList(list: ActivityVO[], id: number) {
  const idx = list.findIndex(a => a.id === id)
  if (idx !== -1) list.splice(idx, 1)
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
