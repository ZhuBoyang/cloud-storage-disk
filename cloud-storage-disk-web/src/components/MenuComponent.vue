<template>
  <div class="menu-component">
    <div class="menu-avatar row-col-center">
      <div class="avatar-image row-col-center">R</div>
      <div class="avatar-name">Rain Drive</div>
    </div>
    <div class="menu-list">
      <div class="menu-item"
           v-for="(o, i) in menus"
           :key="i"
           :class="[{'is-selected': o.avatar === activated}, {'is-hovered': o.avatar === hovered}]"
           @mouseover="onmouseover(o)"
           @mouseout="onmouseout"
           @click="changeMenu(o)">
        <div class="menu-icon">
          <img :src="analysisItemIcon(o)" :alt="o.avatar"/>
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
      { avatar: 'files', name: '文件库' },
      { avatar: 'trash', name: '回收站' },
      { avatar: 'transformer', name: '传输列表' },
      { avatar: 'application', name: '应用列表' }
    ]
    const dataList = reactive({
      menus,
      activated: router.currentRoute.value.path.split('/')[2],
      hovered: ''
    })
    const index = menus.findIndex(o => router.currentRoute.value.path.indexOf(o.avatar) > 0)
    if (index === -1) {
      dataList.activated = menus[0].avatar
      router.push(`${router.currentRoute.value.path}/${menus[0].avatar}`)
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
    changeMenu ({ avatar }) {
      const { path } = this.router.currentRoute.value
      if (path.indexOf(avatar) > 0) {
        return
      }
      this.activated = avatar
      this.router.push(`/${path.split('/')[1]}/${avatar}`)
    },
    // 解析菜单项的 icon 图标
    analysisItemIcon ({ avatar }) {
      if (avatar === this.activated || avatar === this.hovered) {
        return apiConfig().iconBaseUrl + 'icons/' + avatar + '_white.png'
      }
      return apiConfig().iconBaseUrl + 'icons/' + avatar + '_black.png'
    },
    // 鼠标移入
    onmouseover ({ avatar }) {
      this.hovered = avatar
    },
    // 鼠标移出
    onmouseout () {
      this.hovered = ''
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
      &.is-selected,
      &.is-hovered {
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
