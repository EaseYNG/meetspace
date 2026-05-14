<template>
  <el-tag :type="tagType" :hit="true" size="small" effect="dark" round>
    {{ label }}
  </el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ActivityStatus } from '@/types'

const props = defineProps<{ status: ActivityStatus }>()

const tagType = computed(() => {
  switch (props.status) {
    case ActivityStatus.READY:
      return 'success'
    case ActivityStatus.CLOSED:
      return 'primary'
    case ActivityStatus.OVER:
      return 'info'
    case ActivityStatus.DELETED:
      return 'danger'
  }
})

const label = computed(() => {
  switch (props.status) {
    case ActivityStatus.READY:
      return '报名中'
    case ActivityStatus.CLOSED:
      return '已截止'
    case ActivityStatus.OVER:
      return '已结束'
    case ActivityStatus.DELETED:
      return '已删除'
  }
})
</script>
