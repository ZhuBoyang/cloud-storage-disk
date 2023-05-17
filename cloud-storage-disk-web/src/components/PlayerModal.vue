<template>
  <div class="player-modal" v-if="src !== ''">
    <div class="player--background">
      <div class="player--box"
           :style="{width: width === 0 ? '800px' : width + 'px', height: height === 0 ? '400px' : height + 'px'}"
      >
        <div class="player--box-close" @click="closePlayer">
          <img :src="apiConfig().iconBaseUrl + 'icons/Close_Square.png'" alt="关闭"/>
        </div>
        <video-player class="player-box" :src="src" autoplay/>
      </div>
    </div>
  </div>
</template>

<script>
import VideoPlayer from './custom/VideoPlayer.vue'
import apiConfig from '../api/apiConfig.js'

export default {
  name: 'PlayerModal',
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
    }
  },
  components: {
    VideoPlayer
  },
  emits: ['on-close'],
  setup (props, { emit }) {
    return {
      emit
    }
  },
  methods: {
    apiConfig,
    // 关闭播放器
    closePlayer () {
      this.emit('on-close')
    }
  }
}
</script>

<style scoped lang="scss">
.player-modal {
  .player--background {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, .3);
    z-index: 10;
    .player--box {
      position: relative;
      top: 50%;
      left: 50%;
      max-width: 800px;
      max-height: 400px;
      background-color: #000000;
      transform: translate(-50%, -50%);
      .player--box-close {
        position: absolute;
        top: -30px;
        right: 1px;
        width: 20px;
        height: 20px;
        img {
          width: 100%;
          height: 100%;
          filter: brightness(100);
          cursor: pointer;
        }
      }
    }
  }
}
</style>
