<template>
  <div class="explore">
    <h2 class="page-title">发现活动</h2>
    <ActivitySearchFilters ref="filterRef" @search="handleSearch" @reset="handleReset" />
    <LoadingSpinner v-if="loading" text="搜索中..." />
    <template v-else>
      <EmptyState v-if="!results.length" text="没有找到匹配的活动" />
      <el-row v-else :gutter="16">
        <el-col
          v-for="act in results"
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
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { searchActivitiesAPI } from '@/api/activity'
import type { ActivityVO, ActivitySearchQuery } from '@/types'
import ActivityCard from '@/components/activity/ActivityCard.vue'
import ActivitySearchFilters from '@/components/activity/ActivitySearchFilters.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const loading = ref(false)
const results = ref<ActivityVO[]>([])
const filterRef = ref<InstanceType<typeof ActivitySearchFilters> | null>(null)

async function handleSearch(query: ActivitySearchQuery) {
  loading.value = true
  try {
    const res = await searchActivitiesAPI(query)
    results.value = res.data.data ?? []
  } catch {
    results.value = []
  } finally {
    loading.value = false
  }
}

function handleReset() {
  results.value = []
}
</script>

<style scoped lang="scss">
.explore {
  .page-title {
    font-size: 22px;
    font-weight: 600;
    margin-bottom: 16px;
  }
}
</style>
