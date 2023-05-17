<template>
  <div class="dashboard-right">
    <login-user-action/>
    <div class="file-actions"
         @mouseenter="showMoreFileDropdown"
         @mouseleave="hideMoreFileDropdown"
    >
      <div class="add-file-btn">
        添加文件
        <img :src="apiConfig().iconBaseUrl + 'icons/Plus.png'" alt="添加文件"/>
      </div>
      <div class="file-actions--list"
           :class="[{'is-show': showAddBox}]"
      >
        <div class="file-actions--list-item" @click="showUploadModal">上传文件</div>
        <div class="file-actions--list-item" @click="mkdir.mkdirVisible = true">创建文件夹</div>
      </div>
    </div>
    <div class="disk-space-monitor">
      <circular-progress class="storage-progress"
                         :percent="parseInt(((systemSpace.used / systemSpace.total) * 100).toString())"
      ></circular-progress>
      <div class="disk-space-info">
        <div class="disk-space-info-item">{{ globalProperties.common.formatSizeInPerson(systemSpace.used) }}</div>
        <div class="disk-space-info-item">/</div>
        <div class="disk-space-info-item">{{ globalProperties.common.formatSizeInPerson(systemSpace.total) }}</div>
      </div>
    </div>
    <upload-file-list :visible="upload.visible"
                      :check-block-exist-url="upload.checkExist"
                      :upload-block-url="upload.blockUploadUrl"
                      :merge-url="upload.mergeUrl"
                      @on-change="hideUploadModal"
    ></upload-file-list>
  </div>
  <!-- 新建文件夹 -->
  <a-modal :visible="mkdir.mkdirVisible"
           @ok="submitMkdir"
           @cancel="mkdir.mkdirVisible = false"
           @close="closeMkdir"
  >
    <template #title>新建文件夹</template>
    <a-form :model="mkdir.form"
            layout="vertical"
    >
      <a-form-item field="fileName" label="文件夹名称">
        <a-input v-model="mkdir.form.fileName" placeholder="请输入文件夹名称"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import LoginUserAction from './LoginUserAction.vue'
import UploadFileList from './UploadComponent.vue'
import CircularProgress from '../components/custom/CircularProgress.vue'
import { getCurrentInstance, reactive, toRefs } from 'vue'
import http from '../api/http.js'
import emitter from '../utils/emitter.js'
import { useRouter } from 'vue-router'
import apiConfig from '../api/apiConfig.js'

export default {
  name: 'DashboardRight',
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
      mkdir: {
        mkdirVisible: false, // 是否显示新建文件夹的弹窗
        form: {
          fileName: '' // 新建文件夹的文件名
        }
      },
      systemSpace: {
        total: 0, // 系统空间总量
        used: 0 // 系统空间已使用量
      }
    })
    emitter.on('disk-space-change', record => {
      const { size } = record
      dataList.systemSpace.used += size
    })
    return {
      globalProperties,
      router,
      ...toRefs(dataList)
    }
  },
  created () {
    this.getDiskInfo()
  },
  methods: {
    apiConfig,
    // 提交新建文件夹的请求
    submitMkdir () {
      const fileName = this.mkdir.form.fileName
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
        this.mkdir.mkdirVisible = false
      })
    },
    // 查询系统磁盘空间使用量
    getDiskInfo () {
      http.req(http.url.systemSpace.diskInfo, http.methods.get).then(response => {
        const { total, used } = response
        this.systemSpace.total = total
        this.systemSpace.used = used
      })
    },
    // 关闭新建文件夹的窗口后清空变量的值
    closeMkdir () {
      this.mkdir.form.fileName = ''
    },
    // 显示添加文件的下拉框
    showMoreFileDropdown () {
      this.showAddBox = true
    },
    // 隐藏添加文件的下拉框
    hideMoreFileDropdown () {
      this.showAddBox = false
    },
    // 显示上传文件的窗口
    showUploadModal () {
      this.upload.visible = true
    },
    // 隐藏上传文件的窗口
    hideUploadModal (record) {
      const { visible } = record
      this.upload.visible = visible
    }
  }
}
</script>

<style scoped lang="scss">
.dashboard-right {
  margin-top: 40px;
  width: 450px;
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
