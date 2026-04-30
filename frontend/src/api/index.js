
import axios from 'axios'
import { getUser, clearUser } from '../store'

const baseURL = 'http://localhost:8081/api'

// 创建axios实例
const api = axios.create({
  baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    const user = getUser()
    if (user && user.token) {
      config.headers['Authorization'] = `Bearer ${user.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    // 处理401未授权
    if (error.response && error.response.status === 401) {
      clearUser()
      window.location.href = '/login'
    }
    
    // 处理其他错误
    const errorMessage = error.response?.data?.message || '网络请求失败，请稍后重试'
    console.error('API Error:', errorMessage)
    
    return Promise.reject(error)
  }
)

// 基础请求方法
const request = {
  get: (url, params = {}) => api.get(url, { params }),
  post: (url, data = {}) => api.post(url, data),
  put: (url, data = {}) => api.put(url, data),
  delete: (url, params = {}) => api.delete(url, { params })
}

// API模块
export const userApi = {
  register: (data) => request.post('/user/register', data),
  login: (data) => request.post('/user/login', data),
  getInfo: (userId) => request.get('/user/info', { userId }),
  update: (data) => request.post('/user/update', data),
  uploadAvatar: (file, userId) => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('userId', userId)
    return axios.post(`${baseURL}/user/uploadoss`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': getUser()?.token ? `Bearer ${getUser().token}` : ''
      }
    })
  }
}

export const adminApi = {
  login: (data) => request.post('/admin/login', data),
  getUserList: () => request.get('/admin/user/list'),
  getPostList: () => request.get('/admin/post/list')
}

export const categoryApi = {
  getList: () => request.get('/category/list')
}

export const postApi = {
  // 上传单张图片到服务器，返回图片URL
  // @param {File} file - 图片文件对象
  // @returns {Promise} 返回包含code和url的对象
  uploadImage: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return axios.post(`${baseURL}/post/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': getUser()?.token ? `Bearer ${getUser().token}` : ''
      }
    })
  },
  // 发布帖子
  // @param {Object} data - 帖子数据，包含userId, title, content, categoryId, imageUrls
  publish: (data) => request.post('/post/publish', data),
  // 获取帖子列表
  // @param {number} page - 页码，默认第1页
  getList: (page = 1) => request.get('/post/list', { page }),
  // 根据分类获取帖子列表
  // @param {number} categoryId - 分类ID
  // @param {number} page - 页码，默认第1页
  getByCategory: (categoryId, page = 1) => request.get('/post/category', { categoryId, page }),
  // 获取帖子详情
  // @param {number} postId - 帖子ID
  getDetail: (postId) => request.get('/post/detail', { postId }),
  // 删除帖子
  // @param {Object} data - 包含postId和userId
  delete: (data) => request.post('/post/delete', data),
  // 获取分类列表
  // @returns {Promise} 返回分类数组
  getCategories: () => request.get('/category/list')
}

export const commentApi = {
  add: (data) => request.post('/comment/add', data),
  getList: (postId, page = 1) => request.get('/comment/list', { postId, page })
}

export const likeApi = {
  toggle: (data) => request.post('/like/toggle', data),
  getCount: (postId) => request.get('/like/count', { postId })
}

export const collectApi = {
  toggle: (data) => request.post('/collect/toggle', data),
  getMy: (userId, page = 1) => request.get('/collect/my', { userId, page })
}

export const messageApi = {
  getMy: (userId, page = 1) => request.get('/message/my', { userId, page }),
  markRead: (messageId) => request.post('/message/read', { messageId })
}

export default api
