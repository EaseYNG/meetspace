<template>
  <div class="layout">
    <Header />
    <main class="main">
      <Container>
        <slot></slot>
      </Container>
    </main>
  </div>
</template>

<script setup>
import Header from './Header.vue'
import Container from './Container.vue'
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main {
  flex: 1;
  background-color: #f5f7fa;
}
</style>
