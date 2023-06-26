<template>
  <div class="file-info-drawer"
       :class="[{'is-show': Object.keys(file).length > 0}]"
  >
    <login-user-action/>
    <div class="file-info-drawer--box">
      <div class="file-avatar" v-if="Object.keys(file).length > 0 && globalProperties.$type.isVideo(file.ext)">
        <div class="file-avatar--img">
          <img :src="apiConfig().iconBaseUrl + 'icons/Video.png'" alt=""/>
        </div>
        <div class="file-avatar--play-icon-box row-col-center">
          <div class="file-avatar--play-icon row-col-center cursor-pointer" @click="playVideo(file.id)">
            <img :src="apiConfig().iconBaseUrl + 'icons/video/play.png'" alt=""/>
          </div>
        </div>
      </div>
      <div class="file-info-item file-name">{{ Object.keys(file).length === 0 ? '' : file.name + '.' + file.ext }}</div>
      <div class="file-info-item file-upload-time">文件上传时间：{{ globalProperties.common.formatDateTime(file.uploadTime) }}</div>
      <div class="file-info-item file-size">文件大小：{{ globalProperties.common.formatSizeInPerson(file.size) }}</div>
      <a-button type="primary" shape="round" long @click="hideDrawer">隐藏</a-button>
    </div>
  </div>
</template>

<script>
import { defineAsyncComponent, getCurrentInstance } from 'vue'
import apiConfig from '../api/apiConfig.js'

const LoginUserAction = defineAsyncComponent(() => import('./LoginUserAction.vue'))

export default {
  name: 'FileInfoDrawer',
  props: {
    file: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  components: {
    LoginUserAction
  },
  emits: ['on-hide', 'on-play'],
  setup (props, { emit }) {
    const { appContext } = getCurrentInstance()
    const { globalProperties } = appContext.config
    return {
      globalProperties,
      emit
    }
  },
  methods: {
    apiConfig,
    // 隐藏信息弹窗
    hideDrawer () {
      this.emit('on-hide', {})
    },
    // 播放视频
    playVideo (id) {
      this.emit('on-play', this.file.id)
    }
  }
}
</script>

<style scoped lang="scss">
.file-info-drawer {
  position: fixed;
  top: 0;
  right: -100%;
  padding-top: 40px;
  width: 450px;
  height: 100%;
  background-color: #ffffff;
  z-index: 3;
  &.is-show {
    right: 0;
  }
  .file-info-drawer--box {
    margin: 0 10%;
    width: calc(100% - 20%);
    .file-avatar {
      position: relative;
      padding-bottom: 66%;
      width: 100%;
      height: 0;
      border-radius: 10px;
      .file-avatar--img {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        img {
          width: 100%;
          height: 100%;
          filter: blur(3px);
        }
      }
      .file-avatar--play-icon-box {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, .5);
        border-radius: 10px;
        .file-avatar--play-icon {
          width: 62px;
          height: 62px;
          background-color: #6a4bff;
          border-radius: 50%;
          img {
            margin-left: 5px;
            width: 37px;
            height: 37px;
          }
        }
      }
    }
    .file-info-item {
      margin: 24px 0;
      color: #364670;
      font-size: 18px;
      font-weight: 500;
      line-height: 21px;
    }
  }
}
</style>
