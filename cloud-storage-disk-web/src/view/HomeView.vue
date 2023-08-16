<template>
  <div class="home-page">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'HomePage'
}
</script>

<style scoped lang="scss">
.home-page {
  display: flex;
  height: 100%;
}
</style>
