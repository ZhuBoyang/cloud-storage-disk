<template>
  <div class="action-component">
    <login-user-action @on-load="loadUserInfo"/>
    <div class="file-actions" @mouseenter="showAddBox = true" @mouseleave="showAddBox = false">
      <div class="add-file-btn">
        添加文件
        <img :src="apiConfig().iconBaseUrl + 'icons/plus.png'" alt="添加文件"/>
      </div>
      <div class="file-actions--list" :class="[{'is-show': showAddBox}]">
        <div class="file-actions--list-item" @click="this.upload.visible = true">上传文件</div>
        <div class="file-actions--list-item" @click="this.mkdir.visible = true">创建文件夹</div>
      </div>
    </div>
    <div class="disk-space-monitor">
      <circular-progress class="storage-progress"
                         :percent="space.usageRate"
                         :percent-visible="true"
      ></circular-progress>
      <div class="disk-space-info">
        <div class="disk-space-info-item">{{ globalProperties.common.formatSizeInPerson(space.usedSpaceSize) }}</div>
        <div class="disk-space-info-item">/</div>
        <div class="disk-space-info-item">{{ globalProperties.common.formatSizeInPerson(space.totalSpaceSize) }}</div>
      </div>
    </div>
    <upload-file-list :visible="upload.visible"
                      :check-block-exist-url="upload.checkExist"
                      :upload-block-url="upload.blockUploadUrl"
                      :merge-url="upload.mergeUrl"
                      @on-upload-change="hideUploadModal"
    ></upload-file-list>
    <!-- 新建文件夹 -->
    <a-modal :visible="mkdir.visible"
             @ok="submitMkdir"
             @cancel="this.mkdir.visible = false"
             @close="closeMkdir"
    >
      <template #title>新建文件夹</template>
      <a-form :model="mkdir.form"
              layout="vertical"
      >
        <a-form-item field="fileName" label="文件夹名称">
          <a-input v-model="mkdir.form.name" placeholder="请输入文件夹名称"/>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { defineAsyncComponent, getCurrentInstance, reactive, toRefs } from 'vue'
import http from '../api/http.js'
import { useRouter } from 'vue-router'
import apiConfig from '../api/apiConfig.js'
import common from '../tools/common.js'
import emitter from '../tools/emitter.js'

const LoginUserAction = defineAsyncComponent(() => import('./LoginUserAction.vue'))
const UploadFileList = defineAsyncComponent(() => import('./UploadComponent.vue'))
const CircularProgress = defineAsyncComponent(() => import('../components/custom/CircularProgress.vue'))

export default {
  name: 'ActionComponent',
  components: {
    LoginUserAction,
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
    const { appContext } = getCurrentInstance()
    const { globalProperties } = appContext.config
    const router = useRouter()
    const dataList = reactive({
      showAddBox: false, // 是否显示添加文件的下拉框
      upload: {
        visible: false, // 是否显示上传文件的弹窗
        checkExist: http.url.file.checkExist, // 检测文件块是否已上传
        blockUploadUrl: http.url.file.uploadBlocks, // 文件块上传地址
        mergeUrl: http.url.file.mergeFile // 文件合并地址
      },
      space: {
        totalSpaceSize: 0, // 系统空间总量
        usedSpaceSize: 0, // 系统空间已使用量
        usageRate: 0 // 账户已用容量
      },
      mkdir: {
        visible: false, // 是否显示新建文件夹的弹窗
        form: {
          name: '' // 新建文件夹的文件名
        }
      }
    })
    return {
      globalProperties,
      router,
      ...toRefs(dataList)
    }
  },
  methods: {
    apiConfig,
    // 加载当前登录用户的信息
    loadUserInfo (info) {
      const { totalSpaceSize, usedSpaceSize } = info
      this.space.totalSpaceSize = totalSpaceSize
      this.space.usedSpaceSize = usedSpaceSize
      this.space.usageRate = (this.space.usedSpaceSize / this.space.totalSpaceSize * 100).toFixed(2) + '%'
    },
    // 提交新建文件夹的请求
    submitMkdir () {
      const { name } = this.mkdir.form
      if (name.trim() === '') {
        common.notify.warning('请输入文件夹名称')
        return
      }
      const id = this.router.currentRoute.value.query.router
      http.reqUrl.file.mkdir({ id, name }).then(() => {
        // 这里会向组件 FileView 发送一条消息，刷新文件列表，以显示新创建的文件夹
        emitter.emit('on-mkdir')
        this.closeMkdir()
      })
    },
    // 关闭新建文件夹的窗口后清空变量的值
    closeMkdir () {
      this.mkdir.visible = false
      this.mkdir.form.name = ''
    },
    // 隐藏上传文件的窗口
    hideUploadModal () {
      this.upload.visible = false
    }
  }
}
</script>

<style scoped lang="scss">
.action-component {
  margin-top: 40px;
  height: calc(100% - 40px);
  .file-actions {
    position: relative;
    margin: 0 auto;
    width: 80%;
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
    width: 80%;
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
      justify-content: center;
      .disk-space-info-item {
        margin: 0 5px;
        display: flex;
        font-weight: bold;
        font-size: 20px;
        &:first-child {
          margin-left: 0;
        }
        &:last-child {
          margin-right: 0;
        }
      }
    }
  }
}
</style>
