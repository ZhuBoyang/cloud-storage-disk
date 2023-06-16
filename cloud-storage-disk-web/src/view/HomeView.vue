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
    const { id } = router.currentRoute.value.params
    router.push(`/dashboard/${id}`)
  }
}
</script>

<style scoped lang="scss">
.home-page {
  display: flex;
  height: 100%;
}
</style>
