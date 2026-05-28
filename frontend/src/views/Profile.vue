<template>
  <div class="profile-container">
    <!-- 返回按钮 -->
    <div class="back-nav">
      <button class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </button>
    </div>

    <!-- 未登录提示 -->
    <div v-if="!user" class="not-login-card">
      <el-icon class="not-login-icon" :size="60"><Lock /></el-icon>
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
          <img v-lazy="{ src: getAvatarUrl(user.avatar, user.username), size: 'avatar' }" :alt="user.username" class="avatar" width="80" height="80" />
          <button class="avatar-edit-btn" @click="editAvatar">
            <el-icon><Camera /></el-icon>
          </button>
        </div>

        <div class="user-details">
          <h1 class="username">{{ user.username || '用户' }}</h1>
          <p class="user-bio">{{ user.profile || '这个人很懒，什么都没写~' }}</p>
          <div class="user-meta">
            <span class="meta-item">
              <el-icon class="meta-icon"><Calendar /></el-icon>
              加入于 {{ formatDate(user.createTime) }}
            </span>
          </div>
        </div>

        <button class="edit-profile-btn" @click="showEditModal = true">
          <el-icon><Edit /></el-icon>
          编辑资料
        </button>
      </div>

      <!-- 统计数据 -->
      <div class="stats-section">
        <div class="stat-item">
          <span class="stat-value">{{ userPostCount }}</span>
          <span class="stat-label">帖子</span>
        </div>
      </div>

      <!-- 标签页 -->
      <div class="profile-tabs">
        <button
          :class="['tab-btn', { active: activeTab === 'posts' }]"
          @click="activeTab = 'posts'"
        >
          <el-icon class="tab-icon"><Document /></el-icon>
          我的帖子
        </button>
        <button
          :class="['tab-btn', { active: activeTab === 'likes' }]"
          @click="activeTab = 'likes'"
        >
          <el-icon class="tab-icon"><Star /></el-icon>
          我的点赞
        </button>
        <button
          :class="['tab-btn', { active: activeTab === 'collects' }]"
          @click="activeTab = 'collects'"
        >
          <el-icon class="tab-icon"><Collection /></el-icon>
          我的收藏
        </button>
      </div>

      <!-- 内容列表 -->
      <div class="content-list">
        <div v-if="loading" class="loading-state">
          <el-icon class="loading-spinner" :size="40"><Loading /></el-icon>
          <p>加载中...</p>
        </div>

        <div v-else-if="contentItems.length === 0" class="empty-state">
          <el-icon class="empty-icon" :size="50">
            <Document v-if="activeTab === 'posts'" />
            <Star v-else-if="activeTab === 'likes'" />
            <Collection v-else />
          </el-icon>
          <p class="empty-text">
            {{ activeTab === 'posts' ? '还没有发布过帖子' : activeTab === 'likes' ? '还没有点赞过帖子' : '还没有收藏过帖子' }}
          </p>
        </div>

        <div v-else class="post-grid">
          <PostCard
            v-for="item in contentItems"
            :key="item.id"
            :post="item"
            :show-delete="activeTab === 'posts'"
            @click="openPost(item)"
            @like="handleLike"
            @collect="handleCollect"
            @comment="openPost(item)"
            @delete="handleDelete"
          />
        </div>
      </div>
    </div>

    <!-- 编辑资料弹窗 -->
    <Teleport to="body">
      <div v-if="showEditModal" class="modal-overlay" @click.self="showEditModal = false">
        <div class="edit-modal">
          <div class="modal-header">
            <h2 class="modal-title">编辑资料</h2>
            <button class="close-btn" @click="showEditModal = false"><el-icon><Close /></el-icon></button>
          </div>

          <div class="modal-body">
            <div class="form-group">
              <label class="form-label">用户名</label>
              <input
                v-model="editForm.username"
                type="text"
                class="form-input"
                placeholder="请输入用户名"
                maxlength="20"
              />
            </div>

            <div class="form-group">
              <label class="form-label">个人简介</label>
              <textarea
                v-model="editForm.profile"
                class="form-textarea"
                placeholder="介绍一下自己吧~"
                maxlength="100"
              ></textarea>
            </div>

            <div class="form-group">
              <label class="form-label">性别</label>
              <div class="gender-options">
                <button
                  :class="['gender-btn', { active: editForm.gender === '男' }]"
                  @click="editForm.gender = '男'"
                >
                  <el-icon><User /></el-icon> 男
                </button>
                <button
                  :class="['gender-btn', { active: editForm.gender === '女' }]"
                  @click="editForm.gender = '女'"
                >
                  <el-icon><User /></el-icon> 女
                </button>
                <button
                  :class="['gender-btn', { active: editForm.gender === '未知' }]"
                  @click="editForm.gender = '未知'"
                >
                  <el-icon><Hide /></el-icon> 保密
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

    <!-- 帖子详情弹窗 -->
    <PostDetail
      :visible="showPostDetail"
      :post="currentPost"
      @close="closePostDetail"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { userApi, postApi, collectApi, likeApi } from '../api'
import { getUser, setUser } from '../store'
import { getAvatar, getAvatarUrl, formatTime } from '../utils'
import PostDetail from '../components/PostDetail.vue'
import PostCard from '../components/PostCard.vue'

const router = useRouter()
const user = ref(null)
const showEditModal = ref(false)
const activeTab = ref('posts')
const loading = ref(false)
const contentItems = ref([])
const showPostDetail = ref(false)
const currentPost = ref(null)

const userPostCount = ref(0)

const editForm = ref({
  username: '',
  profile: '',
  gender: '未知'
})

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

const saveProfile = async () => {
  if (editForm.value.username) {
    try {
      const response = await userApi.update({
        id: user.value.id,
        username: editForm.value.username,
        profile: editForm.value.profile,
        gender: editForm.value.gender
      })
      if (response.data.code === 200) {
        user.value.username = editForm.value.username
        user.value.profile = editForm.value.profile
        user.value.gender = editForm.value.gender
        setUser(user.value)
        showEditModal.value = false
        alert('资料更新成功！')
      } else {
        alert(response.data.message || '资料更新失败')
      }
    } catch (error) {
      console.error('更新资料失败:', error)
      alert('资料更新失败，请稍后重试')
    }
  }
}

const loadUser = async () => {
  const currentUser = getUser()
  if (!currentUser) {
    return
  }
  try {
    const response = await userApi.getInfo(currentUser.id)
    if (response.data.code === 200) {
      user.value = response.data.data
      setUser(response.data.data)
      editForm.value = {
        username: response.data.data.username || '',
        profile: response.data.data.profile || '',
        gender: response.data.data.gender || '未知'
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    user.value = { ...currentUser }
    editForm.value = {
      username: currentUser.username || '',
      profile: currentUser.profile || '',
      gender: currentUser.gender || '未知'
    }
  }
}

const loadContent = async () => {
  if (!user.value?.id) return
  loading.value = true
  try {
    let response
    if (activeTab.value === 'posts') {
      response = await postApi.getByUser(user.value.id)
      contentItems.value = response.data?.data || []
      userPostCount.value = contentItems.value.length
    } else if (activeTab.value === 'likes') {
      response = await likeApi.getMy(user.value.id)
      contentItems.value = response.data?.data || []
    } else if (activeTab.value === 'collects') {
      response = await collectApi.getMy(user.value.id)
      contentItems.value = response.data?.data || []
    }
  } catch (error) {
    console.error('获取内容失败:', error)
    contentItems.value = []
  } finally {
    loading.value = false
  }
}

watch(activeTab, () => {
  loadContent()
})

const openPost = (post) => {
  currentPost.value = post
  showPostDetail.value = true
}

const closePostDetail = () => {
  showPostDetail.value = false
  currentPost.value = null
}

const handleLike = async (postId) => {
  const currentUser = getUser()
  if (!currentUser) {
    router.push('/login')
    return
  }
  try {
    await likeApi.toggle(currentUser.id, postId)
    loadContent()
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const handleCollect = async (postId) => {
  const currentUser = getUser()
  if (!currentUser) {
    router.push('/login')
    return
  }
  try {
    await collectApi.toggle(currentUser.id, postId)
    loadContent()
  } catch (error) {
    console.error('收藏失败:', error)
  }
}

const handleDelete = async (postId) => {
  const currentUser = getUser()
  if (!currentUser) {
    router.push('/login')
    return
  }
  
  if (!confirm('确定要删除这篇帖子吗？')) {
    return
  }
  
  try {
    const response = await postApi.delete(postId, currentUser.id)
    if (response.data.code === 200) {
      alert('帖子删除成功！')
      loadContent()
    } else {
      alert(response.data.message || '删除失败')
    }
  } catch (error) {
    console.error('删除帖子失败:', error)
    alert('删除帖子失败，请稍后重试')
  }
}

onMounted(async () => {
  await loadUser()
  loadContent()
})
</script>

<style scoped>
.profile-container {
  position: relative;
  width: 100%;
  min-height: 100vh;
  padding: 2rem;
  background: url('../assets/icons/Home2.webp') no-repeat center center fixed;
  background-size: cover;
  box-sizing: border-box;
}

.profile-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.15);
  z-index: 0;
}

.profile-container > * {
  position: relative;
  z-index: 1;
}

.profile-card {
  max-width: 900px;
  margin: 0 auto;
}

.back-nav {
  margin-bottom: 1.5rem;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 1rem;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  font-size: 0.9rem;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.7);
  border-color: rgba(139, 92, 246, 0.3);
  color: #8b5cf6;
}

.not-login-card {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 24px;
  padding: 4rem 2rem;
  text-align: center;
  box-shadow: 0 4px 24px rgba(139, 92, 246, 0.1);
}

.not-login-icon {
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

.profile-card {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(139, 92, 246, 0.1);
}

.profile-header-bg {
  height: 120px;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.8), rgba(168, 85, 247, 0.8), rgba(192, 132, 252, 0.8));
}

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
  border: 4px solid rgba(255, 255, 255, 0.8);
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
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
  border: 2px solid rgba(229, 231, 235, 0.6);
  border-radius: 12px;
  font-size: 0.9rem;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s ease;
}

.edit-profile-btn:hover {
  border-color: rgba(139, 92, 246, 0.5);
  color: #8b5cf6;
}

.stats-section {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  border-top: 1px solid rgba(243, 244, 246, 0.6);
  border-bottom: 1px solid rgba(243, 244, 246, 0.6);
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
  background: rgba(229, 231, 235, 0.6);
}

.profile-tabs {
  display: flex;
  padding: 0 2rem;
  border-bottom: 1px solid rgba(243, 244, 246, 0.6);
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
}

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
  animation: spin 1s linear infinite;
}

.empty-icon {
  margin-bottom: 1rem;
}

.empty-text {
  font-size: 0.95rem;
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
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
}

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
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.5);
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

  .post-grid {
    grid-template-columns: 1fr;
  }
}
</style>
