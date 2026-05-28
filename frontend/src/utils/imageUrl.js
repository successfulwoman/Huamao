/**
 * OSS 图片 URL 处理工具
 * 利用阿里云 OSS 图片处理功能，按需缩放/格式化图片
 * 文档: https://help.aliyun.com/document_detail/44688.html
 */

const OSS_DOMAIN = 'https://huamaoss.oss-cn-qingdao.aliyuncs.com/'

// 检测浏览器是否支持 WebP
let webpSupported = null
function checkWebpSupport() {
  if (webpSupported !== null) return webpSupported
  const canvas = document.createElement('canvas')
  if (canvas.getContext && canvas.getContext('2d')) {
    webpSupported = canvas.toDataURL('image/webp').startsWith('data:image/webp')
  } else {
    webpSupported = false
  }
  return webpSupported
}

/**
 * 构建 OSS 图片处理参数
 * @param {Object} options
 * @param {number} options.width - 缩放宽度（保持宽高比）
 * @param {number} options.quality - 图片质量 1-100
 * @param {string} options.format - 'webp' | 'jpg' | 'png' | 'auto'
 * @param {boolean} options.blur - 是否应用模糊效果（用于 LQIP 占位图）
 * @returns {string} OSS 处理参数查询字符串
 */
function buildOssParams(options = {}) {
  const parts = []
  if (options.width) {
    parts.push(`resize,w_${options.width}`)
  }
  if (options.format === 'webp' && checkWebpSupport()) {
    parts.push('format,webp')
  }
  if (options.quality) {
    parts.push(`quality,q_${options.quality}`)
  }
  if (options.blur) {
    parts.push('blur,r_20,s_10')
  }
  if (parts.length === 0) return ''
  return '?x-oss-process=image/' + parts.join('/')
}

/**
 * 获取处理后的图片 URL
 *
 * 使用场景和推荐尺寸:
 *   avatar:     width=80  (40px 显示，2x  retina)
 *   thumb:      width=400 (网格卡片图片)
 *   preview:    width=1200 (详情弹窗大图)
 *   full:       原始尺寸
 *
 * @param {string|null|undefined} url - 原始 OSS URL 或非 OSS URL
 * @param {Object} options - 见 buildOssParams
 * @returns {string} 处理后的 URL
 */
export function getImageUrl(url, options = {}) {
  if (!url) return ''
  // 只处理我们自己的 OSS URL，其他 URL（如 data:、相对路径等）原样返回
  if (!url.startsWith(OSS_DOMAIN)) {
    return url
  }
  const params = buildOssParams(options)
  return url + params
}

/**
 * 便捷预设函数
 */
export const imageSize = {
  /** 微小 20px 模糊占位图（用于 LQIP 模糊过渡效果） */
  placeholder: (url) => getImageUrl(url, { width: 20, blur: true, quality: 20 }),
  /** 小头像 80px（40px 显示的 2x retina） */
  avatar: (url) => getImageUrl(url, { width: 80, format: 'webp' }),
  /** 网格卡片图 400px */
  thumb: (url) => getImageUrl(url, { width: 400, format: 'webp', quality: 80 }),
  /** 详情页大图 1200px */
  preview: (url) => getImageUrl(url, { width: 1200, format: 'webp', quality: 85 }),
  /** 原始大图不处理 */
  full: (url) => url
}

export default getImageUrl
