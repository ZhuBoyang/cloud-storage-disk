<template>
  <div class="video-layer" v-show="visible"></div>
  <div class="video" v-show="visible">
    <div class="player-box" ref="player" id="player"></div>
    <div class="box-layer" @click="playAndPause"></div>
    <div class="box-loading" v-show="controls.isLoading">
      <icon-loading class="box-loading-icon"/>
    </div>
    <div class="box-close-action" :class="[{'is-show': controls.closeActionIsShow}]">
      <img :src="apiConfig().iconBaseUrl + 'video/close.png'" alt="close" @click="closePlayer"/>
    </div>
    <div class="box-controls" :class="[{'is-show': controls.controlIsShow}]">
      <div class="controls-left">
        <div class="control-item control-play" v-if="controls.paused">
          <img :src="apiConfig().iconBaseUrl + 'video/play.png'" alt="play" @click="playAndPause"/>
        </div>
        <div class="control-item control-mute" v-else>
          <img :src="apiConfig().iconBaseUrl + 'video/pause.png'" alt="pause" @click="playAndPause"/>
        </div>
        <div class="control-item control-prev">
          <img :src="apiConfig().iconBaseUrl + 'video/prev.png'" alt="prev"/>
        </div>
        <div class="control-item control-backward">
          <img :src="apiConfig().iconBaseUrl + 'video/fast_backward.png'" alt="backward" @click="backwardFiveSeconds"/>
        </div>
        <div class="control-item control-forward">
          <img :src="apiConfig().iconBaseUrl + 'video/fast_forward.png'" alt="forward" @click="forwardFiveSeconds"/>
        </div>
        <div class="control-item control-next">
          <img :src="apiConfig().iconBaseUrl + 'video/next.png'" alt="next"/>
        </div>
      </div>
      <div class="controls-right">事件控制</div>
    </div>
  </div>
  <div class="video-list" v-show="visible">
    <div class="video-item" v-for="o in playList" :key="o.id">
      <div class="video-avatar">
        <img :src="apiConfig().iconBaseUrl + 'video/play.png'" alt="avatar"/>
        <div class="video-upload-time">{{ globalProperties.common.formatDate(o.uploadTime) }}</div>
      </div>
      <div class="video-info">
        <div class="video-name">{{ o.name }}</div>
        <div class="video-duration">{{ o.duration }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import DPlayer from 'dplayer'
import apiConfig from '../api/apiConfig.js'
import { getCurrentInstance, reactive, toRefs, watch } from 'vue'
import http from '../api/http.js'
import { IconLoading } from '@arco-design/web-vue/es/icon'

export default {
  name: 'VideoPlayer',
  components: {
    IconLoading
  },
  props: {
    // 视频播放地址查询接口
    url: {
      type: String,
      default: ''
    },
    // 视频文件 id
    videoId: {
      type: String,
      default: ''
    },
    // 播放列表
    playList: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  emits: ['on-close'],
  setup (props, context) {
    const { globalProperties } = getCurrentInstance().appContext.config
    const { emit } = context
    const dataList = reactive({
      visible: false, // 是否显示播放器
      dp: null, // 播放器实体
      controls: { // 控制栏
        isLoading: true, // 视频是否正在加载中
        paused: true, // 是否暂停
        controlIsShow: true, // 是否显示控制栏
        closeActionIsShow: true // 是否显示关闭按钮
      }
    })
    // 根据视频 id 获取视频播放地址
    const playUrl = id => {
      http.req(props.url, http.methods.post, { id }).then(url => {
        setTimeout(() => {
          videoInit(apiConfig().apiBaseUrl + url)
        }, 5000)
      })
    }
    // 初始化并播放视频
    const videoInit = url => {
      dataList.dp.switchVideo({ url })
      dataList.dp.play()
      dataList.controls.isLoading = false
      dataList.controls.paused = false
    }
    // 停止播放视频，并关闭视频播放器
    const videoClose = () => {
      dataList.controls.paused = true
      dataList.visible = false
      dataList.dp.switchVideo({ url: '' })
      dataList.controls.isLoading = true
    }
    watch(() => props.videoId, videoId => {
      if (videoId === '') {
        videoClose()
      } else {
        dataList.visible = true
        playUrl(videoId)
      }
    })
    return {
      globalProperties,
      emit,
      ...toRefs(dataList),
      videoClose
    }
  },
  mounted () {
    this.initialPlayer()
  },
  methods: {
    apiConfig,
    initialPlayer () {
      // eslint-disable-next-line no-new
      this.dp = new DPlayer({
        container: document.getElementById('player'),
        autoplay: false, // 是否自动播放
        theme: '#0093ff', // 主题色
        loop: true, // 视频是否循环播放
        lang: 'zh-cn',
        screenshot: false, // 是否开启截图
        hotkey: false, // 是否开启热键
        preload: 'auto', // 视频是否预加载
        volume: 0.7, // 默认音量
        mutex: false, // 阻止多个播放器同时播放，当前播放器播放时暂停其他播放器
        video: {
          // url: 'http://localhost:8100/word.mp4', // 视频地址
          // type: 'customHls'
          // customType: {
          //   customHls: (video, player) => {
          //     // console.log("查看传递的参数", video, player);
          //     const hls = new Hls() // 实例化Hls  用于解析m3u8
          //     hls.loadSource(video.src)
          //     hls.attachMedia(video)
          //   }
          // }
        }
      })

      // 删除播放器自带的控制栏
      const classes = ['dplayer-controller', 'dplayer-controller-mask']
      for (let i = 0; i < classes.length; i++) {
        const dplayerController = document.getElementsByClassName(classes[i])[0]
        dplayerController.remove()
      }
    },
    // 关闭播放器
    closePlayer () {
      this.emit('on-close')
    },
    // 播放与暂停
    playAndPause () {
      if (this.dp === null || this.videoId.trim() === '') {
        return
      }
      if (this.controls.paused) {
        this.dp.play()
        this.controls.paused = false
      } else {
        this.dp.pause()
        this.controls.paused = true
      }
    },
    // 快退 5 秒
    backwardFiveSeconds () {
      let currentTime = this.dp.video.currentTime - 5
      if (currentTime < 0) {
        currentTime = 0
      }
      this.dp.video.currentTime = currentTime
    },
    // 快进 5 秒
    forwardFiveSeconds () {
      let currentTime = this.dp.video.currentTime + 5
      if (currentTime > this.dp.video.duration) {
        currentTime = this.dp.video.duration
      }
      this.dp.video.currentTime = currentTime
    },
    // 鼠标滑入播放器区域
    mouseInPlayer () {
      this.controls.controlIsShow = true
      this.controls.closeActionIsShow = true
    },
    // 鼠标画出播放器区域
    mouseOutPlayer () {
      this.controls.controlIsShow = false
      this.controls.closeActionIsShow = false
    }
  }
}
</script>

<style scoped lang="scss">
.video-layer {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background-color: rgba(0, 0, 0, .8);
  z-index: 3;
}
.video {
  position: fixed;
  top: 50%;
  left: 50%;
  width: 50%;
  height: 80vh;
  z-index: 4;
  transform: translate(-50%, -50%);
  border-radius: 20px;
  box-shadow: 0 0 10px #0e0e0e;
  overflow: hidden;
  .player-box {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }
  .box-layer {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }
  .box-loading {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #000000;
    .box-loading-icon {
      color: #ffffff;
      font-size: 40px;
    }
  }
  .box-close-action {
    position: absolute;
    top: 20px;
    right: 20px;
    border-radius: 50%;
    box-shadow: 0 0 10px #8a8a8a;
    opacity: 0;
    transition: all .3s;
    &.is-show {
      opacity: 1;
      transition: all .3s;
    }
    img {
      width: 30px;
      height: 30px;
      cursor: pointer;
    }
  }
  .box-controls {
    position: absolute;
    bottom: -50px;
    left: 0;
    padding: 0 10px;
    width: calc(100% - 20px);
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: #ffffff;
    font-size: 20px;
    background-color: rgba(0, 0, 0, .4);
    transition: all .3s;
    &.is-show {
      bottom: 0;
      transition: all .3s;
    }
    .controls-left {
      display: flex;
      & > .control-item {
        margin: 0 5px;
        width: 25px;
        height: 25px;
        img {
          width: 100%;
          height: 100%;
          filter: invert(100%);
          cursor: pointer;;
        }
      }
    }
  }
}
.video-list {
  position: fixed;
  top: 50%;
  left: calc(75% + 10px);
  width: 300px;
  height: 80vh;
  background-color: #000000;
  border-radius: 20px;
  transform: translate(0, -50%);
  z-index: 3;
  .video-item {
    display: flex;
    height: 100px;
    .video-avatar {
      position: relative;
      width: 100px;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      img {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        filter: invert(100%);
        cursor: pointer;
      }
      .video-upload-time {
        position: absolute;
        bottom: 4px;
        right: 6px;
        color: #ffffff;
        font-size: 12px;
        text-shadow: 0 0 10px #efefef;
      }
    }
    .video-info {
      margin: 0 10px;
      width: calc(100% - 120px);
      height: 100%;
      .video-name {
        margin-top: 15px;
        width: 100%;
        color: #ffffff;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        word-break: break-word;
        text-overflow: ellipsis;
        overflow: hidden;
      }
      .video-duration {
        margin-top: 20px;
        color: #8a8a8a;
      }
    }
  }
}
</style>
