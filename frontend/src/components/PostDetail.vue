<template>
  <Teleport to="body">
    <div v-if="visible" class="modal-overlay" @click.self="close">
      <div class="modal-container">
        <div class="left-panel">
          <div class="image-container">
            <button class="nav-btn left-nav" @click="prevImage" v-if="post?.imageUrls?.length > 1">
              <span class="nav-icon">‹</span>
            </button>
            <div class="image-wrapper">
              <img
                v-if="post?.imageUrls?.length"
                :key="currentImageIndex"
                v-lazy="{ src: currentImageUrl, size: 'preview' }"
                :alt="`帖子图片 ${currentImageIndex + 1}/${post.imageUrls.length}`"
                class="post-image"
                width="600"
                height="400"
              />
              <div v-else class="no-image">
                <div class="no-image-icon-wrapper">
                  <el-icon :size="48" color="#d1d5db"><Picture /></el-icon>
                </div>
                <span class="no-image-text">暂无图片</span>
              </div>
            </div>
            <button class="nav-btn right-nav" @click="nextImage" v-if="post?.imageUrls?.length > 1">
              <span class="nav-icon">›</span>
            </button>
          </div>
          <div v-if="post?.imageUrls?.length > 1" class="image-indicator">
            <span
              v-for="(_, index) in post.imageUrls"
              :key="index"
              :class="['indicator-dot', { active: currentImageIndex === index }]"
              @click="setImageIndex(index)"
            ></span>
          </div>
          <div v-if="post?.imageUrls?.length > 1" class="image-counter">
            <span class="counter-current">{{ currentImageIndex + 1 }}</span>
            <span class="counter-separator">/</span>
            <span class="counter-total">{{ post.imageUrls.length }}</span>
          </div>
        </div>
        <div class="right-panel">
          <div class="author-section">
            <div class="author-info">
              <div class="avatar-wrapper">
                <img v-lazy="{ src: getAvatarUrl(post?.avatar, post?.nickname), size: 'avatar' }" :alt="post?.nickname" class="author-avatar" width="52" height="52" />
                <div class="avatar-ring"></div>
              </div>
              <div class="author-detail">
                <span class="author-name">{{ post?.nickname }}</span>
                <span class="author-meta">
                  {{ post?.categoryName ? post.categoryName : '综合' }}
                </span>
              </div>
            </div>
          </div>
          <div class="content-section">
            <h2 class="post-title">{{ post?.title }}</h2>
            <p class="post-content">{{ post?.content }}</p>
            <div class="post-stats" v-if="post?.likeCount || post?.commentCount || post?.collectCount">
              <div class="stat-item">
                <el-icon><ChatLineSquare /></el-icon>
                <span class="stat-value">{{ post?.commentCount || 0 }}</span>
                <span class="stat-label">评论</span>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                <span class="stat-value">{{ post?.likeCount || 0 }}</span>
                <span class="stat-label">点赞</span>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                <span class="stat-value">{{ post?.collectCount || 0 }}</span>
                <span class="stat-label">收藏</span>
              </div>
            </div>
            <div class="post-tags" v-if="post?.tags?.length">
              <span v-for="(tag, index) in post.tags" :key="index" class="tag">#{{ tag }}</span>
            </div>
          </div>
          <div class="comments-section">
            <div class="comments-list">
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-avatar-wrapper">
                  <img v-lazy="{ src: getAvatarUrl(comment.avatar, comment.nickname), size: 'avatar' }" :alt="comment.nickname" class="comment-avatar" width="40" height="40" />
                </div>
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-name">{{ comment.nickname }}</span>
                    <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                  </div>
                  <p class="comment-text">{{ comment.content }}</p>
                  <div class="comment-actions">
                    <el-button size="small" text @click="likeComment(comment.id)">
                      <el-icon><Star /></el-icon>
                      <span>{{ comment.likeCount || 0 }}</span>
                    </el-button>
                    <el-button size="small" text>
                      <el-icon><ChatLineSquare /></el-icon>
                      <span>回复</span>
                    </el-button>
                  </div>
                </div>
              </div>
              <div v-if="comments.length === 0" class="empty-comments">
                <el-icon class="empty-icon"><ChatLineSquare /></el-icon>
                <span>暂无评论，快来抢沙发吧~</span>
              </div>
            </div>
          </div>
          <div class="bottom-section">
            <div class="comment-input-wrapper">
              <div class="input-group">
                <el-input
                  v-model="newComment"
                  size="small"
                  placeholder="说点什么... (按 Enter 发送)"
                  class="comment-input"
                  @keyup.enter="submitComment"
                >
                  <template #suffix>
                    <el-button :icon="ArrowRight" circle size="small" type="primary" @click="submitComment" :disabled="!newComment.trim()" />
                  </template>
                </el-input>
              </div>
            </div>
            <div class="action-bar">
              <el-button :type="isLiked ? 'danger' : 'default'" plain size="small" @click="toggleLike" class="action-bar-btn">
                <el-icon v-if="!isLiked"><Star /></el-icon>
                <el-icon v-else><StarFilled /></el-icon>
                <span>{{ post?.likeCount || 0 }}</span>
              </el-button>
              <el-button :type="isCollected ? 'warning' : 'default'" plain size="small" @click="toggleCollect" class="action-bar-btn">
                <el-icon v-if="!isCollected"><Collection /></el-icon>
                <el-icon v-else><CollectionTag /></el-icon>
                <span>{{ post?.collectCount || 0 }}</span>
              </el-button>
              <el-button plain size="small" class="action-bar-btn">
                <el-icon><ChatLineSquare /></el-icon>
                <span>{{ post?.commentCount || 0 }}</span>
              </el-button>
              <el-button plain size="small" @click="handleShare" class="action-bar-btn">
                <el-icon><Share /></el-icon>
                <span>分享</span>
              </el-button>
            </div>
          </div>
        </div>
        <button class="close-btn" @click="close">
          <span class="close-icon">✕</span>
        </button>
      </div>
    </div>
  </Teleport>
</template>
<script setup>
import { ref, computed, watch } from 'vue'
import { getAvatarUrl } from '../utils'
import { getUser } from '../store'
import { likeApi, collectApi, commentApi } from '../api'
import { ArrowRight, ChatLineSquare, Star, StarFilled, Share, Collection, CollectionTag, Picture } from '@element-plus/icons-vue'
const props = defineProps({
  visible: Boolean,
  post: Object
})
const emit = defineEmits(['close'])
const currentImageIndex = ref(0)
const isLiked = ref(false)
const isCollected = ref(false)
const newComment = ref('')
const comments = ref([])
const currentImageUrl = computed(() => {
  if (!props.post?.imageUrls?.length) return ''
  return props.post.imageUrls[currentImageIndex.value] || ''
})
watch(() => props.post, (newPost) => {
  if (newPost) {
    currentImageIndex.value = 0
    isLiked.value = newPost.liked || false
    isCollected.value = newPost.collected || false
    loadComments()
  }
}, { immediate: true })
watch(() => props.visible, (visible) => {
  if (visible && props.post) {
    loadComments()
  }
})
watch(() => props.visible, (visible) => {
  if (visible) {
    document.addEventListener('keydown', handleKeydown)
  } else {
    document.removeEventListener('keydown', handleKeydown)
  }
})
const handleKeydown = (e) => {
  if (!props.post?.imageUrls?.length || props.post.imageUrls.length <= 1) return
  if (e.key === 'ArrowLeft') {
    prevImage()
  } else if (e.key === 'ArrowRight') {
    nextImage()
  }
}
const loadComments = async () => {
  if (!props.post?.id) return
  try {
    const response = await commentApi.getList(props.post.id)
    if (response.data?.code === 200) {
      comments.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取评论失败:', error)
  }
}
const close = () => {
  emit('close')
}
const prevImage = () => {
  if (!props.post?.imageUrls?.length) return
  currentImageIndex.value = (currentImageIndex.value - 1 + props.post.imageUrls.length) % props.post.imageUrls.length
}
const nextImage = () => {
  if (!props.post?.imageUrls?.length) return
  currentImageIndex.value = (currentImageIndex.value + 1) % props.post.imageUrls.length
}
const setImageIndex = (index) => {
  currentImageIndex.value = index
}
const toggleLike = async () => {
  const user = getUser()
  if (!user) {
    alert('请先登录')
    return
  }
  try {
    const response = await likeApi.toggle(user.id, props.post.id)
    if (response.data?.code === 200) {
      isLiked.value = response.data.data?.liked
      if (props.post) {
        props.post.likeCount = response.data.data?.count
      }
    }
  } catch (error) {
    console.error('点赞失败:', error)
  }
}
const toggleCollect = async () => {
  const user = getUser()
  if (!user) {
    alert('请先登录')
    return
  }
  try {
    const response = await collectApi.toggle(user.id, props.post.id)
    if (response.data?.code === 200) {
      isCollected.value = response.data.data?.collected
      if (props.post) {
        props.post.collectCount = response.data.data?.count
      }
    }
  } catch (error) {
    console.error('收藏失败:', error)
  }
}
const likeComment = (commentId) => {
  const comment = comments.value.find(c => c.id === commentId)
  if (comment) {
    comment.likeCount = (comment.likeCount || 0) + 1
  }
}
const submitComment = async () => {
  const user = getUser()
  if (!user) {
    alert('请先登录')
    return
  }
  if (!newComment.value.trim()) return
  try {
    const response = await commentApi.add(props.post.id, user.id, newComment.value)
    if (response.data?.code === 200) {
      const newCommentData = response.data.data
      comments.value.unshift(newCommentData)
      newComment.value = ''
      if (props.post) {
        props.post.commentCount = (props.post.commentCount || 0) + 1
      }
    }
  } catch (error) {
    console.error('评论失败:', error)
    alert('评论失败，请稍后重试')
  }
}
const formatTime = (dateString) => {
  if (!dateString) return ''
  let date
  if (dateString instanceof Date) {
    date = dateString
  } else {
    const normalizedDateStr = String(dateString).replace(/\s+/g, 'T').trim()
    date = new Date(normalizedDateStr)
    if (isNaN(date.getTime())) {
      const dateMatch = String(dateString).match(/(\d{4})[-/](\d{1,2})[-/](\d{1,2})\s*(\d{1,2}):(\d{2})(?::(\d{2}))?/)
      if (dateMatch) {
        const [, year, month, day, hour, minute, second] = dateMatch
        date = new Date(year, month - 1, day, hour, minute, second || 0)
      } else {
        return ''
      }
    }
  }
  if (isNaN(date.getTime())) {
    return ''
  }
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}
const handleShare = () => {
  const url = window.location.origin + `/post/detail/${props.post.id}`
  if (navigator.share) {
    navigator.share({
      title: props.post.title,
      text: props.post.content,
      url
    })
  } else {
    navigator.clipboard.writeText(url)
    alert('链接已复制到剪贴板')
  }
}
</script>
<style scoped>
/* ========== Overlay ========== */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.55);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: overlayIn 0.3s ease;
  padding: var(--spacing-4);
}
@keyframes overlayIn { from { opacity: 0; } to { opacity: 1; } }
/* ========== Container ========== */
.modal-container {
  display: flex;
  background: white;
  border-radius: var(--radius-3xl);
  overflow: hidden;
  width: 95vw;
  max-width: 1100px;
  height: 85vh;
  max-height: 800px;
  box-shadow: var(--shadow-2xl), 0 0 0 1px rgba(139, 92, 246, 0.1);
  animation: modalIn 0.4s var(--transition-spring);
}
@keyframes modalIn {
  from { opacity: 0; transform: translateY(30px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
/* ========== Left Panel - Image ========== */
.left-panel {
  flex: 0 0 55%;
  background: #0f0f0f;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}
.image-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}
.image-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.post-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  animation: imageFadeIn 0.4s var(--transition-smooth);
}
@keyframes imageFadeIn {
  from { opacity: 0; transform: scale(1.03); }
  to { opacity: 1; transform: scale(1); }
}
.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-4);
  color: rgba(255, 255, 255, 0.4);
}
.no-image-icon-wrapper {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-2xl);
  background: rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-image-text { font-size: var(--font-size-sm); letter-spacing: 0.05em; }
.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 44px;
  height: 44px;
  border-radius: var(--radius-full);
  border: none;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(12px);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 5;
  transition: all var(--transition-spring);
  font-size: 20px;
}
.nav-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-50%) scale(1.1);
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.15);
}
.nav-btn:active { transform: translateY(-50%) scale(0.95); }
.left-nav { left: var(--spacing-3); }
.right-nav { right: var(--spacing-3); }
.nav-icon { font-size: 18px; font-weight: bold; }
.image-indicator {
  display: flex;
  justify-content: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) 0;
}
.indicator-dot {
  width: 6px;
  height: 6px;
  border-radius: var(--radius-full);
  background: rgba(255, 255, 255, 0.3);
  cursor: pointer;
  transition: all var(--transition-smooth);
}
.indicator-dot.active {
  width: 24px;
  border-radius: var(--radius-full);
  background: var(--primary-400);
  box-shadow: 0 0 8px rgba(139, 92, 246, 0.5);
}
.indicator-dot:hover:not(.active) { background: rgba(255, 255, 255, 0.6); }
.image-counter {
  position: absolute;
  top: var(--spacing-3);
  right: var(--spacing-3);
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  color: white;
  padding: var(--spacing-1) var(--spacing-3);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  letter-spacing: 0.05em;
  z-index: 5;
}
.counter-separator { color: rgba(255, 255, 255, 0.4); }
/* ========== Right Panel ========== */
.right-panel {
  flex: 0 0 45%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}
.author-section {
  padding: var(--spacing-5);
  border-bottom: 1px solid var(--gray-100);
  flex-shrink: 0;
}
.author-info { display: flex; align-items: center; gap: var(--spacing-3); }
.avatar-wrapper { position: relative; flex-shrink: 0; }
.author-avatar {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-full);
  object-fit: cover;
  border: 3px solid var(--primary-100);
  transition: border-color var(--transition-fast);
}
.avatar-ring {
  position: absolute;
  inset: -3px;
  border-radius: var(--radius-full);
  background: var(--gradient-primary);
  opacity: 0;
  transition: opacity var(--transition-fast);
  z-index: -1;
}
.author-info:hover .avatar-ring { opacity: 1; }
.author-detail { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.author-name { font-weight: var(--font-weight-bold); font-size: var(--font-size-base); color: var(--gray-800); }
.author-meta { font-size: var(--font-size-xs); color: var(--gray-400); display: flex; align-items: center; gap: var(--spacing-1); }
.content-section {
  padding: var(--spacing-5);
  flex: 1;
  overflow-y: auto;
  border-bottom: 1px solid var(--gray-100);
}
.content-section::-webkit-scrollbar { width: 4px; }
.content-section::-webkit-scrollbar-thumb { background: var(--gray-200); border-radius: var(--radius-full); }
.post-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--gray-900);
  margin-bottom: var(--spacing-4);
  line-height: 1.4;
}
.post-content {
  font-size: var(--font-size-sm);
  color: var(--gray-600);
  line-height: 1.7;
  margin-bottom: var(--spacing-5);
  white-space: pre-wrap;
  word-break: break-word;
}
.post-stats {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  padding: var(--spacing-3) 0;
  margin-bottom: var(--spacing-4);
}
.post-stats .stat-item { display: flex; align-items: center; gap: var(--spacing-1); color: var(--gray-500); font-size: var(--font-size-xs); }
.post-stats .stat-value { font-weight: var(--font-weight-bold); color: var(--gray-700); }
.post-stats .stat-label { color: var(--gray-400); }
.post-stats .stat-divider { width: 1px; height: 12px; background: var(--gray-200); }
.post-tags { display: flex; flex-wrap: wrap; gap: var(--spacing-2); }
.tag {
  display: inline-block;
  padding: var(--spacing-1) var(--spacing-3);
  background: linear-gradient(135deg, var(--primary-50), var(--primary-100));
  color: var(--primary-600);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  transition: all var(--transition-fast);
  cursor: pointer;
}
.tag:hover { background: linear-gradient(135deg, var(--primary-100), var(--primary-200)); transform: translateY(-1px); }
/* ========== Comments ========== */
.comments-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}
.comments-list {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-4) var(--spacing-5);
}
.comments-list::-webkit-scrollbar { width: 4px; }
.comments-list::-webkit-scrollbar-thumb { background: var(--gray-200); border-radius: var(--radius-full); }
.comment-item {
  display: flex;
  gap: var(--spacing-3);
  padding: var(--spacing-3) 0;
  border-bottom: 1px solid var(--gray-50);
}
.comment-item:last-child { border-bottom: none; }
.comment-avatar-wrapper { flex-shrink: 0; }
.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-full);
  object-fit: cover;
  border: 2px solid var(--gray-100);
}
.comment-body { flex: 1; min-width: 0; }
.comment-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  margin-bottom: var(--spacing-1);
}
.commenter-name { font-weight: var(--font-weight-semibold); font-size: var(--font-size-xs); color: var(--gray-800); }
.comment-time { font-size: var(--font-size-xs); color: var(--gray-400); }
.commenter-content { font-size: var(--font-size-sm); color: var(--gray-600); line-height: 1.5; word-break: break-word; }
.no-comments {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-8);
  color: var(--gray-400);
  gap: var(--spacing-2);
}
.no-comments-icon { font-size: 40px; opacity: 0.4; }
.no-comments-text { font-size: var(--font-size-sm); }
/* ========== Responsive ========== */
@media (max-width: 768px) {
  .modal-container { flex-direction: column; width: 100vw; height: 100vh; max-height: 100vh; border-radius: 0; }
  .left-panel { flex: 0 0 45%; }
  .right-panel { flex: 1; }
  .post-title { font-size: var(--font-size-lg); }
  .author-avatar { width: 42px; height: 42px; }
  .modal-overlay { padding: 0; }
}
/* ========== Comment Actions (Element Plus overrides) ========== */
.comment-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  margin-top: var(--spacing-2);
}
.comment-actions .el-button--small {
  padding: 2px 6px;
  font-size: var(--font-size-xs);
  color: var(--gray-400);
}
.comment-actions .el-button--small:hover {
  color: var(--primary-500);
  background: var(--gray-50);
}
/* ========== Action Bar (Element Plus overrides) ========== */
.action-bar {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: var(--spacing-3) var(--spacing-5);
  border-top: 1px solid var(--gray-100);
  gap: var(--spacing-2);
}
.action-bar-btn.el-button--small {
  font-size: var(--font-size-xs);
  border-radius: var(--radius-lg);
  transition: all var(--transition-spring);
}
.action-bar-btn.el-button--small:hover {
  transform: translateY(-1px);
}
/* ========== Comment Input (Element Plus overrides) ========== */
.comment-input-wrapper {
  padding: var(--spacing-3) var(--spacing-5);
  border-top: 1px solid var(--gray-100);
}
.comment-input-wrapper .el-input {
  --el-input-bg-color: var(--gray-50);
  --el-input-border-color: transparent;
  --el-input-hover-border-color: var(--primary-300);
  --el-input-focus-border-color: var(--primary-400);
}
.comment-input-wrapper .el-input .el-input__wrapper {
  border-radius: var(--radius-xl);
  padding-right: 4px;
  transition: all var(--transition-fast);
  box-shadow: none;
}
.comment-input-wrapper .el-input .el-input__wrapper:hover {
  box-shadow: 0 0 0 1px var(--primary-300) inset;
}
.comment-input-wrapper .el-input.is-focus .el-input__wrapper {
  box-shadow: 0 0 0 1px var(--primary-400) inset, 0 0 0 3px rgba(139, 92, 246, 0.1);
  background: white;
}
</style>
