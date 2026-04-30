// 工具函数集合

/**
 * 格式化时间
 * @param {string|Date} dateStr - 时间字符串或Date对象
 * @returns {string} 格式化后的时间
 */
export const formatTime = (dateStr) => {
  if (!dateStr) return ''
  
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  const weeks = Math.floor(days / 7)
  
  if (seconds < 60) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  if (weeks < 4) return `${weeks}周前`
  
  return date.toLocaleDateString('zh-CN')
}

/**
 * 生成用户头像
 * @param {string} nickname - 用户昵称
 * @returns {string} 头像URL
 */
export const generateAvatar = (nickname) => {
  const colors = [
    '#8b5cf6', '#a855f7', '#c084fc', '#d8b4fe', '#e9d5ff',
    '#ec4899', '#f472b6', '#f9a8d4', '#fbcfe8', '#fce7f3',
    '#3b82f6', '#60a5fa', '#93c5fd', '#bfdbfe', '#dbeafe',
    '#10b981', '#34d399', '#6ee7b7', '#a7f3d0', '#d1fae5',
    '#f59e0b', '#fbbf24', '#fcd34d', '#fde68a', '#fef3c7'
  ]
  
  const index = nickname ? nickname.charCodeAt(0) % colors.length : 0
  const color = colors[index]
  const initial = nickname ? nickname.charAt(0).toUpperCase() : '?'
  
  return `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 40 40'%3E%3Ccircle cx='20' cy='20' r='18' fill='${color}'/%3E%3Ctext x='20' y='25' text-anchor='middle' fill='white' font-size='16' font-weight='bold'%3E${initial}%3C/text%3E%3C/svg%3E`
}

/**
 * 本地存储操作
 */
export const storage = {
  /**
   * 存储数据
   * @param {string} key - 键
   * @param {any} value - 值
   */
  set: (key, value) => {
    try {
      localStorage.setItem(key, JSON.stringify(value))
    } catch (error) {
      console.error('Storage set error:', error)
    }
  },
  
  /**
   * 获取数据
   * @param {string} key - 键
   * @param {any} defaultValue - 默认值
   * @returns {any} 存储的数据或默认值
   */
  get: (key, defaultValue = null) => {
    try {
      const value = localStorage.getItem(key)
      return value ? JSON.parse(value) : defaultValue
    } catch (error) {
      console.error('Storage get error:', error)
      return defaultValue
    }
  },
  
  /**
   * 删除数据
   * @param {string} key - 键
   */
  remove: (key) => {
    try {
      localStorage.removeItem(key)
    } catch (error) {
      console.error('Storage remove error:', error)
    }
  },
  
  /**
   * 清空所有数据
   */
  clear: () => {
    try {
      localStorage.clear()
    } catch (error) {
      console.error('Storage clear error:', error)
    }
  }
}

/**
 * 防抖函数
 * @param {Function} fn - 要执行的函数
 * @param {number} delay - 延迟时间(ms)
 * @returns {Function} 防抖后的函数
 */
export const debounce = (fn, delay = 300) => {
  let timer = null
  return function (...args) {
    clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

/**
 * 节流函数
 * @param {Function} fn - 要执行的函数
 * @param {number} delay - 延迟时间(ms)
 * @returns {Function} 节流后的函数
 */
export const throttle = (fn, delay = 300) => {
  let lastTime = 0
  return function (...args) {
    const now = Date.now()
    if (now - lastTime >= delay) {
      lastTime = now
      fn.apply(this, args)
    }
  }
}

/**
 * 深拷贝
 * @param {any} obj - 要拷贝的对象
 * @returns {any} 拷贝后的对象
 */
export const deepClone = (obj) => {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime())
  if (obj instanceof Array) return obj.map(item => deepClone(item))
  if (typeof obj === 'object') {
    const clonedObj = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
}

/**
 * 验证手机号
 * @param {string} phone - 手机号
 * @returns {boolean} 是否有效
 */
export const validatePhone = (phone) => {
  return /^1[3-9]\d{9}$/.test(phone)
}

/**
 * 验证邮箱
 * @param {string} email - 邮箱
 * @returns {boolean} 是否有效
 */
export const validateEmail = (email) => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
}

/**
 * 截断文本
 * @param {string} text - 文本
 * @param {number} maxLength - 最大长度
 * @returns {string} 截断后的文本
 */
export const truncateText = (text, maxLength = 50) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

/**
 * 获取分类图标
 * @param {string} categoryName - 分类名称
 * @returns {string} 图标
 */
export const getCategoryIcon = (categoryName) => {
  const iconMap = {
    '全部': '🌐',
    '日常': '🏠',
    '美食': '🍔',
    '旅行': '✈️',
    '科技': '💻',
    '娱乐': '🎬',
    '学习': '📚',
    '运动': '⚽',
    '音乐': '🎵',
    '电影': '🎥',
    '游戏': '🎮'
  }
  return iconMap[categoryName] || '📦'
}

/**
 * 生成随机ID
 * @returns {string} 随机ID
 */
export const generateId = () => {
  return Date.now().toString(36) + Math.random().toString(36).substr(2)
}

/**
 * 计算文件大小
 * @param {number} bytes - 字节数
 * @returns {string} 格式化后的文件大小
 */
export const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
