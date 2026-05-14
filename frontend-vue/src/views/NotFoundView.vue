<template>
  <div class="not-found">
    <div class="nf-code">404</div>
    <p class="nf-text">页面不存在</p>
    <el-button type="primary" size="large" round @click="$router.push('/')">返回首页</el-button>
  </div>
</template>

<style scoped lang="scss">
@use '../styles/variables' as *;

.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;

  .nf-code {
    font-size: 96px;
    font-weight: 800;
    background: linear-gradient(135deg, $primary, $accent);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin-bottom: 12px;
    letter-spacing: -2px;
  }

  .nf-text {
    font-size: 18px;
    color: $text-muted;
    margin-bottom: 28px;
    font-weight: 500;
  }
}
</style>
