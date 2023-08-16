<template>
  <div class="initial-view">
    <div class="initial-box">
      <a-form layout="vertical" :model="initialForm">
        <a-form-item>
          <div class="box-title row-col-center">欢迎使用煮雨云存储</div>
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-model="initialForm.email" placeholder="请输入邮箱"/>
        </a-form-item>
        <a-form-item label="密码">
          <a-input-password v-model="initialForm.password" placeholder="请输入密码"/>
        </a-form-item>
        <a-form-item label="重复密码">
          <a-input-password v-model="initialForm.repeat" placeholder="请再次输入密码"/>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" long @click="createAccount">创建账户</a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script>
import { reactive, toRefs } from 'vue'
import common from '../tools/common.js'
import md5 from 'js-md5'
import http from '../api/http.js'
import { useRouter } from 'vue-router'

export default {
  name: 'InitialView',
  setup () {
    const router = useRouter()
    const dataList = reactive({
      initialForm: { // 初始化账户表单
        email: '', // 邮箱
        password: '', // 密码
        repeat: '' // 重复输入密码
      }
    })
    return {
      router,
      ...toRefs(dataList)
    }
  },
  methods: {
    // 创建账户
    createAccount () {
      // 参数校验
      if (this.initialForm.email.trim() === '') {
        common.notify.warning('请输入邮箱')
        return
      }
      if (this.initialForm.password.trim() === '') {
        common.notify.warning('请输入密码')
        return
      }
      if (this.initialForm.password.trim() !== this.initialForm.repeat.trim()) {
        common.notify.warning('两次输入的密码不一致，请重新输入')
        return
      }

      // 对密码进行 md5 加密，防止向接口传输中出现明文
      const passwordEncryptCount = Math.floor(Math.random() * 10)
      let password = this.initialForm.password.trim()
      for (let i = 0; i < passwordEncryptCount; i++) {
        password = md5(password)
      }
      password = passwordEncryptCount + password

      // 对重复输入的密码进行 md5 加密
      const repeatEncryptCount = Math.floor(Math.random() * 10)
      let repeat = this.initialForm.repeat.trim()
      for (let i = 0; i < repeatEncryptCount; i++) {
        repeat = md5(repeat)
      }
      repeat = repeatEncryptCount + repeat

      // 发起请求
      http.reqUrl.user.initial({ email: this.initialForm.email, password, repeat }).then(response => {
        if (response) {
          this.router.push('/login')
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.initial-view {
  width: 100%;
  height: 100vh;
  background-color: #faf9fe;
  .initial-box {
    position: fixed;
    top: 50%;
    left: 50%;
    padding: 10px 15px;
    width: 400px;
    background-color: #ffffff;
    border-radius: 10px;
    box-shadow: 0 0 10px #e8e8e8;
    transform: translate(-50%, -50%);
    .box-title {
      margin: 20px 0;
      width: 100%;
      font-size: 20px;
    }
  }
}
</style>
