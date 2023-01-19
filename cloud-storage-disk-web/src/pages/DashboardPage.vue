<template>
  <div class="dashboard-page">
    <div class="dashboard-center--box">
      <div class="file-search-box">
        <a-input class="file-search-input" placeholder="请输入关键词" size="large" allow-clear>
          <template #prefix>
            <img src="../assets/icons/full/search%20icon.svg" alt="search" width="40">
          </template>
        </a-input>
        <div class="file-search-filter-icon">
          <img src="../assets/icons/full/Filter.svg" alt="filter">
        </div>
      </div>
      <div class="file-breads-box">
        <a-breadcrumb :max-count="3">
          <a-breadcrumb-item v-for="(item, index) in data.breads" :key="index">
            <a href="javascript:void(0)" @click="returnParent(item, index)">{{ item.name }}</a>
          </a-breadcrumb-item>
        </a-breadcrumb>
      </div>
      <!--      <div class="file-category-box">-->
      <!--        <h3 class="box-title">类型</h3>-->
      <!--        <div class="box-container">-->
      <!--          <a-row>-->
      <!--            <a-col :xxl="6"-->
      <!--                   :xl="6"-->
      <!--                   :lg="6"-->
      <!--                   :md="6"-->
      <!--            >-->
      <!--              <div class="box-item">-->
      <!--                <div class="item-image item-type-image">-->
      <!--                  <img src="../assets/file/category/image.svg" alt="图片"/>-->
      <!--                </div>-->
      <!--                <div class="item-name">图片</div>-->
      <!--                <div class="item-count">437 个文件</div>-->
      <!--              </div>-->
      <!--            </a-col>-->
      <!--            <a-col :xxl="6"-->
      <!--                   :xl="6"-->
      <!--                   :lg="6"-->
      <!--                   :md="6"-->
      <!--            >-->
      <!--              <div class="box-item">-->
      <!--                <div class="item-image item-type-document">-->
      <!--                  <img src="../assets/file/category/document.svg" alt="文档"/>-->
      <!--                </div>-->
      <!--                <div class="item-name">文档</div>-->
      <!--                <div class="item-count">437 个文件</div>-->
      <!--              </div>-->
      <!--            </a-col>-->
      <!--            <a-col :xxl="6"-->
      <!--                   :xl="6"-->
      <!--                   :lg="6"-->
      <!--                   :md="6"-->
      <!--            >-->
      <!--              <div class="box-item">-->
      <!--                <div class="item-image item-type-voice">-->
      <!--                  <img src="../assets/file/category/voice.svg" alt="音频"/>-->
      <!--                </div>-->
      <!--                <div class="item-name">音频</div>-->
      <!--                <div class="item-count">437 个文件</div>-->
      <!--              </div>-->
      <!--            </a-col>-->
      <!--            <a-col :xxl="6"-->
      <!--                   :xl="6"-->
      <!--                   :lg="6"-->
      <!--                   :md="6"-->
      <!--            >-->
      <!--              <div class="box-item">-->
      <!--                <div class="item-image item-type-video">-->
      <!--                  <img src="../assets/file/category/video.svg" alt="视频"/>-->
      <!--                </div>-->
      <!--                <div class="item-name">视频</div>-->
      <!--                <div class="item-count">437 个文件</div>-->
      <!--              </div>-->
      <!--            </a-col>-->
      <!--          </a-row>-->
      <!--        </div>-->
      <!--      </div>-->
      <a-empty class="empty-box"
               v-if="data.files.length === 0"
               img-src="/src/assets/icons/full/empty-data.svg"
               description="暂无文件，请上传文件"
      />
      <file-box class="file-list-box"
                v-else
                :file-list="data.files"
                @load-more="loadMoreFile"
                @select-change="selectChange"
                @action-change="actionResult"
      ></file-box>
    </div>
  </div>
  <dashboard-right :breads="data.breads"/>
</template>

<script>
import dashboardRight from '../components/DashboardRight.vue'
import FileBox from '../components/FileBox.vue'
import { reactive } from 'vue'
import http from '../api/http.js'
import emitter from '../utils/emitter.js'
import { useRouter } from 'vue-router'

export default {
  name: 'DashboardPage',
  components: {
    dashboardRight,
    FileBox
  },
  setup: function () {
    const router = useRouter()
    const data = reactive({
      breads: [], // 文件面包屑导航数据
      files: [], // 文件列表
      pager: {
        fileId: '', // 起始点文件 id
        size: 10, // 每次请求的数据量
        keyword: '', // 搜索的关键词
        hasMore: true // 是否有更多的文件
      }
    })
    // 查询文件面包屑导航数据
    const queryBreads = () => {
      const query = router.currentRoute.value.query
      let { id } = query
      if (id === undefined) {
        id = ''
      }
      const param = { id }
      http.req(http.url.file.breads, http.methods.get, param).then(response => {
        data.breads = []
        for (const key in response) {
          data.breads.push(response[key])
        }
        const { id } = response[response.length - 1]
        const path = router.currentRoute.value.path
        router.push({
          path,
          query: { id }
        })
        queryFiles()
      })
    }
    // 查询文件列表数据
    const queryFiles = () => {
      let pid = ''
      if (data.breads.length > 0) {
        pid = data.breads[data.breads.length - 1].id
      }
      const param = {
        pid,
        fileId: data.pager.fileId,
        size: data.pager.size,
        fileName: data.pager.keyword
      }
      http.req(http.url.file.list, http.methods.get, param).then(response => {
        if (response.length === 0) {
          data.pager.hasMore = false
          return
        }
        for (const key in response) {
          data.files.push(response[key])
        }
      })
    }
    // 监听新建文件夹
    emitter.on('mkdir-change', record => {
      data.files.push(record)
    })
    // 监听上传文件
    emitter.on('upload-change', record => {
      console.log(record)
      for (const key in record) {
        data.files.push(record[key])
      }
    })
    return {
      router,
      data,
      queryBreads,
      queryFiles
    }
  },
  created () {
    this.queryBreads()
  },
  methods: {
    // 加载更多文件
    loadMoreFile () {
      if (!this.data.pager.hasMore) {
        return
      }
      this.data.pager.fileId = this.data.files[this.data.files.length - 1].id
      this.queryFiles()
    },
    // 跳转到某上层目录
    returnParent (record, recordIndex) {
      const { id } = record
      const path = this.router.currentRoute.value.path
      this.router.push({
        path,
        query: { id }
      })
      const spliceLength = this.data.breads.length - recordIndex - 1
      this.data.breads.splice(recordIndex + 1, spliceLength)
      this.data.files = []
      this.queryFiles()
    },
    // 选择文件
    selectChange (record) {
      const { id, name, type } = record
      if (type === 0) {
        return
      }
      const current = this.router.currentRoute.value.path
      this.router.push({
        path: current,
        query: { id }
      })
      this.data.breads.push({ id, name })
      this.data.files = []
      this.queryFiles()
    },
    // 文件操作的结果
    actionResult (record) {
      const { action, fileIds } = record
      // 删除文件，移动文件
      if (action === 'delete' || action === 'move') {
        for (const key in fileIds) {
          const fileIndex = this.data.files.findIndex(file => file.id === fileIds[key])
          this.data.files.splice(fileIndex, 1)
        }
      }
    }
  }
}
</script>

<style scoped lang="scss">
.dashboard-page {
  width: calc(100% - 258px - 450px);
  .dashboard-center--box {
    margin: 50px auto 0;
    width: 94%;
    .file-search-box {
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
    .file-breads-box {
      margin: 30px 0 20px;
    }
    .empty-box {
      margin: 50px 0;
    }
    //.file-category-box {
    //  margin: 50px auto 0;
    //  .box-title {
    //    margin: 20px 0;
    //    font-size: 16px;
    //    color: #374670;
    //  }
    //  .box-container {
    //    .box-item {
    //      margin: 0 28px;
    //      padding: 20px;
    //      border: 1px solid #eaeaea;
    //      border-radius: 24px;
    //      cursor: pointer;
    //      &:first-child {
    //        margin-left: 0;
    //      }
    //      &:last-child {
    //        margin-inside: 0;
    //      }
    //      .item-image {
    //        width: 60px;
    //        height: 60px;
    //        display: flex;
    //        align-items: center;
    //        justify-content: center;
    //        border-radius: 12px;
    //        &.item-type-image {
    //          background-color: #f7f6ff;
    //        }
    //        &.item-type-document {
    //          background-color: #ddeffc;
    //        }
    //        &.item-type-voice {
    //          background-color: #fff8df;
    //        }
    //        &.item-type-video {
    //          background-color: #e2f7e8;
    //        }
    //        img {
    //          width: 36px;
    //          height: 36px;
    //        }
    //      }
    //      .item-name {
    //        margin: 20px 0 10px;
    //        font-size: 20px;
    //        font-weight: bolder;
    //      }
    //      .item-count {
    //        font-weight: bolder;
    //        color: #92929d;
    //      }
    //    }
    //  }
    //}
  }
}
</style>
