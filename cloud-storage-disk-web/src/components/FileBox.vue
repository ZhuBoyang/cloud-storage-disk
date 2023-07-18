<template>
  <div class="file-box">
    <div class="file-box--header">
      <div class="header--multiselect row-col-center">
        <a-checkbox v-model="selectAll" @change="selectAllFiles"></a-checkbox>
      </div>
      <div class="header--icon row-col-center"></div>
      <div class="header--name">文件名</div>
      <div class="header--category">文件类型</div>
      <div class="header--size">文件大小</div>
      <div class="header--actions"></div>
    </div>
    <div class="file-box--body">
      <div class="file-box--body-item"
           v-for="(item, index) in fileList"
           :class="[{'is-selected': selected[index]}]"
           :key="index"
      >
        <div class="body--multiselect row-col-center">
          <a-checkbox v-model="selected[index]" @change="selectFile(item, index)"></a-checkbox>
        </div>
        <div class="body--icon row-col-center">
          <div class="body--icon-img row-col-center">
            <img :src="globalProperties.common.identifyFileAvatar(item)" alt="文件"/>
          </div>
        </div>
        <div class="body--name" @dblclick="clickFile(item)">
          {{ item.type === 1 || item.ext === '' ? item.name : item.name + item.ext }}
        </div>
        <div class="body--category">{{ item.type === 1 ? '文件夹' : `${item.ext} 文件` }}</div>
        <div class="body--size">{{ item.type === 1 ? '' : globalProperties.common.formatSizeInPerson(item.size) }}</div>
        <div class="body--actions row-col-center">
          <a-dropdown trigger="hover" @select="fileChangeEvent($event, item, index)">
            <div class="body--actions-btn row-col-center">
              <img :src="apiConfig().iconBaseUrl + 'icons/more.png'" alt="更多">
            </div>
            <template #content>
              <a-doption value="rename">重命名</a-doption>
              <a-doption value="copy">复制</a-doption>
              <a-doption value="move">移动</a-doption>
              <a-doption value="download">下载</a-doption>
              <a-doption value="delete">删除</a-doption>
              <a-doption value="share">分享</a-doption>
              <a-doption value="link">获取链接</a-doption>
              <a-doption value="like">添加到喜欢</a-doption>
              <a-doption value="detail">查看详情</a-doption>
            </template>
          </a-dropdown>
        </div>
      </div>
    </div>
    <div class="files-actions"
         :class="[{'is-hide': selectedFiles.length < 2}]"
    >
      <a-trigger position="top" auto-fit-position :unmount-on-close="false">
        <div class="actions-item row-col-center">
          <img :src="apiConfig().iconBaseUrl + 'icons/download.png'" alt="下载"/>
        </div>
        <template #content>
          <div class="action-trigger">下载</div>
        </template>
      </a-trigger>
      <a-trigger position="top" auto-fit-position :unmount-on-close="false">
        <div class="actions-item row-col-center" @click="displayBatchCopy">
          <img :src="apiConfig().iconBaseUrl + 'icons/arrow_right.png'" alt="复制"/>
        </div>
        <template #content>
          <div class="action-trigger">复制</div>
        </template>
      </a-trigger>
      <a-trigger position="top" auto-fit-position :unmount-on-close="false">
        <div class="actions-item row-col-center" @click="displayBatchMove">
          <img :src="apiConfig().iconBaseUrl + 'icons/arrow_right.png'" alt="移动"/>
        </div>
        <template #content>
          <div class="action-trigger">移动</div>
        </template>
      </a-trigger>
      <a-trigger position="top" auto-fit-position :unmount-on-close="false">
        <div class="actions-item row-col-center" @click="batchRemove.visible = true">
          <img :src="apiConfig().iconBaseUrl + 'icons/delete.png'" alt="删除"/>
        </div>
        <template #content>
          <div class="action-trigger">删除</div>
        </template>
      </a-trigger>
      <a-trigger position="top" auto-fit-position :unmount-on-close="false">
        <div class="actions-item row-col-center" @click="clearSelected">
          <img :src="apiConfig().iconBaseUrl + 'icons/close_square.png'" alt="取消"/>
        </div>
        <template #content>
          <div class="action-trigger">取消</div>
        </template>
      </a-trigger>
    </div>
    <!-- 删除文件 -->
    <a-modal :visible="remove.visible"
             @ok="confirmRemoveFile"
             @cancel="remove.visible = false"
             @close="clearRemoveInfo"
    >
      <template #title>删除文件</template>
      <div>文件{{ remove.fileName }}删除将不可恢复，是否确定删除？</div>
    </a-modal>
    <!-- 批量删除文件 -->
    <a-modal :visible="batchRemove.visible"
             @ok="batchRemoveFiles"
             @cancel="batchRemove.visible = false"
             @close="clearRemoveInfo"
    >
      <template #title>批量删除文件</template>
      <div>文件删除将不可恢复，是否确定删除？</div>
    </a-modal>
    <!-- 重命名文件 -->
    <a-modal :visible="rename.visible"
             @ok="confirmRename"
             @cancel="rename.visible = false"
             @close="cancelRename"
    >
      <template #title>重命名文件</template>
      <a-form :model="rename.form" layout="vertical">
        <a-form-item field="rename" label="重命名">
          <a-input v-model="rename.form.name" placeholder="请输入新的文件名"/>
        </a-form-item>
      </a-form>
    </a-modal>
    <file-operator-modal :visible="dirSelector.visible"
                         :operation-name="dirSelector.action"
                         @on-change="operationResult"
    />
    <file-info-drawer :file="movie.file" @on-hide="hideDrawer" @on-play="playVideo"></file-info-drawer>
    <player-modal :src="movie.src" :width="movie.width" :height="movie.height" @on-close="closePlayer"/>
  </div>
</template>

<script>
import { defineAsyncComponent, getCurrentInstance, reactive, toRefs } from 'vue'
import http from '../api/http.js'
import apiConfig from '../api/apiConfig.js'

const FileInfoDrawer = defineAsyncComponent(() => import('./FileInfoDrawer.vue'))
const FileOperatorModal = defineAsyncComponent(() => import('./FileOperatorModal.vue'))
const PlayerModal = defineAsyncComponent(() => import('./PlayerModal.vue'))

export default {
  name: 'FileBox',
  components: {
    FileInfoDrawer,
    FileOperatorModal,
    PlayerModal
  },
  props: {
    fileList: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  emits: ['load-more', 'on-select', 'action-change'],
  setup (props, { emit }) {
    const { appContext } = getCurrentInstance()
    const { globalProperties } = appContext.config
    const dataList = reactive({
      selectAll: false, // 是否选中所有
      selected: [], // 判断文件列表中的文件是否被选中
      selectedFiles: [], // 选中的文件列表
      dirSelector: {
        visible: false, // 是否显示移动文件的警告框
        action: '' // 操作的标识
      },
      remove: {
        visible: false, // 是否显示删除文件的警告框
        fileId: 0, // 待删除文件 id
        fileName: '', // 待删除文件名称
        index: -1 // 待删除文件在列表中的位置
      },
      batchRemove: {
        visible: false // 是否显示删除文件的警告框
      },
      rename: {
        visible: false, // 是否显示重命名的模态框
        form: {
          id: '', // 文件 id
          name: '' // 文件名
        }
      },
      movie: {
        file: {}, // 要在页面右侧显示信息的文件
        src: '', // 视频播放地址
        width: 0, // 视频的宽
        height: 0 // 视频的高
      }
    })
    return {
      globalProperties,
      emit,
      ...toRefs(dataList)
    }
  },
  methods: {
    apiConfig,
    // 选择所有文件
    selectAllFiles (record) {
      this.selected = []
      this.selectedFiles = []
      if (record) {
        for (const key in this.fileList) {
          this.selected[key] = true
          this.selectedFiles.push(this.fileList[key].id)
        }
      }
    },
    // 选择单个文件
    selectFile (record, recordIndex) {
      const selectedIndex = this.selectedFiles.findIndex(item => item === record.id)
      if (selectedIndex === -1) {
        this.selected[recordIndex] = true
        this.selectedFiles.push(record.id)
      } else {
        this.selected[recordIndex] = false
        this.selectedFiles.splice(selectedIndex, 1)
      }
      this.selectAll = this.selectedFiles.length === this.fileList.length
    },
    // 点击文件
    clickFile (record) {
      const { id, name, type } = record
      if (type === 1) {
        this.emit('on-select', { id, name, type })
      }
    },
    // 隐藏文件信息弹窗
    hideDrawer (record) {
      this.movie.file = record
    },
    // 播放视频
    playVideo (fileId) {
      http.req(http.url.file.playUrl, http.methods.post, {
        fileId
      }).then(response => {
        const { path, extend } = response
        const { width, height } = this.calculatePlayerSize(extend)
        this.movie.src = apiConfig.apiBaseUrl + path
        this.movie.width = width
        this.movie.height = height
      })
    },
    // 查询文件详情
    fileDetail (record) {
      this.movie.file = record
    },
    // 计算视频播放器显示的尺寸
    calculatePlayerSize (size) {
      let windowWidth = window.innerWidth
      let windowHeight = window.innerHeight
      const { width, height } = size
      if (width > height) {
        const scale = height / width
        windowHeight = 800 * scale
        return { width: 800, height: windowHeight }
      }
      if (width < height) {
        const scale = width / height
        windowWidth = 400 * scale
        return { width: windowWidth, height: 400 }
      }
      return { width: 400, height: 400 }
    },
    // 关闭视频播放器
    closePlayer () {
      this.movie.src = ''
    },
    // 文件的操作
    fileChangeEvent (action, { id, name }, recordIndex) {
      // 删除单个文件
      if (action === 'delete') {
        this.remove.fileId = id
        this.remove.fileName = name
        this.remove.index = recordIndex
        this.remove.visible = true
        return
      }
      // 移动单个文件
      if (action === 'move' || action === 'copy') {
        this.selected[recordIndex] = true
        this.selectedFiles.push(id)
        this.dirSelector.visible = true
        this.dirSelector.action = action
        return
      }
      // 重命名文件
      if (action === 'rename') {
        this.rename.visible = true
        this.rename.form.id = id
        this.rename.form.name = name
      }
      // 下载文件
      if (action === 'download') {
        window.open(`${apiConfig().apiBaseUrl}${http.url.file.download}${id}`)
      }
    },
    // 弹出批量复制的弹窗
    displayBatchCopy () {
      this.dirSelector.visible = true
      this.dirSelector.action = 'copy'
    },
    // 弹出批量移动的弹窗
    displayBatchMove () {
      this.dirSelector.visible = true
      this.dirSelector.action = 'move'
    },
    // 移动文件
    operationResult ({ action, id }) {
      if (action === 'cancel' || action === 'close') {
        this.selected = []
        this.selectedFiles = []
        this.dirSelector.visible = false
        return
      }
      if (this.dirSelector.action === 'copy') {
        http.reqUrl.file.copy({ sources: this.selectedFiles, target: id }).then(response => {
          if (response) {
            const selectedFiles = this.clearSelected()
            this.emit('action-change', { action: this.dirSelector.action, fileIds: [selectedFiles] })
            this.dirSelector.visible = false
            this.dirSelector.action = ''
          }
        })
      }
      if (this.dirSelector.action === 'move') {
        http.reqUrl.file.move({ sources: this.selectedFiles, target: id }).then(response => {
          if (response) {
            const selectedFiles = this.clearSelected()
            this.emit('action-change', { action: this.dirSelector.action, fileIds: [selectedFiles] })
            this.dirSelector.visible = false
            this.dirSelector.action = ''
          }
        })
      }
    },
    // 确定删除文件
    confirmRemoveFile () {
      http.req(http.url.file.remove, http.methods.post, {
        idsList: [this.remove.fileId]
      }).then(response => {
        if (response !== undefined) {
          this.remove.visible = false
          this.emit('action-change', { action: 'delete', fileIds: [this.remove.fileId] })
        }
      })
    },
    // 清除删除文件的预留 id
    clearRemoveInfo () {
      this.remove.fileId = 0
      this.remove.fileName = ''
      this.remove.index = -1
    },
    // 确定批量删除文件
    batchRemoveFiles () {
      http.req(http.url.file.remove, http.methods.post, {
        idsList: this.selectedFiles
      }).then(response => {
        if (response !== undefined) {
          const selectedFiles = this.clearSelected()
          this.batchRemove.visible = false
          this.emit('action-change', { action: 'delete', fileIds: selectedFiles })
        }
      })
    },
    // 清空已选中的文件
    clearSelected () {
      const selectedFiles = this.selectedFiles
      this.selected = []
      this.selectedFiles = []
      this.selectAll = this.selectedFiles.length === this.fileList.length
      return selectedFiles
    },
    // 重命名文件
    confirmRename () {
      http.reqUrl.file.rename(this.rename.form).then(response => {
        if (response) {
          this.rename.visible = false
          this.emit('action-change', { action: 'rename', file: response })
        }
      })
    },
    // 取消重命名
    cancelRename () {
      this.rename.form.id = ''
      this.rename.form.name = ''
    },
    // 识别文件打开的图标
    identifyOpenFileIcon (ext) {
      if (this.globalProperties.type.isVideo(ext)) {
        return { content: '播放', icon: apiConfig().iconBaseUrl + 'icons/Arrow-Right-2.png' }
      }
      return {}
    }
  }
}
</script>

<style scoped lang="scss">
.file-box {
  height: 100%;
  .file-box--header {
    height: 50px;
    display: flex;
    border-radius: 12px;
    .header--multiselect {
      width: 30px;
    }
    .header--icon {
      margin: 0 20px;
      width: 50px;
      display: flex;
      align-items: center;
    }
    .header--name {
      width: calc(100% - 380px);
      display: flex;
      align-items: center;
    }
    .header--category {
      width: 100px;
      display: flex;
      align-items: center;
    }
    .header--size {
      width: 100px;
      display: flex;
      align-items: center;
    }
    .header--actions {
      width: 60px;
      display: flex;
      align-items: center;
    }
  }
  .file-box--body {
    height: calc(100vh - 50px - 60px - 50px - 24px - 50px);
    overflow-y: auto;
    .file-box--body-item {
      height: 72px;
      display: flex;
      border-radius: 12px;
      cursor: pointer;
      transition: all .3s;
      &.is-selected {
        background-color: #f0edfe;
        transition: all .3s;
      }
      &:hover {
        background-color: #f0edfe;
        transition: all .3s;
        .body--name {
          //.body--name-runner {
          //  display: flex;
          //}
        }
      }
      .body--multiselect {
        width: 30px;
      }
      .body--icon {
        margin: 0 20px;
        width: 50px;
        .body--icon-img {
          width: 48px;
          height: 48px;
          background-color: #f7f6ff;
          border-radius: 12px;
          img {
            width: 26px;
            height: 26px;
          }
        }
      }
      .body--name {
        width: calc(100% - 380px);
        display: flex;
        align-items: center;
        .body--name-runner {
          margin-left: 10px;
          display: none;
          width: 28px;
          height: 28px;
          img {
            width: 100%;
            height: 100%;
          }
        }
      }
      .body--category {
        width: 100px;
        display: flex;
        align-items: center;
      }
      .body--size {
        width: 100px;
        display: flex;
        align-items: center;
      }
      .body--actions {
        margin-right: 10px;
        width: 50px;
        display: flex;
        align-items: center;
        .body--actions-btn {
          width: 48px;
          height: 48px;
          border-radius: 12px;
          cursor: pointer;
          transition: all .3s;
          &:hover {
            background-color: #e3ddff;
            transition: all .3s;
          }
          img {
            width: 28px;
            height: 28px;
          }
        }
      }
    }
  }
}
.files-actions {
  position: fixed;
  top: 50px;
  left: 50%;
  padding: 10px 20px;
  display: flex;
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 0 10px #a3a3a3;
  transform: translateX(-50%);
  transition: all .3s;
  &.is-hide {
    top: -11%;
    transition: all .3s;
  }
  .actions-item {
    margin: 0 10px;
    width: 30px;
    height: 30px;
    border-radius: 10px;
    transition: all .3s;
    cursor: pointer;
    &:first-child {
      margin-left: 0;
    }
    &:last-child {
      margin-right: 0;
    }
    &:hover {
      background-color: #d2d2d2;
      transition: all .3s;
    }
    img {
      width: 80%;
      height: 80%;
    }
  }
}
.action-trigger {
  margin-bottom: 5px;
  padding: 10px;
  color: #ffffff;
  background-color: #626266;
  border-radius: 5px;
}
</style>
