<template>
  <div class="post-card" @click="handleClick">
    <!-- 用户信息 -->
    <div class="post-header">
      <div class="user-info">
        <img 
          :src="generateAvatar(post.nickname)" 
          :alt="post.nickname" 
          class="user-avatar" 
        />
        <div class="user-detail">
          <span class="user-name">{{ post.nickname }}</span>
          <span class="post-time">{{ formatTime(post.createTime) }}</span>
        </div>
      </div>
      <div class="post-category">
        <span class="category-icon">{{ getCategoryIcon(post.categoryName) }}</span>
        <span class="category-name">{{ post.categoryName }}</span>
      </div>
    </div>
    
    <!-- 帖子内容 -->
    <div class="post-content">
      <h3 class="post-title">{{ post.title }}</h3>
      <p class="post-text">{{ truncateText(post.content) }}</p>
    </div>
    
    <!-- 帖子图片 -->
    <div v-if="post.imageUrls && post.imageUrls.length > 0" class="post-images">
      <div :class="getImageGridClass(post.imageUrls.length)">
        <img 
                v-for="(img, index) in post.imageUrls.slice(0, 9)" 
                :key="index"
                v-lazy="img"
                :alt="`帖子图片 ${index + 1}`" 
                class="post-image"
              />
        <div v-if="post.imageUrls.length > 9" class="image-more">
          +{{ post.imageUrls.length - 9 }}
        </div>
      </div>
    </div>
    
    <!-- 神评论 -->
    <div v-if="post.topComment" class="top-comment">
      <div class="comment-info">
        <span class="commenter">{{ post.topComment.nickname }}</span>
        <span class="comment-content">{{ post.topComment.content }}</span>
      </div>
    </div>
    
    <!-- 互动信息 -->
    <div class="post-footer">
      <div class="interaction-stats">
        <span class="stat-item">
          <span class="stat-icon">💬</span>
          <span class="stat-count">{{ post.commentCount }}</span>
        </span>
        <span class="stat-item">
          <span class="stat-icon">❤️</span>
          <span class="stat-count">{{ post.likeCount }}</span>
        </span>
        <span class="stat-item">
          <span class="stat-icon">⭐</span>
          <span class="stat-count">{{ post.collectCount }}</span>
        </span>
      </div>
      <div class="post-actions">
        <button 
          class="action-btn" 
          :class="{ liked: post.liked }"
          @click.stop="handleLike"
        >
          {{ post.liked ? '❤️' : '🤍' }}
        </button>
        <button 
          class="action-btn" 
          :class="{ collected: post.collected }"
          @click.stop="handleCollect"
        >
          {{ post.collected ? '⭐' : '☆' }}
        </button>
        <button class="action-btn" @click.stop="handleComment">
          💬
        </button>
        <button class="action-btn" @click.stop="handleShare">
          🔗
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { generateAvatar, formatTime, getCategoryIcon, truncateText } from '../utils'

const props = defineProps({
  post: {
    type: Object,
    required: true
  }
})

const emit = defineEmits([
  'click',
  'like',
  'collect',
  'comment',
  'share'
])

const handleClick = () => {
  emit('click', props.post)
}

const handleLike = () => {
  emit('like', props.post.id)
}

const handleCollect = () => {
  emit('collect', props.post.id)
}

const handleComment = () => {
  emit('comment', props.post.id)
}

const handleShare = () => {
  emit('share', props.post)
}

const getImageGridClass = (count) => {
  if (count === 1) return 'grid-cols-1'
  if (count === 2) return 'grid-cols-2'
  if (count === 3) return 'grid-cols-2 grid-rows-2'
  if (count === 4) return 'grid-cols-2 grid-rows-2'
  if (count === 5 || count === 6) return 'grid-cols-3'
  if (count === 7 || count === 8) return 'grid-cols-3 grid-rows-3'
  return 'grid-cols-3 grid-rows-3'
}
</script>

<style scoped>
.post-card {
  background: var(--glass-bg);
  border: var(--glass-border);
  border-radius: var(--radius-2xl);
  padding: var(--spacing-5);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.post-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
}

/* 头部信息 */
.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-4);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full);
  object-fit: cover;
  border: 2px solid var(--primary-100);
}

.user-detail {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-1);
}

.user-name {
  font-weight: var(--font-weight-semibold);
  color: var(--gray-800);
  font-size: var(--font-size-sm);
}

.post-time {
  font-size: var(--font-size-xs);
  color: var(--gray-500);
}

.post-category {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  padding: var(--spacing-1) var(--spacing-2);
  background: var(--primary-50);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  color: var(--primary-700);
}

.category-icon {
  font-size: var(--font-size-sm);
}

/* 内容区域 */
.post-content {
  margin-bottom: var(--spacing-4);
}

.post-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--gray-900);
  margin-bottom: var(--spacing-2);
  line-height: 1.4;
}

.post-text {
  font-size: var(--font-size-sm);
  color: var(--gray-600);
  line-height: 1.5;
  margin-bottom: var(--spacing-3);
}

/* 图片区域 */
.post-images {
  margin-bottom: var(--spacing-4);
  border-radius: var(--radius-lg);
  overflow: hidden;
  position: relative;
}

.post-images > div {
  display: grid;
  gap: 2px;
  aspect-ratio: 1 / 1;
}

.grid-cols-1 {
  grid-template-columns: 1fr;
}

.grid-cols-2 {
  grid-template-columns: 1fr 1fr;
}

.grid-cols-3 {
  grid-template-columns: 1fr 1fr 1fr;
}

.grid-rows-2 {
  grid-template-rows: 1fr 1fr;
}

.grid-rows-3 {
  grid-template-rows: 1fr 1fr 1fr;
}

.post-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.post-card:hover .post-image {
  transform: scale(1.05);
}

.image-more {
  position: absolute;
  bottom: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: var(--spacing-1) var(--spacing-2);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  border-radius: var(--radius-sm) 0 0 0;
}

/* 神评论 */
.top-comment {
  background: var(--gray-50);
  border-radius: var(--radius-lg);
  padding: var(--spacing-3);
  margin-bottom: var(--spacing-4);
  border-left: 3px solid var(--primary-400);
}

.comment-info {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-2);
}

.commenter {
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-xs);
  color: var(--gray-700);
  flex-shrink: 0;
}

.comment-content {
  font-size: var(--font-size-xs);
  color: var(--gray-600);
  line-height: 1.4;
  flex: 1;
}

/* 底部互动 */
.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-3);
  border-top: 1px solid var(--gray-100);
}

.interaction-stats {
  display: flex;
  gap: var(--spacing-4);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-xs);
  color: var(--gray-500);
}

.stat-icon {
  font-size: var(--font-size-sm);
}

.stat-count {
  font-weight: var(--font-weight-medium);
}

.post-actions {
  display: flex;
  gap: var(--spacing-3);
}

.action-btn {
  background: transparent;
  border: none;
  font-size: var(--font-size-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
  padding: var(--spacing-1);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
}

.action-btn:hover {
  background: var(--gray-100);
  transform: scale(1.1);
}

.action-btn.liked {
  color: var(--error-500);
  animation: likeAnim 0.3s ease;
}

.action-btn.collected {
  color: var(--warning-500);
  animation: collectAnim 0.3s ease;
}

@keyframes likeAnim {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.3); }
}

@keyframes collectAnim {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2) rotate(-10deg); }
}

/* 响应式 */
@media (max-width: 768px) {
  .post-card {
    padding: var(--spacing-4);
  }
  
  .post-title {
    font-size: var(--font-size-base);
  }
  
  .user-avatar {
    width: 36px;
    height: 36px;
  }
  
  .interaction-stats {
    gap: var(--spacing-3);
  }
  
  .post-actions {
    gap: var(--spacing-2);
  }
  
  .action-btn {
    width: 24px;
    height: 24px;
    font-size: var(--font-size-xs);
  }
}
</style>
