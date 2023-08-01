<template>
  <div class="reset-pwd">
    <div class="page-left">
      <div class="pan-logo-box">
        <div class="pan-logo-bg row-col-center">
          <div class="pan-logo-img">R</div>
        </div>
        <div class="pan-logo-label">云存储</div>
      </div>
      <div class="pan-bg">
        <img :src="apiConfig().iconBaseUrl + 'icons/login-bg.png'" alt="登录背景"/>
      </div>
    </div>
    <div class="page-right">
      <div class="page-box">
        <div class="form-title">重置你的密码</div>
        <div class="form-account-register">请填写以下字段，使用邮箱重置您的账户密码</div>
        <a-form :model="form" layout="vertical">
          <a-form-item field="email" label="邮箱">
            <a-input v-model="form.email" placeholder="请输入邮箱"/>
          </a-form-item>
          <a-form-item field="password" label="密码">
            <a-input-password v-model="form.password" placeholder="请输入密码" allow-clear @focusout="identifyPwd"/>
          </a-form-item>
          <a-form-item field="repeat" label="确认密码">
            <a-input-password v-model="form.repeat" placeholder="请再次输入密码" allow-clear @focusout="identifyPwd"/>
          </a-form-item>
          <a-form-item>
            我有账号。前往
            <a-button type="text" shape="round" @click="globalProperties.common.jumpUrl('/login', router)">登录</a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" long @click="reset">重置</a-button>
          </a-form-item>
        </a-form>
      </div>
    </div>
  </div>
</template>

<script>
import { getCurrentInstance, reactive } from 'vue'
import { useRouter } from 'vue-router'
import http from '../api/http.js'
import apiConfig from '../api/apiConfig.js'

export default {
  name: 'LoginPage',
  setup () {
    const { appContext } = getCurrentInstance()
    const { globalProperties } = appContext.config
    const router = useRouter()
    const form = reactive({
      email: 'zhuboyang1996@foxmail.com', // 邮箱
      password: '123456', // 密码
      repeat: '123456', // 再次输入密码
      isAccept: false // 是否可以提交请求
    })
    return {
      globalProperties,
      router,
      form
    }
  },
  methods: {
    apiConfig,
    // 校验两次输入密码是否一致
    identifyPwd () {
      const { password, repeat } = this.form
      if (repeat !== password) {
        Notification.warning({
          title: '两次输入的密码不一致',
          content: ''
        })
        this.form.isAccept = false
      } else {
        this.form.isAccept = true
      }
    },
    // 重置密码
    reset () {
      const { email, password, isAccept } = this.form
      if (!isAccept) {
        return
      }
      if (email.trim() === '') {
        Notification.warning({
          title: '请输入邮箱',
          content: ''
        })
        return
      }
      if (password.trim() === '') {
        Notification.warning({
          title: '请输入密码',
          content: ''
        })
        return
      }
      http.reqUrl.user.reset({ email, password }).then(response => {
        if (response !== undefined) {
          setTimeout(() => {
            this.router.push('/login')
          }, 2000)
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.reset-pwd {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: space-between;
  background-color: #faf9fe;
  .page-left {
    display: flex;
    flex-direction: column;
    .pan-logo-box {
      margin: 56px;
      padding: 8px;
      display: flex;
      .pan-logo-bg {
        width: 48px;
        height: 48px;
        background-color: #6a4bfe;
        border-radius: 12px;
        .pan-logo-img {
          color: #ffffff;
          font-size: 28px;
          font-weight: bolder;
        }
      }
      .pan-logo-label {
        margin-left: 20px;
        display: flex;
        align-items: center;
        color: #374670;
        font-size: 18px;
        font-weight: bolder;
      }
    }
    .pan-bg {
      margin-left: 56px;
      img {
        width: 100%;
      }
    }
  }
  .page-right {
    position: fixed;
    top: 50%;
    left: 80%;
    width: 80vw;
    height: 80vw;
    background-color: #ffffff;
    border-radius: 50%;
    transform: translate(-50%, -50%);
    .page-box {
      position: fixed;
      top: 30%;
      left: 10vw;
      width: 30%;
      .form-title {
        font-size: 32px;
        font-weight: bolder;
      }
      .form-account-register {
        margin-top: 16px;
        margin-bottom: 48px;
        font-size: 20px;
        * {
          font-size: 20px;
        }
      }
      .account-fix {
        width: 100%;
        display: flex;
        justify-content: end;
      }
    }
  }
  .form-bg {
    $bgWidth: 80vw;
    position: fixed;
    top: 50%;
    left: 88%;
    width: $bgWidth;
    height: $bgWidth;
    background-color: #ffffff;
    border-radius: 50%;
    transform: translate(-50%, -50%);
  }
}
</style>
