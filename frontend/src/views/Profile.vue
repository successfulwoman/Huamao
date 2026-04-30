<template>
  <div class="profile-container">
    <!-- 返回按钮 -->
    <div class="back-nav">
      <button class="back-btn" @click="goBack">
        <span>←</span>
        返回
      </button>
    </div>

    <!-- 未登录提示 -->
    <div v-if="!user" class="not-login-card">
      <div class="not-login-icon">🔒</div>
      <h2 class="not-login-title">请先登录</h2>
      <p class="not-login-desc">登录后即可查看和管理您的个人资料</p>
      <button class="btn btn-primary" @click="goLogin">去登录</button>
    </div>

    <!-- 个人资料卡片 -->
    <div v-else class="profile-card">
      <!-- 头部背景 -->
      <div class="profile-header-bg"></div>
      
      <!-- 用户信息 -->
      <div class="profile-info-section">
        <div class="avatar-wrapper">
          <img :src="user.avatar || getAvatar(user.nickname)" :alt="user.nickname" class="avatar" />
          <button class="avatar-edit-btn" @click="editAvatar">
            <span>📷</span>
          </button>
        </div>
        
        <div class="user-details">
          <h1 class="username">{{ user.nickname || '用户' }}</h1>
          <p class="user-bio">{{ user.bio || '这个人很懒，什么都没写~' }}</p>
          <div class="user-meta">
            <span class="meta-item">
              <span class="meta-icon">📅</span>
              加入于 {{ formatDate(user.createTime) }}
            </span>
          </div>
        </div>

        <button class="edit-profile-btn" @click="showEditModal = true">
          <span>✏️</span>
          编辑资料
        </button>
      </div>

      <!-- 统计数据 -->
      <div class="stats-section">
        <div class="stat-item">
          <span class="stat-value">{{ stats.posts }}</span>
          <span class="stat-label">帖子</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.followers }}</span>
          <span class="stat-label">粉丝</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.following }}</span>
          <span class="stat-label">关注</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.likes }}</span>
          <span class="stat-label">获赞</span>
        </div>
      </div>

      <!-- 标签页 -->
      <div class="profile-tabs">
        <button 
          :class="['tab-btn', { active: activeTab === 'posts' }]"
          @click="activeTab = 'posts'"
        >
          <span class="tab-icon">📝</span>
          我的帖子
        </button>
        <button 
          :class="['tab-btn', { active: activeTab === 'likes' }]"
          @click="activeTab = 'likes'"
        >
          <span class="tab-icon">❤️</span>
          我的点赞
        </button>
        <button 
          :class="['tab-btn', { active: activeTab === 'collects' }]"
          @click="activeTab = 'collects'"
        >
          <span class="tab-icon">⭐</span>
          我的收藏
        </button>
      </div>

      <!-- 内容列表 -->
      <div class="content-list">
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner">⏳</div>
          <p>加载中...</p>
        </div>
        
        <div v-else-if="contentItems.length === 0" class="empty-state">
          <div class="empty-icon">{{ activeTab === 'posts' ? '📝' : activeTab === 'likes' ? '❤️' : '⭐' }}</div>
          <p class="empty-text">
            {{ activeTab === 'posts' ? '还没有发布过帖子' : activeTab === 'likes' ? '还没有点赞过帖子' : '还没有收藏过帖子' }}
          </p>
        </div>

        <div v-else class="content-grid">
          <div v-for="item in contentItems" :key="item.id" class="content-card">
            <img v-if="item.image" :src="item.image" :alt="item.title" class="content-image" />
            <div class="content-info">
              <h3 class="content-title">{{ item.title }}</h3>
              <p class="content-desc">{{ item.content }}</p>
              <div class="content-meta">
                <span class="meta-icon">💬</span>
                <span>{{ item.commentCount || 0 }}</span>
                <span class="meta-icon">❤️</span>
                <span>{{ item.likeCount || 0 }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料弹窗 -->
    <Teleport to="body">
      <div v-if="showEditModal" class="modal-overlay" @click.self="showEditModal = false">
        <div class="edit-modal">
          <div class="modal-header">
            <h2 class="modal-title">编辑资料</h2>
            <button class="close-btn" @click="showEditModal = false">✕</button>
          </div>

          <div class="modal-body">
            <div class="form-group">
              <label class="form-label">昵称</label>
              <input 
                v-model="editForm.nickname" 
                type="text" 
                class="form-input"
                placeholder="请输入昵称"
                maxlength="20"
              />
            </div>

            <div class="form-group">
              <label class="form-label">个人简介</label>
              <textarea 
                v-model="editForm.bio" 
                class="form-textarea"
                placeholder="介绍一下自己吧~"
                maxlength="100"
              ></textarea>
            </div>

            <div class="form-group">
              <label class="form-label">性别</label>
              <div class="gender-options">
                <button 
                  :class="['gender-btn', { active: editForm.gender === 'male' }]"
                  @click="editForm.gender = 'male'"
                >
                  <span>👨</span> 男
                </button>
                <button 
                  :class="['gender-btn', { active: editForm.gender === 'female' }]"
                  @click="editForm.gender = 'female'"
                >
                  <span>👩</span> 女
                </button>
                <button 
                  :class="['gender-btn', { active: editForm.gender === 'secret' }]"
                  @click="editForm.gender = 'secret'"
                >
                  <span>🤫</span> 保密
                </button>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button class="btn btn-secondary" @click="showEditModal = false">取消</button>
            <button class="btn btn-primary" @click="saveProfile">保存</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { userApi, postApi } from '../api'
import { getUser, setUser } from '../store'
import { generateAvatar, formatTime } from '../utils'

const router = useRouter()
const user = ref(null)
const showEditModal = ref(false)
const activeTab = ref('posts')
const loading = ref(false)
const contentItems = ref([])

const stats = ref({
  posts: 0,
  followers: 0,
  following: 0,
  likes: 0
})

const editForm = ref({
  nickname: '',
  bio: '',
  gender: 'secret'
})

// 模拟内容数据
const mockContent = {
  posts: [
    { id: 1, title: '今天天气真好', content: '出门逛逛心情棒棒的~', image: 'https://picsum.photos/200/200?random=1', commentCount: 12, likeCount: 45 },
    { id: 2, title: '分享美食', content: '这家店的味道真不错！', image: 'https://picsum.photos/200/200?random=2', commentCount: 8, likeCount: 23 },
    { id: 3, title: '学习笔记', content: '今天学到了很多新知识', image: null, commentCount: 5, likeCount: 18 }
  ],
  likes: [
    { id: 4, title: '好看的电影推荐', content: '流浪地球真的太好看了', image: 'https://picsum.photos/200/200?random=3', commentCount: 56, likeCount: 234 },
    { id: 5, title: '旅行日记', content: '云南之旅太美了', image: 'https://picsum.photos/200/200?random=4', commentCount: 34, likeCount: 89 }
  ],
  collects: [
    { id: 6, title: '美食食谱', content: '红烧肉详细做法', image: 'https://picsum.photos/200/200?random=5', commentCount: 78, likeCount: 156 },
    { id: 7, title: '摄影技巧', content: '如何拍出好看的照片', image: 'https://picsum.photos/200/200?random=6', commentCount: 45, likeCount: 112 }
  ]
}

const getAvatar = (nickname) => {
  return generateAvatar(nickname || 'User')
}

const formatDate = (dateStr) => {
  if (!dateStr) return '未知'
  const date = new Date(dateStr)
  return `${date.getFullYear()}年${date.getMonth() + 1}月`
}

const goBack = () => {
  router.back()
}

const goLogin = () => {
  router.push('/login')
}

const editAvatar = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (!file) return

    if (file.size > 5 * 1024 * 1024) {
      alert('图片大小不能超过5MB')
      return
    }

    try {
      const response = await userApi.uploadAvatar(file, user.value.id)
      if (response.data.code === 200) {
        const newAvatar = response.data.data
        user.value.avatar = newAvatar
        setUser(user.value)
        alert('头像上传成功！')
      } else {
        alert(response.data.message || '头像上传失败')
      }
    } catch (error) {
      console.error('上传头像失败:', error)
      alert('头像上传失败，请稍后重试')
    }
  }
  input.click()
}

const saveProfile = () => {
  if (editForm.value.nickname) {
    user.value.nickname = editForm.value.nickname
    user.value.bio = editForm.value.bio
    user.value.gender = editForm.value.gender
    setUser(user.value)
    showEditModal.value = false
  }
}

const loadUser = () => {
  const currentUser = getUser()
  if (!currentUser) {
    return
  }
  user.value = { ...currentUser }
  editForm.value = {
    nickname: currentUser.nickname || '',
    bio: currentUser.bio || '',
    gender: currentUser.gender || 'secret'
  }
  // 模拟加载统计数据
  stats.value = {
    posts: 12,
    followers: 88,
    following: 56,
    likes: 234
  }
}

const loadContent = async () => {
  loading.value = true
  // 模拟加载
  setTimeout(() => {
    contentItems.value = mockContent[activeTab.value]
    loading.value = false
  }, 500)
}

watch(activeTab, () => {
  loadContent()
})

onMounted(() => {
  loadUser()
  loadContent()
})
</script>

<style scoped>
.profile-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

/* 返回导航 */
.back-nav {
  margin-bottom: 1.5rem;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 1rem;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  font-size: 0.9rem;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: #f9fafb;
  border-color: #8b5cf6;
  color: #8b5cf6;
}

/* 未登录卡片 */
.not-login-card {
  background: white;
  border-radius: 24px;
  padding: 4rem 2rem;
  text-align: center;
  box-shadow: 0 4px 24px rgba(139, 92, 246, 0.1);
}

.not-login-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.not-login-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 0.5rem;
}

.not-login-desc {
  color: #6b7280;
  margin-bottom: 2rem;
}

/* 个人资料卡片 */
.profile-card {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(139, 92, 246, 0.1);
}

.profile-header-bg {
  height: 120px;
  background: linear-gradient(135deg, #8b5cf6, #a855f7, #c084fc);
}

/* 用户信息 */
.profile-info-section {
  display: flex;
  align-items: flex-end;
  gap: 1.5rem;
  padding: 0 2rem 1.5rem;
  margin-top: -50px;
  position: relative;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  object-fit: cover;
}

.avatar-edit-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  color: white;
  font-size: 0.9rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.3);
}

.user-details {
  flex: 1;
  padding-top: 0.5rem;
}

.username {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 0.25rem;
}

.user-bio {
  font-size: 0.9rem;
  color: #6b7280;
  margin-bottom: 0.5rem;
}

.user-meta {
  display: flex;
  gap: 1rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.85rem;
  color: #9ca3af;
}

.edit-profile-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 1.25rem;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 0.9rem;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s ease;
}

.edit-profile-btn:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
}

/* 统计数据 */
.stats-section {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  border-top: 1px solid #f3f4f6;
  border-bottom: 1px solid #f3f4f6;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
  padding: 0 2rem;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
}

.stat-label {
  font-size: 0.85rem;
  color: #6b7280;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: #e5e7eb;
}

/* 标签页 */
.profile-tabs {
  display: flex;
  padding: 0 2rem;
  border-bottom: 1px solid #f3f4f6;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 1.5rem;
  background: transparent;
  border: none;
  font-size: 0.95rem;
  color: #6b7280;
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;
}

.tab-btn::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 2px;
  background: transparent;
  transition: background 0.3s ease;
}

.tab-btn:hover {
  color: #8b5cf6;
}

.tab-btn.active {
  color: #8b5cf6;
  font-weight: 600;
}

.tab-btn.active::after {
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
}

.tab-icon {
  font-size: 1rem;
}

/* 内容列表 */
.content-list {
  padding: 1.5rem 2rem;
  min-height: 300px;
}

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  color: #9ca3af;
}

.loading-spinner {
  font-size: 2rem;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.empty-text {
  font-size: 0.95rem;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1rem;
}

.content-card {
  background: #f9fafb;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.content-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.15);
}

.content-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.content-info {
  padding: 1rem;
}

.content-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 0.5rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.content-desc {
  font-size: 0.85rem;
  color: #6b7280;
  margin-bottom: 0.75rem;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.content-meta {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 0.8rem;
  color: #9ca3af;
}

.content-meta .meta-icon {
  font-size: 0.9rem;
}

/* 编辑弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.edit-modal {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 480px;
  max-height: 90vh;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #f3f4f6;
}

.modal-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1f2937;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: #f3f4f6;
  color: #6b7280;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: #e5e7eb;
  color: #1f2937;
}

.modal-body {
  padding: 1.5rem;
  overflow-y: auto;
  max-height: calc(90vh - 140px);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #374151;
  font-size: 0.9rem;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 0.875rem 1rem;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  background: #f9fafb;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #8b5cf6;
  background: white;
  box-shadow: 0 0 0 4px rgba(139, 92, 246, 0.1);
}

.form-textarea {
  min-height: 100px;
  resize: vertical;
}

.gender-options {
  display: flex;
  gap: 0.75rem;
}

.gender-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 0.9rem;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s ease;
}

.gender-btn:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
}

.gender-btn.active {
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  border-color: transparent;
  color: white;
}

.modal-footer {
  display: flex;
  gap: 1rem;
  padding: 1.25rem 1.5rem;
  border-top: 1px solid #f3f4f6;
}

.modal-footer .btn {
  flex: 1;
  padding: 0.875rem;
  border-radius: 12px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  border: none;
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 14px rgba(139, 92, 246, 0.3);
}

.btn-secondary {
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  color: #6b7280;
}

.btn-secondary:hover {
  background: #e5e7eb;
  color: #374151;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式 */
@media (max-width: 640px) {
  .profile-container {
    padding: 1rem 0.5rem;
  }

  .profile-info-section {
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 0 1rem 1.5rem;
  }

  .avatar {
    width: 80px;
    height: 80px;
  }

  .user-details {
    padding-top: 0;
  }

  .edit-profile-btn {
    margin-top: 1rem;
  }

  .stats-section {
    flex-wrap: wrap;
    gap: 1rem;
  }

  .stat-item {
    padding: 0 1rem;
  }

  .profile-tabs {
    padding: 0;
    overflow-x: auto;
  }

  .tab-btn {
    padding: 0.875rem 1rem;
    font-size: 0.85rem;
    white-space: nowrap;
  }

  .content-list {
    padding: 1rem;
  }

  .content-grid {
    grid-template-columns: 1fr;
  }
}
</style>
