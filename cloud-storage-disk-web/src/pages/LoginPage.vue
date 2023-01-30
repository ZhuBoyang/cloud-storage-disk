<template>
  <div class="login-page">
    <div class="page-left">
      <div class="pan-logo-box">
        <div class="pan-logo-bg row-col-center">
          <div class="pan-logo-img">R</div>
        </div>
        <div class="pan-logo-label">云存储</div>
      </div>
      <div class="pan-bg">
        <img src="../assets/login-bg.png" alt="登录背景"/>
      </div>
    </div>
    <div class="page-right">
      <div class="page-box">
        <div class="form-title">登录你的账户</div>
        <div class="form-account-register">还没有账号？
          <a-button type="text" shape="round" @click="globalProperties.$common.jumpUrl('/register', router)">注册</a-button>
        </div>
        <a-form :model="form" layout="vertical">
          <a-form-item field="email" label="邮箱">
            <a-input v-model="form.email" placeholder="请输入邮箱"/>
          </a-form-item>
          <a-form-item field="password" label="密码">
            <a-input-password v-model="form.password" placeholder="请输入密码" allow-clear/>
          </a-form-item>
          <a-form-item>
            <div class="account-fix">
              <a-button type="text" shape="round" @click="globalProperties.$common.jumpUrl('/reset', router)">忘记密码？</a-button>
            </div>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" long @click="login">登录</a-button>
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

export default {
  name: 'LoginPage',
  setup () {
    const { appContext } = getCurrentInstance()
    const { config } = appContext
    const { globalProperties } = config
    const router = useRouter()
    const form = reactive({
      email: 'zhuboyang1996@foxmail.com', // 邮箱
      password: '123456' // 密码
    })
    return {
      globalProperties,
      router,
      form
    }
  },
  methods: {
    // 登录
    login () {
      const { email, password } = this.form
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
      http.req(http.url.user.login, http.methods.post, {
        email,
        password
      }).then(response => {
        if (response !== undefined) {
          localStorage.setItem('user', JSON.stringify(response))
          this.router.push('/index')
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.login-page {
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
