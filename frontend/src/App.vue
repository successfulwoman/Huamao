
<template>
  <div class="app">
    <Header v-if="!isAuthPage" />
    <main :class="{ 'has-header': !isAuthPage, 'no-header': isAuthPage }">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import Header from './components/Header.vue'

const router = useRouter()

const isAuthPage = computed(() => {
  const path = router.currentRoute.value.path
  return ['/login', '/register', '/admin/login'].includes(path) || path.startsWith('/admin')
})
</script>

<style scoped>
.app {
  min-height: 100vh;
  background: linear-gradient(180deg, #faf5ff 0%, #f3f4f6 100%);
}

main {
  padding: 2rem 0;
}

main.has-header {
  padding-top: 85px;
}

main.no-header {
  padding: 0;
  min-height: 100vh;
}
</style>
