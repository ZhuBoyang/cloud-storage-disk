<template>
  <div class="audio-player" :class="[{'is-show': player.visible}]">
    <div class="player-outbox">
      <div class="player-inbox">
        <div class="player-layer">
          <div class="music-box">
            <audio controls
                   ref="audio"
                   style="display: none;"
                   @canplay="canplay"
                   @timeupdate="updatePlayProcess"
                   @ended="endPlay"
            ></audio>
            <div class="music-icon">
              <img :src="apiConfig().iconBaseUrl + 'player/music.png'" alt="music-icon"/>
            </div>
            <div class="music-info">
              <div class="music-name">{{ Object.keys(player.info).length === 0 ? '' : player.info.extend.title }}</div>
              <div class="music-author">{{ Object.keys(player.info).length === 0 ? '' : player.info.extend.artist }}</div>
            </div>
            <div class="music-volume">
              <div class="volume-control-box">
                <div class="volume-control-icon" @click="mutedOrNot">
                  <img v-if="control.muted"
                       :src="apiConfig().iconBaseUrl + 'player/mute.png'"
                       alt="volume"
                  />
                  <img v-else
                       :src="apiConfig().iconBaseUrl + 'player/volume.png'"
                       alt="volume"
                  />
                </div>
                <div class="volume-control-process">
                  <a-slider direction="vertical"
                            v-model:model-value="control.volume"
                            :min="0"
                            :max="100"
                            @change="changeVolume"
                  />
                </div>
              </div>
            </div>
          </div>
          <div class="process-box">
            <div class="process-slide">
              <a-slider v-model="control.currenTime"
                        :min="0"
                        :max="control.duration"
                        :step="1"
                        :format-tooltip="formatNumberToTime"
                        @change="changeCurrentTime"
              />
            </div>
            <div class="process-tip">
              <div class="start-time">{{ formatNumberToTime(control.currenTime) }}</div>
              <div class="duration-time">{{ formatNumberToTime(Object.keys(player.info).length === 0 ? 0 : player.info.extend.duration) }}</div>
            </div>
          </div>
          <div class="control-box">
            <!-- 播放模式：随机播放，顺序播放，单首循环 -->
            <div class="control-box--play-mode" @click="switchPlayMode">
              <img :src="apiConfig().iconBaseUrl + `player/${control.mode}.png`" :alt="control.mode"/>
            </div>
            <div class="control-box--prev">
              <img :src="apiConfig().iconBaseUrl + 'player/prev.png'" alt="prev" @click="switchPrevAudio"/>
            </div>
            <div class="control-box--play_pause">
              <img v-if="control.isPause"
                   :src="apiConfig().iconBaseUrl + 'player/play.png'"
                   alt="music-play-control"
                   @click="playOrNot"
              />
              <img v-else
                   :src="apiConfig().iconBaseUrl + 'player/pause.png'"
                   alt="music-play-control"
                   @click="playOrNot"
              />
            </div>
            <div class="control-box--next">
              <img :src="apiConfig().iconBaseUrl + 'player/next.png'" alt="next" @click="switchNextAudio"/>
            </div>
            <div class="control-box--playlist">
              <img :src="apiConfig().iconBaseUrl + 'player/play_list.png'" alt="play_list"/>
              <div class="list-outbox">
                <div class="list-inbox">
                  <div class="list-box">
                    <div class="list-item"
                         v-for="(o, i) in playList"
                         :key="i"
                         :class="[{'is-select': player.info.id === o.id}]"
                         @dblclick="switchAudio(o)"
                    >
                      <div class="item-meta">
                        <div class="item-meta--audio-artist">{{ o.extend.artist }}</div>
                        <div class="item-meta--audio-name">{{ o.extend.title }}</div>
                      </div>
                      <div class="item-info">{{ formatNumberToTime(o.extend.duration) }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="close-control-box">
            <div class="close-control-line"></div>
            <div class="close-control-icon" @click="closePlayer">
              <img :src="apiConfig().iconBaseUrl + 'player/close.png'" alt="close"/>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiConfig from '../api/apiConfig.js'
import { reactive, toRefs } from 'vue'
import http from '../api/http.js'
import common from '../tools/common.js'

export default {
  name: 'AudioPlayer',
  props: {
    // 媒体类型
    mediaType: {
      type: String,
      default: ''
    },
    // 媒体文件播放地址查询接口
    url: {
      type: String,
      default: ''
    },
    // 音频文件 id
    audioId: {
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
      player: { // 播放器实例
        info: {}, // 当前播放的音频的信息
        visible: false // 是否显示播放器
      },
      control: { // 控制属性
        currenTime: 0, // 当前播放进度
        duration: 0, // 总时长
        mode: 'list_circle', // 播放模式：list_circle-列表循环；single_circle-单曲循环；play_in_order-顺序播放
        isPause: true, // 是否处于暂停状态
        volume: 0, // 当前播放器音量
        sourceVolume: 0, // 默认的音量
        muted: false, // 是否静音
        showList: false // 是否显示播放列表
      }
    })
    return {
      emit,
      ...toRefs(dataList)
    }
  },
  mounted () {
    this.autoPlay()
  },
  methods: {
    apiConfig,
    formatNumberToTime (time) {
      return common.formatNumberToTime(time)
    },
    // 初始化播放器实例，自动播放音频
    autoPlay () {
      const audioId = this.audioId
      if (audioId === '') {
        this.player.visible = false
        this.$refs.audio.src = ''
        this.control.isPause = true
      }
      if (audioId !== '') {
        this.player.visible = true
        this.startPlay(audioId)
      }
    },
    // 加载要播放的音频
    startPlay (id) {
      http.req(this.url, http.methods.post, { id }).then(meta => {
        this.$refs.audio.src = apiConfig().apiBaseUrl + meta.path
        this.player.info = meta
        this.control.duration = meta.extend.duration
        this.control.volume = this.$refs.audio.volume * 100
      })
    },
    // 视频加载完成，可以进行播放
    canplay () {
      this.$refs.audio.play()
      this.control.isPause = false
    },
    // 切换播放模式
    switchPlayMode () {
      if (this.control.mode === 'list_circle') {
        this.control.mode = 'single_circle'
        return
      }
      if (this.control.mode === 'single_circle') {
        this.control.mode = 'play_in_order'
        return
      }
      if (this.control.mode === 'play_in_order') {
        this.control.mode = 'list_circle'
      }
    },
    // 播放与暂停播放
    playOrNot () {
      if (this.control.isPause) {
        this.$refs.audio.play()
        this.control.isPause = false
      } else {
        this.$refs.audio.pause()
        this.control.isPause = true
      }
    },
    // 更新播放进度
    updatePlayProcess () {
      if (this.$refs.audio !== null) {
        this.control.currenTime = this.$refs.audio.currentTime
      }
    },
    // 切到指定播放进度
    changeCurrentTime (time) {
      this.control.currenTime = time
      this.$refs.audio.currentTime = time
    },
    // 切换上一首音频
    switchPrevAudio () {
      const { id } = this.player.info
      const currentIndex = this.playList.findIndex(o => o.id === id)
      if (currentIndex === 0) {
        common.notify.warning('当前已为第一首，无法继续切换')
        return
      }
      this.startPlay(this.playList[currentIndex - 1].id)
    },
    // 切换下一首音频
    switchNextAudio () {
      const { id } = this.player.info
      const currentIndex = this.playList.findIndex(o => o.id === id)
      if (currentIndex === this.playList.length - 1) {
        common.notify.warning('当前已为最后一首，无法继续切换')
        return
      }
      this.startPlay(this.playList[currentIndex + 1].id)
    },
    // 音频结束播放
    endPlay () {
      // 列表循环
      if (this.control.mode === 'list_circle') {
        const { id } = this.player.info
        const currentIndex = this.playList.findIndex(o => o.id === id)
        // 判断当前播放的音频文件是否是当前目录下最后一个音频文件。如果是，则从播放列表第一首继续播放；如果不是，则播放下一首
        const nextIndex = currentIndex < this.playList.length - 1 ? currentIndex + 1 : 0
        this.startPlay(this.playList[nextIndex].id)
      }
      // 单曲循环模式
      if (this.control.mode === 'single_circle') {
        this.$refs.audio.play()
      }
      // 顺序循环
      if (this.control.mode === 'play_in_order') {
        const { id } = this.player.info
        const currentIndex = this.playList.findIndex(o => o.id === id)
        if (currentIndex < this.playList.length - 1) {
          this.startPlay(this.playList[currentIndex + 1].id)
        } else {
          this.$refs.audio.pause()
          this.control.isPause = true
          this.control.currenTime = 0
        }
      }
    },
    // 改变音量
    changeVolume (volume) {
      this.control.volume = volume
      this.$refs.audio.volume = volume / 100
    },
    // 静音或解除静音
    mutedOrNot () {
      if (this.control.muted) {
        // 需要解除静音
        this.control.muted = false
        this.$refs.audio.volume = this.control.sourceVolume / 100
        this.control.volume = this.control.sourceVolume
      } else {
        // 需要设置为静音
        this.control.muted = true
        this.control.sourceVolume = this.control.volume
        this.$refs.audio.volume = 0
        this.control.volume = 0
      }
    },
    // 切换音频
    switchAudio ({ id }) {
      this.startPlay(id)
    },
    // 关闭音频播放器
    closePlayer () {
      this.emit('on-close')
    }
  }
}
</script>

<style scoped lang="scss">
.audio-player {
  position: fixed;
  top: 750px;
  left: 0;
  width: 258px;
  .player-outbox {
    position: relative;
    height: 135px;
    border-radius: 20px;
    z-index: 3;
    .player-inbox {
      position: absolute;
      top: -100%;
      left: 0;
      width: 100%;
      background: linear-gradient(to right, #b4bbf0, #dcdffa);
      border-radius: 20px;
      transition: all .3s;
      .player-layer {
        margin: 10px 15px;
        width: calc(100% - 30px);
        height: calc(100% - 20px);
        background: linear-gradient(to right, #808cfb, #c8d2fd);
        border-radius: 20px;
        .music-box {
          position: relative;
          padding: 10px 20px;
          height: 40px;
          display: flex;
          align-items: center;
          .music-icon {
            position: absolute;
            top: 10px;
            left: 20px;
            width: 30px;
            height: 30px;
            img {
              width: 100%;
              height: 100%;
              filter: invert(100%);
            }
          }
          .music-info {
            position: absolute;
            top: 10px;
            left: 50px;
            margin: 0 10px;
            width: calc(100% - 70px);
            height: 30px;
            .music-name {
              color: #ffffff;
              font-size: 14px;
              font-weight: bold;
              overflow: hidden;
              white-space: nowrap;
              text-overflow: ellipsis;
            }
            .music-author {
              margin-top: 10px;
              color: #ffffff;
              font-size: 12px;
              overflow: hidden;
              white-space: nowrap;
              text-overflow: ellipsis;
            }
          }
          .music-volume {
            position: absolute;
            top: 10px;
            right: 10px;
            width: 30px;
            height: 30px;
            .volume-control-box {
              position: relative;
              width: 100%;
              height: 100%;
              &:hover {
                .volume-control-process {
                  bottom: 35px;
                  opacity: 1;
                  transition: all .3s;
                }
              }
              .volume-control-icon {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                img {
                  width: 100%;
                  height: 100%;
                  cursor: pointer;
                }
              }
              .volume-control-process {
                position: absolute;
                left: 50%;
                bottom: 45px;
                margin-left: 5px;
                width: 12px;
                height: 100px;
                display: flex;
                justify-content: center;
                opacity: 0;
                transform: translate(-50%, 0);
                transition: all .3s;
                :deep(.arco-slider-track.arco-slider-track-vertical) {
                  margin: 0;
                  min-height: 100%;
                }
              }
            }
          }
        }
        .process-box {
          margin: 0 auto;
          width: 92%;
          .process-slide {
            height: 15px;
            display: flex;
            align-items: center;
          }
          .process-tip {
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: space-between;
          }
        }
        .control-box {
          padding: 10px 0 15px;
          width: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
          & > div {
            margin: 0 5px;
            width: 50px;
            height: 30px;
            display: flex;
            align-items: center;
            justify-content: center;
            img {
              width: 30px;
              height: 30px;
              cursor: pointer;
            }
          }
          .control-box--playlist {
            position: relative;
            &:hover {
              .list-outbox {
                width: 200px;
                transition: all .3s;
                .list-inbox {
                  .list-box {
                    left: 0;
                    transition: all .3s;
                  }
                }
              }
            }
            .list-outbox {
              position: absolute;
              left: 100%;
              bottom: 0;
              width: 0;
              height: 340px;
              border-radius: 15px;
              box-shadow: 0 0 10px #8f8f8f;
              transition: all .3s;
              overflow: hidden;
              .list-inbox {
                position: relative;
                width: 100%;
                height: 100%;
                .list-box {
                  position: absolute;
                  top: 0;
                  left: -100%;
                  width: 200px;
                  max-height: 340px;
                  background: linear-gradient(to right, #808cfb, #c8d2fd);
                  overflow-y: auto;
                  transition: all .3s;
                  &::-webkit-scrollbar {
                    display: none;
                  }
                  .list-item {
                    margin: 5px;
                    padding: 5px;
                    width: calc(100% - 20px);
                    height: 50px;
                    display: flex;
                    border-radius: 15px;
                    transition: all .3s;
                    cursor: pointer;
                    &.is-select,
                    &:hover {
                      background-color: #707df4;
                      transition: all .3s;
                    }
                    .item-meta {
                      width: 65%;
                      height: 100%;
                      color: #ffffff;
                      font-size: 14px;
                      .item-meta--audio-artist {
                        margin-top: 5px;
                        font-size: 14px;
                      }
                      .item-meta--audio-name {
                        margin-top: 10px;
                        font-size: 12px;
                        overflow: hidden;
                        white-space: nowrap;
                        text-overflow: ellipsis;
                      }
                    }
                    .item-info {
                      width: 35%;
                      height: 100%;
                      display: flex;
                      align-items: center;
                      font-size: 12px;
                    }
                  }
                }
              }
            }
          }
        }
        .close-control-box {
          position: relative;
          width: 100%;
          height: 0;
          .close-control-line {
            position: absolute;
            left: 50%;
            width: 3px;
            height: 30px;
            background: #000000;
            box-shadow: 0 0 10px #888888;
            transform: translate(-50%, 0);
          }
          .close-control-icon {
            position: absolute;
            left: 50%;
            top: 30px;
            width: 30px;
            height: 30px;
            box-shadow: 0 0 10px #888888;
            border-radius: 50%;
            transform: translate(-50%, 0);
            img {
              width: 100%;
              height: 100%;
              filter: invert(100%);
              cursor: pointer;
            }
          }
        }
      }
    }
  }
}
</style>
