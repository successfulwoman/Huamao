<template>
  <Teleport to="body">
    <div v-if="visible" class="modal-overlay" @click.self="close">
      <div class="modal-container">
        <!-- 左侧图片/视频展示区 -->
        <div class="left-panel">
          <div class="image-container">
            <button class="nav-btn left-nav" @click="prevImage" v-if="post?.imageUrls?.length > 1">
              <span>‹</span>
            </button>
            
            <div class="image-wrapper">
              <img 
                v-if="post?.imageUrls?.length" 
                v-lazy="currentImageUrl" 
                alt="帖子图片" 
                class="post-image"
              />
              <div v-else class="no-image">
                <span class="no-image-icon">📷</span>
                <span class="no-image-text">暂无图片</span>
              </div>
            </div>
            
            <button class="nav-btn right-nav" @click="nextImage" v-if="post?.imageUrls?.length > 1">
              <span>›</span>
            </button>
          </div>
          
          <!-- 图片指示器 -->
          <div v-if="post?.imageUrls?.length > 1" class="image-indicator">
            <span 
              v-for="(_, index) in post.imageUrls" 
              :key="index"
              :class="['indicator-dot', { active: currentImageIndex === index }]"
              @click="setImageIndex(index)"
            ></span>
          </div>
        </div>

        <!-- 右侧信息区 -->
        <div class="right-panel">
          <!-- 作者信息 -->
          <div class="author-section">
            <div class="author-info">
              <img :src="getAvatar(post?.nickname)" :alt="post?.nickname" class="author-avatar" />
              <div class="author-detail">
                <span class="author-name">{{ post?.nickname }}</span>
                <span class="author-location">{{ post?.location }}</span>
              </div>
            </div>
            <button 
              :class="['follow-btn', { followed: isFollowed }]" 
              @click="toggleFollow"
            >
              {{ isFollowed ? '已关注' : '关注' }}
            </button>
          </div>

          <!-- 帖子内容 -->
          <div class="content-section">
            <h2 class="post-title">{{ post?.title }}</h2>
            <p class="post-content">{{ post?.content }}</p>
            <div class="post-tags">
              <span v-for="tag in post?.tags" :key="tag" class="tag">#{{ tag }}</span>
            </div>
          </div>

          <!-- 评论列表 -->
          <div class="comments-section">
            <div class="comments-header">
              <span class="comments-count">共 {{ comments.length }} 条评论</span>
            </div>
            
            <div class="comments-list">
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <img :src="getAvatar(comment.nickname)" :alt="comment.nickname" class="comment-avatar" />
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-name">{{ comment.nickname }}</span>
                    <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                  </div>
                  <p class="comment-text">{{ comment.content }}</p>
                  <div class="comment-actions">
                    <button class="comment-action" @click="likeComment(comment.id)">
                      <span>❤️</span>
                      <span>{{ comment.likeCount }}</span>
                    </button>
                    <button class="comment-action reply-btn">
                      <span>💬</span>
                      <span>回复</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 底部互动区 -->
          <div class="bottom-section">
            <div class="comment-input-wrapper">
              <input 
                v-model="newComment" 
                type="text" 
                placeholder="说点什么..." 
                class="comment-input"
                @keyup.enter="submitComment"
              />
            </div>
            <div class="action-bar">
              <button :class="['action-btn', { liked: isLiked }]" @click="toggleLike">
                <span class="action-icon">{{ isLiked ? '❤️' : '🤍' }}</span>
                <span class="action-text">{{ post?.likeCount }}</span>
              </button>
              <button :class="['action-btn', { collected: isCollected }]" @click="toggleCollect">
                <span class="action-icon">{{ isCollected ? '⭐' : '☆' }}</span>
                <span class="action-text">{{ post?.collectCount }}</span>
              </button>
              <button class="action-btn comment-action-btn">
                <span class="action-icon">💬</span>
                <span class="action-text">{{ post?.commentCount }}</span>
              </button>
              <button class="action-btn share-action-btn">
                <span class="action-icon">🔗</span>
                <span class="action-text">分享</span>
              </button>
            </div>
          </div>
        </div>

        <!-- 关闭按钮 -->
        <button class="close-btn" @click="close">✕</button>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  visible: Boolean,
  post: Object
})

const emit = defineEmits(['close'])

// 状态
const currentImageIndex = ref(0)
const isLiked = ref(false)
const isCollected = ref(false)
const isFollowed = ref(false)
const newComment = ref('')

// 评论假数据
const comments = ref([
  {
    id: 1,
    nickname: 'Zoey',
    content: '晚安哟😴',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 2).toISOString(),
    likeCount: 11,
    location: '广东'
  },
  {
    id: 2,
    nickname: '11ju',
    content: '每一帧都绝美！！！',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 5).toISOString(),
    likeCount: 8,
    location: '重庆'
  },
  {
    id: 3,
    nickname: '工作时称植物',
    content: '好美',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 8).toISOString(),
    likeCount: 1,
    location: '四川'
  }
])

// 计算属性
const currentImageUrl = computed(() => {
  if (!props.post?.imageUrls?.length) return ''
  return props.post.imageUrls[currentImageIndex.value] || ''
})

// 方法
const close = () => {
  emit('close')
}

const prevImage = () => {
  if (props.post?.imageUrls?.length && currentImageIndex.value > 0) {
    currentImageIndex.value--
  }
}

const nextImage = () => {
  if (props.post?.imageUrls?.length && currentImageIndex.value < props.post.imageUrls.length - 1) {
    currentImageIndex.value++
  }
}

const setImageIndex = (index) => {
  currentImageIndex.value = index
}

const toggleLike = () => {
  isLiked.value = !isLiked.value
}

const toggleCollect = () => {
  isCollected.value = !isCollected.value
}

const toggleFollow = () => {
  isFollowed.value = !isFollowed.value
}

const likeComment = (commentId) => {
  const comment = comments.value.find(c => c.id === commentId)
  if (comment) {
    comment.likeCount++
  }
}

const submitComment = () => {
  if (newComment.value.trim()) {
    comments.value.push({
      id: Date.now(),
      nickname: '游客',
      content: newComment.value,
      createTime: new Date().toISOString(),
      likeCount: 0,
      location: ''
    })
    newComment.value = ''
  }
}

const getAvatar = (nickname) => {
  const colors = ['#8b5cf6', '#a855f7', '#c084fc', '#d8b4fe', '#e9d5ff']
  const index = nickname ? nickname.charCodeAt(0) % colors.length : 0
  return `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 40 40'%3E%3Ccircle cx='20' cy='20' r='18' fill='${colors[index]}'/%3E%3Ctext x='20' y='25' text-anchor='middle' fill='white' font-size='16' font-weight='bold'%3E${nickname ? nickname.charAt(0) : '?'}`
}

const formatTime = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 30) return `${days}天前`
  
  return `${date.getMonth() + 1}-${date.getDate()}`
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-container {
  width: 90%;
  max-width: 1200px;
  height: 85vh;
  background: white;
  border-radius: 20px;
  display: flex;
  overflow: hidden;
  position: relative;
  animation: slideUp 0.3s ease;
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

/* 左侧面板 */
.left-panel {
  flex: 1;
  background: #1a1a1a;
  position: relative;
}

.image-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.nav-btn {
  position: absolute;
  width: 50px;
  height: 50px;
  background: rgba(0, 0, 0, 0.5);
  border: none;
  border-radius: 50%;
  color: white;
  font-size: 2rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  z-index: 10;
}

.nav-btn:hover {
  background: rgba(0, 0, 0, 0.7);
  transform: scale(1.1);
}

.left-nav {
  left: 20px;
}

.right-nav {
  right: 20px;
}

.image-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.post-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  color: #666;
}

.no-image-icon {
  font-size: 4rem;
}

.no-image-text {
  font-size: 1.2rem;
}

.image-indicator {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}

.indicator-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  cursor: pointer;
  transition: all 0.3s ease;
}

.indicator-dot.active {
  background: white;
  transform: scale(1.2);
}

/* 右侧面板 */
.right-panel {
  width: 400px;
  display: flex;
  flex-direction: column;
  max-height: 100%;
}

/* 作者信息 */
.author-section {
  padding: 1rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #f0f0f0;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.author-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
}

.author-detail {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 600;
  color: #1a1a1a;
}

.author-location {
  font-size: 0.8rem;
  color: #999;
}

.follow-btn {
  padding: 0.5rem 1.5rem;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  border: none;
  border-radius: 20px;
  color: white;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.follow-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.follow-btn.followed {
  background: #f0f0f0;
  color: #666;
}

/* 帖子内容 */
.content-section {
  padding: 1rem;
  border-bottom: 1px solid #f0f0f0;
}

.post-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 0.5rem;
  line-height: 1.4;
}

.post-content {
  font-size: 0.95rem;
  color: #333;
  line-height: 1.6;
  margin-bottom: 0.75rem;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  font-size: 0.85rem;
  color: #8b5cf6;
  cursor: pointer;
}

.tag:hover {
  text-decoration: underline;
}

/* 评论列表 */
.comments-section {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
}

.comments-header {
  margin-bottom: 1rem;
}

.comments-count {
  font-size: 0.9rem;
  color: #666;
  font-weight: 500;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.comment-item {
  display: flex;
  gap: 0.75rem;
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
  gap: 0.75rem;
  margin-bottom: 0.375rem;
}

.comment-name {
  font-weight: 600;
  font-size: 0.9rem;
  color: #1a1a1a;
}

.comment-time {
  font-size: 0.75rem;
  color: #999;
}

.comment-text {
  font-size: 0.9rem;
  color: #333;
  line-height: 1.5;
  margin-bottom: 0.375rem;
}

.comment-actions {
  display: flex;
  gap: 1.5rem;
}

.comment-action {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  background: none;
  border: none;
  font-size: 0.8rem;
  color: #999;
  cursor: pointer;
  transition: color 0.2s ease;
}

.comment-action:hover {
  color: #ef4444;
}

.reply-btn:hover {
  color: #8b5cf6;
}

/* 底部互动区 */
.bottom-section {
  padding: 1rem;
  border-top: 1px solid #f0f0f0;
}

.comment-input-wrapper {
  margin-bottom: 0.75rem;
}

.comment-input {
  width: 100%;
  padding: 0.625rem 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  font-size: 0.9rem;
  outline: none;
  transition: all 0.3s ease;
}

.comment-input:focus {
  border-color: #8b5cf6;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.comment-input::placeholder {
  color: #999;
}

.action-bar {
  display: flex;
  align-items: center;
  justify-content: space-around;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
  background: none;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-icon {
  font-size: 1.5rem;
}

.action-text {
  font-size: 0.75rem;
  color: #666;
}

.action-btn:hover .action-icon {
  transform: scale(1.1);
}

.action-btn:hover .action-text {
  color: #8b5cf6;
}

.action-btn.liked .action-icon {
  animation: likeAnim 0.3s ease;
}

.action-btn.liked .action-text {
  color: #ef4444;
}

.action-btn.collected .action-icon {
  animation: collectAnim 0.3s ease;
}

.action-btn.collected .action-text {
  color: #f59e0b;
}

@keyframes likeAnim {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.3); }
}

@keyframes collectAnim {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2) rotate(-10deg); }
}

/* 关闭按钮 */
.close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 36px;
  height: 36px;
  background: rgba(0, 0, 0, 0.5);
  border: none;
  border-radius: 50%;
  color: white;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 20;
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.7);
}

/* 响应式 */
@media (max-width: 900px) {
  .modal-container {
    width: 100%;
    height: 100vh;
    border-radius: 0;
    flex-direction: column;
  }
  
  .left-panel {
    flex: 1;
    min-height: 300px;
  }
  
  .right-panel {
    width: 100%;
    max-height: 50%;
  }
  
  .comments-section {
    max-height: 200px;
  }
}
</style>
