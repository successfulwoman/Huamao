<template>
  <div class="about-page">
    <!-- 论坛介绍 -->
    <section class="intro-section">
      <div class="intro-card">
        <div class="intro-header">
          <img :src="LOGO_URL" alt="花猫社区" class="intro-logo" width="64" height="64" />
          <h1 class="intro-title">欢迎来到花猫社区</h1>
        </div>
        <p class="intro-desc">{{ siteDescription }}</p>
        <div class="intro-stats">
          <div class="stat-item">
            <span class="stat-number">{{ stats.userCount || 0 }}</span>
            <span class="stat-label">注册用户</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ stats.postCount || 0 }}</span>
            <span class="stat-label">帖子总数</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ stats.commentCount || 0 }}</span>
            <span class="stat-label">评论总数</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 管理员公告 -->
    <section class="section">
      <div class="section-header">
        <el-icon class="section-icon"><Bell /></el-icon>
        <h2 class="section-title">管理员公告</h2>
      </div>
      <div class="notice-list" v-if="notices.length > 0">
        <div v-for="notice in notices" :key="notice.id" class="notice-card">
          <div class="notice-header">
            <span class="notice-badge">公告</span>
            <span class="notice-time">{{ formatTime(notice.createTime) }}</span>
          </div>
          <h3 class="notice-title">{{ notice.title }}</h3>
          <p class="notice-content">{{ notice.content }}</p>
        </div>
      </div>
      <div class="empty-state" v-else>
        <el-icon class="empty-icon"><InfoFilled /></el-icon>
        <p>暂无公告</p>
      </div>
    </section>

    <!-- 管理员发帖 -->
    <section class="section">
      <div class="section-header">
        <el-icon class="section-icon"><Document /></el-icon>
        <h2 class="section-title">管理员发帖</h2>
      </div>
      <div class="post-list" v-if="adminPosts.length > 0">
        <div v-for="post in adminPosts" :key="post.id" class="post-card" @click="goToPost(post.id)">
          <div class="post-header">
            <img :src="getAvatar(post.avatar, post.nickname)" class="post-avatar" />
            <div class="post-author">
              <span class="author-name">{{ post.nickname }}</span>
              <span class="author-badge">管理员</span>
            </div>
            <span class="post-time">{{ formatTime(post.createTime) }}</span>
          </div>
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-summary">{{ truncateContent(post.content) }}</p>
          <div class="post-meta">
            <span class="meta-item">
              <el-icon><View /></el-icon>
              {{ post.viewCount || 0 }}
            </span>
            <span class="meta-item">
              <el-icon><ChatLineRound /></el-icon>
              {{ post.commentCount || 0 }}
            </span>
            <span class="meta-item">
              <el-icon><Star /></el-icon>
              {{ post.likeCount || 0 }}
            </span>
          </div>
        </div>
      </div>
      <div class="empty-state" v-else>
        <el-icon class="empty-icon"><Document /></el-icon>
        <p>暂无管理员发帖</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { noticeApi, postApi, statsApi, adminApi } from '../api'
import { getAvatar } from '../utils'
import { Icons } from '../assets/icons'
import { ElMessage } from 'element-plus'

const router = useRouter()
const LOGO_URL = Icons.logo

const notices = ref([])
const adminPosts = ref([])
const stats = ref({
  userCount: 0,
  postCount: 0,
  commentCount: 0
})
const siteDescription = ref('花猫社区是一个温馨的交流平台，在这里你可以分享生活点滴、交流思想见解、结识志同道合的朋友。我们致力于打造一个友善、积极、有价值的社区环境。')

const loadNotices = async () => {
  try {
    const response = await noticeApi.getAllNotices()
    if (response.data?.code === 200) {
      notices.value = response.data.data || []
    }
  } catch (error) {
    console.error('加载公告失败:', error)
  }
}

const loadAdminPosts = async () => {
  try {
    // 获取所有帖子，然后筛选出管理员发的帖子
    const response = await postApi.getList(1)
    if (response.data?.code === 200) {
      const posts = response.data.data?.list || []
      // 筛选管理员帖子（role === 1 表示管理员）
      adminPosts.value = posts.filter(post => post.role === 1)
    }
  } catch (error) {
    console.error('加载管理员帖子失败:', error)
  }
}

const loadStats = async () => {
  try {
    const response = await statsApi.getOverview()
    if (response.data?.code === 200) {
      stats.value = response.data.data || {
        userCount: 0,
        postCount: 0,
        commentCount: 0
      }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const truncateContent = (content) => {
  if (!content) return ''
  // 去除HTML标签
  const plainText = content.replace(/<[^>]+>/g, '')
  return plainText.length > 100 ? plainText.substring(0, 100) + '...' : plainText
}

const goToPost = (postId) => {
  router.push(`/post/detail/${postId}`)
}

const loadSiteInfo = async () => {
  try {
    const response = await adminApi.getSiteInfo()
    if (response.data?.code === 200) {
      siteDescription.value = response.data.data?.description || siteDescription.value
    }
  } catch (error) {
    console.error('加载网站信息失败:', error)
  }
}

onMounted(() => {
  loadNotices()
  loadAdminPosts()
  loadStats()
  loadSiteInfo()
})
</script>

<style scoped>
.about-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f0ff 0%, #ede9fe 50%, #f3e8ff 100%);
  padding: 2rem;
}

/* 论坛介绍 */
.intro-section {
  max-width: 1200px;
  margin: 0 auto 3rem;
}

.intro-card {
  background: white;
  border-radius: 24px;
  padding: 3rem;
  text-align: center;
  box-shadow: 0 10px 40px rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.1);
}

.intro-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.intro-logo {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  object-fit: cover;
  box-shadow: 0 4px 15px rgba(139, 92, 246, 0.3);
}

.intro-title {
  font-size: 2rem;
  font-weight: 700;
  background: linear-gradient(135deg, #8b5cf6 0%, #a78bfa 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.intro-desc {
  font-size: 1.1rem;
  color: #666;
  line-height: 1.8;
  max-width: 600px;
  margin: 0 auto 2rem;
}

.intro-stats {
  display: flex;
  justify-content: center;
  gap: 3rem;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  color: #8b5cf6;
}

.stat-label {
  font-size: 0.9rem;
  color: #888;
}

/* 区块样式 */
.section {
  max-width: 1200px;
  margin: 0 auto 3rem;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.section-icon {
  font-size: 1.5rem;
  color: #8b5cf6;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
}

/* 公告列表 */
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.notice-card {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 20px rgba(139, 92, 246, 0.08);
  border: 1px solid rgba(139, 92, 246, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.notice-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(139, 92, 246, 0.15);
}

.notice-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.75rem;
}

.notice-badge {
  background: linear-gradient(135deg, #8b5cf6 0%, #a78bfa 100%);
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 500;
}

.notice-time {
  font-size: 0.85rem;
  color: #888;
}

.notice-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
}

.notice-content {
  font-size: 0.95rem;
  color: #666;
  line-height: 1.6;
}

/* 帖子列表 */
.post-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.post-card {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 20px rgba(139, 92, 246, 0.08);
  border: 1px solid rgba(139, 92, 246, 0.1);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(139, 92, 246, 0.15);
}

.post-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.post-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
}

.author-name {
  font-weight: 500;
  color: #333;
}

.author-badge {
  background: linear-gradient(135deg, #8b5cf6 0%, #a78bfa 100%);
  color: white;
  padding: 0.15rem 0.5rem;
  border-radius: 12px;
  font-size: 0.7rem;
}

.post-time {
  font-size: 0.85rem;
  color: #888;
}

.post-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
  line-height: 1.4;
}

.post-summary {
  font-size: 0.9rem;
  color: #666;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.post-meta {
  display: flex;
  gap: 1.5rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.35rem;
  font-size: 0.85rem;
  color: #888;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 3rem;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(139, 92, 246, 0.08);
}

.empty-icon {
  font-size: 3rem;
  color: #ddd;
  margin-bottom: 1rem;
}

.empty-state p {
  color: #888;
  font-size: 1rem;
}
</style>
