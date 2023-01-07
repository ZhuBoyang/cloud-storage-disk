<template>
  <div class="dashboard-right">
    <div class="user-box"
         @mouseenter="showUserInfoDropdown"
         @mouseleave="hideUserInfoDropdown"
    >
      <div class="user-info-btn">
        <div class="user-avatar">
          <img src="../assets/vue.svg" alt="username"/>
        </div>
        <div class="user-intro">
          <div class="user-info">
            <div class="user-name">zhuboyang</div>
            <div class="user-email">email@qq.com</div>
          </div>
          <div class="user-more">
            <img src="../assets/icons/Arrow%20-%20Down%202.svg" alt="more"/>
          </div>
        </div>
      </div>
      <div class="user-info-actions"
           :class="[{'is-show': data.userInfoBox}]"
      >
        <div class="actions-item">设置</div>
        <div class="actions-item">退出登录</div>
      </div>
    </div>
    <div class="file-actions"
         @mouseenter="showMoreFileDropdown"
         @mouseleave="hideMoreFileDropdown"
    >
      <div class="add-file-btn">
        添加文件
        <img src="../assets/icons/Plus.svg" alt="添加文件"/>
      </div>
      <div class="file-actions--list"
           :class="[{'is-show': data.showAddBox}]"
      >
        <div class="file-actions--list-item" @click="showUploadModal">上传文件</div>
        <div class="file-actions--list-item" @click="data.mkdir.mkdirVisible = true">创建文件夹</div>
      </div>
    </div>
    <div class="disk-space-monitor">
      <circular-progress class="storage-progress" :percent="3"></circular-progress>
      <div class="disk-space-info">
        <div class="disk-space-used">
          500
          <div class="disk-space-unit">GB</div>
        </div>
        <div class="disk-space-gap">/</div>
        <div class="disk-space-total">
          1
          <div class="disk-space-unit">TB</div>
        </div>
      </div>
    </div>
    <upload-file-list :visible="data.uploadVisible"
                      @on-change="hideUploadModal"
    ></upload-file-list>
  </div>
  <!-- 新建文件夹 -->
  <a-modal :visible="data.mkdir.mkdirVisible"
           @ok="submitMkdir"
           @cancel="data.mkdir.mkdirVisible = false"
           @close="closeMkdir"
  >
    <template #title>新建文件夹</template>
    <a-form :model="data.mkdir.form"
            layout="vertical"
    >
      <a-form-item field="fileName" label="文件夹名称">
        <a-input v-model="data.mkdir.form.fileName" placeholder="请输入文件夹名称" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import UploadFileList from './UploadFileList.vue'
import CircularProgress from '../components/custom/CircularProgress.vue'
import { reactive } from 'vue'
import http from '../api/http.js'
import emitter from '../utils/emitter.js'

export default {
  name: 'DashboardRight',
  components: {
    UploadFileList,
    CircularProgress
  },
  props: {
    breads: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  setup () {
    const data = reactive({
      userInfoBox: false, // 是否显示用户信息的下拉框
      showAddBox: false, // 是否显示添加文件的下拉框
      uploadVisible: false, // 是否显示上传文件的弹窗
      mkdir: {
        mkdirVisible: false, // 是否显示新建文件夹的弹窗
        form: {
          fileName: '' // 新建文件夹的文件名
        }
      }
    })
    return {
      data
    }
  },
  methods: {
    // 显示用户账户操作的下拉框
    showUserInfoDropdown () {
      this.data.userInfoBox = true
    },
    // 隐藏用户账户操作的下拉框
    hideUserInfoDropdown () {
      this.data.userInfoBox = false
    },
    // 显示添加文件的下拉框
    showMoreFileDropdown () {
      this.data.showAddBox = true
    },
    // 隐藏添加文件的下拉框
    hideMoreFileDropdown () {
      this.data.showAddBox = false
    },
    // 显示上传文件的窗口
    showUploadModal () {
      this.data.uploadVisible = true
    },
    // 隐藏上传文件的窗口
    hideUploadModal (record) {
      const { visible } = record
      this.data.uploadVisible = visible
    },
    // 提交新建文件夹的请求
    submitMkdir () {
      const fileName = this.data.mkdir.form.fileName
      if (fileName.trim() === '') {
        this.$notification.warning('请输入文件夹名称')
        return
      }
      const pid = this.breads[this.breads.length - 1].id
      const param = {
        pid,
        fileName
      }
      http.req(http.url.file.mkdir, http.methods.post, param).then(response => {
        emitter.emit('mkdir-change', response)
        this.data.mkdir.mkdirVisible = false
      })
    },
    // 关闭新建文件夹的窗口后清空变量的值
    closeMkdir () {
      this.data.mkdir.form.fileName = ''
    }
  }
}
</script>

<style scoped lang="scss">
.dashboard-right {
  width: 352px;
  height: 100vh;
  .user-box {
    margin: 40px auto 0;
    position: relative;
    width: 284px;
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
  .file-actions {
    position: relative;
    margin: 25px auto 0;
    width: 284px;
    height: 60px;
    .add-file-btn {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #ffffff;
      font-size: 16px;
      background-color: #6a4bfe;
      border-radius: 14px;
      cursor: pointer;
      img {
        margin-left: 10px;
        width: 30px;
        height: 30px;
        filter: brightness(100);
      }
    }
    .file-actions--list {
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
      z-index: 1;
      &.is-show {
        height: 120px;
        transition: height .3s;
      }
      .file-actions--list-item {
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
  .disk-space-monitor {
    margin: 40px auto 0;
    padding-top: 30px;
    width: 284px;
    background-color: #f7f6ff;
    border-radius: 15px;
    .storage-progress {
      margin: 0 auto;
      width: 60%;
    }
    .disk-space-info {
      margin: 20px auto 0;
      padding-bottom: 40px;
      width: 60%;
      display: flex;
      align-items: center;
      .disk-space-used {
        display: flex;
        font-weight: bold;
        font-size: 20px;
        .disk-space-unit {
          margin-left: 3px;
          font-weight: normal;
          font-size: 20px;
        }
      }
      .disk-space-gap {
        margin: 0 5px;
        font-size: 20px;
      }
      .disk-space-total {
        display: flex;
        font-weight: bold;
        font-size: 20px;
        .disk-space-unit {
          margin-left: 3px;
          font-weight: normal;
          font-size: 20px;
        }
      }
    }
  }
}
</style>
