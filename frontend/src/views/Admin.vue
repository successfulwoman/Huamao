<template>
  <div class="admin-container">
    <!-- 后台头部 -->
    <div class="admin-header">
      <div class="header-left">
        <h1 class="admin-title">
          <span class="title-icon">🛡️</span>
          管理后台
        </h1>
        <span class="admin-subtitle">花猫论坛管理系统</span>
      </div>
      <div class="header-right">
        <button class="back-btn" @click="goHome">
          <span>🏠</span>
          返回首页
        </button>
        <button class="logout-btn" @click="logout">
          <span>🚪</span>
          退出登录
        </button>
      </div>
    </div>

    <!-- 未登录提示 -->
    <div v-if="!isAdmin" class="not-admin-card">
      <div class="not-admin-icon">🔒</div>
      <h2 class="not-admin-title">请先登录管理员账号</h2>
      <p class="not-admin-desc">只有管理员才能访问后台管理系统</p>
      <button class="btn btn-primary" @click="goLogin">去登录</button>
    </div>

    <!-- 管理内容 -->
    <div v-else class="admin-content">
      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon users-icon">👥</div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.users }}</span>
            <span class="stat-label">用户总数</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon posts-icon">📝</div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.posts }}</span>
            <span class="stat-label">帖子总数</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon comments-icon">💬</div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.comments }}</span>
            <span class="stat-label">评论总数</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon today-icon">📅</div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.todayPosts }}</span>
            <span class="stat-label">今日发帖</span>
          </div>
        </div>
      </div>

      <!-- 标签页 -->
      <div class="tabs-container">
        <div class="tabs-header">
          <button 
            :class="['tab-btn', { active: activeTab === 'users' }]"
            @click="activeTab = 'users'"
          >
            <span class="tab-icon">👥</span>
            用户管理
          </button>
          <button 
            :class="['tab-btn', { active: activeTab === 'posts' }]"
            @click="activeTab = 'posts'"
          >
            <span class="tab-icon">📝</span>
            帖子管理
          </button>
          <button 
            :class="['tab-btn', { active: activeTab === 'comments' }]"
            @click="activeTab = 'comments'"
          >
            <span class="tab-icon">💬</span>
            评论管理
          </button>
        </div>

        <!-- 用户管理 -->
        <div v-if="activeTab === 'users'" class="tab-content">
          <div class="content-header">
            <h2 class="content-title">用户列表</h2>
            <div class="search-box">
              <span class="search-icon">🔍</span>
              <input 
                v-model="userSearch" 
                type="text" 
                placeholder="搜索用户..." 
                class="search-input"
              />
            </div>
          </div>
          
          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>用户</th>
                  <th>昵称</th>
                  <th>邮箱</th>
                  <th>注册时间</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in filteredUsers" :key="user.id">
                  <td>{{ user.id }}</td>
                  <td>
                    <div class="user-cell">
                      <img :src="getAvatar(user.username)" class="user-avatar" />
                      <span>{{ user.username }}</span>
                    </div>
                  </td>
                  <td>{{ user.nickname }}</td>
                  <td>{{ user.email }}</td>
                  <td>{{ formatTime(user.createTime) }}</td>
                  <td>
                    <span :class="['status-badge', user.status]">
                      {{ user.status === 'active' ? '正常' : '禁用' }}
                    </span>
                  </td>
                  <td>
                    <div class="action-btns">
                      <button class="action-btn view-btn" title="查看">
                        <span>👁️</span>
                      </button>
                      <button 
                        :class="['action-btn', user.status === 'active' ? 'disable-btn' : 'enable-btn']"
                        :title="user.status === 'active' ? '禁用' : '启用'"
                      >
                        <span>{{ user.status === 'active' ? '🚫' : '✅' }}</span>
                      </button>
                      <button class="action-btn delete-btn" title="删除">
                        <span>🗑️</span>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 帖子管理 -->
        <div v-if="activeTab === 'posts'" class="tab-content">
          <div class="content-header">
            <h2 class="content-title">帖子列表</h2>
            <div class="search-box">
              <span class="search-icon">🔍</span>
              <input 
                v-model="postSearch" 
                type="text" 
                placeholder="搜索帖子..." 
                class="search-input"
              />
            </div>
          </div>
          
          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>标题</th>
                  <th>作者</th>
                  <th>分类</th>
                  <th>评论数</th>
                  <th>发布时间</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="post in filteredPosts" :key="post.id">
                  <td>{{ post.id }}</td>
                  <td class="title-cell">{{ post.title }}</td>
                  <td>{{ post.nickname }}</td>
                  <td>
                    <span class="category-tag">{{ post.categoryName }}</span>
                  </td>
                  <td>{{ post.commentCount }}</td>
                  <td>{{ formatTime(post.createTime) }}</td>
                  <td>
                    <span :class="['status-badge', post.status]">
                      {{ post.status === 'published' ? '已发布' : '待审核' }}
                    </span>
                  </td>
                  <td>
                    <div class="action-btns">
                      <button class="action-btn view-btn" title="查看">
                        <span>👁️</span>
                      </button>
                      <button 
                        :class="['action-btn', post.status === 'published' ? 'disable-btn' : 'enable-btn']"
                        :title="post.status === 'published' ? '下架' : '审核通过'"
                      >
                        <span>{{ post.status === 'published' ? '📦' : '✅' }}</span>
                      </button>
                      <button class="action-btn delete-btn" title="删除">
                        <span>🗑️</span>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 评论管理 -->
        <div v-if="activeTab === 'comments'" class="tab-content">
          <div class="content-header">
            <h2 class="content-title">评论列表</h2>
          </div>
          
          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>评论内容</th>
                  <th>帖子标题</th>
                  <th>评论者</th>
                  <th>发布时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="comment in comments" :key="comment.id">
                  <td>{{ comment.id }}</td>
                  <td class="content-cell">{{ comment.content }}</td>
                  <td class="title-cell">{{ comment.postTitle }}</td>
                  <td>{{ comment.nickname }}</td>
                  <td>{{ formatTime(comment.createTime) }}</td>
                  <td>
                    <div class="action-btns">
                      <button class="action-btn delete-btn" title="删除">
                        <span>🗑️</span>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAdmin, clearAdmin } from '../store'
import { generateAvatar, formatTime } from '../utils'

const router = useRouter()
const activeTab = ref('users')
const userSearch = ref('')
const postSearch = ref('')

const isAdmin = ref(getAdmin())

// 统计数据
const stats = ref({
  users: 156,
  posts: 423,
  comments: 1892,
  todayPosts: 12
})

// 模拟用户数据
const users = ref([
  { id: 1, username: 'xiaoming', nickname: '小明', email: 'xiaoming@example.com', createTime: new Date(Date.now() - 1000 * 60 * 60 * 24 * 30).toISOString(), status: 'active' },
  { id: 2, username: 'meishi', nickname: '美食家', email: 'meishi@example.com', createTime: new Date(Date.now() - 1000 * 60 * 60 * 24 * 20).toISOString(), status: 'active' },
  { id: 3, username: 'lvxing', nickname: '旅行者', email: 'lvxing@example.com', createTime: new Date(Date.now() - 1000 * 60 * 60 * 24 * 15).toISOString(), status: 'active' },
  { id: 4, username: 'yingshi', nickname: '影评人', email: 'yingshi@example.com', createTime: new Date(Date.now() - 1000 * 60 * 60 * 24 * 10).toISOString(), status: 'disabled' },
  { id: 5, username: 'sheying', nickname: '摄影师', email: 'sheying@example.com', createTime: new Date(Date.now() - 1000 * 60 * 60 * 24 * 5).toISOString(), status: 'active' }
])

// 模拟帖子数据
const posts = ref([
  { id: 1, title: '今天天气真好', nickname: '小明', categoryName: '日常', commentCount: 12, createTime: new Date(Date.now() - 1000 * 60 * 60 * 2).toISOString(), status: 'published' },
  { id: 2, title: '分享美食制作过程', nickname: '美食家', categoryName: '美食', commentCount: 56, createTime: new Date(Date.now() - 1000 * 60 * 60 * 5).toISOString(), status: 'published' },
  { id: 3, title: '好看的电影推荐', nickname: '影评人', categoryName: '娱乐', commentCount: 89, createTime: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(), status: 'published' },
  { id: 4, title: '云南旅行日记', nickname: '旅行者', categoryName: '旅行', commentCount: 34, createTime: new Date(Date.now() - 1000 * 60 * 60 * 48).toISOString(), status: 'pending' },
  { id: 5, title: '摄影技巧分享', nickname: '摄影师', categoryName: '科技', commentCount: 45, createTime: new Date(Date.now() - 1000 * 60 * 60 * 72).toISOString(), status: 'published' }
])

// 模拟评论数据
const comments = ref([
  { id: 1, content: '写得真好！', postTitle: '今天天气真好', nickname: '美食家', createTime: new Date(Date.now() - 1000 * 60 * 30).toISOString() },
  { id: 2, content: '好美的地方！', postTitle: '云南旅行日记', nickname: '小明', createTime: new Date(Date.now() - 1000 * 60 * 60).toISOString() },
  { id: 3, content: '谢谢分享~', postTitle: '摄影技巧分享', nickname: '影评人', createTime: new Date(Date.now() - 1000 * 60 * 60 * 3).toISOString() }
])

const filteredUsers = computed(() => {
  if (!userSearch.value) return users.value
  return users.value.filter(u => 
    u.username.includes(userSearch.value) || 
    u.nickname.includes(userSearch.value)
  )
})

const filteredPosts = computed(() => {
  if (!postSearch.value) return posts.value
  return posts.value.filter(p => 
    p.title.includes(postSearch.value) ||
    p.nickname.includes(postSearch.value)
  )
})

const goHome = () => {
  router.push('/')
}

const goLogin = () => {
  router.push('/admin/login')
}

const logout = () => {
  clearAdmin()
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

/* 后台头部 */
.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
  padding: 1.5rem 2rem;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  border-radius: 20px;
  color: white;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.admin-title {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1.5rem;
  font-weight: 700;
}

.title-icon {
  font-size: 1.75rem;
}

.admin-subtitle {
  font-size: 0.9rem;
  opacity: 0.9;
}

.header-right {
  display: flex;
  gap: 1rem;
}

.back-btn,
.logout-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 1.25rem;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  color: white;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover,
.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 未登录卡片 */
.not-admin-card {
  background: white;
  border-radius: 24px;
  padding: 4rem 2rem;
  text-align: center;
  box-shadow: 0 4px 24px rgba(245, 158, 11, 0.1);
}

.not-admin-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.not-admin-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 0.5rem;
}

.not-admin-desc {
  color: #6b7280;
  margin-bottom: 2rem;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.5rem;
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
}

.stat-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  font-size: 1.75rem;
}

.users-icon {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.1), rgba(168, 85, 247, 0.1));
}

.posts-icon {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(96, 165, 250, 0.1));
}

.comments-icon {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.1), rgba(52, 211, 153, 0.1));
}

.today-icon {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.1), rgba(251, 191, 36, 0.1));
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1f2937;
}

.stat-label {
  font-size: 0.85rem;
  color: #6b7280;
}

/* 标签页 */
.tabs-container {
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.tabs-header {
  display: flex;
  border-bottom: 1px solid #f3f4f6;
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
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: transparent;
  border-radius: 3px 3px 0 0;
  transition: background 0.3s ease;
}

.tab-btn:hover {
  color: #f59e0b;
  background: rgba(245, 158, 11, 0.05);
}

.tab-btn.active {
  color: #f59e0b;
  font-weight: 600;
}

.tab-btn.active::after {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.tab-icon {
  font-size: 1rem;
}

/* 内容区域 */
.tab-content {
  padding: 1.5rem;
}

.content-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.content-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1f2937;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
}

.search-icon {
  font-size: 1rem;
}

.search-input {
  border: none;
  background: transparent;
  font-size: 0.9rem;
  outline: none;
  width: 200px;
}

/* 表格 */
.table-container {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #f3f4f6;
}

.data-table th {
  background: #f9fafb;
  font-weight: 600;
  color: #374151;
  font-size: 0.85rem;
}

.data-table tbody tr {
  transition: background 0.2s ease;
}

.data-table tbody tr:hover {
  background: #f9fafb;
}

.data-table td {
  color: #4b5563;
  font-size: 0.9rem;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.title-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.content-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-tag {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
  border-radius: 20px;
  font-size: 0.8rem;
}

.status-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.status-badge.active,
.status-badge.published {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
}

.status-badge.disabled,
.status-badge.pending {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

/* 操作按钮 */
.action-btns {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 8px;
  background: #f3f4f6;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  font-size: 0.9rem;
}

.action-btn:hover {
  transform: scale(1.1);
}

.view-btn:hover {
  background: #e0e7ff;
}

.disable-btn:hover {
  background: #fef2f2;
}

.enable-btn:hover {
  background: #d1fae5;
}

.delete-btn:hover {
  background: #fee2e2;
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
  background: linear-gradient(135deg, #f59e0b, #d97706);
  border: none;
  color: white;
  box-shadow: 0 4px 14px rgba(245, 158, 11, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.4);
}

/* 响应式 */
@media (max-width: 768px) {
  .admin-container {
    padding: 1rem 0.5rem;
  }

  .admin-header {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }

  .header-right {
    width: 100%;
    justify-content: center;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
  }

  .stat-card {
    padding: 1rem;
  }

  .stat-value {
    font-size: 1.5rem;
  }

  .tabs-header {
    overflow-x: auto;
  }

  .tab-btn {
    padding: 0.875rem 1rem;
    font-size: 0.85rem;
    white-space: nowrap;
  }

  .content-header {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .search-box {
    width: 100%;
  }

  .search-input {
    width: 100%;
  }

  .data-table th,
  .data-table td {
    padding: 0.75rem 0.5rem;
    font-size: 0.85rem;
  }
}
</style>
