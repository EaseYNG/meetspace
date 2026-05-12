<template>
  <div class="create-page">
    <div class="back-bar">
      <el-button text @click="$router.back()">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        <span style="margin-left: 4px">{{ editing ? $t('editActivity') : $t('createActivity') }}</span>
      </el-button>
    </div>

    <div class="form-container glass-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large">
        <el-form-item :label="$t('title')" prop="title">
          <el-input v-model="form.title" class="glass-input" />
        </el-form-item>

        <el-form-item :label="$t('description')" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" class="glass-input" />
        </el-form-item>

        <el-form-item :label="$t('address')" prop="address">
          <el-input v-model="form.address" class="glass-input" :placeholder="$t('searchLocation')">
            <template #append>
              <el-button @click="showMapPicker = true">
                <el-icon><MapLocation /></el-icon>
              </el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item :label="$t('image')" prop="image">
          <el-input v-model="form.image" class="glass-input" :placeholder="$t('image')" />
        </el-form-item>

        <div class="form-row">
          <el-form-item :label="$t('startTime')" prop="startTime">
            <el-date-picker v-model="form.startTime" type="datetime" :placeholder="$t('startTime')" style="width: 100%" class="glass-input" />
          </el-form-item>
          <el-form-item :label="$t('endTime')" prop="endTime">
            <el-date-picker v-model="form.endTime" type="datetime" :placeholder="$t('endTime')" style="width: 100%" class="glass-input" />
          </el-form-item>
        </div>

        <el-form-item :label="$t('signupDeadline')" prop="signupDeadline">
          <el-date-picker v-model="form.signupDeadline" type="datetime" :placeholder="$t('signupDeadline')" style="width: 100%" class="glass-input" />
        </el-form-item>

        <div class="form-row">
          <el-form-item :label="$t('minParticipants')" prop="minParticipants">
            <el-input-number v-model="form.minParticipants" :min="0" style="width: 100%" />
          </el-form-item>
          <el-form-item :label="$t('maxParticipants')" prop="maxParticipants">
            <el-input-number v-model="form.maxParticipants" :min="1" style="width: 100%" />
          </el-form-item>
        </div>

        <div class="latlng-row" v-if="form.latitude && form.longitude">
          <span>{{ $t('latitude') }}: {{ form.latitude.toFixed(6) }}</span>
          <span>{{ $t('longitude') }}: {{ form.longitude.toFixed(6) }}</span>
        </div>

        <el-button type="primary" size="large" class="submit-btn" :loading="submitting" @click="handleSubmit">
          {{ editing ? $t('update') : $t('create') }}
        </el-button>
      </el-form>
    </div>

    <LocationPicker v-if="showMapPicker" @confirm="onLocationPicked" @close="showMapPicker = false" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createActivityApi, updateActivityApi, getActivityDetail } from '@/api/activity'
import { ElMessage } from 'element-plus'
import LocationPicker from '@/components/LocationPicker.vue'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const showMapPicker = ref(false)

const editing = computed(() => !!route.params.id)

const form = reactive({
  title: '',
  description: '',
  address: '',
  image: '',
  startTime: null,
  endTime: null,
  signupDeadline: null,
  minParticipants: 0,
  maxParticipants: 10,
  latitude: null,
  longitude: null,
})

const rules = {
  title: [{ required: true, message: ' ', trigger: 'blur' }],
  address: [{ required: true, message: ' ', trigger: 'blur' }],
  startTime: [{ required: true, message: ' ', trigger: 'change' }],
  endTime: [{ required: true, message: ' ', trigger: 'change' }],
  signupDeadline: [{ required: true, message: ' ', trigger: 'change' }],
}

function onLocationPicked({ lat, lng, address: addr }) {
  form.latitude = lat
  form.longitude = lng
  if (addr) form.address = addr
  showMapPicker.value = false
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const payload = {
      ...form,
      startTime: form.startTime.toISOString(),
      endTime: form.endTime.toISOString(),
      signupDeadline: form.signupDeadline.toISOString(),
    }
    if (editing.value) {
      await updateActivityApi(route.params.id, payload)
      ElMessage.success('已更新')
    } else {
      await createActivityApi(payload)
      ElMessage.success('已创建')
    }
    router.push('/activities')
  } catch {} finally { submitting.value = false }
}

async function loadForEdit() {
  if (!route.params.id) return
  try {
    const res = await getActivityDetail(route.params.id)
    const a = res.data
    Object.assign(form, {
      title: a.title || '',
      description: a.description || '',
      address: a.address || '',
      image: a.image || '',
      startTime: a.startTime ? new Date(a.startTime) : null,
      endTime: a.endTime ? new Date(a.endTime) : null,
      signupDeadline: a.signupDeadline ? new Date(a.signupDeadline) : null,
      minParticipants: a.minParticipants ?? 0,
      maxParticipants: a.maxParticipants ?? 10,
      latitude: a.latitude ?? null,
      longitude: a.longitude ?? null,
    })
  } catch { router.back() }
}

onMounted(() => { loadForEdit() })
</script>

<style scoped>
.create-page {
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

.form-container {
  margin: 16px;
  padding: 24px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.latlng-row {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: var(--radius-sm);
  margin-top: 8px;
}
</style>
