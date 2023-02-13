<template>
  <div class="login-user-action">
    <div class="user-box"
         @mouseenter="showUserInfoDropdown"
         @mouseleave="hideUserInfoDropdown"
    >
      <div class="user-info-btn">
        <div class="user-avatar">
          <img src="../assets/vue.svg" alt="username"/>
        </div>
        <div class="user-intro">
          <div class="user-info">
            <div class="user-name">{{ data.user.userName }}</div>
            <div class="user-email">{{ data.user.email }}</div>
          </div>
          <div class="user-more">
            <img src="../assets/icons/full/Arrow-Down-2.svg" alt="more"/>
          </div>
        </div>
      </div>
      <div class="user-info-actions"
           :class="[{'is-show': data.userInfoBox}]"
      >
        <div class="actions-item">设置</div>
        <div class="actions-item" @click="logout">退出登录</div>
      </div>
    </div>
  </div>
</template>

<script>
import http from '../api/http.js'
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'LoginUserAction',
  setup () {
    const router = useRouter()
    const data = reactive({
      userInfoBox: false, // 是否显示用户信息的下拉框
      user: {} // 用户信息
    })
    return {
      router,
      data
    }
  },
  created () {
    this.identifyUserInfo()
  },
  methods: {
    // 获取当前登录的用户信息
    identifyUserInfo () {
      const userJson = localStorage.getItem('user')
      this.data.user = JSON.parse(userJson)
    },
    // 退出登录
    logout () {
      http.req(http.url.user.logout, http.methods.post).then(response => {
        if (response !== undefined) {
          localStorage.removeItem('user')
          this.router.push('/login')
        }
      })
    },
    // 显示用户账户操作的下拉框
    showUserInfoDropdown () {
      this.data.userInfoBox = true
    },
    // 隐藏用户账户操作的下拉框
    hideUserInfoDropdown () {
      this.data.userInfoBox = false
    }
  }
}
</script>

<style scoped lang="scss">
.login-user-action {
  .user-box {
    position: relative;
    margin: 0 auto 25px;
    width: 80%;
    height: 80px;
    .user-info-btn {
      position: absolute;
      top: 0;
      left: 0;
      width: calc(100% - 32px);
      height: calc(100% - 24px);
      padding: 12px 16px;
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
    .user-info-actions {
      position: absolute;
      top: 100%;
      left: 50%;
      width: 90%;
      height: 0;
      background-color: #ffffff;
      border: 1px solid #eaeaea;
      border-bottom-left-radius: 16px;
      border-bottom-right-radius: 16px;
      transform: translateX(-50%);
      overflow: hidden;
      transition: height .3s;
      z-index: 2;
      &.is-show {
        height: 120px;
        transition: height .3s;
      }
      .actions-item {
        padding: 0 24px;
        width: calc(100% - 48px);
        height: 60px;
        display: flex;
        align-items: center;
        font-weight: bolder;
        border-bottom: 1px solid #eaeaea;
        cursor: pointer;
      }
    }
  }
}
</style>
