<template>
  <div class="post-detail">
    <!-- 返回导航 -->
    <div class="back-nav">
      <button class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回</span>
      </button>
    </div>

    <!-- 骨架屏 -->
    <div v-if="loading" class="skeleton-card">
      <div class="skeleton skeleton-title"></div>
      <div class="skeleton skeleton-meta"></div>
      <div class="skeleton skeleton-line"></div>
      <div class="skeleton skeleton-line short"></div>
      <div class="skeleton skeleton-line medium"></div>
    </div>

    <!-- 加载失败 -->
    <div v-else-if="loadError" class="error-card card">
      <el-icon :size="48" color="#ef4444"><WarningFilled /></el-icon>
      <h3>加载失败</h3>
      <p>{{ loadError }}</p>
      <el-button type="primary" @click="loadPost">重新加载</el-button>
    </div>

    <!-- 帖子内容 -->
    <div v-else-if="post" class="card post-card">
      <!-- 标题与元信息 -->
      <h2 class="post-title">{{ post.title }}</h2>
      <div class="meta">
        <div class="meta-left">
          <span class="meta-item">
            <el-icon><User /></el-icon>
            {{ post.nickname || '匿名用户' }}
          </span>
          <span class="meta-divider">·</span>
          <span class="meta-item">
            <el-icon><Clock /></el-icon>
            {{ formatDate(post.createTime) }}
          </span>
        </div>
        <span class="meta-category">
          <el-icon><PriceTag /></el-icon>
          {{ post.categoryName || '综合' }}
        </span>
      </div>

      <!-- 帖子正文 -->
      <div class="content">{{ post.content }}</div>

      <!-- 底部统计与操作 -->
      <div class="post-footer">
        <div class="stats">
          <span class="stat">
            <el-icon><ChatLineSquare /></el-icon>
            {{ formatCount(post.commentCount || 0) }}
          </span>
          <span class="stat">
            <el-icon><Star /></el-icon>
            {{ formatCount(likeCount) }}
          </span>
          <span class="stat">
            <el-icon><Collection /></el-icon>
            {{ formatCount(post.collectCount || 0) }}
          </span>
        </div>
        <div class="actions">
          <button
            class="action-btn"
            :class="{ active: liked }"
            :disabled="likeLoading"
            @click="toggleLike"
          >
            <el-icon :size="18"><Star v-if="!liked" /><StarFilled v-else /></el-icon>
          </button>
          <button
            class="action-btn"
            :class="{ active: collected }"
            :disabled="collectLoading"
            @click="toggleCollect"
          >
            <el-icon :size="18"><Collection v-if="!collected" /><CollectionTag v-else /></el-icon>
          </button>
          <button
            v-if="user && user.id === post.userId"
            class="action-btn del-btn"
            :disabled="deleteLoading"
            @click="deletePost"
          >
            <el-icon :size="18"><Delete /></el-icon>
          </button>
        </div>
      </div>
    </div>

    <!-- 评论区 -->
    <div class="card comment-section" v-if="!loading && !loadError">
      <h3 class="section-title">
        <el-icon><ChatLineSquare /></el-icon>
        评论 ({{ comments.length }})
      </h3>

      <!-- 评论列表 -->
      <div v-if="comments.length > 0" class="comment-list">
        <div v-for="item in comments" :key="item.id" class="comment-item">
          <div class="comment-avatar">{{ (item.nickname || '?')[0] }}</div>
          <div class="comment-body">
            <div class="comment-head">
              <span class="comment-name">{{ item.nickname }}</span>
              <span class="comment-time">{{ formatDate(item.createTime) }}</span>
            </div>
            <p class="comment-text">{{ item.content }}</p>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-comments">
        <el-icon :size="40" color="#d1d5db"><ChatDotRound /></el-icon>
        <p>暂无评论，来抢沙发吧~</p>
      </div>

      <!-- 评论表单 -->
      <div v-if="user" class="comment-form">
        <div class="form-row">
          <el-input
            v-model="commentContent"
            type="textarea"
            placeholder="写下你的想法..."
            :rows="2"
            maxlength="500"
            show-word-limit
            class="comment-input"
          />
          <el-button
            type="primary"
            :loading="commentLoading"
            :disabled="!commentContent.trim()"
            @click="submitComment"
            class="submit-btn"
          >
            发表
          </el-button>
        </div>
      </div>
      <div v-else class="login-tip">
        <router-link to="/login">登录</router-link> 后参与评论
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  User, Clock, PriceTag, Star, StarFilled,
  Collection, CollectionTag, ChatLineSquare,
  ChatDotRound, ArrowLeft, Delete, WarningFilled
} from '@element-plus/icons-vue'
import { postApi, commentApi, likeApi, collectApi } from '../api'
import { getUser } from '../store'

const route = useRoute()
const router = useRouter()
const post = ref(null)
const comments = ref([])
const commentContent = ref('')
const likeCount = ref(0)
const user = getUser()

const loading = ref(true)
const loadError = ref('')
const likeLoading = ref(false)
const collectLoading = ref(false)
const commentLoading = ref(false)
const deleteLoading = ref(false)
const liked = ref(false)
const collected = ref(false)

const loadPost = async () => {
  loading.value = true
  loadError.value = ''
  try {
    const postId = parseInt(route.params.id)
    if (!postId || isNaN(postId)) {
      loadError.value = '无效的帖子ID'
      return
    }
    const res = await postApi.getDetail(postId)
    if (res.data.code === 200) {
      post.value = res.data.data
      likeCount.value = post.value.likeCount || post.value.likeNum || 0
      liked.value = !!post.value.liked
      collected.value = !!post.value.collected
    } else {
      loadError.value = res.data.message || '帖子加载失败'
    }
  } catch (err) {
    console.error('加载帖子失败:', err)
    loadError.value = '网络请求失败，请检查网络连接'
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  try {
    const postId = parseInt(route.params.id)
    const res = await commentApi.getList(postId)
    if (res.data.code === 200) {
      comments.value = res.data.data || []
    }
  } catch (err) {
    console.error('加载评论失败:', err)
  }
}

const submitComment = async () => {
  if (!user || !commentContent.value.trim()) return
  commentLoading.value = true
  try {
    const postId = parseInt(route.params.id)
    const res = await commentApi.add(postId, user.id, commentContent.value)
    if (res.data.code === 200) {
      commentContent.value = ''
      await loadComments()
      if (post.value) {
        post.value.commentCount = (post.value.commentCount || 0) + 1
      }
    }
  } catch (err) {
    console.error('发表评论失败:', err)
  } finally {
    commentLoading.value = false
  }
}

const toggleLike = async () => {
  if (!user) { router.push('/login'); return }
  likeLoading.value = true
  try {
    const postId = parseInt(route.params.id)
    await likeApi.toggle(user.id, postId)
    likeCount.value += liked.value ? -1 : 1
    liked.value = !liked.value
  } catch (err) {
    console.error('点赞失败:', err)
  } finally {
    likeLoading.value = false
  }
}

const toggleCollect = async () => {
  if (!user) { router.push('/login'); return }
  collectLoading.value = true
  try {
    const postId = parseInt(route.params.id)
    await collectApi.toggle(user.id, postId)
    collected.value = !collected.value
  } catch (err) {
    console.error('收藏失败:', err)
  } finally {
    collectLoading.value = false
  }
}

const deletePost = async () => {
  if (!user) return
  deleteLoading.value = true
  try {
    const postId = parseInt(route.params.id)
    const res = await postApi.delete(postId, user.id)
    if (res.data.code === 200) {
      router.push('/')
    }
  } catch (err) {
    console.error('删除失败:', err)
  } finally {
    deleteLoading.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return d.toLocaleDateString('zh-CN') + ' ' + d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const formatCount = (num) => {
  if (!num) return '0'
  if (num >= 10000) return (num / 10000).toFixed(1).replace(/\.0$/, '') + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1).replace(/\.0$/, '') + 'k'
  return String(num)
}

const goBack = () => router.back()

onMounted(() => {
  loadPost()
  loadComments()
})
</script>

<style scoped>
/* ========== 布局 ========== */
.post-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 1.25rem;
}

/* ========== 返回按钮 ========== */
.back-nav {
  margin-bottom: 1rem;
}
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.4rem 0.9rem;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  font-size: 0.875rem;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
}
.back-btn:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
  background: #faf5ff;
}

/* ========== 卡片 ========== */
.card {
  background: white;
  border-radius: 14px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06), 0 1px 2px rgba(0, 0, 0, 0.04);
  margin-bottom: 1rem;
}

/* ========== 骨架屏 ========== */
.skeleton-card {
  background: white;
  border-radius: 14px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  margin-bottom: 1rem;
}
.skeleton {
  background: linear-gradient(90deg, #e5e7eb 25%, #f3f4f6 50%, #e5e7eb 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s ease-in-out infinite;
  border-radius: 6px;
  margin-bottom: 0.75rem;
}
.skeleton-title { height: 26px; width: 60%; }
.skeleton-meta  { height: 14px; width: 40%; }
.skeleton-line  { height: 14px; width: 100%; }
.skeleton-line.short  { width: 75%; }
.skeleton-line.medium { width: 50%; }
@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

/* ========== 错误卡片 ========== */
.error-card {
  text-align: center;
  padding: 3rem 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
}
.error-card h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #1f2937;
}
.error-card p {
  margin: 0;
  font-size: 0.875rem;
  color: #6b7280;
}

/* ========== 帖子卡片 ========== */
.post-title {
  font-size: 1.35rem;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.4;
  margin: 0 0 0.75rem;
}

.meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1.25rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #f3f4f6;
}
.meta-left {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  flex-wrap: wrap;
}
.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.8rem;
  color: #9ca3af;
}
.meta-divider {
  color: #d1d5db;
  font-size: 0.75rem;
  margin: 0 0.25rem;
}
.meta-category {
  display: inline-flex;
  align-items: center;
  gap: 0.2rem;
  font-size: 0.75rem;
  color: #8b5cf6;
  background: #faf5ff;
  padding: 0.2rem 0.6rem;
  border-radius: 20px;
  font-weight: 500;
}

/* 正文 */
.content {
  font-size: 0.95rem;
  line-height: 1.8;
  color: #374151;
  white-space: pre-wrap;
  word-break: break-word;
  margin-bottom: 1.25rem;
}

/* 底部栏 */
.post-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 0.75rem;
  border-top: 1px solid #f3f4f6;
}
.stats {
  display: flex;
  gap: 1rem;
}
.stat {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.8rem;
  color: #9ca3af;
}
.actions {
  display: flex;
  gap: 0.4rem;
}
.action-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: 1px solid #e5e7eb;
  border-radius: 50%;
  background: white;
  color: #9ca3af;
  cursor: pointer;
  transition: all 0.2s;
}
.action-btn:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
  background: #faf5ff;
}
.action-btn.active {
  border-color: #ef4444;
  color: #ef4444;
  background: #fef2f2;
}
.action-btn.active:hover {
  border-color: #ef4444;
}
.del-btn:hover {
  border-color: #ef4444;
  color: #ef4444;
  background: #fef2f2;
}
.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ========== 评论区 ========== */
.comment-section {
  padding-bottom: 1rem;
}
.section-title {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 1rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 1rem;
}

/* 评论列表 */
.comment-list {
  display: flex;
  flex-direction: column;
  margin-bottom: 1rem;
}
.comment-item {
  display: flex;
  gap: 0.65rem;
  padding: 0.75rem 0;
  border-bottom: 1px solid #f9fafb;
}
.comment-item:last-child { border-bottom: none; }
.comment-avatar {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #c084fc, #8b5cf6);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 600;
}
.comment-body { flex: 1; min-width: 0; }
.comment-head {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  margin-bottom: 0.2rem;
}
.comment-name {
  font-size: 0.8rem;
  font-weight: 600;
  color: #374151;
}
.comment-time {
  font-size: 0.7rem;
  color: #c4c4c4;
}
.comment-text {
  font-size: 0.875rem;
  line-height: 1.6;
  color: #4b5563;
  margin: 0;
  word-break: break-word;
}

/* 空评论 */
.empty-comments {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  padding: 2rem 0;
  color: #9ca3af;
  font-size: 0.875rem;
  margin-bottom: 1rem;
}
.empty-comments p { margin: 0; }

/* 评论表单 */
.comment-form {
  padding-top: 0.75rem;
  border-top: 1px solid #f3f4f6;
}
.form-row {
  display: flex;
  align-items: flex-end;
  gap: 0.5rem;
}
.comment-input {
  flex: 1;
}
.submit-btn {
  flex-shrink: 0;
}

/* 未登录提示 */
.login-tip {
  text-align: center;
  padding: 0.75rem 0 0;
  font-size: 0.85rem;
  color: #9ca3af;
  border-top: 1px solid #f3f4f6;
}
.login-tip a {
  color: #8b5cf6;
  font-weight: 500;
}

/* ========== 响应式 ========== */
@media (max-width: 640px) {
  .post-detail { padding: 0.75rem; }
  .card { padding: 1rem; border-radius: 12px; }
  .post-title { font-size: 1.15rem; }
  .meta { flex-direction: column; align-items: flex-start; }
  .post-footer { flex-direction: column; gap: 0.75rem; }
  .form-row { flex-direction: column; }
  .submit-btn { width: 100%; }
}
</style>
