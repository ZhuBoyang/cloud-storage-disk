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
    <div class="page-right row-col-center">
      <div class="page-box">
        <a-form :model="form" layout="vertical">
          <a-form-item field="email" label="邮箱">
            <a-input v-model="form.email" placeholder="请输入邮箱"/>
          </a-form-item>
          <a-form-item label="邮箱验证码">
            <a-input-search v-model="form.code"
                            placeholder="请输入邮箱验证码"
                            button-text="点击发送验证码"
                            search-button
                            @search="sendCoder"
            />
          </a-form-item>
          <a-form-item field="password" label="密码">
            <a-input-password v-model="form.password" placeholder="请输入密码" allow-clear @focusout="identifyPwd"/>
          </a-form-item>
          <a-form-item field="repeat" label="确认密码">
            <a-input-password v-model="form.repeat" placeholder="请再次输入密码" allow-clear @focusout="identifyPwd"/>
          </a-form-item>
          <a-form-item>已有账号？
            <a-button type="text" shape="round" @click="globalProperties.common.jumpUrl('/login', router)">登录
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" long @click="register">注册</a-button>
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
import common from '../tools/common.js'
import type from '../tools/type.js'

export default {
  name: 'LoginPage',
  setup () {
    const { globalProperties } = getCurrentInstance().appContext.config
    const router = useRouter()
    const dataList = reactive({
      form: {
        email: '', // 邮箱
        code: '', // 邮箱验证码
        password: '', // 密码
        repeat: '', // 再次输入密码
        canReg: false // 是否可以进行注册
      }
    })
    return {
      globalProperties,
      router,
      ...toRefs(dataList)
    }
  },
  methods: {
    // 校验两次输入密码是否一致
    identifyPwd () {
      const { password, repeat } = this.form
      this.form.canReg = repeat === password
      if (repeat !== password) {
        common.notify.warning('两次输入的密码不一致')
      }
    },
    // 发送邮箱验证码
    sendCoder () {
      if (this.form.email.trim() === '') {
        common.notify.warning('请输入邮箱')
        return
      }
      if (!type.isEmail(this.form.email)) {
        common.notify.warning('邮箱格式输入错误')
        return
      }
      http.reqUrl.email.sendRegCoder(this.form.email).then(() => {
      })
    },
    // 用户注册
    register () {
      const { email, code, password, canReg } = this.form
      if (email.trim() === '') {
        common.notify.warning('请输入邮箱')
        return
      }
      if (password.trim() === '') {
        common.notify.warning('请输入密码')
        return
      }
      if (!canReg) {
        common.notify.warning('请输入信息')
        return
      }
      http.reqUrl.user.register({ email, code, password }).then(response => {
        if (response) {
          this.router.push('/login')
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.register-page {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: space-between;
  background-color: #faf9fe;
  .page-left {
    width: 50%;
    height: 100%;
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
    position: relative;
    width: 50%;
    height: 100%;
    background-color: #ffffff;
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
