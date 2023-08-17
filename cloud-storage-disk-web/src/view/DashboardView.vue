<template>
  <div class="dashboard">
    <div class="menu">
      <menu-component/>
    </div>
    <div class="file">
      <router-view/>
    </div>
    <div class="action">
      <action-component/>
    </div>
  </div>
</template>

<script>
import { defineAsyncComponent } from 'vue'

const MenuComponent = defineAsyncComponent(() => import('../components/MenuComponent.vue'))
const ActionComponent = defineAsyncComponent(() => import('../components/ActionComponent.vue'))

export default {
  name: 'DashboardView',
  components: {
    MenuComponent,
    ActionComponent
  }
}
</script>

<style scoped lang="scss">
.dashboard {
  width: 100%;
  display: flex;
  .menu {
    width: 258px;
  }
  .file {
    width: calc(100% - 258px - 450px);
    height: 100vh;
  }
  .action {
    width: 450px;
  }
}
</style>
