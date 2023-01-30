<template>
  <div class="home-page">
    <dashboard-menu/>
    <router-view/>
  </div>
</template>

<script>
import DashboardMenu from '../components/DashboardMenu.vue'

export default {
  name: 'HomePage',
  components: {
    DashboardMenu
  }
}
</script>

<style scoped lang="scss">
.home-page {
  display: flex;
  height: 100%;
}
</style>
