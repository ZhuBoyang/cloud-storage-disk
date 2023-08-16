<template>
  <div class="user-self">
    <div class="detail-form">
      <a-form :model="user" label-align="left">
        <a-form-item label="用户名">
          <span>{{ identifyProperty(user.nickName) }}</span>
          <a-button class="edit-btn" type="text" shape="round" @click="displayModal('nickName')">编辑</a-button>
        </a-form-item>
        <a-form-item label="邮箱">
          <span>{{ identifyProperty(user.email) }}</span>
        </a-form-item>
        <a-form-item label="生日">
          <span>{{ user.birthday > 0 ? globalProperties.common.formatDate(user.birthday) : '暂未设置' }}</span>
          <a-button class="edit-btn" type="text" shape="round" @click="displayModal('birthday')">编辑</a-button>
        </a-form-item>
        <a-form-item label="年龄">
          <span>{{ user.birthday > 0 && user.age >= 0 ? `${user.age}岁` : '暂未设置' }}</span>
        </a-form-item>
        <a-form-item label="性别">
          <span>{{ user.gender === -1 ? '暂未设置' : user.gender > 0 ? '男' : '女' }}</span>
          <a-button class="edit-btn" type="text" shape="round" @click="displayModal('gender')">编辑</a-button>
        </a-form-item>
        <a-form-item label="手机号">
          <span>{{ identifyProperty(user.phone) }}</span>
          <a-button class="edit-btn" type="text" shape="round" @click="displayModal('phone')">编辑</a-button>
        </a-form-item>
      </a-form>
      <a-modal v-model:visible="modelVisible" :on-before-ok="validateData" @ok="updateInfo" @cancel="cancelUpdate">
        <template #title>修改个人资料</template>
        <a-form layout="vertical" :model="form">
          <a-form-item label="昵称" v-if="form.property === 'nickName'">
            <a-input v-model:model-value="form.nickName" placeholder="请输入昵称"/>
          </a-form-item>
          <a-form-item label="出生日期" v-if="form.property === 'birthday'">
            <a-date-picker v-model:model-value="form.birthday" :hide-trigger="true"/>
          </a-form-item>
          <a-form-item label="性别" v-if="form.property === 'gender'">
            <a-radio-group v-model:model-value="form.gender">
              <a-radio :value="1">男</a-radio>
              <a-radio :value="0">女</a-radio>
            </a-radio-group>
          </a-form-item>
          <a-form-item label="手机号" v-if="form.property === 'phone'">
            <a-input v-model:model-value="form.phone" placeholder="请输入手机号"/>
          </a-form-item>
        </a-form>
      </a-modal>
    </div>
  </div>
</template>

<script>
import apiConfig from '../api/apiConfig.js'
import { getCurrentInstance, reactive, toRefs } from 'vue'
import common from '../tools/common.js'
import type from '../tools/type.js'
import http from '../api/http.js'

export default {
  name: 'UserSelfView',
  setup () {
    const { globalProperties } = getCurrentInstance().appContext.config
    const dataList = reactive({
      user: {
        id: '',
        nickName: '',
        avatar: '',
        email: '',
        birthday: 0,
        age: 0,
        gender: -1,
        phone: '',
        totalSpaceSize: 0,
        usedSpaceSize: 0
      },
      modelVisible: false,
      form: {
        id: '',
        avatar: '',
        property: '',
        nickName: '',
        birthday: 0,
        age: 0,
        gender: -1,
        phone: ''
      }
    })
    return {
      globalProperties,
      ...toRefs(dataList)
    }
  },
  created () {
    this.queryAccountInfo()
  },
  methods: {
    apiConfig,
    // 查询账户信息
    queryAccountInfo () {
      http.reqUrl.user.info().then(response => {
        for (const key in this.user) {
          this.user[key] = response[key]
        }
      })
    },
    // 显示修改资料的窗口
    displayModal (property) {
      this.form.id = this.user.id
      this.form.property = property
      if (property === 'gender') {
        this.form.gender = this.user.gender
      }
      this.modelVisible = true
    },
    // 校验修改的资料
    validateData () {
      if (this.form.property === 'nickName') {
        if (this.form.nickName.trim() === '') {
          common.notify.warning('请输入昵称')
          return false
        }
      } else if (this.form.property === 'birthday') {
        const chooseTime = new Date(this.form.birthday).getTime()
        const today = new Date(common.formatDate(Date.now())).getTime()
        if (chooseTime >= today) {
          common.notify.warning('无法选择未来的日期，只能选择过去的日期')
          return false
        }
        this.form.birthday = chooseTime
      } else if (this.form.property === 'gender') {
        if (this.form.gender === -1) {
          common.notify.warning('请选择性别')
          return false
        }
      } else if (this.form.property === 'phone') {
        if (this.form.phone.trim() === '') {
          common.notify.warning('请输入手机号')
          return false
        }
        if (!type.isPhone(this.form.phone)) {
          common.notify.warning('手机号格式有误，请查证后再试')
          return false
        }
      }
      return true
    },
    // 提交修改的资料
    updateInfo () {
      http.reqUrl.user.update(this.form).then(response => {
        if (response) {
          this.cancelUpdate()
          this.queryAccountInfo()
        }
      })
    },
    // 取消修改资料
    cancelUpdate () {
      this.form = {
        id: '',
        avatar: '',
        property: '',
        nickName: '',
        birthday: 0,
        age: 0,
        gender: -1,
        phone: ''
      }
    },
    identifyProperty (value) {
      if (typeof value === 'string') {
        return value === '' ? '暂未设置' : value
      }
      return value === 0 ? '暂未设置' : value
    }
  }
}
</script>

<style scoped lang="scss">
.user-self {
  .detail-form {
    margin-top: 20px;
    margin-left: 50px;
    overflow-y: auto;
    .edit-btn {
      margin-left: 10px;
    }
  }
}
</style>
