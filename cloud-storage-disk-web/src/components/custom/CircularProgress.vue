<template>
  <div class="circular-progress">
    <div class="circular-progress--percent">
      <div class="circular-progress--percent-number">{{ percent }}%</div>
      <div class="circular-progress--percent-label">已使用</div>
    </div>
    <svg viewBox="0 0 100 100" class="circular-progress--svg">
      <circle r="50"
              cx="50"
              cy="50"
              fill="transparent"
              class="circular-progress--background"
      ></circle>
      <circle cx="50"
              cy="50"
              fill="transparent"
              class="circular-progress--bar"
              stroke-linecap="round"
              :r="data.circle.r"
              :stroke-dasharray="data.dashArray"
              :stroke-dashoffset="dashOffset"
      ></circle>
    </svg>
  </div>
</template>

<script>
import { reactive } from 'vue'

export default {
  name: 'CircularProgress',
  props: {
    percent: {
      type: Number,
      default: 0
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
      return (1 - this.percent / 100) * this.data.dashArray
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
      stroke-width: 10px;
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
