<template>
  <transition name="file-operator-bg">
    <div class="file-operator-list" v-show="visible && !data.hideModalVisible"></div>
  </transition>
  <div class="file-operator-modal"
       :class="[{'is-close': !visible}]"
  >
    <div class="box-header">
      <div class="box-header--theme">文件{{ identifyOperationName(operationName) }}</div>
      <div class="box-header--close" @click="closeModal">
        <img :src="config.apiBaseUrl + 'icons/full/Close_Square.svg'" alt="关闭"/>
      </div>
    </div>
    <div class="box-bread">
      <a-breadcrumb>
        <a-breadcrumb-item v-for="(item, index) in data.breads" :key="index">
          <a href="javascript:void(0)" @click="returnFolder(index)">{{ item.name }}</a>
        </a-breadcrumb-item>
      </a-breadcrumb>
    </div>
    <div class="box-body" v-if="data.pager.list.length > 0">
      <div class="box-body--list">
        <div class="box-body--list-item"
             v-for="(item, index) in data.pager.list"
             :key="index"
             @click="openFolder(item)"
        >{{ item.name }}
        </div>
      </div>
    </div>
    <div class="box-body-empty" v-else>
      <a-empty :img-src="config.apiBaseUrl + 'icons/full/empty-data.svg'"/>
    </div>
    <div class="box-footer">
      <a-button type="primary" shape="round" @click="moveToCurrentFolder">{{ identifyOperationName(operationName) }}至此</a-button>
      <a-button type="dashed" shape="round" @click="cancelMove">取消</a-button>
    </div>
  </div>
</template>

<script>
import { reactive } from 'vue'
import http from '../api/http.js'
import config from '../api/config.js'

export default {
  name: 'FileOperatorModal',
  props: {
    // 是否显示上传文件的窗口
    visible: {
      type: Boolean,
      default: false
    },
    operationName: {
      type: String,
      default: ''
    }
  },
  emits: ['on-change'],
  setup (props, { emit }) {
    const data = reactive({
      hideModalVisible: false, // 是否半隐藏上传文件的弹窗
      parentId: '', // 父级目录文件 id
      breads: [], // 面包屑数据
      pager: {
        list: [], // 文件夹列表
        dirId: '', // 查询列表的起始点
        size: 10 // 每次查询的数据量
      }
    })
    // 查询文件夹的面包屑导航数据
    const queryBreads = () => {
      http.req(http.url.file.dirBreads, http.methods.get, {
        parentId: data.pager.dirId
      }).then(response => {
        data.breads = []
        for (const key in response) {
          data.breads.push(response[key])
        }
      })
    }
    // 查询目录下次一级所有的文件夹
    const queryDirs = () => {
      http.req(http.url.file.dirs, http.methods.get, {
        parentId: data.pager.dirId,
        size: data.pager.size
      }).then(response => {
        if (response.length === 0) {
          return
        }
        for (const key in response) {
          data.pager.list.push(response[key])
        }
      })
    }
    return {
      config,
      emit,
      data,
      queryBreads,
      queryDirs
    }
  },
  watch: {
    visible (newVal) {
      if (newVal) {
        this.queryBreads()
        this.queryDirs()
      } else {
        this.data.pager.list = []
        this.data.pager.dirId = ''
      }
    }
  },
  methods: {
    // 进入文件夹
    openFolder (record) {
      const { id, name } = record
      this.data.breads.push({ id, name })
      this.data.pager.list = []
      this.data.pager.dirId = id
      this.queryDirs()
    },
    // 返回文件夹
    returnFolder (recordIndex) {
      const bread = this.data.breads[recordIndex]
      this.data.breads.splice(recordIndex + 1, this.data.breads.length - recordIndex)
      this.data.pager.list = []
      const { id } = bread
      this.data.pager.dirId = id
      this.queryDirs()
    },
    // 移动至此
    moveToCurrentFolder () {
      const currentFolder = this.data.breads[this.data.breads.length - 1]
      const { id } = currentFolder
      this.emit('on-change', { action: 'confirm', id })
    },
    // 取消移动
    cancelMove () {
      this.emit('on-change', { action: 'cancel' })
    },
    // 官博弹窗
    closeModal () {
      this.emit('on-change', { action: 'close' })
    },
    // 识别操作名称
    identifyOperationName (action) {
      if (action === 'move') {
        return '移动'
      }
      if (action === 'copy') {
        return '复制'
      }
    }
  }
}
</script>

<style scoped lang="scss">
.file-operator-list {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background-color: rgba(0, 0, 0, .5);
  z-index: 10;
}
.file-operator-modal {
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
  &.is-close {
    top: 150%;
    transition: all .3s;
  }
  .box-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .box-header--theme {
      margin-left: 10px;
      font-size: 16px;
      font-weight: bolder;
    }
    .box-header--close {
      width: 28px;
      height: 28px;
      cursor: pointer;
      img {
        width: 100%;
        height: 100%;
      }
    }
  }
  .box-bread {
    margin: 10px;
  }
  .box-body {
    .box-body--list {
      .box-body--list-item {
        padding: 0 10px;
        width: calc(100% - 20px);
        height: 50px;
        display: flex;
        align-items: center;
        border-radius: 12px;
        cursor: pointer;
        transition: all .3s;
        &:hover {
          background-color: #f0edfe;
          transition: all .3s;
        }
      }
    }
  }
  .box-footer {
    width: 100%;
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: right;
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

.file-operator-bg-enter-active {
  animation: file-operator-bg-animation .5s;
}
.file-operator-bg-leave-active {
  animation: file-operator-bg-animation .5s reverse;
}
@keyframes file-operator-bg-animation {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}
</style>
