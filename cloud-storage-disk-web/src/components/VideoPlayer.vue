<template>
  <div class="video-layer" v-show="visible"></div>
  <div class="video" v-show="visible">
    <div class="player-box" ref="player" id="player"></div>
    <div class="box-layer" @click="playAndPause"></div>
    <div class="box-close-action">
      <img :src="apiConfig().iconBaseUrl + 'video/close.png'" alt="close" @click="closePlayer"/>
    </div>
    <div class="box-controls">
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
</template>

<script>
import DPlayer from 'dplayer'
import apiConfig from '../api/apiConfig.js'
import { reactive, toRefs, watch } from 'vue'

export default {
  name: 'VideoPlayer',
  props: {
    url: {
      type: String,
      default: ''
    },
    list: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  emits: ['on-close'],
  setup (props, context) {
    const { emit } = context
    const dataList = reactive({
      // 是否显示播放器
      visible: false,
      // 播放器
      dp: null,
      // 控制
      controls: {
        paused: true // 是否暂停
      }
    })
    watch(() => props.url, url => {
      if (url === '') {
        dataList.visible = false
        dataList.controls.paused = true
        dataList.dp.pause()
      } else {
        dataList.visible = true
        dataList.dp.switchVideo({ url })
        dataList.dp.play()
        dataList.controls.paused = false
      }
    })
    return {
      emit,
      ...toRefs(dataList)
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
      if (this.dp === null) {
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
  z-index: 3;
  transform: translate(-50%, -50%);
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
  .box-close-action {
    position: absolute;
    top: 0;
    right: -40px;
    img {
      width: 30px;
      height: 30px;
      cursor: pointer;
    }
  }
  .box-controls {
    position: absolute;
    bottom: 0;
    left: 0;
    padding: 0 10px;
    width: calc(100% - 20px);
    height: 50px;
    color: #ffffff;
    font-size: 20px;
    background-color: rgba(0, 0, 0, .4);
    display: flex;
    align-items: center;
    justify-content: space-between;
    .controls-left {
      display: flex;
      & > .control-item {
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
</style>
