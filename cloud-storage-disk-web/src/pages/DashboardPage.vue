<template>
  <div class="dashboard-page">
    <div class="dashboard-left">
      <div class="dashboard-avatar row-col-center">
        <div class="avatar-image row-col-center">R</div>
        <div class="avatar-name">Rain Drive</div>
      </div>
      <div class="dashboard-menu">
        <div class="menu-item is-selected">
          <div class="menu-icon">
            <img src="../assets/icons/Category.png" alt="dashboard">
          </div>
          <div class="menu-name">仪表盘</div>
        </div>
        <div class="menu-item">
          <div class="menu-icon">
            <img src="../assets/icons/Heart.png" alt="Stared">
          </div>
          <div class="menu-name">关注的</div>
        </div>
        <div class="menu-item">
          <div class="menu-icon">
            <img src="../assets/icons/Delete.png" alt="Deleted">
          </div>
          <div class="menu-name">已删除的</div>
        </div>
      </div>
    </div>
    <div class="dashboard-center">center</div>
    <div class="dashboard-right">
      <div class="user-box">
        <div class="user-avatar">
          <img src="../assets/vue.svg" alt="username"/>
        </div>
        <div class="user-intro">
          <div class="user-info">
            <div class="user-name">zhuboyang</div>
            <div class="user-email">email@qq.com</div>
          </div>
          <div class="user-more">
            <img src="../assets/icons/Arrow%20-%20Down%202.png" alt="more"/>
          </div>
        </div>
      </div>
      <div class="file-actions">
        上传文件
        <img src="../assets/icons/Plus.png" alt="上传文件"/>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DashboardPage'
}
</script>

<style scoped lang="scss">
.dashboard-page {
  display: flex;
  .dashboard-left {
    width: 258px;
    height: 100vh;
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
  .dashboard-center {
    width: calc(100% - 258px - 352px);
    height: 100vh;
  }
  .dashboard-right {
    width: 352px;
    height: 100vh;
    .user-box {
      margin: 40px auto 0;
      padding: 12px 16px;
      width: 252px;
      height: 60px;
      display: flex;
      align-items: center;
      border-radius: 16px;
      border: 1px solid #eaeaea;
      .user-avatar {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        overflow: hidden;
        img {
          width: 100%;
          height: 100%;
        }
      }
      .user-intro {
        margin-left: 10px;
        width: calc(100% - 70px);
        display: flex;
        align-items: center;
        justify-content: space-between;
        .user-info {
          .user-email {
            margin-top: 5px;
          }
        }
        .user-more {
          width: 30px;
          height: 30px;
          img {
            width: 100%;
            height: 100%;
            cursor: pointer;
          }
        }
      }
    }
    .file-actions {
      margin: 25px auto 0;
      width: 284px;
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #ffffff;
      font-size: 16px;
      background-color: #6a4bfe;
      border-radius: 14px;
      cursor: pointer;
      img {
        margin-left: 10px;
        width: 30px;
        height: 30px;
      }
    }
  }
}
</style>
