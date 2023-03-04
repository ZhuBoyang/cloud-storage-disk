<template>
  <div class="dashboard-right">
    <login-user-action/>
    <div class="file-actions"
         @mouseenter="showMoreFileDropdown"
         @mouseleave="hideMoreFileDropdown"
    >
      <div class="add-file-btn">
        添加文件
        <img :src="config.iconBaseUrl + 'icons/full/Plus.svg'" alt="添加文件"/>
      </div>
      <div class="file-actions--list"
           :class="[{'is-show': data.showAddBox}]"
      >
        <div class="file-actions--list-item" @click="showUploadModal">上传文件</div>
        <div class="file-actions--list-item" @click="data.mkdir.mkdirVisible = true">创建文件夹</div>
      </div>
    </div>
    <div class="disk-space-monitor">
      <circular-progress class="storage-progress"
                         :percent="parseInt(((data.systemSpace.used / data.systemSpace.total) * 100).toString())"
      ></circular-progress>
      <div class="disk-space-info">
        <div class="disk-space-info-item">{{ globalProperties.$common.formatSizeInPerson(data.systemSpace.used) }}</div>
        <div class="disk-space-info-item">/</div>
        <div class="disk-space-info-item">{{ globalProperties.$common.formatSizeInPerson(data.systemSpace.total) }}</div>
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
        <a-input v-model="data.mkdir.form.fileName" placeholder="请输入文件夹名称"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import LoginUserAction from './LoginUserAction.vue'
import UploadFileList from './UploadFileList.vue'
import CircularProgress from '../components/custom/CircularProgress.vue'
import { getCurrentInstance, reactive } from 'vue'
import http from '../api/http.js'
import emitter from '../utils/emitter.js'
import { useRouter } from 'vue-router'
import config from '../api/config.js'

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
    const data = reactive({
      showAddBox: false, // 是否显示添加文件的下拉框
      uploadVisible: false, // 是否显示上传文件的弹窗
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
      data.systemSpace.used += size
    })
    return {
      config,
      globalProperties,
      router,
      data
    }
  },
  created () {
    this.getDiskInfo()
  },
  methods: {
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
    // 查询系统磁盘空间使用量
    getDiskInfo () {
      http.req(http.url.systemSpace.diskInfo, http.methods.get).then(response => {
        const { total, used } = response
        this.data.systemSpace.total = total
        this.data.systemSpace.used = used
      })
    },
    // 关闭新建文件夹的窗口后清空变量的值
    closeMkdir () {
      this.data.mkdir.form.fileName = ''
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
