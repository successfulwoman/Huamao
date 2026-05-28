// 图片懒加载指令 - 支持 OSS 图片处理 + LQIP 模糊过渡

import { getImageUrl, imageSize } from '../utils/imageUrl'

// 灰色占位图（非 OSS URL 时的 fallback）
const SVG_PLACEHOLDER = 'data:image/svg+xml,' + encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 40 40">' +
  '<rect width="40" height="40" fill="#f3f4f6"/></svg>'
)

// 加载失败时的错误图
const SVG_ERROR = 'data:image/svg+xml,' + encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200">' +
  '<rect width="200" height="200" fill="#f3f4f6"/>' +
  '<text x="100" y="110" text-anchor="middle" fill="#9ca3af" font-size="14">加载失败</text></svg>'
)

// 尺寸预设映射
const sizeMap = {
  avatar: imageSize.avatar,
  thumb: imageSize.thumb,
  preview: imageSize.preview,
  full: imageSize.full
}

export const lazyLoad = {
  mounted(el, binding) {
    // 支持 string URL 和 {src, size} 对象两种绑定方式
    const src = typeof binding.value === 'string' ? binding.value : binding.value?.src
    const size = (typeof binding.value === 'object' && binding.value.size) || 'thumb'

    if (!src) return

    let isLoaded = false
    let observer = null

    const getSized = sizeMap[size] || sizeMap.thumb

    // 1. 设置微小模糊占位图（LQIP）
    const placeholderUrl = imageSize.placeholder(src)
    if (placeholderUrl.includes('x-oss-process')) {
      // OSS URL：使用微小模糊图作为占位
      el.src = placeholderUrl
    } else {
      // 非 OSS URL（data:、相对路径等）：使用灰色 SVG
      el.src = SVG_PLACEHOLDER
    }
    el.classList.add('lazy-placeholder')

    // 2. 加载实际尺寸图片
    const loadImage = () => {
      if (isLoaded) return

      const fullSrc = getSized(src)
      const image = new Image()
      image.src = fullSrc

      image.onload = () => {
        el.src = fullSrc
        el.classList.remove('lazy-placeholder')
        el.classList.add('lazy-loaded')
        isLoaded = true
      }

      image.onerror = () => {
        el.src = SVG_ERROR
        el.classList.remove('lazy-placeholder')
        el.classList.add('lazy-error')
      }
    }

    // 3. IntersectionObserver 监听进入视口
    observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          loadImage()
          observer.unobserve(el)
        }
      })
    }, {
      rootMargin: '300px' // 提前 300px 开始加载，确保滚动时图片已就绪
    })

    observer.observe(el)

    // 保存引用以便 cleanup
    el._lazyObserver = observer
  },

  unmounted(el) {
    if (el._lazyObserver) {
      el._lazyObserver.unobserve(el)
      el._lazyObserver = null
    }
  }
}
