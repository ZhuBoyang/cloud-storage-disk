<template>
  <div class="dashboard">
    <menu-component/>
    <file-view/>
    <action-component/>
  </div>
</template>

<script>
import { defineAsyncComponent } from 'vue'

const MenuComponent = defineAsyncComponent(() => import('../components/MenuComponent.vue'))
const FileView = defineAsyncComponent(() => import('./FileView.vue'))
const ActionComponent = defineAsyncComponent(() => import('../components/ActionComponent.vue'))

export default {
  name: 'DashboardView',
  components: {
    MenuComponent,
    FileView,
    ActionComponent
  }
}
</script>

<style scoped lang="scss">
.dashboard {
  width: 100%;
  display: flex;
}
</style>
