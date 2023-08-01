<template>
  <div class="circular-progress">
    <div class="circular-progress--percent" v-if="percentVisible">
      <div class="circular-progress--percent-label">已使用</div>
      <div class="circular-progress--percent-number">{{ percent }}</div>
    </div>
    <svg viewBox="0 0 100 100" class="circular-progress--svg">
      <circle r="50"
              cx="50"
              cy="50"
              fill="transparent"
              class="circular-progress--background"
              :style="{strokeWidth: `${strokeWidthNumber}px`}"
      ></circle>
      <circle cx="50"
              cy="50"
              fill="transparent"
              class="circular-progress--bar"
              stroke-linecap="round"
              :r="data.circle.r"
              :stroke-dasharray="data.dashArray"
              :stroke-dashoffset="dashOffset"
              :style="{strokeWidth: `${strokeWidthNumber}px`}"
      ></circle>
    </svg>
  </div>
</template>

<script>
import { reactive } from 'vue'
import typeUtil from '../../tools/type.js'

export default {
  name: 'CircularProgress',
  props: {
    strokeWidthNumber: {
      type: Number,
      default: 10
    },
    percent: {
      type: [Number, String],
      default: ''
    },
    percentVisible: {
      type: Boolean,
      default: false
    }
  },
  setup () {
    const circle = {
      r: 50
    }
    const data = reactive({
      circle,
      dashArray: Math.PI * 2 * circle.r
    })
    return {
      data
    }
  },
  computed: {
    dashOffset () {
      if (!typeUtil.isNumber(this.percent)) {
        if (this.percent.length === 0) {
          return 0
        }
        let percent = this.percent.substring(0, this.percent.length - 1)
        if (typeUtil.isNumber(percent)) {
          return (1 - parseInt(this.percent) / 100) * this.data.dashArray
        }
        percent = percent.substring(0, percent.length - 1)
        return (1 - parseInt(this.percent) / 100) * this.data.dashArray
      }
      return (1 - parseInt(this.percent) / 100) * this.data.dashArray
    }
  }
}
</script>

<style lang="scss">
.circular-progress {
  position: relative;
  .circular-progress--percent {
    position: absolute;
    top: 50%;
    left: 50%;
    text-align: center;
    color: rgb(106, 75, 254);
    transform: translate(-50%, -50%);
    .circular-progress--percent-number {
      font-size: 24px;
      font-weight: bolder;
    }
    .circular-progress--percent-label {
      font-size: 18px;
      font-weight: bolder;
    }
  }
  .circular-progress--svg {
    circle {
      stroke: rgb(66, 66, 66);
      transform-origin: center;
      transform: scale(.9);
      &.circular-progress--background {
        stroke: rgb(233, 227, 255);
      }
      &.circular-progress--bar {
        stroke: rgb(106, 75, 254);
        transform: rotate(-90deg) scale(.9);
      }
    }
  }
}
</style>
