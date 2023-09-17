<template>
  <div class="video-layer" v-show="visible"></div>
  <div class="video"
       id="video"
       v-show="visible"
       :class="[{'is-full': controls.webpageIsFull}]"
       @mouseover="mouseInPlayer"
       @mouseout="mouseOutPlayer"
  >
    <div class="player-box" id="player"/>
    <div class="box-layer" @click="playAndPause"></div>
    <div class="box-loading" v-show="controls.isLoading">
      <icon-loading class="box-loading-icon"/>
    </div>
    <div class="box-close-action" :class="[{'is-show': controls.closeActionIsShow}]">
      <img :src="apiConfig().iconBaseUrl + 'video/close.png'" alt="close" @click="closePlayer"/>
    </div>
    <div class="box-controls" :class="[{'is-show': controls.controlIsShow}]">
      <div class="box-controls--process">
        <a-slider v-model="process.currentTime"
                  :max="process.duration"
                  :format-tooltip="formatCurrentTime"
                  @change="toPlayTime"
        />
      </div>
      <div class="box-controls--box">
        <div class="controls-left">
          <div class="control-item control-play" v-if="controls.paused">
            <img :src="apiConfig().iconBaseUrl + 'video/play.png'" alt="play" @click="playAndPause"/>
          </div>
          <div class="control-item control-mute" v-else>
            <img :src="apiConfig().iconBaseUrl + 'video/pause.png'" alt="pause" @click="playAndPause"/>
          </div>
          <div class="control-item control-prev">
            <img :src="apiConfig().iconBaseUrl + 'video/prev.png'" alt="prev" @click="switchPrevVideo"/>
          </div>
          <div class="control-item control-backward">
            <img :src="apiConfig().iconBaseUrl + 'video/fast_backward.png'"
                 alt="backward"
                 @click="backwardFiveSeconds"
            />
          </div>
          <div class="control-item control-forward">
            <img :src="apiConfig().iconBaseUrl + 'video/fast_forward.png'" alt="forward" @click="forwardFiveSeconds"/>
          </div>
          <div class="control-item control-next">
            <img :src="apiConfig().iconBaseUrl + 'video/next.png'" alt="next" @click="switchNextVideo"/>
          </div>
        </div>
        <div class="controls-right">
          <div class="video-volume-control" @mouseover="showVolumeRegulator" @mouseout="hideVolumeRegulator">
            <div class="control-icon">
              <img v-if="controls.muted"
                   :src="apiConfig().iconBaseUrl + 'video/mute.png'"
                   alt="volume"
                   @click="muteOrNot"
              />
              <img v-else
                   :src="apiConfig().iconBaseUrl + 'video/volume.png'"
                   alt="volume"
                   @click="muteOrNot"
              />
            </div>
            <div class="control-box" :class="[{'is-show': controls.muteIsShow}]">
              <div class="max-volume">100</div>
              <div class="control-box--process">
                <a-slider direction="vertical"
                          v-model:model-value="controls.volume"
                          :min="0"
                          :max="100"
                          @change="changeVolume"
                />
              </div>
            </div>
          </div>
          <div class="video-webpage-control" v-if="!controls.screenIsFull">
            <div class="control-icon">
              <img v-if="controls.webpageIsFull"
                   :src="apiConfig().iconBaseUrl + 'video/webpage_non_full.png'"
                   alt="webpage"
                   @click="outWebpageFull"
              />
              <img v-else
                   :src="apiConfig().iconBaseUrl + 'video/webpage_full.png'"
                   alt="webpage"
                   @click="inWebpageFull"
              />
            </div>
          </div>
          <div class="video-screen-control">
            <div class="control-icon">
              <img v-if="controls.screenIsFull"
                   :src="apiConfig().iconBaseUrl + 'video/screen_non_full.png'"
                   alt="webpage"
                   @click="outScreenFull"
              />
              <img v-else
                   :src="apiConfig().iconBaseUrl + 'video/screen_full.png'"
                   alt="webpage"
                   @click="inScreenFull"
              />
            </div>
          </div>
          <div class="video-list-control" v-if="!controls.webpageIsFull && !controls.screenIsFull">
            <div class="control-icon">
              <img v-if="controls.listIsShow"
                   :src="apiConfig().iconBaseUrl + 'video/list_shrink.png'"
                   alt="list"
                   @click="showOrHideVideoList"
              />
              <img v-else
                   :src="apiConfig().iconBaseUrl + 'video/list_unfold.png'"
                   alt="list"
                   @click="showOrHideVideoList"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="video-list" v-show="visible" :class="[{'is-show': controls.listIsShow}]">
    <div class="video-item" v-for="o in playList" :key="o.id" :class="[{'is-select': process.videoId === o.id}]">
      <div class="video-avatar">
        <div class="video-avatar-box">
          <img :src="identifyFileAvatar(o)" alt="avatar"/>
        </div>
        <div class="video-duration">{{ formatNumberToTime(o.duration) }}</div>
      </div>
      <div class="video-info">
        <div class="video-name">{{ o.name }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import DPlayer from 'dplayer'
import apiConfig from '../api/apiConfig.js'
import { reactive, toRefs, watch } from 'vue'
import http from '../api/http.js'
import { IconLoading } from '@arco-design/web-vue/es/icon'
import common from '../tools/common.js'

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
    const { emit } = context
    const dataList = reactive({
      visible: false, // 是否显示播放器
      dp: null, // 播放器实体
      process: { // 视频播放进度
        videoId: '', // 当前播放的视频 id
        currentTime: 0, // 当前播放进度
        duration: 0 // 视频总时长
      },
      default: { // 各数据的默认值
        volume: 70 // 默认音量
      },
      controls: { // 控制栏
        isLoading: true, // 视频是否正在加载中
        paused: true, // 是否暂停
        controlIsShow: true, // 是否显示控制栏
        closeActionIsShow: true, // 是否显示关闭按钮
        muteIsShow: false, // 是否显示音量控制器
        volume: 70, // 当前音量
        muted: false, // 是否静音
        listIsShow: false, // 是否显示视频播放列表
        webpageIsFull: false, // 是否网页全屏
        screenIsFull: false // 是否全屏
      }
    })
    watch(() => props.videoId, videoId => {
      dataList.process.videoId = videoId
    })
    return {
      emit,
      ...toRefs(dataList)
    }
  },
  mounted () {
    // 初始化播放器实例
    this.initPlayer(document.getElementById('player'))
    this.monitorVideoId()
  },
  methods: {
    apiConfig,
    // 识别文件的缩略图
    identifyFileAvatar (o) {
      return common.identifyFileAvatar(o)
    },
    formatNumberToTime (time) {
      return common.formatNumberToTime(time)
    },
    // 监控 props id 的变化
    monitorVideoId () {
      this.$watch('process.videoId', videoId => {
        if (videoId === '') {
          // 关闭播放器，停止视频播放
          this.stopPlay()
          this.destroyPlayer()
        } else {
          // 初始化播放器，并开始播放视频
          this.initPlayer(document.getElementById('player'))
          this.visible = true
          this.startPlay(videoId)
        }
      })
    },
    // 初始化播放器
    player (element) {
      return new DPlayer({
        container: element,
        autoplay: false, // 是否自动播放
        theme: '#0093ff', // 主题色
        loop: false, // 视频是否循环播放
        lang: 'zh-cn',
        screenshot: false, // 是否开启截图
        hotkey: false, // 是否开启热键
        preload: 'auto', // 视频是否预加载
        volume: this.controls.volume / 100, // 默认音量
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
    },
    // 初始化播放器实例
    initPlayer (element) {
      this.dp = this.player(element)
      // 删除播放器自带的控制栏
      const classes = ['dplayer-controller', 'dplayer-controller-mask']
      for (let i = 0; i < classes.length; i++) {
        const dPlayerController = document.getElementsByClassName(classes[i])[0]
        dPlayerController.remove()
      }
      // 设置事件绑定
      this.timeupdate()
      this.ended()
      this.canplay()
    },
    // 关闭播放器
    closePlayer () {
      this.emit('on-close')
    },
    // 销毁播放器实例
    destroyPlayer () {
      this.dp.destroy()
    },
    // 开始播放当前视频
    startPlay (id) {
      http.req(this.url, http.methods.post, { id }).then(url => {
        this.dp.switchVideo({ url: apiConfig().apiBaseUrl + url })
      })
    },
    // 播放器事件-视频可以开始播放了
    canplay () {
      this.dp.on('canplay', () => {
        this.controls.isLoading = false
        this.controls.paused = false
        this.process.currentTime = 0
        this.process.duration = this.dp.video.duration
        this.dp.play()
        this.dp.volume(this.controls.volume / 100, false, false)
      })
    },
    // 停止播放当前视频
    stopPlay () {
      // 修改播放器控制状态
      this.controls.paused = true
      this.visible = false
      this.controls.isLoading = true
    },
    // 播放与暂停
    playAndPause () {
      if (this.dp === null || this.process.videoId.trim() === '') {
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
      let currentTime = this.dp.video.currentTime
      currentTime -= 5
      if (currentTime < 0) {
        currentTime = 0
      }
      this.dp.video.currentTime = currentTime
    },
    // 快进 5 秒
    forwardFiveSeconds () {
      let currentTime = this.dp.video.currentTime
      currentTime += 5
      if (currentTime >= this.dp.video.duration) {
        currentTime = this.dp.video.duration
      }
      this.dp.video.currentTime = currentTime
    },
    // 上一个视频
    switchPrevVideo () {
      const currentIndex = this.playList.findIndex(o => this.process.videoId === o.id)
      if (currentIndex === 0) {
        common.notify.warning('亲，这已经是第一个视频了')
        return
      }
      const { id } = this.playList[currentIndex - 1]
      this.process.videoId = id
      this.startPlay(id)
    },
    // 下一个视频
    switchNextVideo () {
      const currentIndex = this.playList.findIndex(o => this.process.videoId === o.id)
      if (currentIndex === this.playList.length - 1) {
        common.notify.warning('亲，这已经是最后一个视频了')
        return
      }
      const { id } = this.playList[currentIndex + 1]
      this.process.videoId = id
      this.startPlay(id)
    },
    // 跳转指定播放时间
    toPlayTime (time) {
      this.dp.video.currentTime = time
    },
    // 格式化当前播放的时间
    formatCurrentTime () {
      if (this.dp === null) {
        return ''
      }
      return common.formatNumberToTime(this.dp.video.currentTime)
    },
    // 显示音量调节器
    showVolumeRegulator () {
      this.controls.muteIsShow = true
    },
    // 隐藏音量调节器
    hideVolumeRegulator () {
      this.controls.muteIsShow = false
    },
    // 调节音量
    changeVolume (volume) {
      this.controls.volume = volume
      this.dp.volume(volume / 100, false, false)
      this.default.volume = volume
      this.controls.muted = volume === 0
    },
    // 静音或开启声音
    muteOrNot () {
      if (this.controls.muted) {
        // 已经静音了，需要打开声音
        this.controls.muted = false
        this.controls.volume = this.default.volume
      } else {
        // 需要静音
        this.default.volume = this.controls.volume
        this.controls.muted = true
        this.controls.volume = 0
      }
      this.dp.volume(this.controls.volume / 100, false, false)
    },
    // 进入网页全屏
    inWebpageFull () {
      this.controls.webpageIsFull = true
    },
    // 退出网页全屏
    outWebpageFull () {
      this.controls.webpageIsFull = false
    },
    // 进入全屏模式
    inScreenFull () {
      this.controls.screenIsFull = true
      this.switchDomFullScreen(document.getElementById('video'))
    },
    // 退出全屏模式
    outScreenFull () {
      this.controls.screenIsFull = false
      this.switchDomNonFullScreen()
    },
    // 指定 DOM 全屏
    switchDomFullScreen (element) {
      if (element.requestFullscreen) { // 全屏
        element.requestFullscreen()
      } else if (element.mozRequestFullScreen) { // 兼容Firefox全屏
        element.mozRequestFullScreen()
      } else if (element.webkitRequestFullscreen) { // 兼容Chrome Safari Opera全屏
        element.webkitRequestFullscreen()
      } else if (element.msRequestFullscreen) { // 兼容IE Edge全屏
        element.msRequestFullscreen()
      }
    },
    // 退出 DOM 全屏
    switchDomNonFullScreen () {
      // 退出全屏
      if (document.exitFullscreen) {
        document.exitFullscreen()
      } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen()
      } else if (document.webkitCancelFullScreen) {
        document.webkitCancelFullScreen()
      } else if (document.msExitFullscreen) {
        document.msExitFullscreen()
      }
    },
    // 显示或隐藏视频列表
    showOrHideVideoList () {
      this.controls.listIsShow = !this.controls.listIsShow
    },
    mouseInPlayer () {
      this.controls.controlIsShow = true
      this.controls.closeActionIsShow = true
    },
    // 鼠标画出播放器区域
    mouseOutPlayer () {
      this.controls.controlIsShow = false
      this.controls.closeActionIsShow = false
    },
    // 播放时间更新事件
    timeupdate () {
      this.dp.on('timeupdate', () => {
        this.process.currentTime = this.dp.video.currentTime
        this.process.duration = this.dp.video.duration
      })
    },
    // 视频播放结束事件
    ended () {
      this.dp.on('ended', () => {
        this.playAndPause()
      })
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
  height: 60vh;
  z-index: 4;
  transform: translate(-50%, -50%);
  border-radius: 20px;
  box-shadow: 0 0 10px #0e0e0e;
  overflow: hidden;
  &.is-full {
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    transform: translate(0, 0);
  }
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
    width: 30px;
    height: 30px;
    background-color: #000000;
    border-radius: 50%;
    box-shadow: 0 0 10px #8a8a8a;
    opacity: 0;
    transition: all .3s;
    &.is-show {
      opacity: 1;
      transition: all .3s;
    }
    img {
      width: 100%;
      height: 100%;
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
    color: #ffffff;
    font-size: 20px;
    background-color: rgba(0, 0, 0, .4);
    transition: all .3s;
    &.is-show {
      bottom: 0;
      transition: all .3s;
    }
    .box-controls--box {
      display: flex;
      align-items: center;
      justify-content: space-between;
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
      .controls-right {
        display: flex;
        & > div {
          margin: 0 5px;
          width: 25px;
          height: 25px;
          .control-icon {
            img {
              width: 100%;
              height: 100%;
              cursor: pointer;
            }
          }
        }
        .video-volume-control {
          position: relative;
          .control-icon {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            img {
              filter: invert(100);
            }
          }
          .control-box {
            position: absolute;
            bottom: 40px;
            left: 0;
            width: 25px;
            height: 230px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            background-color: #25262a;
            opacity: 0;
            z-index: 2;
            transition: all .2s;
            &.is-show {
              bottom: 30px;
              opacity: 1;
              z-index: 3;
              transition: all .2s;
            }
            .max-volume {
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 12px;
            }
            .control-box--process {
              width: 100%;
              display: flex;
              align-items: center;
              justify-content: center;
              .arco-slider {
                height: 200px;
                min-width: 12px;
              }
            }
          }
        }
        .video-webpage-control,
        .video-screen-control {
          .control-icon {
            img {
              filter: invert(100);
            }
          }
        }
        .video-list-control {
          .control-icon {
            width: 100%;
            height: 100%;
          }
        }
      }
    }
  }
}
.video-list {
  position: fixed;
  top: 50%;
  left: calc(75% - 300px);
  width: 300px;
  height: 60vh;
  background-color: #000000;
  border-radius: 20px;
  transform: translate(0, -50%);
  z-index: 3;
  transition: all .3s;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  &::-webkit-scrollbar {
    display:none
  }
  &.is-show {
    left: calc(75% + 10px);
    transition: all .3s;
  }
  .video-item {
    padding: 0 10px;
    height: 100px;
    display: flex;
    &.is-select {
      .video-info {
        .video-name {
          color: #08B4B4;
        }
      }
    }
    .video-avatar {
      position: relative;
      width: 100px;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      .video-avatar-box {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        img {
          max-width: 100%;
          max-height: 100%;
          filter: invert(100%);
          cursor: pointer;
        }
      }
      .video-duration {
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
      display: flex;
      align-items: center;
      .video-name {
        width: 100%;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        color: #ffffff;
        font-size: 13px;
        word-break: break-word;
        text-overflow: ellipsis;
        overflow: hidden;
      }
    }
  }
}
</style>
