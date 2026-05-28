<template>
  <div class="search-container">
    <!-- 标签页切换 -->
    <div class="tabs-header">
      <div class="tabs-wrapper">
        <button
          class="tab-item"
          :class="{ active: activeTab === 'posts' }"
          @click="activeTab = 'posts'"
        >
          <span>帖子</span>
          <span class="tab-count">{{ totalPosts }}</span>
        </button>
        <button
          class="tab-item"
          :class="{ active: activeTab === 'users' }"
          @click="activeTab = 'users'"
        >
          <span>用户</span>
          <span class="tab-count">{{ filteredUsers.length }}</span>
        </button>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="search-results">
      <!-- 帖子列表 -->
      <div v-if="activeTab === 'posts'">
        <!-- 帖子列表 -->
        <div v-if="currentPosts.length > 0" class="post-list">
          <div
            v-for="post in currentPosts"
            :key="post.id"
            class="post-item"
            @click="openPostDetail(post)"
          >
            <div class="post-info">
              <!-- 帖子标签 -->
              <div class="post-badges">
                <span v-if="post.isTop" class="badge badge-top">置顶</span>
                <span v-if="post.isBest" class="badge badge-best">精华</span>
                <span class="badge badge-category">{{ post.categoryName }}</span>
              </div>
              <h3 class="post-title">{{ post.title }}</h3>
              <p class="post-content">{{ post.content }}</p>
              <div class="post-meta">
                <span class="author">{{ post.nickname }}</span>
                <span class="time">{{ formatDate(post.createTime) }}</span>
              </div>
              <div class="post-stats">
                <span class="stat-item">
                  <el-icon><Message /></el-icon>
                  {{ post.commentCount }}
                </span>
                <span class="stat-item">
                  <el-icon><Star /></el-icon>
                  {{ post.likeCount }}
                </span>
                <span class="stat-item">
                  <el-icon><Collection /></el-icon>
                  {{ post.collectCount }}
                </span>
              </div>
            </div>
            <div v-if="post.imageUrls && post.imageUrls.length > 0" class="post-preview">
              <img v-lazy="{ src: post.imageUrls[0], size: 'thumb' }" :alt="post.title" class="preview-image" width="200" height="200" />
            </div>
          </div>
        </div>

        <!-- 帖子空状态 -->
        <div v-else class="empty-state">
          <el-icon class="empty-icon" :size="60"><Document /></el-icon>
          <p class="empty-text">暂无相关帖子</p>
          <p class="empty-hint">尝试使用其他关键词搜索</p>
        </div>

        <!-- 分页 -->
        <div v-if="totalPosts > pageSize" class="pagination-wrapper">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="totalPosts"
            :page-size="pageSize"
            :current-page="currentPage"
            @current-change="handlePageChange"
          />
        </div>
      </div>

      <!-- 用户列表 -->
      <div v-if="activeTab === 'users'">
        <!-- 用户列表 -->
        <div v-if="filteredUsers.length > 0" class="user-list">
          <div
            v-for="user in filteredUsers"
            :key="user.id"
            class="user-item"
            @click="goUserProfile(user.id)"
          >
            <img v-lazy="{ src: getAvatarUrl(user.avatar, user.nickname), size: 'avatar' }" :alt="user.nickname" class="user-avatar" width="56" height="56" />
            <div class="user-info">
              <div class="user-name">{{ user.nickname }}</div>
              <div class="user-desc">{{ user.description || '暂无简介' }}</div>
              <div class="user-stats">
                <span class="user-stat">帖子 {{ user.postCount }}</span>
                <span class="user-stat">粉丝 {{ user.followerCount }}</span>
                <span class="user-stat">关注 {{ user.followingCount }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 用户空状态 -->
        <div v-else class="empty-state">
          <el-icon class="empty-icon" :size="60"><User /></el-icon>
          <p class="empty-text">暂无相关用户</p>
          <p class="empty-hint">尝试使用其他关键词搜索</p>
        </div>
      </div>
    </div>

    <!-- 帖子详情弹窗 -->
    <PostDetail
      :visible="showDetail"
      :post="selectedPost"
      @close="closePostDetail"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getAvatarUrl } from '../utils'
import { postApi, userApi } from '../api'
import { clearSearchKeyword } from '../store'
import PostDetail from '../components/PostDetail.vue'
import { Message, Star, Collection, Document, User } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const keyword = ref('')
const activeTab = ref('posts')
const currentPage = ref(1)
const pageSize = ref(10)

const allPosts = ref([])
const allUsers = ref([])
const loading = ref(false)

const showDetail = ref(false)
const selectedPost = ref(null)

const totalPosts = computed(() => allPosts.value.length)

const currentPosts = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return allPosts.value.slice(start, end)
})

const filteredUsers = computed(() => {
  if (!keyword.value.trim()) {
    return allUsers.value
  }
  const query = keyword.value.toLowerCase()
  return allUsers.value.filter(user => {
    const nicknameMatch = String(user.nickname).toLowerCase().includes(query)
    const descMatch = String(user.description).toLowerCase().includes(query)
    return nicknameMatch || descMatch
  })
})

const searchPosts = async (page = 1) => {
  if (!keyword.value.trim()) {
    allPosts.value = []
    return
  }
  
  loading.value = true
  try {
    const res = await postApi.search(keyword.value, page)
    if (res.data.code === 200) {
      allPosts.value = res.data.data
      currentPage.value = page
    }
  } catch (error) {
    console.error('搜索失败:', error)
  } finally {
    loading.value = false
  }
}

const searchUsers = async () => {
  if (!keyword.value.trim()) {
    allUsers.value = []
    return
  }
  
  try {
    const res = await userApi.getUserList()
    if (res.data.code === 200) {
      allUsers.value = res.data.data
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

const openPostDetail = (post) => {
  selectedPost.value = post
  showDetail.value = true
  // 清空搜索栏
  clearSearchKeyword()
}

const closePostDetail = () => {
  showDetail.value = false
  selectedPost.value = null
}

const goUserProfile = (userId) => {
  router.push(`/profile?id=${userId}`)
}

const handlePageChange = (page) => {
  searchPosts(page)
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) {
    return '刚刚'
  } else if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  } else if (diff < 604800000) {
    return `${Math.floor(diff / 86400000)}天前`
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

watch(keyword, (newKeyword) => {
  if (newKeyword.trim()) {
    currentPage.value = 1
    if (activeTab.value === 'posts') {
      searchPosts(1)
    } else {
      searchUsers()
    }
  }
})

watch(activeTab, (newTab) => {
  if (keyword.value.trim()) {
    currentPage.value = 1
    if (newTab === 'posts') {
      searchPosts(1)
    } else {
      searchUsers()
    }
  }
})

onMounted(() => {
  const urlKeyword = route.query.keyword
  if (urlKeyword) {
    keyword.value = decodeURIComponent(urlKeyword)
    searchPosts(1)
  }
})
</script>

<style scoped>
.search-container {
  min-height: 100vh;
  background: #f9fafb;
  padding-top: 100px;
}

/* 标签页头部 */
.tabs-header {
  position: sticky;
  top: 70px;
  background: white;
  border-bottom: 1px solid #e5e7eb;
  z-index: 100;
}

.tabs-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  gap: 2rem;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 0;
  background: transparent;
  border: none;
  font-size: 1rem;
  font-weight: 500;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 2px solid transparent;
}

.tab-item:hover {
  color: #8b5cf6;
}

.tab-item.active {
  color: #8b5cf6;
  border-bottom-color: #8b5cf6;
  font-weight: 600;
}

.tab-count {
  padding: 0.125rem 0.5rem;
  background: #f3f4f6;
  border-radius: 10px;
  font-size: 0.75rem;
  font-weight: 500;
}

.tab-item.active .tab-count {
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
}

/* 搜索结果 */
.search-results {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
}

/* 帖子列表 */
.post-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.5rem;
}

.post-item {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.post-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(139, 92, 246, 0.15);
}

.post-info {
  padding: 1.25rem;
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 帖子标签 */
.post-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 0.375rem;
  margin-bottom: 0.75rem;
}

.badge {
  padding: 0.25rem 0.625rem;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 500;
}

.badge-top {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
}

.badge-best {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
}

.badge-category {
  background: #f3f4f6;
  color: #6b7280;
}

.post-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 0.5rem;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-content {
  font-size: 0.9rem;
  color: #4b5563;
  line-height: 1.6;
  margin: 0 0 0.75rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
}

.author {
  font-size: 0.85rem;
  color: #8b5cf6;
  font-weight: 500;
}

.time {
  font-size: 0.8rem;
  color: #9ca3af;
}

.post-stats {
  display: flex;
  align-items: center;
  gap: 1.25rem;
  padding-top: 0.75rem;
  border-top: 1px solid #f3f4f6;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.85rem;
  color: #6b7280;
}

.stat-item el-icon {
  font-size: 0.9rem;
}

/* 帖子预览图 */
.post-preview {
  width: 100%;
  aspect-ratio: 16/10;
  overflow: hidden;
  background: #f3f4f6;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 用户列表 */
.user-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 1.25rem;
  background: white;
  border-radius: 16px;
  padding: 1rem 1.25rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 16px rgba(139, 92, 246, 0.1);
}

.user-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f3e8ff;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 1rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 0.25rem;
}

.user-desc {
  font-size: 0.85rem;
  color: #6b7280;
  margin: 0 0 0.5rem;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.user-stats {
  display: flex;
  align-items: center;
  gap: 1.25rem;
}

.user-stat {
  font-size: 0.8rem;
  color: #9ca3af;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
}

.empty-icon {
  margin-bottom: 1rem;
  color: #d1d5db;
}

.empty-text {
  font-size: 1.1rem;
  color: #6b7280;
  margin: 0;
}

.empty-hint {
  font-size: 0.9rem;
  color: #9ca3af;
  margin: 0;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 2rem 0;
}

.el-pagination {
  display: flex;
  align-items: center;
}

.el-pagination button {
  margin: 0 0.25rem;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  background: white;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;
}

.el-pagination button:hover:not(:disabled) {
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
  border-color: #8b5cf6;
}

.el-pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.el-pagination .el-pager li {
  margin: 0 0.25rem;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.el-pagination .el-pager li:hover:not(.active) {
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
}

.el-pagination .el-pager li.active {
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  color: white;
}

/* 响应式 */
@media (max-width: 768px) {
  .post-list {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .user-stats {
    gap: 0.75rem;
  }
}
</style>