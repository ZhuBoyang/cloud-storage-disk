<template>
  <div class="office-layer" ref="officePlayer" :class="[{'is-show': visible}]"></div>
  <div class="office-landscape"
       ref="officeLandscape"
       :class="[{'is-show': visible}]"
       @mouseover="showControls"
       @mouseout="hideControls"
  >
    <div class="office-box" v-if="Object.keys(metadata).length > 0">
      <div class="page-item office-word-pdf"
           v-if="isShowDocumentsActions()"
      >
        <div class="item-avatar" v-for="o in play.pageTotal" :key="o">
          <img :src="`${apiConfig().iconBaseUrl}snapshot/${metadata.id}/${o - 1}.png`" :alt="metadata.id"/>
        </div>
      </div>
      <div class="page-item office-ppt" v-if="isShowPptActions()">
        <img :src="`${apiConfig().iconBaseUrl}snapshot/${metadata.id}/${play.currentIndex}.png`" :alt="metadata.id"/>
      </div>
      <div class="page-item office-error" v-if="isShowError()">
        <a-empty>
          <template #image>
            <icon-exclamation-circle-fill :size="50"/>
          </template>
          <div class="error-exp">{{ recognizeErrExp() }}</div>
          <div class="error-msg">{{ metadata.extend.errMsg }}</div>
        </a-empty>
      </div>
      <div class="box-actions"
           v-if="isShowDocumentsActions()"
           :class="[{'is-show': control.visible}]"
      >
        <div class="action-display">
          <div class="item-label">共</div>
          <div class="item-total">{{ play.pageTotal }}</div>
          <div class="item-label">页</div>
        </div>
      </div>
      <div class="box-actions"
           v-if="isShowPptActions()"
           :class="[{'is-show': control.visible}]"
      >
        <div class="action-item">
          <img :src="apiConfig().iconBaseUrl + 'general/left.png'" alt="left" @click="changePrev"/>
        </div>
<!--        <div class="action-item">-->
<!--          <img :src="apiConfig().iconBaseUrl + 'general/blow_up.png'" alt="blow_up" @click="blowUp"/>-->
<!--        </div>-->
        <div class="action-display">
          <div class="item-current">{{ play.currentIndex + 1 }}</div>
          <div class="item-separator">/</div>
          <div class="item-total">{{ play.pageTotal }}</div>
        </div>
<!--        <div class="action-item">-->
<!--          <img :src="apiConfig().iconBaseUrl + 'general/reduce.png'" alt="reduce" @click="reduce"/>-->
<!--        </div>-->
        <div class="action-item">
          <img :src="apiConfig().iconBaseUrl + 'general/right.png'" alt="right" @click="changeNext"/>
        </div>
      </div>
      <div class="box-close-action" :class="[{'is-show': control.visible}]">
        <img :src="apiConfig().iconBaseUrl + 'player/close.png'" alt="close" @click="closePlayer"/>
      </div>
    </div>
  </div>
  <div class="office-sames-box"
       v-if="files.length > 0"
       :class="[{'is-show': visible}, {'is-display': action.visible}]"
  >
    <div class="office-item"
         v-for="o in files"
         :key="o.id"
         :class="[{'is-selected': o.id === metadata.id}]"
         @dblclick="changeDocument(o)"
    >
      <div class="item-avatar">
        <img :src="recognizeDocumentIcon(o)" alt="close"/>
      </div>
      <a-tooltip position="right" :content="o.name">
        <div class="item-name">{{ o.name }}{{ o.ext }}</div>
      </a-tooltip>
    </div>
  </div>
  <div class="office-sames-action"
       :class="[{'is-show': visible}, {'is-display': action.visible}]"
       @click="action.visible = !action.visible"
  >
    <icon-caret-left size="20" v-if="action.visible"/>
    <icon-caret-right size="20" v-else/>
  </div>
</template>

<script>
import apiConfig from '../api/apiConfig.js'
import { reactive, toRefs } from 'vue'
import { IconExclamationCircleFill, IconCaretLeft, IconCaretRight } from '@arco-design/web-vue/es/icon'
import common from '../tools/common.js'
import type from '../tools/type.js'

export default {
  name: 'DocumentPlayer',
  components: {
    IconExclamationCircleFill,
    IconCaretLeft,
    IconCaretRight
  },
  computed: {
    type () {
      return type
    }
  },
  props: {
    metadata: {
      type: Object,
      default: () => {
        return {}
      }
    },
    files: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  emits: ['on-change', 'on-close'],
  setup (props, context) {
    const { emit } = context
    const dataList = reactive({
      visible: false, // 是否显示播放器
      control: {
        visible: false // 是否显示控制栏
      },
      action: {
        visible: false // 是否显示同级目录下所有的文档
      },
      play: {
        currentIndex: -1, // 当前显示的页面图片
        pageTotal: 0 // 总页数
      }
    })
    return {
      emit,
      ...toRefs(dataList)
    }
  },
  mounted () {
    this.monitorDocument()
  },
  methods: {
    apiConfig,
    // 打开文档
    monitorDocument () {
      this.$watch(() => this.metadata, metadata => {
        if (Object.keys(metadata).length > 0) {
          this.play.currentIndex = 0
          const { extend } = metadata
          const { pageTotal } = extend
          this.play.pageTotal = pageTotal
          this.$refs.officePlayer.style.display = 'block'
          this.$refs.officeLandscape.style.display = 'block'
          setTimeout(() => {
            this.visible = true
          }, 1)
        } else {
          this.visible = false
          this.clearRecord()
          setTimeout(() => {
            this.$refs.officePlayer.style.display = 'none'
            this.$refs.officeLandscape.style.display = 'none'
          }, 310)
        }
      })
    },
    // 显示控制栏
    showControls () {
      this.control.visible = true
    },
    // 隐藏控制栏
    hideControls () {
      this.control.visible = false
    },
    // 切换至上一页
    changePrev () {
      if (this.play.currentIndex <= 0) {
        common.notify.warning('没有上一页了')
        return
      }
      this.play.currentIndex--
    },
    // // 放大
    // blowUp () {
    // },
    // // 缩小
    // reduce () {
    // },
    // 切换至下一页
    changeNext () {
      if (this.play.currentIndex >= this.play.pageTotal - 1) {
        common.notify.warning('没有下一页了')
        return
      }
      this.play.currentIndex++
    },
    // 切换文档
    changeDocument (o) {
      this.emit('on-change', o)
    },
    // 关闭 ppt 播放器
    closePlayer () {
      this.emit('on-close')
    },
    // 清理缓存
    clearRecord () {
      this.play.currentIndex = -1
      this.play.pageTotal = 0
    },
    // 是否显示非 ppt 的操作栏
    isShowDocumentsActions () {
      const metadata = this.metadata
      const { extend } = metadata
      const { errExp, errMsg } = extend
      if (errExp !== '' && errMsg !== '') {
        return false
      }
      return type.isWord(metadata.ext) || type.isPdf(metadata.ext) || type.isExcel(metadata.ext) || type.isTxt(metadata.ext)
    },
    // 是否显示非 ppt 的操作栏
    isShowPptActions () {
      const metadata = this.metadata
      const { extend } = metadata
      const { errExp, errMsg } = extend
      if (errExp !== '' && errMsg !== '') {
        return false
      }
      return type.isPpt(metadata.ext)
    },
    // 是否显示报错内容
    isShowError () {
      const metadata = this.metadata
      const { extend } = metadata
      const { errExp, errMsg } = extend
      return errExp !== '' && errMsg !== ''
    },
    // 识别报错内容（转为人能看懂的内容）
    recognizeErrExp () {
      const metadata = this.metadata
      const { extend } = metadata
      const { errExp } = extend
      if (errExp === 'BadPaddingException') {
        return '检测到此文档已被加密，目前系统不支持文档解密，因此暂不支持此类文件的预览'
      }
      return ''
    },
    // 识别文档的 icon 图标
    recognizeDocumentIcon (o) {
      const { extend } = o
      const { thumbnail } = extend
      if (thumbnail === '') {
        return common.identifyFileAvatar(o)
      }
      return apiConfig().apiBaseUrl + thumbnail
    }
  }
}
</script>

<style scoped lang="scss">
.office-layer {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  display: none;
  background-color: rgba(0, 0, 0, .8);
  opacity: 0;
  transition: opacity .3s;
  z-index: 3;
  &.is-show {
    opacity: 1;
    transition: opacity .3s;
  }
}
.office-landscape {
  position: fixed;
  top: 50%;
  left: 50%;
  width: 50vw;
  min-width: 520px;
  height: 25vw;
  min-height: 260px;
  display: none;
  background-color: #ffffff;
  border-radius: 10px;
  transform: translate(-50%, -60%);
  opacity: 0;
  transition: all .3s;
  z-index: 3;
  &.is-show {
    opacity: 1;
    transform: translate(-50%, -50%);
    transition: all .3s;
  }
  .office-box {
    position: relative;
    width: 100%;
    height: 100%;
    .page-item {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      border-radius: 10px;
      &.office-word-pdf {
        overflow-y: auto;
        &::-webkit-scrollbar {
          display: none;
        }
        .item-avatar {
          width: 100%;
          img {
            width: 100%;
          }
        }
      }
      &.office-ppt {
        overflow: hidden;
        img {
          width: 100%;
          height: 100%;
        }
      }
      &.office-error {
        display: flex;
        align-items: center;
        justify-content: center;
        .error-exp {
          margin: 10px 0 5px;
        }
      }
    }
    .box-actions {
      position: absolute;
      bottom: 0;
      left: 50%;
      display: flex;
      transform: translate(-50%, 0);
      opacity: 0;
      transition: all .3s;
      &.is-show {
        bottom: 10px;
        opacity: 1;
        transition: all .3s;
      }
      .action-item {
        margin: 0 10px;
        width: 30px;
        height: 30px;
        border-radius: 50%;
        border: 3px solid #000000;
        box-shadow: 0 0 10px #343434;
        cursor: pointer;
        img {
          margin: 5%;
          width: 90%;
          height: 90%;
          border-radius: 50%;
        }
      }
      .action-display {
        display: flex;
        align-items: center;
        & > div {
          margin: 0 5px;
          color: #ffffff;
          text-shadow: 0 0 5px #000000;
          &:first-child {
            margin-left: 0;
          }
          &:last-child {
            margin-right: 0;
          }
        }
      }
    }
    .box-close-action {
      position: absolute;
      top: 10px;
      right: 10px;
      width: 30px;
      height: 30px;
      opacity: 0;
      transition: all .3s;
      box-shadow: 0 0 10px #343434;
      border-radius: 50%;
      cursor: pointer;
      &.is-show {
        opacity: 1;
        transition: all .3s;
      }
      img {
        width: 100%;
        height: 100%;
        filter: invert(100%);
      }
    }
  }
}
.office-sames-box {
  position: fixed;
  top: 50%;
  left: -260px;
  width: 260px;
  height: 25vw;
  min-height: 260px;
  display: none;
  background-color: #f4f4f4;
  transform: translate(0, -50%);
  transition: left .3s;
  overflow-y: auto;
  z-index: 3;
  &::-webkit-scrollbar {
    display: none;
  }
  &.is-show {
    display: block;
  }
  &.is-display {
    left: 0;
    transition: left .3s;
  }
  .office-item {
    padding: 0 10px;
    width: calc(100% - 21px);
    height: 50px;
    display: flex;
    align-items: center;
    overflow: hidden;
    white-space: nowrap;
    cursor: pointer;
    &:hover {
      background-color: #ececec;
    }
    &.is-selected {
      border-left: 3px solid #64b47c;
      .item-name {
        color: #64b782;
      }
    }
    .item-avatar {
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      img {
        max-width: 100%;
        max-height: 100%;
      }
    }
    .item-name {
      margin-left: 10px;
      width: calc(100% - 70px);
      height: 100%;
      display: flex;
      align-items: center;
      overflow: hidden;
    }
  }
}
.office-sames-action {
  position: fixed;
  top: 50%;
  left: 0;
  width: 20px;
  height: 50px;
  display: none;
  color: #242933;
  background-color: #ffffff;
  cursor: pointer;
  transform: translate(0, -50%);
  transition: left .3s;
  z-index: 3;
  &.is-show {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  &.is-display {
    left: 260px;
    transition: left .3s;
  }
}
</style>
