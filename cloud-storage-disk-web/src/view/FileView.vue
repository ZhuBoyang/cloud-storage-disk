<template>
  <div class="file-view">
    <div class="view-box">
      <div class="file-searcher">
        <a-input class="file-search-input" placeholder="请输入关键词" size="large" v-model:model-value="pager.name"
                 allow-clear>
          <template #prefix>
            <img :src="apiConfig().iconBaseUrl + 'icons/search.png'" alt="search" width="20">
          </template>
        </a-input>
        <div class="file-search-filter-icon">
          <img :src="apiConfig().iconBaseUrl + 'icons/filter.png'" alt="filter">
        </div>
      </div>
      <div class="file-bread">
        <a-breadcrumb :max-count="3">
          <a-breadcrumb-item v-for="(o, i) in breads" :key="i">
            <a href="javascript:void(0)" @click="returnParent(o, i)">{{ o.name }}</a>
          </a-breadcrumb-item>
        </a-breadcrumb>
      </div>
      <a-empty class="empty-box"
               v-if="files.length === 0"
               description="暂无文件，请上传文件"
               :img-src="apiConfig().iconBaseUrl + 'icons/empty_data.png'"
      />
      <file-box class="file-list-box"
                v-else
                :file-list="files"
                :actions="['download', 'copy', 'move', 'remove', 'cancel']"
                :file-actions="['rename', 'copy', 'move', 'download', 'delete', 'share', 'detail']"
                @on-select="selectChange"
                @action-change="actionResult"
      ></file-box>
      <div class="file-pagination row-col-center">
        <a-pagination v-model:current="pager.pageIndex" v-model:page-size="pager.pageSize" :total="pager.total"
                      @change="changePage"/>
      </div>
    </div>
    <video-player v-if="play.videoMedia"
                  :url="play.url"
                  :video-id="play.playId"
                  :play-list="play.playList"
                  @on-close="closePlayer"
    />
    <audio-player v-if="play.audioMedia"
                  :url="play.url"
                  :audio-id="play.playId"
                  :play-list="play.playList"
                  @on-close="closePlayer"
    />
    <document-player :metadata="play.metadata"
                     :files="play.playList"
                     @on-change="changeSnapshot"
                     @on-close="closePlayer"
    />
  </div>
</template>

<script>
import { defineAsyncComponent, reactive, toRefs, watch } from 'vue'
import emitter from '../tools/emitter.js'
import { useRouter } from 'vue-router'
import apiConfig from '../api/apiConfig.js'
import http from '../api/http.js'
import common from '../tools/common.js'
import type from '../tools/type.js'

const FileBox = defineAsyncComponent(() => import('../components/FileBox.vue'))
const VideoPlayer = defineAsyncComponent(() => import('../components/VideoPlayer.vue'))
const AudioPlayer = defineAsyncComponent(() => import('../components/AudioPlayer.vue'))
const DocumentPlayer = defineAsyncComponent(() => import('../components/DocumentPlayer.vue'))

export default {
  name: 'FileView',
  components: {
    FileBox,
    VideoPlayer,
    AudioPlayer,
    DocumentPlayer
  },
  setup: function () {
    const router = useRouter()
    const dataList = reactive({
      breads: [], // 文件面包屑导航数据
      files: [], // 文件列表
      pager: {
        pageIndex: 1, // 当前页码
        pageSize: 10, // 每页显示的数据量
        total: 0, // 总量
        name: '' // 搜索的关键词
      },
      play: {
        url: http.url.file.playUrl, // 获取视频播放地址的接口
        metadata: {}, // 文件元数据
        playList: [], // 播放列表
        videoMedia: false, // 是否显示视频播放器
        audioMedia: false, // 是否显示音频播放器
        documentMedia: false // 是否显示 office 播放器
      }
    })
    // 查询文件面包屑导航数据
    const queryBreads = () => {
      let id = router.currentRoute.value.query.router
      if (id === undefined) {
        id = dataList.breads.length === 0 ? '_' : dataList.breads[dataList.breads.length - 1].id
      }
      http.reqUrl.file.breads({ id }).then(response => {
        dataList.breads = []
        for (let i = 0; i < response.length; i++) {
          dataList.breads.push(response[i])
        }
        common.setUrlQuery(router, 'router', dataList.breads[dataList.breads.length - 1].id)
      })
    }
    // 查询文件列表数据
    const queryFiles = (pid) => {
      if (pid === undefined) {
        pid = dataList.breads.length === 0 ? '_' : dataList.breads[dataList.breads.length - 1].id
      }
      dataList.pager.pid = pid
      http.reqUrl.file.pager(dataList.pager).then(response => {
        dataList.files = response.data
        dataList.pager.total = response.total
      })
    }
    // 监听新建文件夹
    emitter.on('on-mkdir', () => {
      // 这里监听来自 ActionComponent 组件发送的新建文件夹的消息，用以刷新文件列表
      if (dataList.files.length === dataList.pager.pageSize) {
        dataList.pager.pageIndex++
      }
      queryFiles()
    })
    // 监听上传文件
    emitter.on('on-flush', () => {
      // 这里监听来自 UploadComponent 组件的消息，以刷新页面中的文件列表
      if (dataList.files.length === dataList.pager.pageSize) {
        dataList.pager.pageIndex++
      }
      queryFiles()
    })
    watch(() => dataList.pager.name, () => {
      queryFiles()
    })
    return {
      router,
      ...toRefs(dataList),
      queryBreads,
      queryFiles
    }
  },
  created () {
    this.queryBreads()
    this.queryFiles(this.router.currentRoute.value.query.router)
  },
  methods: {
    apiConfig,
    // 跳转到某上层目录
    returnParent (record, recordIndex) {
      this.breads.splice(recordIndex + 1, this.breads.length - recordIndex - 1)
      common.setUrlQuery(this.router, 'router', this.breads[this.breads.length - 1].id)
      this.queryFiles(this.breads[this.breads.length - 1].id)
    },
    // 选择文件
    selectChange (metadata) {
      const { id, name, type: fileType, ext } = metadata
      if (fileType === 0) {
        this.closePlayer()
        // 打开的文件是视频文件
        if (type.isVideo(ext)) {
          this.play.playId = id
          http.reqUrl.file.videos({ id: this.breads[this.breads.length - 1].id }).then(response => {
            this.play.videoMedia = true
            this.play.playList = response
          })
        }
        // 打开的文件是音频文件
        if (type.isAudio(ext)) {
          this.play.playId = id
          http.reqUrl.file.audios({ id: this.breads[this.breads.length - 1].id }).then(response => {
            this.play.audioMedia = true
            this.play.playList = response
          })
        }
        // 打开的文件是文档
        if (type.isDocument(ext)) {
          this.play.metadata = metadata
          http.reqUrl.file.document({ id: this.breads[this.breads.length - 1].id }).then(response => {
            this.play.documentMedia = true
            this.play.playList = response
          })
        }
      } else {
        const bread = { id, name }
        this.breads.push(bread)
        common.setUrlQuery(this.router, 'router', this.breads[this.breads.length - 1].id)
        this.queryFiles(id)
      }
    },
    // 切换预览的文件
    changeSnapshot (o) {
      this.play.metadata = o
    },
    // 切换页面
    changePage (pageIndex) {
      this.pager.pageIndex = pageIndex
      this.queryFiles()
    },
    // 文件操作的结果
    actionResult (record) {
      const { action } = record
      // 删除文件，移动文件
      if (action === 'delete' || action === 'copy' || action === 'move') {
        this.queryFiles()
      }
      // 文件重命名
      if (action === 'rename') {
        const fileIndex = this.files.findIndex(file => file.id === record.file.id)
        this.files[fileIndex] = record.file
        this.queryFiles()
      }
    },
    // 关闭播放器
    closePlayer () {
      this.play.metadata = {}
      this.play.playId = ''
      this.play.playList = []
      this.play.videoMedia = false
      this.play.audioMedia = false
    }
  }
}
</script>

<style scoped lang="scss">
.file-view {
  height: 100%;
  .view-box {
    margin: 0 auto;
    padding-top: 50px;
    width: 94%;
    height: calc(100vh - 50px);
    .file-searcher {
      display: flex;
      justify-content: space-between;
      .file-search-filter-icon {
        margin-left: 30px;
        width: 60px;
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: #6a4bfe;
        border-radius: 14px;
        cursor: pointer;
        img {
          width: 28px;
          height: 28px;
          filter: brightness(100);
        }
      }
    }
    .file-bread {
      margin: 30px 0 20px;
    }
    .empty-box {
      margin: 50px 0;
    }
    .file-list-box {
      height: calc(100% - 60px - 50px - 50px - 50px);
    }
  }
  .file-pagination {
    width: 100%;
    height: 50px;
  }
}
</style>
