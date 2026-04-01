<template>
  <el-card class="activity-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <h3>{{ activity.title }}</h3>
        <el-tag :type="getStatusTagType(activity.status)" size="small">
          {{ formatStatus(activity.status) }}
        </el-tag>
      </div>
    </template>
    <div class="card-content">
      <div class="info-item">
        <el-icon><Calendar /></el-icon>
        <span>{{ formatDateTime(activity.startTime) }} - {{ formatDateTime(activity.endTime) }}</span>
      </div>
      <div class="info-item">
        <el-icon><Location /></el-icon>
        <span>{{ activity.location }}</span>
      </div>
      <div class="info-item" v-if="activity.description">
        <el-icon><Document /></el-icon>
        <span>{{ activity.description }}</span>
      </div>
      <div class="info-item" v-if="activity.signupDeadline">
        <el-icon><Clock /></el-icon>
        <span>报名截止: {{ formatDateTime(activity.signupDeadline) }}</span>
      </div>
    </div>
    <template #footer v-if="showActions">
      <div class="card-actions">
        <el-button
          v-if="isCreator"
          type="primary"
          size="small"
          @click="$emit('edit', activity)"
        >
          编辑
        </el-button>
        <el-button
          v-if="isCreator"
          type="danger"
          size="small"
          @click="$emit('delete', activity)"
        >
          删除
        </el-button>
        <el-button
          v-else-if="!isParticipated"
          type="success"
          size="small"
          @click="$emit('signup', activity)"
        >
          报名
        </el-button>
        <el-tag v-else type="success" size="small">已报名</el-tag>
      </div>
    </template>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import { Calendar, Location, Document, Clock } from '@element-plus/icons-vue'
import { formatDateTime, formatStatus, getStatusTagType } from '@/utils/format'

const props = defineProps({
  activity: {
    type: Object,
    required: true
  },
  showActions: {
    type: Boolean,
    default: true
  },
  currentUserId: {
    type: Number,
    default: null
  }
})

defineEmits(['edit', 'delete', 'signup'])

const isCreator = computed(() => {
  return props.currentUserId && props.activity.creatorId === props.currentUserId
})

const isParticipated = computed(() => {
  return props.activity.isParticipated || false
})
</script>

<style scoped>
.activity-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.card-content {
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  color: #606266;
  font-size: 14px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
