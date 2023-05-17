<template>
  <div class="dashboard-left">
    <div class="dashboard-avatar row-col-center">
      <div class="avatar-image row-col-center">R</div>
      <div class="avatar-name">Rain Drive</div>
    </div>
    <div class="dashboard-menu">
      <div class="menu-item is-selected">
        <div class="menu-icon">
          <img :src="apiConfig().iconBaseUrl + 'icons/Category.png'" alt="dashboard">
        </div>
        <div class="menu-name">仪表盘</div>
      </div>
      <div class="menu-item">
        <div class="menu-icon">
          <img :src="apiConfig().iconBaseUrl + 'icons/Heart.png'" alt="Stared">
        </div>
        <div class="menu-name">关注的</div>
      </div>
      <div class="menu-item">
        <div class="menu-icon">
          <img :src="apiConfig().iconBaseUrl + 'icons/Delete.png'" alt="Deleted">
        </div>
        <div class="menu-name">已删除的</div>
      </div>
    </div>
  </div>
</template>

<script>
import apiConfig from '../api/apiConfig.js'

export default {
  name: 'DashboardMenu',
  methods: {
    apiConfig
  }
}
</script>

<style scoped lang="scss">
.dashboard-left {
  width: 258px;
  height: 100%;
  background-color: rgba(106, 75, 255, .05);
  .dashboard-avatar {
    margin: 50px auto 0;
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
  .dashboard-menu {
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
