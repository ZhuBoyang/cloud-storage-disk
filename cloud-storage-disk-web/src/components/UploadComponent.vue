<template>
  <transition name="upload-bg">
    <div class="upload-component" v-show="visible && !hideModalVisible"></div>
  </transition>
  <div class="file-list-box"
       :class="[{'is-hide': visible && hideModalVisible}, {'is-close': !visible}]"
  >
    <div class="box-header">
      <div class="box-header--info">
        <div class="box-upload-icon">
          <img :src="apiConfig().iconBaseUrl + 'icons/upload.png'" alt="文件上传"/>
        </div>
        <div class="box-upload-title">上传文件</div>
      </div>
      <div class="box-header--close">
        <img :src="apiConfig().iconBaseUrl + 'icons/close_square.png'"
             alt="关闭"
             v-if="visible && !hideModalVisible && upload.uploading.findIndex(item => item.status === 'loading') === -1"
             @click="closeUploadModal"
        />
        <img :src="apiConfig().iconBaseUrl + 'icons/arrow_up.png'"
             alt="显示"
             v-if="visible && hideModalVisible"
             @click="hideModalVisible = false"
        />
      </div>
    </div>
    <div class="upload-list">
      <div class="upload-item"
           v-for="(o, i) in upload.uploading"
           :key="i"
      >
        <div class="item-info">
          <div class="item-icon row-col-center">
            <img :src="globalProperties.common.identifyFileAvatar(o)" alt="文档"/>
          </div>
          <div class="item-name">{{ o.fileName }}</div>
          <div class="item-size">{{ globalProperties.common.formatSizeInPerson(o.fileSize) }}</div>
          <div class="item-status">
            <img :src="apiConfig().iconBaseUrl + 'icons/success.png'" class="upload-success" alt="成功" v-if="o.status === 'success'"/>
          </div>
        </div>
        <div class="item-process">
          <a-progress :percent="o.process" :show-text="false"/>
        </div>
      </div>
    </div>
    <div class="upload-actions">
      <input type="file" style="display: none" ref="file" multiple="multiple" @change="readUploadFiles"/>
      <a-button type="outline" shape="round" @click="hideUploadModal">隐藏</a-button>
      <a-button type="primary" shape="round" @click="triggerUpload">上传文件</a-button>
    </div>
  </div>
</template>

<script>
import { getCurrentInstance, reactive, toRefs } from 'vue'
import http from '../api/http.js'
import SparkMD5 from 'spark-md5'
import { useRouter } from 'vue-router'
import apiConfig from '../api/apiConfig.js'
import { nanoid } from 'nanoid'
import emitter from '../tools/emitter.js'

export default {
  name: 'UploadComponent',
  props: {
    // 是否显示上传文件的窗口
    visible: {
      type: Boolean,
      default: false
    },
    // 检测文件块是否已上传的接口地址
    checkBlockExistUrl: {
      type: String,
      default: ''
    },
    // 上传文件块的接口地址
    uploadBlockUrl: {
      type: String,
      default: ''
    },
    // 合并文件的接口地址
    mergeUrl: {
      type: String,
      default: ''
    }
  },
  emits: ['on-upload-change'],
  setup (props, { emit }) {
    const { appContext } = getCurrentInstance()
    const { globalProperties } = appContext.config
    const router = useRouter()
    const dataList = reactive({
      hideModalVisible: false, // 是否半隐藏上传文件的弹窗
      upload: {
        blockSize: 4 * 1024 * 1024, // 文件块大小
        uploading: [], // 上传文件列表
        uploaded: [] // 已上传文件元数据
      }
    })
    return {
      globalProperties,
      router,
      emit,
      ...toRefs(dataList)
    }
  },
  methods: {
    apiConfig,
    // 关闭上传文件的窗口
    closeUploadModal () {
      this.upload.uploading = []
      this.hideModalVisible = false
      // 这里会向父级组件发送消息，以关闭上传文件的模态框
      this.emit('on-upload-change')
      // 这里会向 FileView 发送消息，以刷新文件列表，并向 LoginUserAction 组件发送消息，以刷新账户空间使用率
      emitter.emit('on-upload-change')
    },
    // 最小化上传文件的窗口
    hideUploadModal () {
      // 最小化弹窗
      this.hideModalVisible = true
    },
    // 触发上传文件的事件
    triggerUpload () {
      this.$refs.file.value = ''
      this.$refs.file.click()
    },
    // 读取上传的文件
    readUploadFiles (e) {
      const files = e.target.files
      const uploadedCount = this.upload.uploading.length
      for (let i = 0; i < files.length; i++) {
        const current = files[i]
        const file = {
          fileName: current.name, // 文件名
          fileSize: current.size, // 文件大小
          ext: current.name.substring(current.name.lastIndexOf('.') + 1), // 文件后缀
          process: 0, // 校验进度或上传进度
          blockNumber: 0, // 已上传文件块数量
          blockCount: 0 // 文件块总数据量
        }
        this.upload.uploading.push(file)
        this.splitFileBlock(current, uploadedCount + i)
      }
    },
    // 切分文件块
    splitFileBlock (file, fileIndex) {
      const blocks = []
      const blockSize = this.upload.blockSize
      const blockCount = file.size % blockSize === 0 ? parseInt(`${file.size / blockSize}`) : parseInt(`${file.size / blockSize + 1}`)
      for (let i = 0; i < blockCount; i++) {
        blocks.push(file.slice(i * blockSize, Math.min((i + 1) * blockSize, file.size)))
      }
      // 设置上传的文件的文件块总数量
      this.upload.uploading[fileIndex].blockCount = blocks.length
      this.generateUploadFormData(file, blocks, fileIndex)
    },
    // 将文件分片生成 form 表单数据
    generateUploadFormData (file, blocks, fileIndex) {
      const identifier = nanoid() + '_' + new Date().getTime()
      for (let index = 0; index < blocks.length; index++) {
        const item = blocks[index]
        const { fileName, ext } = this.cutFileName(file.name)
        const param = {
          blockIndex: index + 1, // 当前文件块序号
          blockSize: item.size, // 当前文件块大小
          blockCount: blocks.length, // 文件块数量
          shardingSize: this.upload.blockSize, // 标准分片大小
          fileSize: file.size, // 文件大小
          identifier, // 文件唯一标识
          fileName, // 文件名
          ext, // 文件名后缀
          id: this.router.currentRoute.value.query.router, // 当前所在目录的文件 id
          shard: 1 // 是否开启分片
        }
        // 计算文件块的 hash
        this.calculateFileHash(item).then(response => {
          const { hash } = response
          param.hash = hash
          const formData = new FormData()
          for (const key in param) {
            formData.append(key, param[key])
          }
          formData.append('file', item)
          this.sendRequest(param, formData, fileIndex)
        })
      }
    },
    // 发送上传文件的请求
    sendRequest (blockParameter, blockData, fileIndex) {
      http.req(this.checkBlockExistUrl, http.methods.post, blockParameter).then(response => {
        if (response) {
          // 文件块已存在，无需再次上传。更新文件上传进度
          const currentItem = this.updateFileUploadProcess(fileIndex, false)
          if (currentItem.blockNumber === currentItem.blockCount) {
            // 所有文件块均已上传，需要进行文件合并
            this.mergeRequest(blockParameter.identifier, fileIndex)
          }
        } else {
          const header = { 'Content-Type': 'multipart/form-data' }
          http.req(this.uploadBlockUrl, http.methods.form, blockData, header).then(response => {
            if (response) {
              const currentItem = this.updateFileUploadProcess(fileIndex, false)
              if (currentItem.blockNumber === currentItem.blockCount) {
                // 所有文件块均已上传，需要进行文件合并
                this.mergeRequest(blockParameter.identifier, fileIndex)
              }
            }
          })
        }
      })
    },
    // 更新文件上传进度
    updateFileUploadProcess (fileIndex, flag) {
      const currentItem = this.upload.uploading[fileIndex]
      if (flag) {
        currentItem.process = 1
        currentItem.status = 'success'
      } else {
        currentItem.blockNumber++
        currentItem.process = currentItem.blockNumber / (currentItem.blockCount + 1)
      }
      this.upload.uploading[fileIndex] = currentItem
      return currentItem
    },
    // 文件合并
    mergeRequest (identifier, fileIndex) {
      http.req(this.mergeUrl, http.methods.post, {
        identifier
      }).then(response => {
        this.upload.uploaded.push({ fileIndex, file: response })
        this.updateFileUploadProcess(fileIndex, true)
      })
    },
    // 切分文件名
    cutFileName (name) {
      const fileName = name.substring(0, name.lastIndexOf('.'))
      const ext = name.substring(name.lastIndexOf('.'))
      return { fileName, ext }
    },
    // 计算文件 hash 值
    calculateFileHash (file) {
      return new Promise(resolve => {
        const reader = new FileReader()
        reader.readAsArrayBuffer(file)
        reader.onload = ev => {
          const buffer = ev.target.result
          const spark = new SparkMD5.ArrayBuffer()
          spark.append(buffer)
          const hash = spark.end()
          resolve({
            hash
          })
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.upload-component {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background-color: rgba(0, 0, 0, .5);
  z-index: 10;
}
.file-list-box {
  position: fixed;
  top: 50%;
  left: 50%;
  padding: 36px 40px;
  min-width: 1012px;
  width: 60%;
  background-color: #ffffff;
  border-radius: 24px;
  box-shadow: 0 0 10px #909090;
  transform: translate(-50%, -50%);
  transition: all .3s;
  z-index: 10;
  &.is-hide {
    top: 130%;
    transition: all .3s;
  }
  &.is-close {
    top: 150%;
    transition: all .3s;
  }
  .box-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .box-header--info {
      display: flex;
      align-items: center;
      .box-upload-icon {
        width: 28px;
        height: 28px;
        img {
          width: 100%;
          height: 100%;
        }
      }
      .box-upload-title {
        margin-left: 10px;
        font-size: 16px;
        font-weight: bolder;
      }
    }
    .box-header--close {
      width: 28px;
      height: 28px;
      img {
        width: 100%;
        height: 100%;
        cursor: pointer;
      }
    }
  }
  .upload-list {
    margin-top: 40px;
    height: 60vh;
    overflow-y: auto;
    .upload-item {
      padding: 10px 0;
      .item-info {
        display: flex;
        align-items: center;
        .item-icon {
          width: 48px;
          height: 48px;
          background-color: #f7f6ff;
          border-radius: 12px;
          img {
            width: 28px;
            height: 28px;
          }
        }
        .item-name {
          margin-left: 32px;
          width: calc(100% - 48px - 32px - 200px - 28px);
        }
        .item-size {
          width: 200px;
        }
        .item-status {
          width: 28px;
          height: 28px;
          img {
            width: 100%;
            &.upload-loading {
              animation: infinite-rotation 1s linear infinite;
            }
          }
        }
      }
    }
  }
  .upload-actions {
    text-align: right;
    button {
      margin: 0 10px;
      &:first-child {
        margin-left: 0;
      }
      &:last-child {
        margin-right: 0;
      }
    }
  }
}

// 无限旋转动画
@keyframes infinite-rotation {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
    transition: all 5s;
  }
}

.upload-bg-enter-active {
  animation: upload-bg-animation .5s;
}
.upload-bg-leave-active {
  animation: upload-bg-animation .5s reverse;
}
@keyframes upload-bg-animation {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}
</style>
