<template>
  <transition name="file-operator-bg">
    <div class="file-operator-list" v-show="visible && !hideModalVisible"></div>
  </transition>
  <div class="file-operator-modal"
       :class="[{'is-close': !visible}]"
  >
    <div class="box-header">
      <div class="box-header--theme">文件{{ identifyOperationName(operationName) }}</div>
      <div class="box-header--close" @click="closeModal">
        <img :src="apiConfig().iconBaseUrl + 'icons/close_square.png'" alt="关闭"/>
      </div>
    </div>
    <div class="box-bread">
      <a-breadcrumb>
        <a-breadcrumb-item v-for="(o, i) in breads" :key="i">
          <a href="javascript:void(0)" @click="returnFolder(o, i)">{{ o.name }}</a>
        </a-breadcrumb-item>
      </a-breadcrumb>
    </div>
    <div class="box-body" v-if="dirs.length > 0">
      <div class="box-body--list">
        <div class="box-body--list-item"
             v-for="(o, i) in dirs"
             :key="i"
             @click="openFolder(o)"
        >{{ o.name }}
        </div>
      </div>
    </div>
    <div class="box-body-empty" v-else>
      <a-empty :img-src="apiConfig().iconBaseUrl + 'icons/empty_data.png'"/>
    </div>
    <div class="box-footer">
      <a-button type="primary" shape="round" @click="moveToCurrentFolder">{{ identifyOperationName(operationName) }}至此</a-button>
      <a-button type="dashed" shape="round" @click="cancelMove">取消</a-button>
    </div>
  </div>
</template>

<script>
import { reactive, toRefs, watch } from 'vue'
import http from '../api/http.js'
import apiConfig from '../api/apiConfig.js'

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
    const dataList = reactive({
      hideModalVisible: false, // 是否半隐藏上传文件的弹窗
      parentId: '', // 父级目录文件 id
      breads: [], // 面包屑数据
      dirs: [], // 文件夹列表
      pager: {
        pid: '', // 父级目录 id
        dirId: '', // 查询列表的起始点
        size: 10 // 每次查询的数据量
      }
    })
    // 查询文件夹的面包屑导航数据
    const queryBreads = () => {
      const id = dataList.breads.length === 0 ? '_' : dataList.breads[dataList.breads.length - 1].id
      http.reqUrl.file.breads({ id }).then(response => {
        dataList.breads = []
        for (let i = 0; i < response.length; i++) {
          dataList.breads.push(response[i])
        }
      })
    }
    // 查询目录下次一级所有的文件夹
    const queryDirs = () => {
      dataList.pager.pid = dataList.breads.length === 0 ? '_' : dataList.breads[dataList.breads.length - 1].id
      http.reqUrl.file.dirs(dataList.pager).then(response => {
        if (response.length > 0) {
          for (let i = 0; i < response.length; i++) {
            dataList.dirs.push(response[i])
          }
        }
      })
    }
    watch(() => props.visible, visible => {
      if (visible) {
        queryBreads()
        queryDirs()
      } else {
        dataList.parentId = ''
        dataList.breads = []
        dataList.dirs = []
        dataList.pager = {
          pid: '',
          dirId: '',
          size: 10
        }
      }
    })
    return {
      emit,
      ...toRefs(dataList),
      queryBreads,
      queryDirs
    }
  },
  methods: {
    apiConfig,
    // 进入文件夹
    openFolder ({ id, name }) {
      this.breads.push({ id, name })
      this.refreshDirs(id)
    },
    // 返回文件夹
    returnFolder ({ id }, recordIndex) {
      this.breads.splice(recordIndex + 1, this.breads.length - recordIndex)
      this.refreshDirs(id)
    },
    // 刷新文件夹列表
    refreshDirs (id) {
      this.dirs = []
      this.pager.dirId = id
      this.queryDirs()
    },
    // 移动至此
    moveToCurrentFolder () {
      this.emit('on-change', { action: 'confirm', id: this.breads[this.breads.length - 1].id })
    },
    // 取消移动
    cancelMove () {
      this.emit('on-change', { action: 'cancel' })
    },
    // 关闭弹窗
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
