<template>
  <div class="home-page">
    <router-view/>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import http from '../api/http.js'

export default {
  name: 'HomePage',
  setup () {
    const router = useRouter()
    const checkHasInitialed = () => {
      http.reqUrl.user.hasInitialed().then(response => {
        router.push(response ? '/login' : '/initial')
      })
    }
    checkHasInitialed()
  }
}
</script>

<style scoped lang="scss">
.home-page {
  display: flex;
  height: 100%;
}
</style>
