<template>
  <div class="hello-world">
    <input type="file" multiple="multiple" class="upload-file" ref="file" accept="*/*" @change="readFile($event)">
  </div>
</template>

<script>
import http from "../api/http.js";
// import { ref } from 'vue'
//
// defineProps({
//   msg: String,
// })
//
// const count = ref(0)
export default {
  name: 'HelloWorld',
  methods: {
    // 点击上传文件
    readFile(e) {
      const uploadFiles = e.target.files
      console.log(uploadFiles)
      for (let i = 0; i < uploadFiles.length; i++) {
        this.splitFileBlock(uploadFiles[i])
      }
      //   const uploadedFileLength = this.uploadList.length
      //   for (let i = 0; i < uploadFiles.length; i++) {
      //     const currentFile = uploadFiles[i]
      //     const fileFullName = currentFile.name
      //     const fileSuffix = fileFullName.substring(fileFullName.lastIndexOf('.'))
      //     const file = {
      //       fileName: fileFullName,
      //       fileSize: currentFile.size,
      //       fileSuffix: fileSuffix,
      //       process: 0,
      //       blockNumber: 0,
      //       status: 'normal'
      //     }
      //     this.uploadList.push(file)
      //     this.splitFileBlock(currentFile, uploadedFileLength + i)
      //   }
    },
    splitFileBlock(file) {
      const fileBlockList = []
      fileBlockList.push(file.slice(0, file.size))
      const blockList = this.generateUploadFormData(file, fileBlockList)
      this.sendRequest(blockList)
    },
    // 将文件分片生成form表单数据
    generateUploadFormData(file, fileBlockList) {
      const identifier = new Date().getTime()
      return fileBlockList.map((item, index) => {
        const formData = new FormData()
        formData.append('file', item, new Date().getTime().toString())
        formData.append('data', item)
        formData.append('chunkIndex', index + 1)
        formData.append('shardingSize', this.blockSize.toString())
        formData.append('chunkSize', item.size)
        formData.append('fileSize', file.size)
        formData.append('identifier', identifier.toString())
        formData.append('fileName', file.name)
        formData.append('chunkCount', fileBlockList.length)
        formData.append('shard', false)
        return {formData: formData}
      })
    },
    // 发送上传文件的请求
    sendRequest(blocks) {
      const header = {'Content-Type': 'multipart/form-data'}
      for (let i = 0; i < blocks.length; i++) {
        http.req(http.url.uploadBlocks, http.methods.form, blocks[i].formData, header).then(response => {
          console.log(response)
        })
      }
    }
  }
}
</script>

<style scoped lang="scss">
.read-the-docs {
  color: #888;
}
</style>
