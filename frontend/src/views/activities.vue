<template>
  <div class="activities-page">
    <div class="page-header">
      <h1 class="page-title">{{ $t('activities') }}</h1>
      <el-button type="primary" circle size="large" class="create-btn" @click="$router.push('/activity/create')">
        <el-icon :size="20"><Plus /></el-icon>
      </el-button>
    </div>

    <el-tabs v-model="activeTab" class="glass-tabs" @tab-change="fetchData">
      <el-tab-pane :label="$t('created')" name="created" />
      <el-tab-pane :label="$t('signedUpTab')" name="signed" />
      <el-tab-pane :label="$t('participated')" name="participated" />
    </el-tabs>

    <div v-loading="loading" class="activity-list">
      <ActivityCard v-for="item in activities" :key="item.id" :activity="item" />
      <el-empty v-if="!loading && activities.length === 0" :description="$t('noActivities')" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCreatedActivitiesApi, getSignedUpActivitiesApi, getParticipatedActivitiesApi } from '@/api/activity'
import ActivityCard from '@/components/ActivityCard.vue'

const activeTab = ref('created')
const activities = ref([])
const loading = ref(true)

async function fetchData() {
  loading.value = true
  try {
    const apis = {
      created: getCreatedActivitiesApi,
      signed: getSignedUpActivitiesApi,
      participated: getParticipatedActivitiesApi,
    }
    const res = await apis[activeTab.value]()
    activities.value = res.data || []
  } catch {
    activities.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => { fetchData() })
</script>

<style scoped>
.activities-page {
  padding: 24px 16px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text);
}

.create-btn {
  background: var(--primary) !important;
  border: none;
  box-shadow: 0 4px 16px rgba(76, 175, 80, 0.3);
}

.glass-tabs {
  background: var(--surface);
  backdrop-filter: blur(var(--blur));
  -webkit-backdrop-filter: blur(var(--blur));
  border-radius: var(--radius-sm);
  padding: 4px 12px;
  margin-bottom: 16px;
  border: 1px solid var(--surface-border);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
</style>
