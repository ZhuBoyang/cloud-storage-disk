<template>
  <div class="file-box">
    <div class="file-box--header">
      <div class="header--multiselect row-col-center">
        <a-checkbox v-model="data.selectAll" @change="selectAllFiles"></a-checkbox>
      </div>
      <div class="header--icon row-col-center"></div>
      <div class="header--name">文件名</div>
      <div class="header--category">文件类型</div>
      <div class="header--size">文件大小</div>
      <div class="header--actions"></div>
    </div>
    <div class="file-box--body"
         @scroll="scrollEvent"
    >
      <div class="file-box--body-item"
           v-for="(item, index) in fileList"
           :class="[{'is-selected': data.selected[index]}]"
           :key="index"
           @click="clickFile(item)"
      >
        <div class="body--multiselect row-col-center">
          <a-checkbox v-model="data.selected[index]" @change="selectFile(item, index)"></a-checkbox>
        </div>
        <div class="body--icon row-col-center">
          <div class="body--icon-img row-col-center">
            <img :src="globalProperties.$common.identifyFileIcon(item)" alt="文件"/>
          </div>
        </div>
        <div class="body--name">
          <div class="body--name-content">{{ item.type === 1 ? item.name : item.name + '.' + item.ext }}</div>
          <div class="body--name-runner">
            <a-tooltip :content="identifyOpenFileIcon(item.ext).content">
              <img v-if="Object.keys(identifyOpenFileIcon(item.ext)).length > 0"
                   alt="播放"
                   :src="identifyOpenFileIcon(item.ext).icon"
                   @click="playVideo(item.id)"
              />
            </a-tooltip>
          </div>
        </div>
        <div class="body--category">{{ item.type === 1 ? '文件夹' : `${item.ext} 文件` }}</div>
        <div class="body--size">{{ globalProperties.$common.formatSizeInPerson(item.size) }}</div>
        <div class="body--actions row-col-center">
          <a-dropdown trigger="hover" @select="fileChangeEvent($event, item, index)">
            <div class="body--actions-btn row-col-center">
              <img src="../assets/icons/full/more.svg" alt="更多">
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
         :class="[{'is-hide': data.selectedFiles.length < 2}]"
    >
      <a-trigger position="top" auto-fit-position :unmount-on-close="false">
        <div class="actions-item row-col-center">
          <img src="../assets/icons/full/Download.svg" alt="下载"/>
        </div>
        <template #content>
          <div class="action-trigger">下载</div>
        </template>
      </a-trigger>
      <a-trigger position="top" auto-fit-position :unmount-on-close="false">
        <div class="actions-item row-col-center" @click="displayBatchCopy">
          <img src="../assets/icons/full/Arrow%20-%20Right%202.svg" alt="复制"/>
        </div>
        <template #content>
          <div class="action-trigger">复制</div>
        </template>
      </a-trigger>
      <a-trigger position="top" auto-fit-position :unmount-on-close="false">
        <div class="actions-item row-col-center" @click="displayBatchMove">
          <img src="../assets/icons/half/Arrow%20-%20Right%202.png" alt="移动"/>
        </div>
        <template #content>
          <div class="action-trigger">移动</div>
        </template>
      </a-trigger>
      <a-trigger position="top" auto-fit-position :unmount-on-close="false">
        <div class="actions-item row-col-center" @click="data.batchRemove.visible = true">
          <img src="../assets/icons/full/Delete.svg" alt="删除"/>
        </div>
        <template #content>
          <div class="action-trigger">删除</div>
        </template>
      </a-trigger>
      <a-trigger position="top" auto-fit-position :unmount-on-close="false">
        <div class="actions-item row-col-center" @click="clearSelected">
          <img src="../assets/icons/full/Close%20Square.svg" alt="取消"/>
        </div>
        <template #content>
          <div class="action-trigger">取消</div>
        </template>
      </a-trigger>
    </div>
    <!-- 删除文件 -->
    <a-modal :visible="data.remove.visible"
             @ok="confirmRemoveFile"
             @cancel="data.remove.visible = false"
             @close="clearRemoveInfo"
    >
      <template #title>删除文件</template>
      <div>文件{{ data.remove.fileName }}删除将不可恢复，是否确定删除？</div>
    </a-modal>
    <!-- 批量删除文件 -->
    <a-modal :visible="data.batchRemove.visible"
             @ok="batchRemoveFiles"
             @cancel="data.batchRemove.visible = false"
             @close="clearRemoveInfo"
    >
      <template #title>批量删除文件</template>
      <div>文件删除将不可恢复，是否确定删除？</div>
    </a-modal>
    <!-- 重命名文件 -->
    <a-modal :visible="data.rename.visible"
             @ok="confirmRename"
             @cancel="data.rename.visible = false"
             @close="cancelRename"
    >
      <template #title>重命名文件</template>
      <a-form :model="data.rename.form" layout="vertical">
        <a-form-item field="rename" label="重命名">
          <a-input v-model="data.rename.form.name" placeholder="请输入新的文件名"/>
        </a-form-item>
      </a-form>
    </a-modal>
    <file-operator-modal :visible="data.dirSelector.visible"
                         :operation-name="data.dirSelector.action"
                         @on-change="operationResult"
    />
    <file-info-drawer :file="data.movie.file" @on-hide="hideDrawer" @on-play="playVideo"></file-info-drawer>
    <player-modal :src="data.movie.src" :width="data.movie.width" :height="data.movie.height" @on-close="closePlayer"/>
  </div>
</template>

<script>
import FileInfoDrawer from './FileInfoDrawer.vue'
import FileOperatorModal from './FileOperatorModal.vue'
import PlayerModal from './PlayerModal.vue'
import { getCurrentInstance, reactive } from 'vue'
import http from '../api/http.js'
import config from '../api/config.js'

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
  emits: ['load-more', 'select-change', 'action-change'],
  setup (props, { emit }) {
    const { appContext } = getCurrentInstance()
    const { config } = appContext
    const { globalProperties } = config
    const data = reactive({
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
      data
    }
  },
  methods: {
    // 元素滚动到底部
    scrollEvent (e) {
      if (e.srcElement.scrollTop + e.srcElement.clientHeight === e.srcElement.scrollHeight) {
        this.emit('load-more')
      }
    },
    // 选择所有文件
    selectAllFiles (record) {
      this.data.selected = []
      this.data.selectedFiles = []
      if (record) {
        for (const key in this.fileList) {
          this.data.selected[key] = true
          this.data.selectedFiles.push(this.fileList[key].id)
        }
      }
    },
    // 选择单个文件
    selectFile (record, recordIndex) {
      const selectedIndex = this.data.selectedFiles.findIndex(item => item === record.id)
      if (selectedIndex === -1) {
        this.data.selected[recordIndex] = true
        this.data.selectedFiles.push(record.id)
      } else {
        this.data.selected[recordIndex] = false
        this.data.selectedFiles.splice(selectedIndex, 1)
      }
      this.data.selectAll = this.data.selectedFiles.length === this.fileList.length
    },
    // 点击文件
    clickFile (record) {
      const { id, name, type } = record
      if (type === 1) {
        this.emit('select-change', { id, name, type })
      }
      this.data.movie.file = record
    },
    // 隐藏文件信息弹窗
    hideDrawer (record) {
      this.data.movie.file = record
    },
    // 播放视频
    playVideo (fileId) {
      http.req(http.url.file.playUrl, http.methods.post, {
        fileId
      }).then(response => {
        const { path, extend } = response
        const { width, height } = this.calculatePlayerSize(extend)
        this.data.movie.src = config.apiBaseUrl + path
        this.data.movie.width = width
        this.data.movie.height = height
      })
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
      this.data.movie.src = ''
    },
    // 文件的操作
    fileChangeEvent (action, record, recordIndex) {
      // 删除单个文件
      if (action === 'delete') {
        const { id, name } = record
        this.data.remove.fileId = id
        this.data.remove.fileName = name
        this.data.remove.index = recordIndex
        this.data.remove.visible = true
        return
      }
      // 移动单个文件
      if (action === 'move' || action === 'copy') {
        const { id } = record
        this.data.selected[recordIndex] = true
        this.data.selectedFiles.push(id)
        this.data.dirSelector.visible = true
        this.data.dirSelector.action = action
        return
      }
      // 重命名文件
      if (action === 'rename') {
        const { id, name } = record
        this.data.rename.visible = true
        this.data.rename.form.id = id
        this.data.rename.form.name = name
      }
    },
    // 弹出批量复制的弹窗
    displayBatchCopy () {
      this.data.dirSelector.visible = true
      this.data.dirSelector.action = 'copy'
    },
    // 弹出批量移动的弹窗
    displayBatchMove () {
      this.data.dirSelector.visible = true
      this.data.dirSelector.action = 'move'
    },
    // 移动文件
    operationResult (record) {
      const { action, id } = record
      if (action === 'cancel' || action === 'close') {
        this.data.selected = []
        this.data.selectedFiles = []
        this.data.dirSelector.visible = false
        return
      }
      const url = this.data.dirSelector.action === 'move' ? http.url.file.batchMove : http.url.file.batchCopy
      http.req(url, http.methods.post, {
        sources: this.data.selectedFiles,
        target: id
      }).then(response => {
        if (response !== undefined) {
          const selectedFiles = this.clearSelected()
          this.emit('action-change', { action: this.data.dirSelector.action, fileIds: [selectedFiles] })
          this.data.dirSelector.visible = false
          this.data.dirSelector.action = ''
        }
      })
    },
    // 确定删除文件
    confirmRemoveFile () {
      http.req(http.url.file.batchDelete, http.methods.post, {
        fileString: this.data.remove.fileId
      }).then(response => {
        if (response !== undefined) {
          this.data.remove.visible = false
          this.emit('action-change', { action: 'delete', fileIds: [this.data.remove.fileId] })
        }
      })
    },
    // 清除删除文件的预留 id
    clearRemoveInfo () {
      this.data.remove.fileId = 0
      this.data.remove.fileName = ''
      this.data.remove.index = -1
    },
    // 确定批量删除文件
    batchRemoveFiles () {
      http.req(http.url.file.batchDelete, http.methods.post, {
        fileString: this.data.selectedFiles.join(',')
      }).then(response => {
        if (response !== undefined) {
          const selectedFiles = this.clearSelected()
          this.data.batchRemove.visible = false
          this.emit('action-change', { action: 'delete', fileIds: selectedFiles })
        }
      })
    },
    // 清空已选中的文件
    clearSelected () {
      const selectedFiles = this.data.selectedFiles
      this.data.selected = []
      this.data.selectedFiles = []
      this.data.selectAll = this.data.selectedFiles.length === this.fileList.length
      return selectedFiles
    },
    // 重命名文件
    confirmRename () {
      const { id, name } = this.data.rename.form
      http.req(http.url.file.rename, http.methods.post, {
        id,
        name
      }).then(response => {
        if (response !== undefined) {
          this.data.rename.visible = false
          this.emit('action-change', { action: 'rename', file: response })
        }
      })
    },
    // 取消重命名
    cancelRename () {
      this.data.rename.form.id = ''
      this.data.rename.form.name = ''
    },
    // 识别文件打开的图标
    identifyOpenFileIcon (ext) {
      if (this.globalProperties.$type.isVideo(ext)) {
        return { content: '播放', icon: '/src/assets/icons/full/Arrow%20-%20Right%202.svg' }
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
          .body--name-runner {
            display: inline-block;
          }
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
