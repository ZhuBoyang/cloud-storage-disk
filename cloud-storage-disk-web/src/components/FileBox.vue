<template>
  <div class="file-box">
    <div class="file-box--header">
      <div class="header--multiselect row-col-center">
        <a-checkbox value="1"></a-checkbox>
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
           :key="index"
           @click="clickFile(item)"
      >
        <div class="body--multiselect row-col-center">
          <a-checkbox value="1"></a-checkbox>
        </div>
        <div class="body--icon row-col-center">
          <div class="body--icon-img row-col-center">
            <img :src="globalProperties.$identifyFileIcon(item)" alt="文件"/>
          </div>
        </div>
        <div class="body--name">{{ item.name }}</div>
        <div class="body--category">{{ item.type === 1 ? '文件夹' : `${item.ext} 文件` }}</div>
        <div class="body--size">{{ globalProperties.$formatSizeInPerson(item.size) }}</div>
        <div class="body--actions row-col-center">
          <a-dropdown trigger="hover">
            <div class="body--actions-btn row-col-center">
              <img src="../assets/icons/more.svg" alt="更多">
            </div>
            <template #content>
              <a-doption>分享</a-doption>
              <a-doption>获取链接</a-doption>
              <a-doption>下载</a-doption>
              <a-doption>重命名</a-doption>
              <a-doption>复制</a-doption>
              <a-doption>删除</a-doption>
              <a-doption>添加到喜欢</a-doption>
              <a-doption>查看详情</a-doption>
            </template>
          </a-dropdown>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCurrentInstance } from 'vue'

export default {
  name: 'FileBox',
  props: {
    fileList: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  emits: ['load-more', 'select-change'],
  setup (props, { emit }) {
    const { appContext } = getCurrentInstance()
    const { config } = appContext
    const { globalProperties } = config
    return {
      globalProperties,
      emit
    }
  },
  methods: {
    // 元素滚动到底部
    scrollEvent (e) {
      if (e.srcElement.scrollTop + e.srcElement.clientHeight === e.srcElement.scrollHeight) {
        this.emit('load-more')
      }
    },
    // 点击文件
    clickFile (record) {
      const { id, name, type } = record
      this.emit('select-change', { id, name, type })
    }
  }
}
</script>

<style scoped lang="scss">
.file-box {
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
      &:hover {
        background-color: #f0edfe;
        transition: all .3s;
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
</style>
