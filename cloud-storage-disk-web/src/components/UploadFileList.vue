<template>
  <transition name="upload-bg">
    <div class="upload-file-list" v-show="visible && !data.hideModalVisible"></div>
  </transition>
  <div class="file-list-box"
       :class="[{'is-hide': visible && data.hideModalVisible}, {'is-close': !visible}]"
  >
    <div class="box-header">
      <div class="box-header--info">
        <div class="box-upload-icon">
          <img :src="config.iconBaseUrl + 'icons/full/Upload.svg'" alt="文件上传"/>
        </div>
        <div class="box-upload-title">上传文件</div>
      </div>
      <div class="box-header--close">
        <img :src="config.iconBaseUrl + 'icons/full/Close_Square.svg'"
             alt="关闭"
             v-if="visible && !data.hideModalVisible && data.upload.uploading.findIndex(item => item.status === 'loading') === -1"
             @click="closeUploadModal"
        />
        <img :src="config.iconBaseUrl + 'icons/full/Arrow-Up-Circle.svg'"
             alt="显示"
             v-if="visible && data.hideModalVisible"
             @click="data.hideModalVisible = false"
        />
      </div>
    </div>
    <div class="upload-list">
      <div class="upload-item"
           v-for="(item, index) in data.upload.uploading"
           :key="index"
      >
        <div class="item-info">
          <div class="item-icon row-col-center">
            <img :src="globalProperties.$common.identifyFileIcon(item)" alt="文档"/>
          </div>
          <div class="item-name">{{ item.fileName }}</div>
          <div class="item-size">{{ globalProperties.$common.formatSizeInPerson(item.fileSize) }}</div>
          <div class="item-status">
            <img :src="config.iconBaseUrl + 'icons/full/success.svg'" class="upload-success" alt="成功" v-if="item.status === 'success'"/>
            <img :src="config.iconBaseUrl + 'icons/full/error.svg'" class="upload-error" alt="失败" v-if="item.status === 'error'"/>
            <img :src="config.iconBaseUrl + 'icons/full/loading.svg'" class="upload-loading" alt="加载中" v-if="item.status === 'loading'"/>
          </div>
        </div>
        <div class="item-process">
          <a-progress :percent="item.process" :show-text="false"/>
        </div>
      </div>
    </div>
    <div class="upload-actions">
      <input type="file" style="display: none" ref="file" @change="readUploadFiles"/>
      <a-button type="outline" shape="round" @click="hideUploadModal">隐藏</a-button>
      <a-button type="primary" shape="round" @click="triggerUpload">上传文件</a-button>
    </div>
  </div>
</template>

<script>
import { getCurrentInstance, reactive } from 'vue'
import http from '../api/http.js'
import SparkMD5 from 'spark-md5'
import { useRouter } from 'vue-router'
import emitter from '../utils/emitter.js'
import config from '../api/config.js'

export default {
  name: 'UploadFileList',
  props: {
    // 是否显示上传文件的窗口
    visible: {
      type: Boolean,
      default: false
    }
  },
  emits: ['on-change'],
  setup (props, { emit }) {
    const { appContext } = getCurrentInstance()
    const { globalProperties } = appContext.config
    const router = useRouter()
    const data = reactive({
      hideModalVisible: false, // 是否半隐藏上传文件的弹窗
      upload: {
        blockSize: 4 * 1024 * 1024, // 文件块大小
        uploading: [], // 上传文件列表
        uploaded: [] // 已上传文件元数据
      }
    })
    return {
      config,
      globalProperties,
      router,
      emit,
      data
    }
  },
  methods: {
    // 关闭上传文件的窗口
    closeUploadModal () {
      this.data.upload.uploading = []
      this.data.hideModalVisible = false
      this.emit('on-change', { visible: false })
      // 筛出已上传完成，但是还未发送至文件列表的文件
      const send = []
      const uploaded = this.data.upload.uploaded
      for (const key in uploaded) {
        const file = uploaded[key].file
        if (undefined === file.sent || file.sent === 0) {
          send.push(file)
          this.data.upload.uploaded[key].file.sent = 1
        }
      }
      // 将文件发送至文件列表
      emitter.emit('upload-change', send)
    },
    // 最小化上传文件的窗口
    hideUploadModal () {
      // 最小化弹窗
      this.data.hideModalVisible = true
      // 筛出已上传完成，但是还未发送至文件列表的文件
      const send = []
      for (const key in this.data.upload.uploading) {
        const item = this.data.upload.uploading[key]
        if (item.status === 'success' && item.sent === 0) {
          const fileIndex = this.data.upload.uploaded.findIndex(v => v.fileIndex === parseInt(key))
          const file = this.data.upload.uploaded[fileIndex].file
          this.data.upload.uploading[key].sent = 1
          send.push(file)
        }
      }
      // 将文件发送至文件列表
      emitter.emit('upload-change', send)
    },
    // 触发上传文件的事件
    triggerUpload () {
      this.$refs.file.value = ''
      this.$refs.file.click()
    },
    // 读取上传的文件
    async readUploadFiles (e) {
      const files = e.target.files
      const uploadedCount = this.data.upload.uploading.length
      for (let i = 0; i < files.length; i++) {
        const current = files[i]
        const file = {
          fileName: current.name, // 文件名
          fileSize: current.size, // 文件大小
          ext: current.name.substring(current.name.lastIndexOf('.') + 1), // 文件后缀
          process: 0, // 上传进度
          blockNumber: 0, // 已上传文件块数量
          blockCount: 0, // 文件块总数据量
          status: 'loading', // 上传状态
          type: 0, // 文件类型
          sent: 0 // 是否已通过 emitter 发送至文件列表
        }
        this.data.upload.uploading.push(file)
        await this.splitFileBlock(current, uploadedCount + i)
      }
    },
    async splitFileBlock (file, fileIndex) {
      const fileBlockList = []
      const blockSize = this.data.upload.blockSize
      const blockCount = file.size % blockSize === 0 ? parseInt(`${file.size / blockSize}`) : parseInt(`${file.size / blockSize + 1}`)
      for (let i = 0; i < blockCount; i++) {
        fileBlockList.push(file.slice(i * blockSize, Math.min((i + 1) * blockSize, file.size)))
      }
      const blockList = await this.generateUploadFormData(file, fileBlockList)
      // 设置上传的文件的文件块总数量
      const currentItem = this.data.upload.uploading[fileIndex]
      currentItem.blockCount = blockList.length
      this.data.upload.uploading[fileIndex] = currentItem
      // 计算文件 hash
      const { hash } = await this.getFileHash(file)
      // 发起上传文件块的请求
      this.sendRequest(blockList, fileIndex, hash)
    },
    // 将文件分片生成form表单数据
    async generateUploadFormData (file, fileBlockList) {
      const query = this.router.currentRoute.value.query
      const { id } = query
      const identifier = new Date().getTime()
      const blocks = []
      for (let index = 0; index < fileBlockList.length; index++) {
        const item = fileBlockList[index]
        const formData = new FormData()
        formData.append('file', item, identifier.toString())
        formData.append('data', item)
        formData.append('blockIndex', index + 1)
        formData.append('shardingSize', this.data.upload.blockSize.toString())
        formData.append('blockSize', item.size)
        formData.append('fileSize', file.size)
        const { hash } = await this.getFileHash(item)
        formData.append('hash', hash)
        formData.append('identifier', identifier.toString())
        formData.append('fileName', file.name)
        formData.append('blockCount', fileBlockList.length)
        formData.append('shard', true)
        formData.append('pid', id)
        const param = {
          pid: id, // 当前所在目录的文件 id
          blockIndex: index + 1, // 当前文件块序号
          shardingSize: this.data.upload.blockSize, // 标准分片大小
          blockSize: item.size, // 当前文件块大小
          fileSize: file.size, // 文件大小
          hash, // 文件块 hash
          identifier, // 文件唯一标识
          fileName: file.name, // 文件名
          blockCount: fileBlockList.length, // 文件块数量
          shard: true // 是否开启分片
        }
        blocks.push({ formData, param })
      }
      return blocks
    },
    // 发送上传文件的请求
    sendRequest (blocks, fileIndex, fileHash) {
      // 封装文件块 hash 列表，并检测是否所有文件块都需要上传
      const hashList = []
      for (const key in blocks) {
        hashList.push(blocks[key].param.hash)
      }
      const blockParam = blocks[0].param
      blockParam.hashList = hashList.join(',')
      http.req(http.url.fileBlock.checkExist, http.methods.form, blockParam).then(response => {
        console.log('check exist response', response)
        for (let i = 0; i < response.length; i++) {
          if (response[i]) {
            // 文件块已上传，无需再次上传。更新文件上传进度
            const currentItem = this.updateFileUploadProcess(fileIndex, false)
            if (currentItem.blockNumber === currentItem.blockCount) {
              // 所有文件块均已上传，需要进行文件合并
              this.mergeRequest(blocks[0].param.identifier, fileIndex, fileHash)
            }
          } else {
            const header = { 'Content-Type': 'multipart/form-data' }
            http.req(http.url.fileBlock.uploadBlocks, http.methods.form, blocks[i].formData, header).then(response => {
              if (response) {
                const currentItem = this.updateFileUploadProcess(fileIndex, false)
                if (currentItem.blockNumber === currentItem.blockCount) {
                  // 所有文件块均已上传，需要进行文件合并
                  this.mergeRequest(blocks[0].param.identifier, fileIndex, fileHash)
                }
              }
            })
          }
        }
      })
    },
    // 更新文件上传进度
    updateFileUploadProcess (fileIndex, flag) {
      const currentItem = this.data.upload.uploading[fileIndex]
      if (flag) {
        currentItem.process = 1
        currentItem.status = 'success'
      } else {
        currentItem.blockNumber++
        currentItem.process = currentItem.blockNumber / (currentItem.blockCount + 1)
      }
      this.data.upload.uploading[fileIndex] = currentItem
      return currentItem
    },
    // 文件合并
    mergeRequest (identifier, fileIndex, fileHash) {
      const param = {
        identifier,
        fileHash
      }
      http.req(http.url.fileBlock.mergeFile, http.methods.post, param).then(response => {
        this.data.upload.uploaded.push({ fileIndex, file: response })
        this.updateFileUploadProcess(fileIndex, true)
        // 如果上传文件的弹窗已经最小化了，那么上传完成的文件就自动发送至文件列表
        if (this.data.hideModalVisible) {
          emitter.emit('upload-change', [response])
        }
      })
    },
    // 计算文件 hash 值
    async getFileHash (file) {
      return new Promise(resolve => {
        const reader = new FileReader()
        reader.readAsArrayBuffer(file)
        reader.onload = ev => {
          const buffer = ev.target.result
          const spark = new SparkMD5.ArrayBuffer()
          spark.append(buffer)
          const hash = spark.end()
          resolve({
            buffer,
            hash
          })
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.upload-file-list {
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
    min-height: 60vh;
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
