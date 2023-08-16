<template>
  <router-view v-slot="{ Component }">
    <transition>
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<script>
import { useRouter } from 'vue-router'
import http from './api/http.js'

export default {
  name: 'App',
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

<style>
</style>
