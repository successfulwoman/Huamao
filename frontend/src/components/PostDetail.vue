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
                :src="currentImageUrl"
                :alt="`帖子图片 ${currentImageIndex + 1}/${post.imageUrls.length}`"
                class="post-image"
              />
              <div v-else class="no-image">
                <div class="no-image-icon-wrapper">
                  <span class="no-image-icon">📷</span>
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
                <img :src="getAvatar(post?.avatar, post?.nickname)" :alt="post?.nickname" class="author-avatar" />
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
                  <img :src="getAvatar(comment.avatar, comment.nickname)" :alt="comment.nickname" class="comment-avatar" />
                </div>
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-name">{{ comment.nickname }}</span>
                    <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                  </div>
                  <p class="comment-text">{{ comment.content }}</p>
                  <div class="comment-actions">
                    <button class="comment-action" @click="likeComment(comment.id)">
                      <el-icon><Star /></el-icon>
                      <span>{{ comment.likeCount || 0 }}</span>
                    </button>
                    <button class="comment-action reply-btn">
                      <el-icon><ChatLineSquare /></el-icon>
                      <span>回复</span>
                    </button>
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
                <input
                  v-model="newComment"
                  type="text"
                  placeholder="说点什么... (按 Enter 发送)"
                  class="comment-input"
                  @keyup.enter="submitComment"
                />
                <button class="send-btn" @click="submitComment" :disabled="!newComment.trim()">
                  <el-icon><ArrowRight /></el-icon>
                </button>
              </div>
            </div>
            <div class="action-bar">
              <button :class="['action-btn', { liked: isLiked }]" @click="toggleLike">
                <el-icon v-if="!isLiked"><Star /></el-icon>
                <el-icon v-else><StarFilled /></el-icon>
                <span class="btn-text">{{ post?.likeCount || 0 }}</span>
              </button>
              <button :class="['action-btn', { collected: isCollected }]" @click="toggleCollect">
                <el-icon v-if="!isCollected"><Collection /></el-icon>
                <el-icon v-else><CollectionTag /></el-icon>
                <span class="btn-text">{{ post?.collectCount || 0 }}</span>
              </button>
              <button class="action-btn">
                <el-icon><ChatLineSquare /></el-icon>
                <span class="btn-text">{{ post?.commentCount || 0 }}</span>
              </button>
              <button class="action-btn" @click="handleShare">
                <el-icon><Share /></el-icon>
                <span class="btn-text">分享</span>
              </button>
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
import { getAvatar } from '../utils'
import { getUser } from '../store'
import { likeApi, collectApi, commentApi } from '../api'
import { ArrowRight, ChatLineSquare, Star, StarFilled, Share, Collection, CollectionTag } from '@element-plus/icons-vue'

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
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.88);
  backdrop-filter: blur(12px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-container {
  display: flex;
  width: 95%;
  max-width: 1150px;
  height: 88vh;
  background: linear-gradient(180deg, #ffffff 0%, #fafbfc 100%);
  border-radius: 28px;
  overflow: hidden;
  position: relative;
  box-shadow:
    0 32px 64px -12px rgba(0, 0, 0, 0.5),
    0 8px 24px -4px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: scale(0.92) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.left-panel {
  flex: 1.2;
  background: linear-gradient(135deg, #0a0a0a 0%, #1a1a2e 50%, #16213e 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.left-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 30% 30%, rgba(255, 77, 79, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.image-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 20px;
}

.image-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.post-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
  animation: imageFadeIn 0.3s ease;
}

@keyframes imageFadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.6);
}

.no-image-icon-wrapper {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  border: 2px dashed rgba(255, 255, 255, 0.2);
}

.no-image-icon {
  font-size: 3.5rem;
  opacity: 0.5;
}

.no-image-text {
  font-size: 1rem;
  opacity: 0.6;
  letter-spacing: 0.5px;
}

.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  opacity: 0;
  z-index: 10;
}

.image-container:hover .nav-btn {
  opacity: 1;
}

.nav-btn:hover {
  background: rgba(255, 255, 255, 0.22);
  transform: translateY(-50%) scale(1.12);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.4);
}

.nav-btn:active {
  transform: translateY(-50%) scale(0.95);
}

.nav-icon {
  font-size: 2.2rem;
  font-weight: 300;
  line-height: 1;
  color: white;
}

.left-nav {
  left: 28px;
}

.right-nav {
  right: 28px;
}

.image-indicator {
  position: absolute;
  bottom: 28px;
  display: flex;
  gap: 10px;
  z-index: 5;
}

.indicator-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.35);
  cursor: pointer;
  transition: all 0.3s ease;
}

.indicator-dot:hover {
  background: rgba(255, 255, 255, 0.6);
  transform: scale(1.25);
}

.indicator-dot.active {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff6b6b 100%);
  width: 28px;
  border-radius: 6px;
  transform: scale(1);
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.4);
}

.image-counter {
  position: absolute;
  top: 24px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.55);
  backdrop-filter: blur(12px);
  color: white;
  padding: 8px 20px;
  border-radius: 24px;
  font-size: 0.9rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
  z-index: 5;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.counter-current {
  font-size: 1.1rem;
  font-weight: 700;
}

.counter-separator {
  opacity: 0.5;
}

.counter-total {
  opacity: 0.7;
}

.right-panel {
  width: 420px;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #ffffff 0%, #f8f9fa 100%);
  overflow-y: auto;
}

.author-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  background: white;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.avatar-wrapper {
  position: relative;
}

.author-avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid white;
  box-shadow:
    0 4px 12px rgba(0, 0, 0, 0.1),
    0 2px 4px rgba(0, 0, 0, 0.05);
}

.avatar-ring {
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border-radius: 50%;
  border: 2px solid transparent;
  background: linear-gradient(135deg, #ff4d4f 0%, #ff6b6b 50%, #ffa502 100%) border-box;
  -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: destination-out;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-wrapper:hover .avatar-ring {
  opacity: 1;
}

.author-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-weight: 700;
  font-size: 1.05rem;
  color: #1a1a1a;
  letter-spacing: -0.01em;
}

.author-meta {
  font-size: 0.8rem;
  color: #999;
  letter-spacing: 0.01em;
}

.content-section {
  padding: 24px;
  background: white;
}

.post-title {
  font-size: 1.45rem;
  font-weight: 800;
  margin-bottom: 14px;
  color: #0a0a0a;
  line-height: 1.3;
  letter-spacing: -0.02em;
}

.post-content {
  color: #4a4a4a;
  line-height: 1.8;
  margin-bottom: 20px;
  font-size: 0.98rem;
}

.post-stats {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 16px;
  padding: 16px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #888;
  font-size: 0.85rem;
  font-weight: 500;
}

.stat-value {
  font-size: 1rem;
  font-weight: 700;
  color: #333;
}

.stat-label {
  color: #999;
  font-size: 0.8rem;
}

.stat-divider {
  width: 1px;
  height: 24px;
  background: #e8e8e8;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 12px;
}

.tag {
  background: linear-gradient(135deg, #f0f9ff 0%, #e8f4ff 100%);
  color: #0369a1;
  font-size: 0.82rem;
  padding: 6px 14px;
  border-radius: 14px;
  font-weight: 600;
  border: 1px solid rgba(3, 105, 161, 0.1);
  transition: all 0.2s ease;
}

.tag:hover {
  background: linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%);
  transform: translateY(-1px);
}

.comments-section {
  flex: 1;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #fafbfc;
}

.comments-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 24px;
  background: #fafbfc;
}

.comment-item {
  display: flex;
  padding: 18px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
  transition: all 0.2s ease;
}

.comment-item:hover {
  background: rgba(0, 0, 0, 0.02);
}

.comment-avatar-wrapper {
  flex-shrink: 0;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
  transition: transform 0.2s ease;
}

.comment-avatar:hover {
  transform: scale(1.05);
}

.comment-content {
  flex: 1;
  margin-left: 14px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-name {
  font-weight: 700;
  font-size: 0.92rem;
  color: #2a2a2a;
}

.comment-time {
  font-size: 0.78rem;
  color: #bbb;
}

.comment-text {
  font-size: 0.92rem;
  color: #555;
  line-height: 1.65;
  margin-bottom: 10px;
}

.comment-actions {
  display: flex;
  gap: 18px;
}

.comment-action {
  display: flex;
  align-items: center;
  gap: 5px;
  background: none;
  border: none;
  color: #aaa;
  font-size: 0.82rem;
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 4px 8px;
  border-radius: 6px;
}

.comment-action:hover {
  color: #666;
  background: rgba(0, 0, 0, 0.04);
}

.empty-comments {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #ccc;
  gap: 12px;
}

.empty-icon {
  font-size: 3rem;
  opacity: 0.4;
}

.bottom-section {
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  padding: 20px 24px;
  background: white;
}

.comment-input-wrapper {
  margin-bottom: 14px;
}

.input-group {
  display: flex;
  gap: 12px;
  align-items: center;
}

.comment-input {
  flex: 1;
  padding: 14px 20px;
  border: 2px solid #f0f0f0;
  border-radius: 28px;
  font-size: 0.92rem;
  outline: none;
  background: #fafbfc;
  transition: all 0.3s ease;
}

.comment-input:focus {
  border-color: #ff4d4f;
  background: white;
  box-shadow: 0 0 0 4px rgba(255, 77, 79, 0.08);
}

.comment-input::placeholder {
  color: #bbb;
}

.send-btn {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #ff4d4f 0%, #ff6b6b 100%);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(255, 77, 79, 0.35);
}

.send-btn:hover:not(:disabled) {
  transform: scale(1.08);
  box-shadow: 0 8px 28px rgba(255, 77, 79, 0.45);
}

.send-btn:active:not(:disabled) {
  transform: scale(0.95);
}

.send-btn:disabled {
  background: #e0e0e0;
  cursor: not-allowed;
  box-shadow: none;
}

.action-bar {
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
}

.action-btn {
  background: none;
  border: none;
  color: #777;
  cursor: pointer;
  padding: 10px 18px;
  display: flex;
  align-items: center;
  gap: 8px;
  border-radius: 14px;
  transition: all 0.3s ease;
  font-weight: 600;
  font-size: 0.9rem;
}

.action-btn:hover {
  color: #444;
  background: rgba(0, 0, 0, 0.04);
}

.action-btn.liked {
  color: #ff4d4f;
  background: rgba(255, 77, 79, 0.08);
}

.action-btn.collected {
  color: #ffc107;
  background: rgba(255, 193, 7, 0.08);
}

.btn-text {
  font-size: 0.88rem;
}

.close-btn {
  position: absolute;
  top: 18px;
  right: 18px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.35s ease;
  z-index: 100;
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.65);
  transform: rotate(90deg) scale(1.1);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.close-icon {
  font-size: 1.2rem;
  font-weight: 300;
}

::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #ddd 0%, #ccc 100%);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #ccc 0%, #bbb 100%);
}

@media (max-width: 900px) {
  .modal-container {
    flex-direction: column;
    width: 98%;
    height: 95vh;
  }

  .left-panel {
    height: 45%;
  }

  .right-panel {
    width: 100%;
    height: 55%;
  }
}
</style>
