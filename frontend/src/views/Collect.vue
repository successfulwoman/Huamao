<template>
  <div class="collect-container">
    <!-- 返回导航 -->
    <div class="back-nav">
      <button class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </button>
    </div>

    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">
        <el-icon class="title-icon"><CollectionTag /></el-icon>
        我的收藏
      </h1>
      <p class="page-desc">收藏你喜欢的帖子，随时查看</p>
    </div>

    <!-- 未登录提示 -->
    <div v-if="!user" class="not-login-card">
      <el-icon class="not-login-icon"><Lock /></el-icon>
      <h2 class="not-login-title">请先登录</h2>
      <p class="not-login-desc">登录后即可查看和管理您的收藏</p>
      <button class="btn btn-primary" @click="goLogin">去登录</button>
    </div>

    <!-- 主内容区 -->
    <div v-else class="main-content">
      <!-- 左侧收藏列表 -->
      <div class="collect-sidebar">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <el-icon class="loading-spinner"><Loading /></el-icon>
          <p>加载中...</p>
        </div>

        <!-- 空状态 -->
        <div v-else-if="posts.length === 0" class="empty-state">
          <el-icon class="empty-icon"><Collection /></el-icon>
          <h2 class="empty-title">还没有收藏</h2>
          <p class="empty-desc">去首页看看有什么喜欢的帖子吧~</p>
          <button class="btn btn-primary" @click="goHome">去首页</button>
        </div>

        <!-- 收藏列表 -->
        <div v-else class="post-list">
          <div
            v-for="post in posts"
            :key="post.id"
            :class="['post-item', { active: selectedPost?.id === post.id }]"
            @click="selectPost(post)"
          >
            <div v-if="post.imageUrls && post.imageUrls.length > 0" class="item-image">
              <img v-lazy="{ src: post.imageUrls[0], size: 'thumb' }" :alt="post.title" width="120" height="120" />
            </div>
            <div class="item-content">
              <h4 class="item-title">{{ post.title }}</h4>
              <p class="item-text">{{ truncateText(post.content, 50) }}</p>
              <div class="item-meta">
                <span class="item-author">{{ post.nickname }}</span>
                <span class="item-time">{{ formatTime(post.createTime) }}</span>
              </div>
            </div>
            <button class="item-remove" @click.stop="removeCollect(post.id)" title="取消收藏">
              <el-icon><Close /></el-icon>
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧预览区域 -->
      <div class="preview-panel">
        <!-- 未选中状态 -->
        <div v-if="!selectedPost" class="empty-preview">
          <el-icon class="preview-icon"><Document /></el-icon>
          <h3>选择一个帖子</h3>
          <p>点击左侧收藏列表中的帖子查看详情</p>
        </div>

        <!-- 帖子详情预览 -->
        <div v-else class="post-preview">
          <!-- 预览头部 -->
          <div class="preview-header">
            <button class="close-preview" @click="selectedPost = null">
              <el-icon><Close /></el-icon>
            </button>
          </div>

          <!-- 帖子内容 -->
          <div class="preview-content">
            <h2 class="preview-title">{{ selectedPost.title }}</h2>
            
            <div class="preview-meta">
              <img v-lazy="{ src: getAvatarUrl(selectedPost.avatar, selectedPost.nickname), size: 'avatar' }" :alt="selectedPost.nickname" class="preview-avatar" width="36" height="36" />
              <span class="preview-author">{{ selectedPost.nickname }}</span>
              <span class="preview-divider">·</span>
              <span class="preview-time">{{ formatTime(selectedPost.createTime) }}</span>
            </div>

            <div class="preview-body">
              <p>{{ selectedPost.content }}</p>
              
              <!-- 图片展示 -->
              <div v-if="selectedPost.imageUrls && selectedPost.imageUrls.length > 0" class="preview-images">
                <img 
                  v-for="(img, index) in selectedPost.imageUrls" 
                  :key="index" 
                  v-lazy="{ src: img, size: 'thumb' }"
                  :alt="`图片${index + 1}`"
                  class="preview-image"
                  width="200"
                  height="200"
                />
              </div>
            </div>

            <!-- 互动栏 -->
            <div class="preview-actions">
              <button class="action-btn" @click="toggleLike(selectedPost)">
                <el-icon :size="18" v-if="!selectedPost.isLiked"><Star /></el-icon>
                <el-icon :size="18" v-else><StarFilled /></el-icon>
                <span>{{ selectedPost.likeCount || 0 }}</span>
              </button>
              <button class="action-btn" @click="toggleCollect(selectedPost)">
                <el-icon :size="18"><CollectionTag /></el-icon>
                <span>已收藏</span>
              </button>
              <button class="action-btn" @click="goDetail(selectedPost.id)">
                <el-icon :size="18"><ArrowRight /></el-icon>
                <span>查看全文</span>
              </button>
            </div>
          </div>

          <!-- 评论区域 -->
          <div class="preview-comments">
            <h3 class="comments-title">评论 ({{ comments.length }})</h3>
            
            <!-- 评论列表 -->
            <div v-if="comments.length > 0" class="comments-list">
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <img v-lazy="{ src: getAvatarUrl(comment.avatar, comment.nickname), size: 'avatar' }" :alt="comment.nickname" class="comment-avatar" width="36" height="36" />
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-author">{{ comment.nickname }}</span>
                    <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                  </div>
                  <p class="comment-text">{{ comment.content }}</p>
                </div>
              </div>
            </div>
            
            <!-- 空评论状态 -->
            <div v-else class="empty-comments">
              <p>暂无评论，快来抢沙发吧~</p>
            </div>

            <!-- 发表评论 -->
            <div v-if="user" class="comment-form">
              <textarea 
                v-model="commentContent" 
                placeholder="说点什么..." 
                class="comment-input"
                @keydown.enter.exact.prevent="submitComment"
              ></textarea>
              <button class="btn btn-primary comment-btn" @click="submitComment">发表评论</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { collectApi, likeApi, commentApi } from '../api'
import { getUser } from '../store'
import { getAvatar, getAvatarUrl, formatTime, truncateText } from '../utils'
import { ArrowLeft, Collection, CollectionTag, Lock, Loading, Close, Document, Star, StarFilled, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const posts = ref([])
const loading = ref(false)
const selectedPost = ref(null)
const comments = ref([])
const commentContent = ref('')

const user = ref(getUser())

const goBack = () => {
  router.back()
}

const goLogin = () => {
  router.push('/login')
}

const goHome = () => {
  router.push('/')
}

const goDetail = (postId) => {
  router.push(`/post/detail/${postId}`)
}

const loadCollects = async () => {
  loading.value = true
  try {
    const response = await collectApi.getMy(user.value.id)
    posts.value = response.data?.data || []
    // 默认选中第一个
    if (posts.value.length > 0 && !selectedPost.value) {
      selectPost(posts.value[0])
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    posts.value = []
  } finally {
    loading.value = false
  }
}

const loadComments = async (postId) => {
  try {
    const response = await commentApi.getList(postId)
    comments.value = response.data?.data || []
  } catch (error) {
    console.error('获取评论失败:', error)
    comments.value = []
  }
}

const selectPost = (post) => {
  selectedPost.value = post
  loadComments(post.id)
}

const removeCollect = async (postId) => {
  if (confirm('确定要取消收藏吗？')) {
    try {
      await collectApi.toggle(user.value.id, postId)
      posts.value = posts.value.filter(p => p.id !== postId)
      if (selectedPost.value?.id === postId) {
        selectedPost.value = posts.value.length > 0 ? posts.value[0] : null
        if (selectedPost.value) {
          loadComments(selectedPost.value.id)
        }
      }
    } catch (error) {
      console.error('取消收藏失败:', error)
      alert('取消收藏失败，请稍后重试')
    }
  }
}

const toggleLike = async (post) => {
  if (!user.value) {
    router.push('/login')
    return
  }
  try {
    await likeApi.toggle(user.value.id, post.id)
    post.likeCount = (post.likeCount || 0) + (post.isLiked ? -1 : 1)
    post.isLiked = !post.isLiked
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const toggleCollect = async (post) => {
  await removeCollect(post.id)
}

const submitComment = async () => {
  if (!user.value || !commentContent.value.trim()) return
  
  try {
    await commentApi.add(selectedPost.value.id, user.value.id, commentContent.value)
    commentContent.value = ''
    loadComments(selectedPost.value.id)
    if (selectedPost.value) {
      selectedPost.value.commentCount = (selectedPost.value.commentCount || 0) + 1
    }
  } catch (error) {
    console.error('发表评论失败:', error)
    alert('发表评论失败，请稍后重试')
  }
}

watch(user, () => {
  if (user.value) {
    loadCollects()
  }
})

onMounted(() => {
  if (user.value) {
    loadCollects()
  }
})
</script>

<style scoped>
.collect-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
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

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 2rem;
}

.page-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  font-size: 1.75rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 0.5rem;
}

.title-icon {
  font-size: 2rem;
}

.page-desc {
  color: #6b7280;
  font-size: 0.95rem;
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

/* 主内容区 */
.main-content {
  display: flex;
  gap: 1.5rem;
  min-height: 600px;
}

/* 左侧收藏列表 */
.collect-sidebar {
  width: 400px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.loading-state,
.empty-state {
  padding: 3rem 2rem;
  text-align: center;
}

.loading-spinner {
  font-size: 2.5rem;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.empty-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 0.5rem;
}

.empty-desc {
  color: #6b7280;
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
}

/* 帖子列表 */
.post-list {
  overflow-y: auto;
  max-height: calc(100vh - 280px);
}

.post-item {
  display: flex;
  gap: 0.75rem;
  padding: 0.875rem;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.post-item:hover {
  background: #f9fafb;
}

.post-item.active {
  background: #f3f0ff;
  border-left: 3px solid #8b5cf6;
}

.item-image {
  width: 56px;
  height: 56px;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-content {
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.item-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 0.25rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-text {
  font-size: 0.8rem;
  color: #6b7280;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 0.375rem;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.75rem;
  color: #9ca3af;
}

.item-remove {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0);
  color: #9ca3af;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.2s ease;
}

.post-item:hover .item-remove {
  opacity: 1;
}

.item-remove:hover {
  background: #fee2e2;
  color: #dc2626;
}

/* 右侧预览区域 */
.preview-panel {
  flex: 1;
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.empty-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 4rem;
  text-align: center;
}

.preview-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-preview h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 0.5rem;
}

.empty-preview p {
  color: #6b7280;
}

/* 帖子预览 */
.post-preview {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.preview-header {
  padding: 1rem;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  justify-content: flex-end;
}

.close-preview {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: #f3f4f6;
  color: #6b7280;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.close-preview:hover {
  background: #e5e7eb;
  color: #374151;
}

.preview-content {
  padding: 1.5rem;
  flex: 1;
  overflow-y: auto;
}

.preview-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 1rem;
}

.preview-meta {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.preview-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.preview-author {
  font-size: 0.9rem;
  font-weight: 500;
  color: #374151;
}

.preview-divider {
  color: #d1d5db;
}

.preview-time {
  font-size: 0.85rem;
  color: #9ca3af;
}

.preview-body {
  font-size: 1rem;
  line-height: 1.8;
  color: #374151;
  white-space: pre-wrap;
}

.preview-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 1.5rem;
}

.preview-image {
  width: 100%;
  height: auto;
  border-radius: 12px;
  object-fit: cover;
}

.preview-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #f3f4f6;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: white;
  font-size: 0.85rem;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:hover {
  background: #f9fafb;
  border-color: #8b5cf6;
  color: #8b5cf6;
}

.action-icon {
  font-size: 0.9rem;
}

/* 评论区域 */
.preview-comments {
  padding: 1.5rem;
  border-top: 1px solid #f3f4f6;
  background: #fafafa;
}

.comments-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 1rem;
}

.comments-list {
  margin-bottom: 1rem;
}

.comment-item {
  display: flex;
  gap: 0.75rem;
  padding: 0.75rem;
  background: white;
  border-radius: 12px;
  margin-bottom: 0.75rem;
}

.comment-item:last-child {
  margin-bottom: 0;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.375rem;
}

.comment-author {
  font-size: 0.85rem;
  font-weight: 500;
  color: #374151;
}

.comment-time {
  font-size: 0.75rem;
  color: #9ca3af;
}

.comment-text {
  font-size: 0.9rem;
  color: #4b5563;
  line-height: 1.5;
}

.empty-comments {
  text-align: center;
  padding: 2rem;
  color: #9ca3af;
  font-size: 0.9rem;
}

/* 评论表单 */
.comment-form {
  margin-top: 1rem;
}

.comment-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  font-size: 0.9rem;
  resize: vertical;
  min-height: 80px;
  font-family: inherit;
  margin-bottom: 0.75rem;
}

.comment-input:focus {
  outline: none;
  border-color: #8b5cf6;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.comment-btn {
  width: 100%;
}

/* 按钮样式 */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
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
  box-shadow: 0 4px 14px rgba(139, 92, 246, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 92, 246, 0.4);
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 响应式 */
@media (max-width: 900px) {
  .main-content {
    flex-direction: column;
  }
  
  .collect-sidebar {
    width: 100%;
    max-height: 300px;
  }
  
  .post-list {
    max-height: 220px;
  }
  
  .preview-images {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .collect-container {
    padding: 0.75rem;
  }
  
  .page-title {
    font-size: 1.5rem;
  }
  
  .preview-content {
    padding: 1rem;
  }
  
  .preview-title {
    font-size: 1.25rem;
  }
  
  .preview-actions {
    flex-wrap: wrap;
  }
  
  .action-btn {
    flex: 1;
    min-width: calc(33.33% - 0.66rem);
    justify-content: center;
  }
  
  .comment-item {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>
