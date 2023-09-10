<template>
  <div class="video-player" ref="playerBox">
    <div class="video-player--box">
      <video ref="player"
             :controls="false"
             :src="src"
             @canplay="getVideoTotalPlayTime"
             @timeupdate="updateVideoCurrentTime"
      >您的浏览器不支持此播放模式，请升级浏览器后再试。</video>
    </div>
    <div class="video-player--controls">
      <div class="video-player--controls-actions">
        <div class="video-player--controls-action">
          <div class="video-player--controls-actions-btn row-col-center" @click="playOrPause">
            <img :src="apiConfig().iconBaseUrl + 'icons/video/' + (data.player.isPaused ? 'play' : 'pause') + '.png'" alt="播放"/>
          </div>
          <div class="video-player--controls-actions-btn row-col-center" @click="stopPlay">
            <img :src="apiConfig().iconBaseUrl + 'icons/video/stop.png'" alt="停止"/>
          </div>
          <div class="video-player--controls-actions-btn row-col-center">
            {{ formatSecondTime(data.player.currentTime) }}/{{ formatSecondTime(data.player.totalTime) }}
          </div>
        </div>
        <div class="video-player--controls-action">
          <div class="video-player--controls-actions-btn row-col-center" @click="mutedOrNot">
            <img :src="apiConfig().iconBaseUrl + 'icons/video/' + (data.player.isMuted ? 'mute' : 'volume') + '.png'" alt="音量"/>
          </div>
          <div class="video-player--controls-actions-btn row-col-center volume-control">
            <a-slider class="volume-control-btn" :default-value="data.player.volume" :model-value="data.player.volume" :min="0" :max="100" @change="changeVolume"/>
          </div>
          <div class="video-player--controls-actions-btn row-col-center" @click="fullScreenOrNot">
            <img :src="apiConfig().iconBaseUrl + 'icons/video/full_screen.png'" alt="全屏"/>
          </div>
        </div>
      </div>
      <div class="video-player--controls-process">
        <a-slider :model-value="data.player.currentTime" :min="0" :max="data.player.totalTime" @change="changeProcess"/>
      </div>
    </div>
  </div>
</template>

<script>
import { getCurrentInstance, reactive } from 'vue'
import apiConfig from '../../api/apiConfig.js'

export default {
  name: 'VideoPlayers',
  props: {
    src: {
      type: String,
      default: ''
    },
    width: {
      type: Number,
      default: 0
    },
    height: {
      type: Number,
      default: 0
    },
    autoplay: {
      type: Boolean,
      default: false
    }
  },
  setup () {
    const player = getCurrentInstance().proxy.$refs.player
    const playerBox = getCurrentInstance().proxy.$refs.playerBox
    const data = reactive({
      player: {
        isPaused: true, // 当前视频播放的装填
        currentTime: 0, // 当前播放时间
        totalTime: 0, // 视频总时长
        isMuted: false, // 是否静音
        process: 0, // 播放的进度
        volume: 100 // 当前播放的音量
      }
    })
    return {
      player,
      playerBox,
      data
    }
  },
  mounted () {
    this.player.controls = false
    this.player.volume = this.data.player.volume / 100
    if (this.autoplay) {
      this.player.play()
      this.data.player.isPaused = false
    }
  },
  methods: {
    apiConfig,
    // 获取视频总时长
    getVideoTotalPlayTime () {
      this.data.player.totalTime = this.player.duration
    },
    // 后去视频当前播放到的时间位置
    updateVideoCurrentTime () {
      if (this.player !== null) {
        this.data.player.currentTime = this.player.currentTime
      }
    },
    // 播放与暂停
    playOrPause () {
      if (this.data.player.isPaused) {
        this.player.play()
        this.data.player.isPaused = false
      } else {
        this.player.pause()
        this.data.player.isPaused = true
      }
    },
    // 停止播放
    stopPlay () {
      this.player.pause()
      this.data.player.isPaused = true
      this.player.currentTime = 0
    },
    // 静音与放开声音
    mutedOrNot () {
      this.data.player.isMuted = !this.data.player.isMuted
      this.player.muted = !this.player.muted
    },
    // 切换全屏
    fullScreenOrNot () {
      document.webkitIsFullScreen || document.fullscreen ? this.exitFullScreen() : this.openFullScreen(this.playerBox)
    },
    // 开启全屏
    openFullScreen (element) {
      if (element.requestFullscreen) {
        element.requestFullscreen()
      } else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen()
      } else if (element.webkitRequestFullscreen) {
        element.webkitRequestFullscreen()
      } else if (element.msRequestFullscreen) {
        element.msRequestFullscreen()
      }
    },
    // 拖动修改视频播放进度
    changeProcess (record) {
      this.player.currentTime = record
      this.data.player.currentTime = record
    },
    // 调节音量
    changeVolume (record) {
      this.player.volume = record / 100
      this.data.player.volume = record
      this.data.player.isMuted = record === 0
    },
    // 退出全屏
    exitFullScreen () {
      if (document.exitFullscreen) {
        document.exitFullscreen()
      } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen()
      } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen()
      }
    },
    // 将秒转为 时:分:秒 的格式
    formatSecondTime (second) {
      let hour = Math.floor(second / 3600)
      let minute = Math.floor(second % 3600 / 60)
      let seconds = Math.floor(second % 60)
      hour = hour < 10 ? '0' + hour : hour
      minute = minute < 10 ? '0' + minute : minute
      seconds = seconds < 10 ? '0' + seconds : seconds
      return `${hour}:${minute}:${seconds}`
    }
  }
}
</script>

<style scoped lang="scss">
.video-player {
  position: relative;
  width: 100%;
  height: 100%;
  .video-player--box {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    video {
      width: 100%;
      height: 100%;
      object-fit: fill;
    }
  }
  .video-player--controls {
    position: fixed;
    bottom: 5vh;
    left: 50%;
    padding: 10px;
    width: 500px;
    background: rgba(0, 0, 0, .2);
    border-radius: 10px;
    transform: translateX(-50%);
    .video-player--controls-actions {
      display: flex;
      justify-content: space-between;
      .video-player--controls-action {
        display: flex;
        .video-player--controls-actions-btn {
          margin: 5px;
          color: #ffffff;
          font-weight: bolder;
          cursor: pointer;
          &:first-child {
            margin-left: 0;
          }
          &:last-child {
            margin-right: 0;
          }
          &.volume-control {
            width: 70px;
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
