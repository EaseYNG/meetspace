<template>
  <div class="agent-page">
    <div class="page-header">
      <h1 class="page-title">{{ $t('agent') }}</h1>
    </div>

    <div class="placeholder-card glass-card">
      <div class="placeholder-icon">
        <el-icon :size="48"><MagicStick /></el-icon>
      </div>
      <h2>{{ $t('agent') }}</h2>
      <p>{{ $t('agentComingSoon') }}</p>
    </div>
  </div>
</template>

<style scoped>
.agent-page {
  padding: 24px 16px;
  min-height: calc(100vh - 80px);
  display: flex;
  flex-direction: column;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text);
}

.placeholder-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  text-align: center;
  gap: 16px;
}

.placeholder-icon {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.15), rgba(129, 199, 132, 0.15));
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary);
}

.placeholder-card h2 {
  font-size: 20px;
  font-weight: 600;
  color: var(--text);
}

.placeholder-card p {
  font-size: 14px;
  color: var(--text-secondary);
  max-width: 260px;
  line-height: 1.6;
}
</style>
