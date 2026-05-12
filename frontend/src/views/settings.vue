<template>
  <div class="settings-page">
    <div class="back-bar">
      <el-button text @click="$router.back()">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        <span style="margin-left: 4px">{{ $t('settings') }}</span>
      </el-button>
    </div>

    <div class="settings-list">
      <div class="settings-item glass-card">
        <div class="item-left">
          <el-icon><Iphone /></el-icon>
          <span>{{ $t('theme') }}</span>
        </div>
        <el-switch
          :model-value="appStore.isDark"
          :active-text="$t('dark')"
          :inactive-text="$t('light')"
          @change="appStore.toggleTheme"
        />
      </div>

      <div class="settings-item glass-card">
        <div class="item-left">
          <el-icon><ChatDotRound /></el-icon>
          <span>{{ $t('language') }}</span>
        </div>
        <el-select :model-value="appStore.locale" @change="changeLocale" size="small" style="width: 100px">
          <el-option label="中文" value="zh" />
          <el-option label="English" value="en" />
        </el-select>
      </div>

      <div class="settings-item glass-card">
        <div class="item-left">
          <el-icon><InfoFilled /></el-icon>
          <span>{{ $t('about') }}</span>
        </div>
        <span class="version">{{ $t('versionInfo') }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/stores/app'

const router = useRouter()
const { locale } = useI18n()
const appStore = useAppStore()

function changeLocale(val) {
  appStore.setLocale(val)
  locale.value = val
}
</script>

<style scoped>
.settings-page {
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

.settings-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.settings-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-left .el-icon {
  font-size: 20px;
  color: var(--primary);
}

.item-left span {
  font-size: 15px;
  color: var(--text);
}

.version {
  font-size: 13px;
  color: var(--text-secondary);
}
</style>
