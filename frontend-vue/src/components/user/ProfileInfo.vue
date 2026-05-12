<template>
  <el-descriptions :column="1" border>
    <el-descriptions-item label="昵称">{{ profile.nickname }}</el-descriptions-item>
    <el-descriptions-item label="用户名">{{ profile.username }}</el-descriptions-item>
    <el-descriptions-item label="年龄">{{ profile.age ?? '-' }}</el-descriptions-item>
    <el-descriptions-item label="性别">{{ profile.gender ?? '-' }}</el-descriptions-item>
    <el-descriptions-item label="邮箱">{{ profile.email ?? '-' }}</el-descriptions-item>
    <el-descriptions-item label="名">{{ profile.firstname ?? '-' }}</el-descriptions-item>
    <el-descriptions-item label="姓">{{ profile.lastname ?? '-' }}</el-descriptions-item>
  </el-descriptions>
</template>

<script setup lang="ts">
import type { UserProfileVO } from '@/types'

defineProps<{ profile: UserProfileVO }>()
</script>
