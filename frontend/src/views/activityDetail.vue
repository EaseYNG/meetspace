<template>
  <div v-if="activity" class="detail-page">
    <div class="back-bar">
      <el-button text @click="$router.back()">
        <el-icon :size="20"><ArrowLeft /></el-icon>
      </el-button>
    </div>

    <div v-if="activity.image" class="cover-image">
      <img :src="activity.image" :alt="activity.title" />
      <span class="cover-status" :class="activity.status.toLowerCase()">{{ statusText }}</span>
    </div>

    <div class="detail-body">
      <div class="detail-header">
        <h1 class="detail-title">{{ activity.title }}</h1>
        <span v-if="!activity.image" class="status-badge" :class="activity.status.toLowerCase()">{{ statusText }}</span>
      </div>

      <div class="info-grid">
        <div class="info-item glass-card">
          <el-icon><Clock /></el-icon>
          <div>
            <label>{{ $t('startTime') }}</label>
            <span>{{ formatTime(activity.startTime) }}</span>
          </div>
        </div>
        <div class="info-item glass-card">
          <el-icon><Clock /></el-icon>
          <div>
            <label>{{ $t('endTime') }}</label>
            <span>{{ formatTime(activity.endTime) }}</span>
          </div>
        </div>
        <div class="info-item glass-card">
          <el-icon><Timer /></el-icon>
          <div>
            <label>{{ $t('signupDeadline') }}</label>
            <span>{{ formatTime(activity.signupDeadline) }}</span>
          </div>
        </div>
        <div class="info-item glass-card">
          <el-icon><User /></el-icon>
          <div>
            <label>{{ $t('participants') }}</label>
            <span>{{ activity.minParticipants }}-{{ activity.maxParticipants }} {{ $t('people') }}</span>
          </div>
        </div>
      </div>

      <div class="section glass-card">
        <h3>{{ $t('address') }}</h3>
        <p>{{ activity.address }}</p>
        <div v-if="activity.latitude && activity.longitude" ref="mapRef" class="mini-map"></div>
      </div>

      <div v-if="activity.description" class="section glass-card">
        <h3>{{ $t('description') }}</h3>
        <p>{{ activity.description }}</p>
      </div>

      <div class="action-bar">
        <el-button v-if="activity.status === 'READY' && !isSignedUp" type="primary" size="large" :loading="loading" @click="handleSignup">
          {{ $t('signUp') }}
        </el-button>
        <el-button v-if="isSignedUp" size="large" :loading="loading" @click="handleQuit">
          {{ $t('quit') }}
        </el-button>
        <el-button v-if="activity.latitude && activity.longitude" size="large" @click="navigateMap">
          {{ $t('navigate') }}
        </el-button>
      </div>
    </div>
  </div>

  <div v-else v-loading="true" class="loading-page" />
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getActivityDetail, signupActivityApi, quitActivityApi } from '@/api/activity'
import { ElMessage, ElMessageBox } from 'element-plus'
import AMapLoader from '@amap/amap-jsapi-loader'

const route = useRoute()
const router = useRouter()
const activity = ref(null)
const isSignedUp = ref(false)
const loading = ref(false)
const mapRef = ref(null)

const statusText = computed(() => {
  const map = { READY: '报名中', CLOSED: '已截止', OVER: '已结束', DELETED: '已删除' }
  return map[activity.value?.status] || activity.value?.status || ''
})

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

async function fetchDetail() {
  try {
    const res = await getActivityDetail(route.params.id)
    activity.value = res.data
    initMap()
  } catch { router.back() }
}

async function initMap() {
  if (!activity.value?.latitude || !activity.value?.longitude) return
  await nextTick()
  if (!mapRef.value) return
  try {
    const AMap = await AMapLoader.load({
      key: import.meta.env.VITE_AMAP_KEY,
      version: '2.0',
    })
    const map = new AMap.Map(mapRef.value, {
      zoom: 15,
      center: [activity.value.longitude, activity.value.latitude],
      layers: [new AMap.TileLayer.Satellite()],
    })
    new AMap.Marker({
      position: [activity.value.longitude, activity.value.latitude],
      map,
    })
  } catch (e) { console.warn('Map load failed', e) }
}

async function handleSignup() {
  loading.value = true
  try {
    await signupActivityApi(route.params.id)
    ElMessage.success('报名成功')
    isSignedUp.value = true
  } catch {} finally { loading.value = false }
}

async function handleQuit() {
  try {
    await ElMessageBox.confirm('确定要退出此活动吗？', '提示', { type: 'warning' })
    loading.value = true
    await quitActivityApi(route.params.id)
    ElMessage.success('已退出')
    isSignedUp.value = false
  } catch {} finally { loading.value = false }
}

function navigateMap() {
  const { latitude, longitude, address } = activity.value
  const url = `https://uri.amap.com/marker?position=${longitude},${latitude}&name=${encodeURIComponent(address)}`
  window.open(url, '_blank')
}

onMounted(() => { fetchDetail() })
</script>

<style scoped>
.detail-page {
  padding-bottom: 24px;
}

.back-bar {
  padding: 12px 16px;
  position: sticky;
  top: 0;
  z-index: 10;
  background: var(--surface);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.cover-image {
  position: relative;
  width: 100%;
  height: 240px;
  overflow: hidden;
}

.cover-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-status {
  position: absolute;
  bottom: 16px;
  left: 16px;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  backdrop-filter: blur(12px);
  color: white;
}

.cover-status.ready { background: rgba(76, 175, 80, 0.85); }
.cover-status.closed { background: rgba(255, 152, 0, 0.85); }
.cover-status.over, .cover-status.deleted { background: rgba(158, 158, 158, 0.85); }

.detail-body {
  padding: 20px 16px;
}

.detail-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 20px;
}

.detail-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text);
  flex: 1;
}

.status-badge {
  flex-shrink: 0;
  padding: 4px 14px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.ready { background: rgba(76, 175, 80, 0.15); color: #4caf50; }
.status-badge.closed { background: rgba(255, 152, 0, 0.15); color: #ff9800; }
.status-badge.over, .status-badge.deleted { background: rgba(158, 158, 158, 0.15); color: #9e9e9e; }

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
}

.info-item .el-icon {
  font-size: 22px;
  color: var(--primary);
}

.info-item label {
  display: block;
  font-size: 11px;
  color: var(--text-secondary);
  margin-bottom: 2px;
}

.info-item span {
  font-size: 13px;
  font-weight: 500;
  color: var(--text);
}

.section {
  padding: 16px 20px;
  margin-bottom: 16px;
}

.section h3 {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--text);
}

.section p {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.mini-map {
  width: 100%;
  height: 160px;
  border-radius: var(--radius-sm);
  margin-top: 12px;
  overflow: hidden;
}

.action-bar {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.action-bar .el-button {
  flex: 1;
  border-radius: var(--radius-sm);
}

.loading-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
