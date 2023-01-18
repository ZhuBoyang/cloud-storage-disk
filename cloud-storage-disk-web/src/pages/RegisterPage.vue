<template>
  <div class="register-page">
    <div class="page-left">
      <div class="pan-logo-box">
        <div class="pan-logo-bg row-col-center">
          <div class="pan-logo-img">R</div>
        </div>
        <div class="pan-logo-label">云存储</div>
      </div>
      <div class="pan-welcome">
        <div class="welcome-title">注册一个新的账号</div>
        <div class="welcome-description">云存储让您安全访问您的所有文件。通过任何设备与朋友、家人和同事协作。</div>
      </div>
    </div>
    <div class="page-right">
      <div class="page-box">
        <a-form :model="form" layout="vertical">
          <a-form-item field="userName" label="用户名">
            <a-input v-model="form.userName" placeholder="请输入用户名"/>
          </a-form-item>
          <a-form-item field="email" label="邮箱">
            <a-input v-model="form.email" placeholder="请输入邮箱"/>
          </a-form-item>
          <a-form-item field="password" label="密码">
            <a-input-password v-model="form.password" placeholder="请输入密码" allow-clear/>
          </a-form-item>
          <a-form-item field="repeat" label="确认密码">
            <a-input-password v-model="form.repeat" placeholder="请再次输入密码" allow-clear/>
          </a-form-item>
          <a-form-item>已有账号？
            <a-button type="text" shape="round" @click="globalProperties.$jumpUrl('/login', router)">登录</a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" long>注册</a-button>
          </a-form-item>
        </a-form>
      </div>
    </div>
  </div>
</template>

<script>
import { getCurrentInstance, reactive } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'LoginPage',
  setup () {
    const { appContext } = getCurrentInstance()
    const { config } = appContext
    const { globalProperties } = config
    const router = useRouter()
    const form = reactive({
      userName: '', // 用户名
      email: '', // 邮箱
      password: '', // 密码
      repeat: '' // 再次输入密码
    })
    return {
      globalProperties,
      router,
      form
    }
  }
}
</script>

<style scoped lang="scss">
.register-page {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: space-between;
  background-color: #faf9fe;
  .page-left {
    width: 50%;
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
    .pan-welcome {
      margin-left: 56px;
      margin-top: 197px;
      .welcome-title {
        font-size: 52px;
        font-weight: bolder;
      }
      .welcome-description {
        margin-top: 40px;
        width: 70%;
        font-size: 20px;
        color: #92929d;
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
      top: 32vw;
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
