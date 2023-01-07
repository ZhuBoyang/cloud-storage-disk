const identifyFileIcon = (file) => {
  const { ext, type } = file
  if (type === 1) {
    return '/src/assets/file/category/directory.svg'
  }
  if (ext === 'doc' || ext === 'docx') {
    return '/src/assets/file/category/docx.svg'
  }
  if (ext === 'ppt' || ext === 'pptx') {
    return '/src/assets/file/category/ppt.svg'
  }
  if (ext === 'xls' || ext === 'xlsx') {
    return '/src/assets/file/category/xls.svg'
  }
  if (ext === 'java') {
    return '/src/assets/file/category/java.svg'
  }
  if (ext === 'zip') {
    return '/src/assets/file/category/zip.svg'
  }
  return '/src/assets/file/category/document.svg'
}
// 格式化文件大小至人类可识别格式
const formatSizeInPerson = (size) => {
  if (size === 0) {
    return ''
  }
  const units = ['B', 'KB', 'MB', 'GB', 'TB', 'PB']
  const mod = 1024.0
  let i = 0
  while (size >= mod) {
    size /= mod
    i++
  }
  return formatNum(size, 1) + units[i]
}
// 格式化数字
const formatNum = (size, n) => {
  const sizeStr = size.toString()
  if (sizeStr.lastIndexOf('.') > -1) {
    return sizeStr.substring(0, sizeStr.toString().indexOf('.') + 1 + n)
  }
  return sizeStr
}

const common = {
  identifyFileIcon,
  formatSizeInPerson
}

export default common
