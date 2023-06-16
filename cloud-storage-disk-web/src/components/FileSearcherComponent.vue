<template>
  <div class="file-searcher">
    <a-input class="file-search-input" placeholder="请输入关键词" size="large" allow-clear>
      <template #prefix>
        <img :src="apiConfig().iconBaseUrl + 'icons/search.png'" alt="search" width="20">
      </template>
    </a-input>
    <div class="file-search-filter-icon">
      <img :src="apiConfig().iconBaseUrl + 'icons/Filter.png'" alt="filter">
    </div>
  </div>
</template>

<script>
import apiConfig from '../api/apiConfig.js'

export default {
  name: 'FileSearcherComponent',
  methods: {
    apiConfig
  }
}
</script>

<style scoped lang="scss">
.file-searcher {
  display: flex;
  justify-content: space-between;
  .file-search-filter-icon {
    margin-left: 30px;
    width: 60px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #6a4bfe;
    border-radius: 14px;
    cursor: pointer;
    img {
      width: 28px;
      height: 28px;
      filter: brightness(100);
    }
  }
}
</style>
