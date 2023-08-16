<template>
  <div class="user-center">
    <div class="center-header">
      <div class="header-box">
        <div class="header-left">
          <div class="header-avatar row-col-center">R</div>
          <div class="header-menu">
            <div class="menu-item">
              <a-button type="text" shape="round" @click="toIndex">存储空间</a-button>
            </div>
          </div>
        </div>
        <div class="header-right row-col-center">
          <div class="user-avatar row-col-center">Z</div>
          <div class="user-actions">
            <a-button type="text" shape="round" @click="logout">退出登录</a-button>
          </div>
        </div>
      </div>
    </div>
    <div class="center-box">
      <div class="information-box">
        <div class="user-information">
          <div class="username">{{ identifyProperty(user.nickName) }}</div>
          <div class="user-id-form">
            <div class="form-label">账户创建时间</div>
            <div class="form-content">{{ globalProperties.common.formatDateTime(user.createTime) }}</div>
          </div>
        </div>
        <div class="user-menu">
          <div class="space-statistics">
            <div class="space-statistics-size">
              <div class="statistics-item">
                <div class="form-label">总容量</div>
                <div class="form-content">{{ globalProperties.common.formatSizeInPerson(user.totalSpaceSize) }}</div>
              </div>
              <div class="statistics-item">
                <div class="form-label">已使用</div>
                <div class="form-content">{{ globalProperties.common.formatSizeInPerson(user.usedSpaceSize) }}</div>
              </div>
            </div>
<!--            <div class="space-statistics-chart">图表</div>-->
          </div>
          <div class="menu-box">
            <div class="menu-item"
                 v-for="(o, i) in menus"
                 :key="i"
                 :class="[{'is-selected': o.key === currentRoute}]"
                 @click="changeMenu(o)"
            >
              <div class="item-icon">
                <img :src="apiConfig().iconBaseUrl + `center/${o.key}.png`" :alt="o.name"/>
              </div>
              <div class="item-word">{{ o.name }}</div>
              <div class="item-selected"></div>
            </div>
          </div>
        </div>
      </div>
      <div class="center-container">
        <div class="detail-background-avatar-box">
          <div class="background-avatar-image">
            <img :src="apiConfig().iconBaseUrl + 'center/user_default_background.jpg'" alt=""/>
          </div>
          <!--      <div class="background-avatar-action row-col-center">点击上传背景图</div>-->
          <!--      <input type="file" ref="backgroundUploader"/>-->
        </div>
        <div class="detail-information">
          <div class="user-avatar-box">
            <div class="avatar-image-box">
              <div class="avatar-image">
                <img :src="getAvatar()" :alt="'username'"/>
              </div>
              <div class="avatar-upload" @click="openChooseAvatar">点击上传头像</div>
              <input type="file" ref="avatarUploader" @change="uploadAvatar"/>
            </div>
          </div>
          <div class="user-info-box">
            <div class="user-name">{{ identifyProperty(user.nickName) }}</div>
            <div class="user-email">{{ identifyProperty(user.email) }}</div>
          </div>
        </div>
        <router-view class="center-container-view"/>
      </div>
    </div>
  </div>
</template>

<script>
import { getCurrentInstance, reactive, toRefs } from 'vue'
import { useRouter } from 'vue-router'
import apiConfig from '../api/apiConfig.js'
import http from '../api/http.js'

export default {
  name: 'UserCenterView',
  setup () {
    const { globalProperties } = getCurrentInstance().appContext.config
    const router = useRouter()
    const dataList = reactive({
      currentRoute: router.currentRoute.value.path.substring(router.currentRoute.value.path.lastIndexOf('/') + 1),
      menus: [
        { key: 'user', name: '账户信息' },
        { key: 'password', name: '修改密码' }
      ],
      user: {
        avatar: '',
        nickName: '',
        totalSpaceSize: 0,
        usedSpaceSize: 0,
        createTime: 0
      }
    })
    return {
      globalProperties,
      router,
      ...toRefs(dataList)
    }
  },
  created () {
    this.queryAccountInfo()
  },
  methods: {
    apiConfig,
    toIndex () {
      this.router.push('/dashboard')
    },
    // 退出登录
    logout () {
      http.reqUrl.user.logout().then(response => {
        if (response) {
          localStorage.removeItem('t')
          this.router.push('/login')
        }
      })
    },
    // 查询账户信息
    queryAccountInfo () {
      http.reqUrl.user.info().then(response => {
        for (const key in this.user) {
          this.user[key] = response[key]
        }
      })
    },
    // 切换导航
    changeMenu (o) {
      const { key } = o
      const { path } = this.router.currentRoute.value
      if (key === path) {
        return
      }
      this.currentRoute = key
      this.router.push(key)
    },
    // 选择新头像
    openChooseAvatar () {
      this.$refs.avatarUploader.value = ''
      this.$refs.avatarUploader.click()
    },
    // 选择文件并上传
    uploadAvatar (event) {
      const avatarFile = event.target.files[0]

      const avatarData = new FormData()
      avatarData.append('file', avatarFile)
      avatarData.append('name', avatarFile.name)

      const header = { 'Content-Type': 'multipart/form-data' }
      http.req(http.url.file.simpleUpload, http.methods.form, avatarData, header).then(response => {
        if (response) {
          this.form.avatar = response
          this.updateInfo()
        }
      })
    },
    // 获取用户头像
    getAvatar () {
      if (this.user.avatar === '') {
        return apiConfig().iconBaseUrl + 'center/user_default_avatar.jpg'
      }
      return apiConfig().apiBaseUrl + this.user.avatar
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
.user-center {
  width: 100%;
  height: 100vh;
  background-color: #F3F7FD;
  .center-header {
    width: 100%;
    height: 60px;
    background-color: #ffffff;
    .header-box {
      margin: 0 auto;
      width: 80%;
      height: 100%;
      display: flex;
      justify-content: space-between;
      .header-left {
        height: 100%;
        display: flex;
        align-items: center;
        .header-avatar {
          width: 48px;
          height: 48px;
          color: #ffffff;
          font-size: 28px;
          font-weight: bolder;
          background-color: #6a4bfe;
          border-radius: 12px;
        }
        .header-menu {
          margin-left: 100px;
          height: 100%;
          display: flex;
          align-items: center;
          .menu-item {
            margin: 0 20px;
            &:first-child {
              margin-left: 0;
            }
            &:last-child {
              margin-inside: 0;
            }
          }
        }
      }
      .header-right {
        .user-avatar {
          width: 48px;
          height: 48px;
          background-color: #efefef;
          border-radius: 50%;
        }
        .user-actions {
          margin: 0 20px;
        }
      }
    }
  }
  .center-box {
    margin: 50px auto 0;
    width: 80%;
    height: calc(100vh - 60px - 50px - 50px);
    display: flex;
    .information-box {
      width: 240px;
      color: #ffffff;
      .user-information {
        padding: 20px;
        width: calc(100% - 40px);
        height: 90px;
        background-color: #2E57F8;
        border-radius: 10px;
        .username {
          font-size: 24px;
        }
        .user-id-form {
          margin-top: 20px;
        }
      }
      .user-menu {
        position: relative;
        margin-top: 20px;
        padding: 20px;
        width: calc(100% - 40px);
        height: calc(100% - 130px - 20px - 40px);
        background-color: #2E57F8;
        border-radius: 10px;
        .space-statistics {
          padding-bottom: 20px;
          display: flex;
          border-bottom: 1px solid #7692FD;
          .space-statistics-size {
            width: 50%;
            .statistics-item {
              margin-top: 20px;
              &:first-child {
                margin-top: 0;
              }
            }
          }
          .space-statistics-chart {
            width: 50%;
          }
        }
        .menu-box {
          margin-top: 20px;
          .menu-item {
            width: 100%;
            height: 50px;
            display: flex;
            align-items: center;
            color: #D5DDFA;
            cursor: pointer;
            &.is-selected {
              .item-word {
                color: #ffffff;
              }
              .item-selected {
                background-color: #ffffff;
              }
            }
            .item-icon {
              width: 20px;
              height: 20px;
              img {
                width: 100%;
                height: 100%;
              }
            }
            .item-word {
              margin-left: 10px;
              width: calc(100% - 25px);
            }
            .item-selected {
              width: 5px;
              height: 100%;
              border-top-left-radius: 5px;
              border-bottom-left-radius: 5px;
            }
          }
        }
      }
    }
    .center-container {
      margin-left: 30px;
      padding: 20px;
      width: calc(100% - 240px - 30px - 40px);
      height: calc(100% - 40px);
      background-color: #ffffff;
      border-radius: 10px;
      .detail-background-avatar-box {
        position: relative;
        width: 100%;
        height: 130px;
        background-color: #2C51E7;
        border-radius: 10px;
        cursor: pointer;
        overflow: hidden;
        .background-avatar-image {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background-color: #efefef;
          img {
            width: 100%;
            height: 100%;
          }
        }
        .background-avatar-action {
          position: absolute;
          bottom: 0;
          left: 0;
          width: 100%;
          height: 20px;
          color: #ffffff;
          font-size: 12px;
          background-color: rgba(0, 0, 0, .1);
        }
      }
      .detail-information {
        position: relative;
        width: 100%;
        height: 70px;
        .user-avatar-box {
          position: absolute;
          top: -40px;
          left: 40px;
          width: 100px;
          height: 100px;
          border-radius: 50%;
          overflow: hidden;
          .avatar-image-box {
            position: relative;
            width: 100%;
            height: 100%;
            .avatar-image {
              position: absolute;
              top: 0;
              left: 0;
              width: 100%;
              height: 100%;
              img {
                width: 100%;
                height: 100%;
              }
            }
            .avatar-upload {
              position: absolute;
              bottom: 0;
              left: 0;
              width: 100%;
              height: 30px;
              color: #ffffff;
              font-size: 12px;
              text-align: center;
              background-color: rgba(0, 0, 0, .2);
              cursor: pointer;
            }
          }
        }
        .user-info-box {
          position: absolute;
          top: 10px;
          left: 170px;
          .user-name {
            margin-bottom: 5px;
            color: #2E57F8;
            font-size: 20px;
          }
          .user-email {
            color: #68768B;
            font-size: 14px;
          }
        }
      }
      .center-container-view {
        width: 100%;
        height: calc(100% - 130px - 70px);
        overflow-y: auto;
      }
    }
  }
}
.form-label {
  color: #D5DDFA;
  font-size: 14px;
}
.form-content {
  margin-top: 10px;
  font-size: 16px;
}
</style>
