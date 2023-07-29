<template>
  <div class="trash">
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
      <a-empty class="empty-box"
               v-if="files.length === 0"
               description="您没有删除的文件哦"
               :img-src="apiConfig().iconBaseUrl + 'icons/empty_data.png'"
      />
      <file-box class="file-list-box"
                v-else
                :file-list="files"
                :actions="['rollback', 'cancel']"
                @action-change="actionResult"
      ></file-box>
      <div class="file-pagination row-col-center">
        <a-pagination v-model:current="pager.pageIndex" v-model:page-size="pager.pageSize" :total="pager.total" @change="changePage"/>
      </div>
    </div>
  </div>
</template>

<script>
import apiConfig from '../api/apiConfig.js'
import { useRouter } from 'vue-router'
import { defineAsyncComponent, reactive, toRefs } from 'vue'
import http from '../api/http.js'

const FileBox = defineAsyncComponent(() => import('../components/FileBox.vue'))

export default {
  name: 'TrashView',
  components: {
    FileBox
  },
  setup () {
    const router = useRouter()
    const dataList = reactive({
      files: [], // 文件列表
      pager: {
        pageIndex: 1, // 当前页码
        pageSize: 10, // 每页显示的数据量
        total: 0, // 总量
        name: '' // 搜索的关键词
      }
    })
    const queryTrashFiles = () => {
      http.reqUrl.file.trash(dataList.pager).then(response => {
        dataList.pager.total = response.total
        dataList.files = response.data
      })
    }
    return {
      router,
      ...toRefs(dataList),
      queryTrashFiles
    }
  },
  created () {
    this.queryTrashFiles()
  },
  methods: {
    apiConfig,
    // 切换页面
    changePage (pageIndex) {
      this.pager.pageIndex = pageIndex
      this.queryTrashFiles()
    },
    // 文件操作的结果
    actionResult (record) {
      const { action } = record
      if (action === 'rollback') {
        this.queryTrashFiles()
      }
    }
  }
}
</script>

<style scoped lang="scss">
.trash {
  .view-box {
    margin: 0 auto;
    padding-top: 50px;
    width: 94%;
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
    .empty-box {
      margin-top: 50px;
    }
    .file-list-box {
      margin-top: 50px;
    }
    .file-pagination {
      width: 100%;
      height: 50px;
    }
  }
}
</style>
