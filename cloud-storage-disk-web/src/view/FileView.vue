<template>
  <div class="file-view">
    <div class="view-box">
      <file-searcher-component/>
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
                @on-select="selectChange"
                @action-change="actionResult"
      ></file-box>
      <div class="file-pagination row-col-center">
        <a-pagination v-model:current="pager.pageIndex" v-model:page-size="pager.pageSize" :total="pager.total"/>
      </div>
    </div>
  </div>
</template>

<script>
import { defineAsyncComponent, reactive, toRefs } from 'vue'
import emitter from '../tools/emitter.js'
import { useRouter } from 'vue-router'
import apiConfig from '../api/apiConfig.js'
import http from '../api/http.js'

const FileSearcherComponent = defineAsyncComponent(() => import('../components/FileSearcherComponent.vue'))
const FileBox = defineAsyncComponent(() => import('../components/FileBox.vue'))

export default {
  name: 'FileView',
  components: {
    FileSearcherComponent,
    FileBox
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
      }
    })
    // 查询文件面包屑导航数据
    const queryBreads = () => {
      const { id } = router.currentRoute.value.params
      http.reqUrl.file.breads({ id }).then(response => {
        dataList.breads = []
        for (let i = 0; i < response.length; i++) {
          dataList.breads.push(response[i])
        }
      })
    }
    // 查询文件列表数据
    const queryFiles = () => {
      const pid = dataList.breads.length === 0 ? router.currentRoute.value.params.id : dataList.breads[dataList.breads.length - 1].id
      const param = { pid }
      for (const key in dataList.pager) {
        param[key] = dataList.pager[key]
      }
      http.reqUrl.file.pager(param).then(response => {
        const { total, data } = response
        dataList.files = data
        dataList.pager.total = total
      })
    }
    // 监听新建文件夹
    emitter.on('on-mkdir', () => {
      if (dataList.files.length === dataList.pager.pageSize) {
        dataList.pager.pageIndex++
      }
      queryFiles()
    })
    // 监听上传文件
    emitter.on('on-upload-close', () => {
      // 刷新页面中的文件列表
      if (dataList.files.length === dataList.pager.pageSize) {
        dataList.pager.pageIndex++
      }
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
    this.queryFiles()
  },
  methods: {
    apiConfig,
    // 跳转到某上层目录
    returnParent (record, recordIndex) {
      const { id } = record
      this.router.push(`${id}`)
      this.breads.splice(recordIndex + 1, this.breads.length - recordIndex - 1)
      this.queryFiles()
    },
    // 选择文件
    selectChange (record) {
      const { id, name, type } = record
      if (type === 0) {
        // if (commonType.isVideo(ext)) {
        //   this.router.push({
        //     path: 'player',
        //     query: {
        //       id
        //     }
        //   })
        // }
        return
      }
      this.router.push(`${id}`)
      this.breads.push({ id, name })
      this.queryFiles()
    },
    // 文件操作的结果
    actionResult (record) {
      const { action } = record
      // 删除文件，移动文件
      if (action === 'delete' || action === 'move') {
        for (const key in record.fileIds) {
          const fileIndex = this.data.files.findIndex(file => file.id === record.fileIds[key])
          this.data.files.splice(fileIndex, 1)
        }
      }
      // 文件重命名
      if (action === 'rename') {
        const fileIndex = this.data.files.findIndex(file => file.id === record.file.id)
        this.data.files[fileIndex] = record.file
      }
    }
  }
}
</script>

<style scoped lang="scss">
.file-view {
  width: calc(100% - 258px - 450px);
  height: 100%;
  .view-box {
    margin: 50px auto 0;
    width: 94%;
    height: calc(100% - 50px);
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
