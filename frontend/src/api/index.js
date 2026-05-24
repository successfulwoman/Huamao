import axios from 'axios'
import { getUser, getAdmin, clearUser } from '../store'

const baseURL = '/api'

const api = axios.create({
  baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use(
  (config) => {
    const user = getUser()
    const admin = getAdmin()
    const token = admin?.token || user?.token
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      clearUser()
      window.location.href = '/login'
    }
    const errorMessage = error.response?.data?.message || '网络请求失败，请稍后重试'
    console.error('API Error:', errorMessage)
    return Promise.reject(error)
  }
)

const request = {
  get: (url, params = {}) => api.get(url, { params }),
  post: (url, data = {}, config = {}) => api.post(url, data, config),
  put: (url, data = {}) => api.put(url, data),
  delete: (url, params = {}) => api.delete(url, { params })
}

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
  login: (data) => request.post('/user/admin/login', data),
  getUserList: () => request.get('/user/admin/list'),
  updateUserStatus: (id, status) => request.post('/user/admin/status', null, { params: { id, status } }),
  updateUserRole: (id, role) => request.post('/user/admin/role', null, { params: { id, role } }),
  deleteUser: (id) => request.delete(`/user/admin/${id}`),
  countUsers: () => request.get('/user/admin/count'),
  getPostList: () => request.get('/post/admin/list'),
  updatePostStatus: (id, status) => request.post('/post/admin/status', null, { params: { id, status } }),
  updatePostTop: (id, isTop) => request.post('/post/admin/top', null, { params: { id, isTop } }),
  updatePostBest: (id, isBest) => request.post('/post/admin/best', null, { params: { id, isBest } }),
  deletePost: (id) => request.delete(`/post/admin/${id}`),
  countPosts: () => request.get('/post/admin/count'),
  getNoticeList: () => request.get('/notice/admin/list'),
  publishNotice: (data) => request.post('/notice/admin/publish', data),
  updateNotice: (data) => request.post('/notice/admin/update', data),
  deleteNotice: (id) => request.delete(`/notice/admin/${id}`),
  countNotices: () => request.get('/notice/admin/count'),
  setNoticeTop: (id) => request.post(`/notice/admin/top/${id}`),
  cancelNoticeTop: (id) => request.post(`/notice/admin/cancelTop/${id}`),
  getReportList: () => request.get('/report/admin/list'),
  getPendingReports: () => request.get('/report/admin/pending'),
  handleReport: (id, status) => request.post('/report/admin/handle', null, { params: { id, status } }),
  deleteReport: (id) => request.delete(`/report/admin/${id}`),
  countPendingReports: () => request.get('/report/admin/pending/count'),
  getSensitiveWordList: () => request.get('/sensitive/admin/list'),
  addSensitiveWord: (word) => request.post('/sensitive/admin/add', null, { params: { word } }),
  deleteSensitiveWord: (id) => request.delete(`/sensitive/admin/${id}`),
  batchAddSensitiveWords: (words) => request.post('/sensitive/admin/batch', words),
  batchDeleteSensitiveWords: (ids) => request.post('/sensitive/admin/batchDelete', ids),
  countSensitiveWords: () => request.get('/sensitive/admin/count'),
  testSensitiveFilter: (content) => request.post('/sensitive/admin/testFilter', { content }),
  refreshSensitiveCache: () => request.post('/sensitive/admin/refreshCache'),
  getCommentList: () => request.get('/comment/admin/list'),
  deleteComment: (id) => request.delete(`/comment/admin/${id}`),
  countComments: () => request.get('/comment/admin/count'),
  // 系统设置管理
  getAllSettings: () => request.get('/settings/admin/all'),
  getAllSettingsAsMap: () => request.get('/settings/admin/map'),
  updateSetting: (key, value) => request.post('/settings/admin/update', { key, value }),
  batchUpdateSettings: (settings) => request.post('/settings/admin/batchUpdate', settings),
  initDefaultSettings: () => request.post('/settings/admin/init'),
  getSiteInfo: () => request.get('/settings/siteInfo'),
  getSiteStatus: () => request.get('/settings/siteStatus')
}

export const categoryApi = {
  getList: () => request.get('/category/list'),
  getByUser: (userId) => request.get(`/category/user/${userId}`),
  addCustomTag: (tagName, userId) => request.post('/category/custom', { tagName, userId }),
  getAdminList: () => request.get('/category/admin/list'),
  addCategory: (data) => request.post('/category/admin/add', data),
  updateCategory: (data) => request.put('/category/admin/update', data),
  deleteCategory: (id) => request.delete(`/category/admin/${id}`)
}

export const postApi = {
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
  publish: (data) => request.post('/post/publish', data),
  getList: (page = 1) => request.get('/post/list', { page }),
  getByCategory: (categoryId, page = 1) => request.get('/post/category', { categoryId, page }),
  getByUser: (userId, page = 1) => request.get('/post/user', { userId, page }),
  getDetail: (postId) => request.get('/post/detail', { postId }),
  delete: (postId, userId) => request.delete('/post/delete', { postId, userId }),
  getCategories: () => request.get('/category/list'),
  search: (keyword, page = 1) => request.get('/post/search', { keyword, page })
}

export const commentApi = {
  add: (postId, userId, content) => api.post('/comment/add', null, { params: { postId, userId, content } }),
  getList: (postId, page = 1) => api.get('/comment/list', { params: { postId, page } })
}

export const likeApi = {
  toggle: (userId, postId) => request.post('/like/toggle', null, { params: { userId, postId } }),
  getCount: (postId) => request.get('/like/count', { postId }),
  getMy: (userId, page = 1) => request.get('/like/my', { userId, page })
}

export const collectApi = {
  toggle: (userId, postId) => request.post('/collect/toggle', null, { params: { userId, postId } }),
  getMy: (userId, page = 1) => request.get('/collect/my', { userId, page })
}

export const messageApi = {
  getMy: (userId, page = 1) => request.get('/message/my', { userId, page }),
  markRead: (messageId) => request.post('/message/read', { messageId })
}

export const noticeApi = {
  getAllNotices: () => request.get('/notice/list'),
  getLatestNotice: () => request.get('/notice/latest')
}

export const statsApi = {
  getOverview: () => request.get('/stats/overview')
}

export const reportApi = {
  addReport: (data) => request.post('/report/add', data)
}

export default api
