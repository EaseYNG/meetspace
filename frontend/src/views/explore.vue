<template>
  <div class="explore-page">
    <div class="page-header">
      <h1 class="page-title">{{ $t('explore') }}</h1>
      <p class="page-subtitle">{{ $t('appName') }}</p>
    </div>

    <div class="search-bar glass-card">
      <el-input
        v-model="keyword"
        :placeholder="$t('searchActivities')"
        size="large"
        clearable
        class="glass-input"
        @input="debounceSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <div v-loading="loading" class="activity-list">
      <ActivityCard v-for="item in activities" :key="item.id" :activity="item" />
      <el-empty v-if="!loading && activities.length === 0" :description="$t('noActivities')" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { searchActivitiesApi, getParticipatedActivitiesApi } from '@/api/activity'
import ActivityCard from '@/components/ActivityCard.vue'

const activities = ref([])
const keyword = ref('')
const loading = ref(true)

let debounceTimer = null
function debounceSearch() {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(fetchActivities, 300)
}

async function fetchActivities() {
  loading.value = true
  try {
    const params = {}
    if (keyword.value) params.title = keyword.value
    const res = await searchActivitiesApi(params)
    activities.value = res.data || []
  } catch {
    activities.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchActivities()
})
</script>

<style scoped>
.explore-page {
  padding: 24px 16px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text);
}

.page-subtitle {
  color: var(--text-secondary);
  font-size: 14px;
  margin-top: 4px;
}

.search-bar {
  padding: 8px 16px;
  margin-bottom: 20px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
</style>
