<template>
  <div class="home-page">
    <router-view/>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'HomePage',
  setup () {
    const router = useRouter()
    router.push('/dashboard')
  }
}
</script>

<style scoped lang="scss">
.home-page {
  display: flex;
  height: 100%;
}
</style>
