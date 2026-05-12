<template>
  <div class="profile">
    <LoadingSpinner v-if="!auth.profile" />
    <template v-else>
      <el-card shadow="never" class="profile-card">
        <div class="profile-header">
          <el-avatar :size="64" class="profile-avatar">{{ auth.profile.nickname[0] }}</el-avatar>
          <div>
            <h2>{{ auth.profile.nickname }}</h2>
            <p class="profile-username">@{{ auth.profile.username }}</p>
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="profile-card" style="margin-top: 16px">
        <div class="section-header">
          <h3>{{ editing ? '编辑资料' : '个人资料' }}</h3>
          <el-button
            v-if="!editing"
            text
            type="primary"
            @click="editing = true"
          >
            编辑
          </el-button>
        </div>

        <ProfileInfo v-if="!editing" :profile="auth.profile" />
        <ProfileEdit
          v-else
          :profile="auth.profile"
          @submit="handleUpdate"
          @cancel="editing = false"
        />
      </el-card>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { updateProfileAPI, getProfileAPI } from '@/api/user'
import type { ProfileUpdateRequest } from '@/types'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import ProfileInfo from '@/components/user/ProfileInfo.vue'
import ProfileEdit from '@/components/user/ProfileEdit.vue'

const auth = useAuthStore()
const editing = ref(false)

async function handleUpdate(data: ProfileUpdateRequest, done: () => void) {
  try {
    await updateProfileAPI(data)
    ElMessage.success('资料已更新')
    editing.value = false
    const res = await getProfileAPI()
    auth.setProfile(res.data.data)
  } finally {
    done()
  }
}
</script>

<style scoped lang="scss">
.profile {
  max-width: 640px;
  margin: 0 auto;

  .profile-card {
    border-radius: 12px;

    .profile-header {
      display: flex;
      align-items: center;
      gap: 16px;

      .profile-avatar {
        background-color: #4CAF50;
        color: #fff;
        font-size: 24px;
        font-weight: 600;
      }

      .profile-username {
        font-size: 14px;
        color: #909399;
        margin-top: 2px;
      }
    }

    .section-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 16px;

      h3 {
        font-size: 16px;
        font-weight: 600;
      }
    }
  }
}
</style>
