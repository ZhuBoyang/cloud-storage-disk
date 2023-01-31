<template>
  <div class="video-player" ref="playerBox">
    <div class="video-player--box">
      <video ref="player"
             :controls="false"
             @canplay="getVideoTotalPlayTime"
             @timeupdate="updateVideoCurrentTime"
      >
        <source :src="src"/>
      </video>
    </div>
    <div class="video-player--controls">
      <div class="video-player--controls-actions">
        <div class="video-player--controls-action">
          <div class="video-player--controls-actions-btn row-col-center" @click="playOrPause">
            <img :src="'/src/assets/video/' + (data.player.isPaused ? 'play' : 'pause') + '.svg'" alt="播放"/>
          </div>
          <div class="video-player--controls-actions-btn row-col-center" @click="stopPlay">
            <img src="../../assets/video/stop.svg" alt="停止"/>
          </div>
          <div class="video-player--controls-actions-btn row-col-center">
            {{ formatSecondTime(data.player.currentTime) }}/{{ formatSecondTime(data.player.totalTime) }}
          </div>
        </div>
        <div class="video-player--controls-action">
          <div class="video-player--controls-actions-btn row-col-center" @click="mutedOrNot">
            <img :src="'/src/assets/video/' + (data.player.isMuted ? 'mute' : 'volume') + '.svg'" alt="音量"/>
          </div>
          <div class="video-player--controls-actions-btn row-col-center" @click="fullScreenOrNot">
            <img src="../../assets/video/full_screen.svg" alt="全屏"/>
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

export default {
  name: 'VideoPlayer',
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
        process: 0 // 播放的进度
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
    if (this.autoplay) {
      this.player.play()
      this.data.player.isPaused = false
    }
  },
  methods: {
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
