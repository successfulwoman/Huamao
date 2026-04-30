<template>
  <div class="collect-container">
    <!-- 返回导航 -->
    <div class="back-nav">
      <button class="back-btn" @click="goBack">
        <span>←</span>
        返回
      </button>
    </div>

    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">
        <span class="title-icon">⭐</span>
        我的收藏
      </h1>
      <p class="page-desc">收藏你喜欢的帖子，随时查看</p>
    </div>

    <!-- 未登录提示 -->
    <div v-if="!user" class="not-login-card">
      <div class="not-login-icon">🔒</div>
      <h2 class="not-login-title">请先登录</h2>
      <p class="not-login-desc">登录后即可查看和管理您的收藏</p>
      <button class="btn btn-primary" @click="goLogin">去登录</button>
    </div>

    <!-- 收藏列表 -->
    <div v-else class="collect-list">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner">⏳</div>
        <p>加载中...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="posts.length === 0" class="empty-state">
        <div class="empty-icon">⭐</div>
        <h2 class="empty-title">还没有收藏</h2>
        <p class="empty-desc">去首页看看有什么喜欢的帖子吧~</p>
        <button class="btn btn-primary" @click="goHome">去首页</button>
      </div>

      <!-- 收藏卡片列表 -->
      <div v-else class="collect-grid">
        <div 
          v-for="post in posts" 
          :key="post.id" 
          class="collect-card"
          @click="goDetail(post.id)"
        >
          <!-- 图片区域 -->
          <div v-if="post.imageUrls && post.imageUrls.length > 0" class="card-image">
            <img :src="post.imageUrls[0]" :alt="post.title" />
            <div v-if="post.imageUrls.length > 1" class="image-count">
              <span>🖼️</span>
              {{ post.imageUrls.length }}
            </div>
          </div>

          <!-- 内容区域 -->
          <div class="card-content">
            <h3 class="card-title">{{ post.title }}</h3>
            <p class="card-text">{{ truncateText(post.content, 80) }}</p>

            <!-- 作者信息 -->
            <div class="card-author">
              <img :src="getAvatar(post.nickname)" :alt="post.nickname" class="author-avatar" />
              <span class="author-name">{{ post.nickname }}</span>
              <span class="divider">·</span>
              <span class="author-time">{{ formatTime(post.createTime) }}</span>
            </div>

            <!-- 互动信息 -->
            <div class="card-stats">
              <span class="stat-item">
                <span class="stat-icon">💬</span>
                {{ post.commentCount || 0 }}
              </span>
              <span class="stat-item">
                <span class="stat-icon">❤️</span>
                {{ post.likeCount || 0 }}
              </span>
            </div>
          </div>

          <!-- 取消收藏按钮 -->
          <button 
            class="uncollect-btn" 
            @click.stop="removeCollect(post.id)"
            title="取消收藏"
          >
            <span>❤️</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { collectApi } from '../api'
import { getUser } from '../store'
import { generateAvatar, formatTime, truncateText } from '../utils'

const router = useRouter()
const posts = ref([])
const loading = ref(false)

const user = ref(getUser())

// 模拟收藏数据
const mockCollects = [
  { 
    id: 1, 
    title: '今天天气真好', 
    content: '出门逛逛心情棒棒的~阳光明媚，微风徐徐，最适合出去玩了！', 
    nickname: '小明',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 2).toISOString(),
    imageUrls: ['https://picsum.photos/400/300?random=10'],
    commentCount: 12,
    likeCount: 45
  },
  { 
    id: 2, 
    title: '分享美食制作过程', 
    content: '今天教大家做一道简单又美味的红烧肉，步骤简单，一看就会！', 
    nickname: '美食家',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 5).toISOString(),
    imageUrls: [
      'https://picsum.photos/400/300?random=11',
      'https://picsum.photos/400/300?random=12',
      'https://picsum.photos/400/300?random=13'
    ],
    commentCount: 56,
    likeCount: 128
  },
  { 
    id: 3, 
    title: '好看的电影推荐', 
    content: '流浪地球3真的太好看了，特效炸裂，剧情感人！强烈推荐大家去看！', 
    nickname: '影评人',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(),
    imageUrls: ['https://picsum.photos/400/300?random=14'],
    commentCount: 89,
    likeCount: 256
  },
  { 
    id: 4, 
    title: '旅行日记 - 云南之旅', 
    content: '云南真的是一个很美的地方，蓝天白云，风景如画。', 
    nickname: '旅行者',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 48).toISOString(),
    imageUrls: [
      'https://picsum.photos/400/300?random=15',
      'https://picsum.photos/400/300?random=16'
    ],
    commentCount: 34,
    likeCount: 78
  }
]

const getAvatar = (nickname) => {
  return generateAvatar(nickname || 'User')
}

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
  // 模拟加载
  setTimeout(() => {
    posts.value = mockCollects
    loading.value = false
  }, 500)
}

const removeCollect = async (postId) => {
  if (confirm('确定要取消收藏吗？')) {
    posts.value = posts.value.filter(p => p.id !== postId)
  }
}

onMounted(() => {
  if (user.value) {
    loadCollects()
  }
})
</script>

<style scoped>
.collect-container {
  max-width: 1000px;
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

/* 加载和空状态 */
.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 24px;
  box-shadow: 0 4px 24px rgba(139, 92, 246, 0.1);
}

.loading-spinner {
  font-size: 3rem;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 0.5rem;
}

.empty-desc {
  color: #6b7280;
  margin-bottom: 2rem;
}

/* 收藏列表 */
.collect-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

/* 收藏卡片 */
.collect-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.collect-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(139, 92, 246, 0.15);
}

/* 图片区域 */
.card-image {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.collect-card:hover .card-image img {
  transform: scale(1.05);
}

.image-count {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.375rem 0.625rem;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 8px;
  font-size: 0.75rem;
  color: white;
}

/* 内容区域 */
.card-content {
  padding: 1.25rem;
}

.card-title {
  font-size: 1.05rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 0.5rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-text {
  font-size: 0.9rem;
  color: #6b7280;
  line-height: 1.5;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 作者信息 */
.card-author {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.author-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
}

.author-name {
  font-size: 0.85rem;
  color: #4b5563;
  font-weight: 500;
}

.divider {
  color: #d1d5db;
}

.author-time {
  font-size: 0.8rem;
  color: #9ca3af;
}

/* 互动信息 */
.card-stats {
  display: flex;
  gap: 1rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.85rem;
  color: #6b7280;
}

.stat-icon {
  font-size: 0.9rem;
}

/* 取消收藏按钮 */
.uncollect-btn {
  position: absolute;
  top: 0.75rem;
  left: 0.75rem;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  opacity: 0;
}

.collect-card:hover .uncollect-btn {
  opacity: 1;
}

.uncollect-btn:hover {
  background: #fef2f2;
  transform: scale(1.1);
}

.uncollect-btn span {
  font-size: 1rem;
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
@media (max-width: 640px) {
  .collect-container {
    padding: 1rem 0.5rem;
  }

  .collect-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .page-title {
    font-size: 1.5rem;
  }

  .uncollect-btn {
    opacity: 1;
  }
}
</style>
