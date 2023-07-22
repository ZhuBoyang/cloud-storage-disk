<template>
  <div class="menu-component">
    <div class="menu-avatar row-col-center">
      <div class="avatar-image row-col-center">R</div>
      <div class="avatar-name">Rain Drive</div>
    </div>
    <div class="menu-list">
      <div class="menu-item" v-for="(o, i) in menus" :key="i" :class="[{'is-selected': o.alt === activated}]"
           @click="changeMenu(o)">
        <div class="menu-icon">
          <img :src="apiConfig().iconBaseUrl + o.avatar" :alt="o.alt"/>
        </div>
        <div class="menu-name">{{ o.name }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import apiConfig from '../api/apiConfig.js'
import { useRouter } from 'vue-router'
import { reactive, toRefs } from 'vue'

export default {
  name: 'MenuComponent',
  setup () {
    const router = useRouter()
    const menus = [
      { avatar: 'icons/category.png', name: '仪表盘', alt: 'files' },
      { avatar: 'icons/delete.png', name: '回收站', alt: 'trash' },
      { avatar: 'icons/swap_black.png', name: '传输列表', alt: 'transform' }
    ]
    const dataList = reactive({
      menus,
      activated: router.currentRoute.value.path.split('/')[2]
    })
    const index = menus.findIndex(o => router.currentRoute.value.path.indexOf(o.alt) > 0)
    if (index === -1) {
      dataList.activated = menus[0].alt
      router.push(`${router.currentRoute.value.path}/${menus[0].alt}`)
    } else {
      router.push(router.currentRoute.value.path)
    }
    return {
      router,
      ...toRefs(dataList)
    }
  },
  methods: {
    apiConfig,
    changeMenu ({ alt }) {
      const { path } = this.router.currentRoute.value
      if (path.indexOf(alt) > 0) {
        return
      }
      this.activated = alt
      this.router.push(`/${path.split('/')[1]}/${alt}`)
    }
  }
}
</script>

<style scoped lang="scss">
.menu-component {
  padding-top: 50px;
  height: calc(100% - 50px);
  background-color: rgba(106, 75, 255, .05);
  .menu-avatar {
    margin: 0 auto;
    .avatar-image {
      width: 48px;
      height: 48px;
      color: #ffffff;
      font-size: 24px;
      background-color: rgb(106, 75, 254);
      border-radius: 12px;
    }
    .avatar-name {
      margin-left: 16px;
      font-size: 18px;
    }
  }
  .menu-list {
    margin: 88px auto 0;
    width: 210px;
    .menu-item {
      margin: 12px 0;
      padding: 0 20px;
      height: 58px;
      display: flex;
      align-items: center;
      border-radius: 20px;
      cursor: pointer;
      transition: all .3s;
      &:hover,
      &.is-selected {
        color: #ffffff;
        background-color: rgb(106, 75, 254);
        transition: all .3s;
      }
      &:first-child {
        margin-top: 0;
      }
      &:last-child {
        margin-bottom: 0;
      }
      .menu-icon {
        width: 28px;
        height: 28px;
        img {
          width: 100%;
          height: 100%;
        }
      }
      .menu-name {
        margin-left: 24px;
      }
    }
  }
}
</style>
