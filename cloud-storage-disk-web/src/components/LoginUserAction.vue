<template>
  <div class="login-user-action">
    <div class="user-box"
         @mouseenter="userInfoBox = true"
         @mouseleave="userInfoBox = false"
    >
      <div class="user-info-btn">
        <div class="user-avatar row-col-center">
          <span v-if="user.nickName !== ''">{{ user.nickName.substring(0, 1) }}</span>
          <img :src="apiConfig().iconBaseUrl + 'file/ppt.png'" v-else alt="nickName"/>
        </div>
        <div class="user-intro">
          <div class="user-info">
            <div class="user-name">{{ user.nickName }}</div>
            <div class="user-email">{{ user.email }}</div>
          </div>
          <div class="user-more">
            <img :src="apiConfig().iconBaseUrl + 'icons/arrow_down.png'" alt="more"/>
          </div>
        </div>
      </div>
      <div class="user-info-actions"
           :class="[{'is-show': userInfoBox}]"
      >
        <div class="actions-item">设置</div>
        <div class="actions-item" @click="logout">退出登录</div>
      </div>
    </div>
  </div>
</template>

<script>
import http from '../api/http.js'
import { reactive, toRefs } from 'vue'
import { useRouter } from 'vue-router'
import apiConfig from '../api/apiConfig.js'

export default {
  name: 'LoginUserAction',
  emits: ['on-load'],
  setup (props, context) {
    const { emit } = context
    const router = useRouter()
    const dataList = reactive({
      userInfoBox: false, // 是否显示用户信息的下拉框
      user: {
        email: '', // 邮箱地址
        nickName: '', // 昵称
        avatar: '' // 头像
      }
    })
    return {
      emit,
      router,
      ...toRefs(dataList)
    }
  },
  created () {
    this.queryUserInfo()
  },
  methods: {
    apiConfig,
    // 获取当前登录的用户信息
    queryUserInfo () {
      http.reqUrl.user.info().then(response => {
        const { email, nickName, avatar } = response
        this.user.email = email
        this.user.nickName = nickName
        this.user.avatar = avatar
        this.emit('on-load', response)
      })
    },
    // 退出登录
    logout () {
      http.reqUrl.user.logout().then(response => {
        if (response) {
          localStorage.removeItem('t')
          this.router.push('/login')
        }
      })
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
        border: 1px solid #efefef;
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
