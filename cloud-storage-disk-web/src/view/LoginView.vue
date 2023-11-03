<template>
  <div class="login-view">
    <div class="page-left">
      <div class="pan-logo-box">
        <div class="pan-logo-bg row-col-center">
          <div class="pan-logo-img">R</div>
        </div>
        <div class="pan-logo-label">云存储</div>
      </div>
      <div class="pan-bg">
        <img :src="apiConfig().iconBaseUrl + 'background/login_bg.png'" alt="登录背景"/>
      </div>
    </div>
    <div class="page-right row-col-center">
      <div class="page-box">
        <div class="form-title">登录你的账户</div>
        <a-form :model="form" layout="vertical">
          <a-form-item field="email" label="邮箱">
            <a-input v-model="form.email" placeholder="请输入邮箱"/>
          </a-form-item>
          <a-form-item field="password" label="密码">
            <a-input-password v-model="form.password" placeholder="请输入密码" allow-clear/>
          </a-form-item>
          <a-form-item>
            <div class="account-fix">
              <a-button type="text" shape="round" @click="globalProperties.common.jumpUrl('/reset', router)">忘记密码？
              </a-button>
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
import { getCurrentInstance, reactive, toRefs } from 'vue'
import { useRouter } from 'vue-router'
import http from '../api/http.js'
import apiConfig from '../api/apiConfig.js'
import common from '../tools/common.js'
import md5 from 'js-md5'

export default {
  name: 'LoginView',
  setup () {
    const { globalProperties } = getCurrentInstance().appContext.config
    const router = useRouter()
    const dataList = reactive({
      form: {
        email: '', // 邮箱
        password: '' // 密码
      }
    })
    return {
      globalProperties,
      router,
      ...toRefs(dataList)
    }
  },
  created () {
    this.loadSupportsExt()
  },
  methods: {
    apiConfig,
    // 加载系统支持的各类文件偶追
    loadSupportsExt () {
      http.reqUrl.file.typeSupports().then(response => {
        localStorage.setItem('type_supports', JSON.stringify(response))
        this.hadLogin()
      })
    },
    // 检测是否已登录
    hadLogin () {
      const token = localStorage.getItem('t')
      if (token !== undefined && token !== null) {
        this.router.push('/dashboard')
      }
    },
    // 登录
    login () {
      // 参数校验
      if (this.form.email.trim() === '') {
        common.notify.warning('请输入邮箱')
        return
      }
      if (this.form.password.trim() === '') {
        common.notify.warning('请输入密码')
        return
      }

      // 对密码进行 md5 加密，防止向接口传输中出现明文
      const passwordEncryptCount = Math.floor(Math.random() * 10)
      let password = this.form.password.trim()
      for (let i = 0; i < passwordEncryptCount; i++) {
        password = md5(password)
      }
      password = passwordEncryptCount + password

      // 请求
      http.reqUrl.user.login({ email: this.form.email, password }).then(response => {
        localStorage.setItem('t', response.token)
        localStorage.setItem('id', response.id)
        this.router.push('/dashboard')
      })
    }
  }
}
</script>

<style scoped lang="scss">
.login-view {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: space-between;
  background-color: #faf9fe;
  .page-left {
    width: 50%;
    height: 100%;
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
      height: calc(100% - 56px * 2 - 64px);
      img {
        max-width: 100%;
        max-height: 100%;
      }
    }
  }
  .page-right {
    position: relative;
    width: 50%;
    height: 100%;
    background-color: #ffffff;
    &:before {
      position: absolute;
      top: 0;
      left: 0;
      width: 100px;
      height: 100px;
      background-color: #000000;
    }
    .page-box {
      width: 50%;
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
