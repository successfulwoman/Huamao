<template>
  <div class="message-container">
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
        <span class="title-icon">💬</span>
        消息通知
      </h1>
      <p class="page-desc">查看系统消息和互动通知</p>
    </div>

    <!-- 未登录提示 -->
    <div v-if="!user" class="not-login-card">
      <div class="not-login-icon">🔒</div>
      <h2 class="not-login-title">请先登录</h2>
      <p class="not-login-desc">登录后即可查看您的消息通知</p>
      <button class="btn btn-primary" @click="goLogin">去登录</button>
    </div>

    <!-- 消息列表 -->
    <div v-else class="message-list">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner">⏳</div>
        <p>加载中...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="messages.length === 0" class="empty-state">
        <div class="empty-icon">📭</div>
        <h2 class="empty-title">暂无消息</h2>
        <p class="empty-desc">有新的消息时会在这里显示~</p>
      </div>

      <!-- 消息分组 -->
      <div v-else class="message-sections">
        <!-- 未读消息 -->
        <div v-if="unreadMessages.length > 0" class="message-section">
          <div class="section-header">
            <span class="section-title">未读消息</span>
            <span class="section-count">{{ unreadMessages.length }}</span>
            <button class="mark-all-btn" @click="markAllRead">全部标为已读</button>
          </div>
          <div class="message-cards">
            <div 
              v-for="msg in unreadMessages" 
              :key="msg.id" 
              :class="['message-card', 'unread', `type-${msg.type}`]"
              @click="markRead(msg)"
            >
              <div class="message-icon">
                <span v-if="msg.type === 'like'">❤️</span>
                <span v-else-if="msg.type === 'comment'">💬</span>
                <span v-else-if="msg.type === 'follow'">👤</span>
                <span v-else-if="msg.type === 'system'">🔔</span>
                <span v-else>📩</span>
              </div>
              <div class="message-content">
                <div class="message-header">
                  <span class="message-sender">{{ msg.sender }}</span>
                  <span class="message-time">{{ formatTime(msg.createTime) }}</span>
                </div>
                <p class="message-text">{{ msg.content }}</p>
                <div v-if="msg.postTitle" class="message-post">
                  <span class="post-label">相关内容：</span>
                  <span class="post-title">{{ msg.postTitle }}</span>
                </div>
              </div>
              <div class="unread-dot"></div>
            </div>
          </div>
        </div>

        <!-- 已读消息 -->
        <div v-if="readMessages.length > 0" class="message-section">
          <div class="section-header">
            <span class="section-title">已读消息</span>
            <span class="section-count">{{ readMessages.length }}</span>
          </div>
          <div class="message-cards">
            <div 
              v-for="msg in readMessages" 
              :key="msg.id" 
              :class="['message-card', `type-${msg.type}`]"
              @click="markRead(msg)"
            >
              <div class="message-icon">
                <span v-if="msg.type === 'like'">❤️</span>
                <span v-else-if="msg.type === 'comment'">💬</span>
                <span v-else-if="msg.type === 'follow'">👤</span>
                <span v-else-if="msg.type === 'system'">🔔</span>
                <span v-else>📩</span>
              </div>
              <div class="message-content">
                <div class="message-header">
                  <span class="message-sender">{{ msg.sender }}</span>
                  <span class="message-time">{{ formatTime(msg.createTime) }}</span>
                </div>
                <p class="message-text">{{ msg.content }}</p>
                <div v-if="msg.postTitle" class="message-post">
                  <span class="post-label">相关内容：</span>
                  <span class="post-title">{{ msg.postTitle }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { messageApi } from '../api'
import { getUser } from '../store'
import { formatTime } from '../utils'

const router = useRouter()
const loading = ref(false)

const user = ref(getUser())

const messages = ref([])

const unreadMessages = computed(() => messages.value.filter(m => !m.isRead))
const readMessages = computed(() => messages.value.filter(m => m.isRead))

const goBack = () => {
  router.back()
}

const goLogin = () => {
  router.push('/login')
}

const loadMessages = async () => {
  loading.value = true
  try {
    const response = await messageApi.getMy(user.value.id)
    if (response.data?.code === 200) {
      messages.value = response.data.data || []
    }
  } catch (error) {
    console.error('加载消息失败:', error)
    messages.value = []
  } finally {
    loading.value = false
  }
}

const markRead = (msg) => {
  if (!msg.isRead) {
    msg.isRead = 1
  }
}

const markAllRead = () => {
  messages.value.forEach(msg => {
    msg.isRead = 1
  })
}

onMounted(() => {
  if (user.value) {
    loadMessages()
  }
})
</script>

<style scoped>
.message-container {
  max-width: 800px;
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
}

/* 消息分组 */
.message-sections {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.message-section {
  background: white;
  border-radius: 20px;
  padding: 1.5rem;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #f3f4f6;
}

.section-title {
  font-size: 1rem;
  font-weight: 700;
  color: #1f2937;
}

.section-count {
  padding: 0.125rem 0.5rem;
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  border-radius: 10px;
  font-size: 0.75rem;
  color: white;
  font-weight: 600;
}

.mark-all-btn {
  margin-left: auto;
  padding: 0.375rem 0.75rem;
  background: transparent;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 0.8rem;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s ease;
}

.mark-all-btn:hover {
  background: #f9fafb;
  border-color: #8b5cf6;
  color: #8b5cf6;
}

/* 消息卡片 */
.message-cards {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.message-card {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  padding: 1rem;
  border-radius: 16px;
  background: #f9fafb;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.message-card:hover {
  background: #f3f4f6;
}

.message-card.unread {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.05) 0%, rgba(168, 85, 247, 0.03) 100%);
  border: 1px solid rgba(139, 92, 246, 0.1);
}

.message-card.unread:hover {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.1) 0%, rgba(168, 85, 247, 0.05) 100%);
}

.message-icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 12px;
  font-size: 1.25rem;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.375rem;
}

.message-sender {
  font-size: 0.95rem;
  font-weight: 600;
  color: #1f2937;
}

.message-time {
  font-size: 0.8rem;
  color: #9ca3af;
}

.message-text {
  font-size: 0.9rem;
  color: #4b5563;
  line-height: 1.5;
  margin-bottom: 0.5rem;
}

.message-post {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.8rem;
}

.post-label {
  color: #9ca3af;
}

.post-title {
  color: #8b5cf6;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.unread-dot {
  position: absolute;
  top: 1rem;
  right: 1rem;
  width: 8px;
  height: 8px;
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  border-radius: 50%;
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
  .message-container {
    padding: 1rem 0.5rem;
  }

  .page-title {
    font-size: 1.5rem;
  }

  .message-card {
    padding: 0.875rem;
  }

  .message-icon {
    width: 40px;
    height: 40px;
    font-size: 1.1rem;
  }

  .message-sender {
    font-size: 0.9rem;
  }

  .message-text {
    font-size: 0.85rem;
  }
}
</style>
