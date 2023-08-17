<template>
  <div class="user-password">
    <div class="password-box">
      <a-form layout="vertical" :model="form" label-align="left">
        <a-form-item label="新密码">
          <a-input-password v-model:model-value="form.password"/>
        </a-form-item>
        <a-form-item label="再次输入">
          <a-input-password v-model:model-value="form.repeat"/>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" long @click="updatePassword">提交</a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script>
import { reactive, toRefs } from 'vue'
import common from '../tools/common.js'
import http from '../api/http.js'
import { useRouter } from 'vue-router'

export default {
  name: 'UserPasswordView',
  setup () {
    const router = useRouter()
    const dataList = reactive({
      form: {
        password: '',
        repeat: ''
      }
    })
    return {
      router,
      ...toRefs(dataList)
    }
  },
  methods: {
    updatePassword () {
      // 参数校验
      if (this.form.password.trim() === '') {
        common.notify.warning('请输入密码')
        return
      }
      if (this.form.password.trim() !== this.form.repeat.trim()) {
        common.notify.warning('两次密码输入不一致')
      }

      // 密码加密
      const password = common.encrypt(this.form.password, Math.floor(Math.random() * 10))
      const repeat = common.encrypt(this.form.repeat, Math.floor(Math.random() * 10))

      // 发送请求
      http.reqUrl.user.passwordUpdate({ password, repeat }).then(response => {
        if (response) {
          localStorage.removeItem('info')
          localStorage.removeItem('t')
          localStorage.removeItem('id')
          setTimeout(() => {
            this.router.push('/login')
          }, 1000)
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.user-password {
  .password-box {
    margin-top: 20px;
    margin-left: 50px;
    width: 500px;
  }
}
</style>
