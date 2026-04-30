// 图片懒加载指令

export const lazyLoad = {
  mounted(el, binding) {
    // 图片加载状态
    let isLoaded = false
    
    // 占位图
    const placeholder = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"%3E%3Crect width="200" height="200" fill="%23f3f4f6"/%3E%3Ctext x="100" y="110" font-size="14" text-anchor="middle" fill="%239ca3af"%3E加载中...%3C/text%3E%3C/svg%3E'
    
    // 设置占位图
    el.src = placeholder
    
    // 图片加载函数
    const loadImage = () => {
      if (isLoaded) return
      
      const image = new Image()
      image.src = binding.value
      
      image.onload = () => {
        el.src = binding.value
        el.classList.add('lazy-loaded')
        isLoaded = true
      }
      
      image.onerror = () => {
        // 加载失败时显示默认错误图
        el.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"%3E%3Crect width="200" height="200" fill="%23f3f4f6"/%3E%3Ctext x="100" y="110" font-size="14" text-anchor="middle" fill="%239ca3af"%3E加载失败%3C/text%3E%3C/svg%3E'
        el.classList.add('lazy-error')
      }
    }
    
    // 观察器
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          loadImage()
          observer.unobserve(el)
        }
      })
    }, {
      rootMargin: '200px' // 提前200px开始加载
    })
    
    observer.observe(el)
    
    // 保存观察器引用，以便在卸载时清理
    el._observer = observer
  },
  
  unmounted(el) {
    // 清理观察器
    if (el._observer) {
      el._observer.unobserve(el)
    }
  }
}
