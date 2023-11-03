const typeSupports = localStorage.getItem('type_supports')

const type = {
  isString (o) {
    // 是否字符串
    return Object.prototype.toString.call(o).slice(8, -1) === 'String'
  },

  isNumber (o) {
    // 是否数字
    return /^([0-9]+\.?[0-9]*|-[0-9]+\.?[0-9]*)$/.test(o)
  },

  isEmail (o) {
    // 是否是邮箱
    return /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(o)
  },

  isPhone (o) {
    // 是否是手机号
    return /^(0|86|17951)?(13[0-9]|15[012356789]|16[6]|19[89]]|17[01345678]|18[0-9]|14[579])[0-9]{8}$/.test(o)
  },

  isLandline (o) {
    // 是否是座机号
    return /^((0\d{2,3}-\d{7,8})|(1[3456789]\d{9}))$/.test(o)
  },

  isObj (o) {
    // 是否对象
    return Object.prototype.toString.call(o).slice(8, -1) === 'Object'
  },

  isArray (o) {
    // 是否数组
    return Object.prototype.toString.call(o).slice(8, -1) === 'Array'
  },

  isDate (o) {
    // 是否时间
    return Object.prototype.toString.call(o).slice(8, -1) === 'Date'
  },

  isBoolean (o) {
    // 是否boolean
    return Object.prototype.toString.call(o).slice(8, -1) === 'Boolean'
  },

  is (o) {
    // 是否函数
    return Object.prototype.toString.call(o).slice(8, -1) === ''
  },

  isNull (o) {
    // 是否为null
    return Object.prototype.toString.call(o).slice(8, -1) === 'Null'
  },

  isUndefined (o) {
    // 是否undefined
    return Object.prototype.toString.call(o).slice(8, -1) === 'Undefined'
  },

  isFalse (o) {
    return o === '' || o === undefined || o == null || o === 'null' || o === 'undefined' || o === 0 || o === false || isNaN(o)
  },

  isTrue (o) {
    return !this.isFalse(o)
  },

  isIos () {
    const u = navigator.userAgent
    // 安卓手机
    if (u.indexOf('Android') > -1 || u.indexOf('Linux') > -1) {
      // return "Android";
      return false
      // 苹果手机
    } else if (u.indexOf('iPhone') > -1) {
      // return "iPhone";
      return true
      // iPad
    } else if (u.indexOf('iPad') > -1) {
      // return "iPad";
      return false
      // winphone手机
    } else if (u.indexOf('Windows Phone') > -1) {
      // return "Windows Phone";
      return false
    }
    return false
  },

  /**
   * 是否为PC端
   *
   * @returns {boolean}
   */
  isPc () {
    const userAgentInfo = navigator.userAgent
    const Agents = ['Android', 'iPhone', 'SymbianOS', 'Windows Phone', 'iPad', 'iPod']
    let flag = true
    for (let v = 0; v < Agents.length; v++) {
      if (userAgentInfo.indexOf(Agents[v]) > 0) {
        flag = false
        break
      }
    }
    return flag
  },

  browserType () {
    // 取得浏览器的userAgent字符串
    const userAgent = navigator.userAgent
    // 判断是否Opera浏览器
    const isOpera = userAgent.indexOf('Opera') > -1
    // 判断是否IE浏览器
    const isIE = userAgent.indexOf('compatible') > -1 && userAgent.indexOf('MSIE') > -1 && !isOpera
    // 判断是否IE的Edge浏览器
    const isEdge = userAgent.indexOf('Edge') > -1
    // 判断是否Firefox浏览器
    const isFF = userAgent.indexOf('Firefox') > -1
    // 判断是否Safari浏览器
    const isSafari = userAgent.indexOf('Safari') > -1 && userAgent.indexOf('Chrome') === -1
    // 判断Chrome浏览器
    const isChrome = userAgent.indexOf('Chrome') > -1 && userAgent.indexOf('Safari') > -1
    if (isIE) {
      const reIE = /(\\d+\\.\\d+)/
      reIE.test(userAgent)
      const fIEVersion = parseFloat(RegExp.$1)
      if (fIEVersion === 7) {
        return 'IE7'
      } else if (fIEVersion === 8) {
        return 'IE8'
      } else if (fIEVersion === 9) {
        return 'IE9'
      } else if (fIEVersion === 10) {
        return 'IE10'
      } else if (fIEVersion === 11) {
        return 'IE11'
      } else {
        return 'IE7以下'
      }
    }

    if (isFF) return 'FF'
    if (isOpera) return 'Opera'
    if (isEdge) return 'Edge'
    if (isSafari) return 'Safari'
    if (isChrome) return 'Chrome'
  },

  checkStr (str, type) {
    switch (type) {
      case 'phone':
        // 手机号码
        return /^1[3|45678][0-9]{9}$/.test(str)
      case 'tel':
        // 座机
        return /^(0\d{2,3}-\d{7,8})(-\d{1,4})?$/.test(str)
      case 'card':
        // 身份证
        return /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(str)
      case 'pwd':
        // 密码以字母开头，长度在6~18之间，只能包含字母、数字和下划线
        return /^[a-zA-Z]\w{5,17}$/.test(str)
      case 'postal':
        // 邮政编码
        return /[1-9]\d{5}(?!\d)/.test(str)
      case 'QQ':
        // QQ号
        return /^[1-9][0-9]{4,9}$/.test(str)
      case 'email':
        // 邮箱
        return /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(str)
      case 'money':
        // 金额(小数点2位)
        return /^\d*(?:\.\d{0,2})?$/.test(str)
      case 'URL':
        // 网址
        return /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-.,@?^=%&:/~+#]*[\w\-@?^=%&/~+#])?/.test(str)
      case 'IP':
        // IP
        return /((?:(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d))/.test(str)
      case 'date':
        // 日期时间
        return /^(\d{4})-(\d{2})-(\d{2}) (\d{2})(?::\d{2}|:(\d{2}):(\d{2}))$/.test(str) || /^(\d{4})-(\d{2})-(\d{2})$/.test(str)
      case 'number':
        // 数字
        return /^[0-9]$/.test(str)
      case 'english':
        // 英文
        return /^[a-zA-Z]+$/.test(str)
      case 'chinese':
        // 中文
        return /^[\u4E00-\u9FA5]+$/.test(str)
      case 'lower':
        // 小写
        return /^[a-z]+$/.test(str)
      case 'upper':
        // 大写
        return /^[A-Z]+$/.test(str)
      case 'html':
        // html标记
        return /<("[^"]*"|'[^']*'|[^'">])*>/.test(str)
      default:
        return true
    }
  },

  /**
   * 是否是视频
   */
  isVideo (ext) {
    const supportsTypes = typeSupports === null || typeSupports === undefined ? {} : JSON.parse(typeSupports)
    return supportsTypes.video.indexOf(ext) >= 0
  },

  /**
   * 是否是音频
   */
  isAudio (ext) {
    const supportsTypes = typeSupports === null || typeSupports === undefined ? {} : JSON.parse(typeSupports)
    return supportsTypes.audio.indexOf(ext) >= 0
  },

  // 是否是 word
  isWord (ext) {
    const supportsTypes = typeSupports === null || typeSupports === undefined ? {} : JSON.parse(typeSupports)
    return supportsTypes.document.word.indexOf(ext) >= 0
  },

  // 是否是 ppt
  isPpt (ext) {
    const supportsTypes = typeSupports === null || typeSupports === undefined ? {} : JSON.parse(typeSupports)
    return supportsTypes.document.ppt.indexOf(ext) >= 0
  },

  // 是否是 excel
  isExcel (ext) {
    const supportsTypes = typeSupports === null || typeSupports === undefined ? {} : JSON.parse(typeSupports)
    return supportsTypes.document.excel.indexOf(ext) >= 0
  },

  // 是否是 pdf
  isPdf (ext) {
    const supportsTypes = typeSupports === null || typeSupports === undefined ? {} : JSON.parse(typeSupports)
    return supportsTypes.document.pdf.indexOf(ext) >= 0
  },

  // 是否是 txt
  isTxt (ext) {
    const supportsTypes = typeSupports === null || typeSupports === undefined ? {} : JSON.parse(typeSupports)
    return supportsTypes.document.txt.indexOf(ext) >= 0
  },

  // 是否是文档
  isDocument (ext) {
    return this.isWord(ext) || this.isPpt(ext) || this.isExcel(ext) || this.isPdf(ext) || this.isTxt(ext)
  },

  // 检测是否是系统支持预览的文件类型
  isPreviewSupport (ext) {
    return this.isVideo(ext) || this.isAudio(ext) || this.isDocument(ext)
  },

  /**
   * 严格的身份证校验
   *
   * @param sId
   * @returns {boolean}
   */
  isCardID (sId) {
    if (!/(^\d{15}$)|(^\d{17}(\d|X|x)$)/.test(sId)) {
      alert('你输入的身份证长度或格式错误')
      return false
    }
    // 身份证城市
    const aCity = {
      11: '北京',
      12: '天津',
      13: '河北',
      14: '山西',
      15: '内蒙古',
      21: '辽宁',
      22: '吉林',
      23: '黑龙江',
      31: '上海',
      32: '江苏',
      33: '浙江',
      34: '安徽',
      35: '福建',
      36: '江西',
      37: '山东',
      41: '河南',
      42: '湖北',
      43: '湖南',
      44: '广东',
      45: '广西',
      46: '海南',
      50: '重庆',
      51: '四川',
      52: '贵州',
      53: '云南',
      54: '西藏',
      61: '陕西',
      62: '甘肃',
      63: '青海',
      64: '宁夏',
      65: '新疆',
      71: '台湾',
      81: '香港',
      82: '澳门',
      91: '国外'
    }
    if (!aCity[parseInt(sId.substr(0, 2))]) {
      alert('你的身份证地区非法')
      return false
    }

    // 出生日期验证
    const sBirthday = (sId.substr(6, 4) + '-' + Number(sId.substr(10, 2)) + '-' + Number(sId.substr(12, 2))).replace(/-/g, '/')
    const d = new Date(sBirthday)
    if (sBirthday !== (d.getFullYear() + '/' + (d.getMonth() + 1) + '/' + d.getDate())) {
      alert('身份证上的出生日期非法')
      return false
    }

    // 身份证号码校验
    let sum = 0
    const weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
    const codes = '10X98765432'
    for (let i = 0; i < sId.length - 1; i++) {
      sum += sId[i] * weights[i]
    }
    // 计算出来的最后一位身份证号码
    const last = codes[sum % 11]
    if (sId[sId.length - 1] !== last) {
      alert('你输入的身份证号非法')
      return false
    }
    return true
  }
}

export default type
