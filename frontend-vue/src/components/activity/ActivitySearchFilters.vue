<template>
  <el-form :model="filters" class="search-filters" label-width="80px">
    <el-row :gutter="16">
      <el-col :span="8">
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="filters.startTime"
            type="datetime"
            placeholder="最早开始"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="filters.endTime"
            type="datetime"
            placeholder="最晚结束"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="人数上限">
          <el-input-number
            v-model="filters.maxParticipants"
            :min="0"
            placeholder="不限"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="16">
      <el-col :span="8">
        <el-form-item label="经度">
          <el-input-number
            v-model="filters.longitude"
            :precision="4"
            :step="0.01"
            placeholder="选填"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="纬度">
          <el-input-number
            v-model="filters.latitude"
            :precision="4"
            :step="0.01"
            placeholder="选填"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="半径(km)">
          <el-input-number
            v-model="filters.radiusKm"
            :min="1"
            :max="100"
            placeholder="选填"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24" style="text-align: right">
        <el-button @click="emit('reset')">重置</el-button>
        <el-button type="primary" @click="emit('search', filters)">搜索</el-button>
      </el-col>
    </el-row>
  </el-form>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import type { ActivitySearchQuery } from '@/types'

const filters = reactive<ActivitySearchQuery>({
  startTime: undefined,
  endTime: undefined,
  longitude: undefined,
  latitude: undefined,
  radiusKm: undefined,
  maxParticipants: undefined,
  minParticipants: undefined,
})

const emit = defineEmits<{
  search: [query: ActivitySearchQuery]
  reset: []
}>()

function reset() {
  filters.startTime = undefined
  filters.endTime = undefined
  filters.longitude = undefined
  filters.latitude = undefined
  filters.radiusKm = undefined
  filters.maxParticipants = undefined
  filters.minParticipants = undefined
  emit('reset')
}

defineExpose({ reset })
</script>

<style scoped lang="scss">
.search-filters {
  background: #fff;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 16px;
}
</style>
