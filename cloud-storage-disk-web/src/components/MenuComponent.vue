<template>
  <div class="menu-component">
    <div class="menu-avatar row-col-center">
      <div class="avatar-image row-col-center">R</div>
      <div class="avatar-name">Rain Drive</div>
    </div>
    <div class="menu-list">
      <div class="menu-item is-selected">
        <div class="menu-icon">
          <img :src="apiConfig().iconBaseUrl + 'icons/category.png'" alt="dashboard"/>
        </div>
        <div class="menu-name">仪表盘</div>
      </div>
      <div class="menu-item">
        <div class="menu-icon">
          <img :src="apiConfig().iconBaseUrl + 'icons/delete.png'" alt="recycle"/>
        </div>
        <div class="menu-name">回收站</div>
      </div>
      <div class="menu-item">
        <div class="menu-icon">
          <img :src="apiConfig().iconBaseUrl + 'icons/swap.png'" alt="transformer"/>
        </div>
        <div class="menu-name">传输列表</div>
      </div>
    </div>
  </div>
</template>

<script>
import apiConfig from '../api/apiConfig.js'

export default {
  name: 'MenuComponent',
  methods: {
    apiConfig
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
        .menu-icon {
          img {
            filter: brightness(100);
          }
        }
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
