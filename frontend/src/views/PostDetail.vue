
<template>
  <div class="post-detail">
    <!-- 返回导航 -->
    <div class="back-nav">
      <button class="back-btn" @click="goBack">
        <span>←</span>
        返回
      </button>
    </div>

    <div v-if="post" class="card">
      <h2>{{ post.title }}</h2>
      <div class="meta">
        <span>作者: {{ post.nickname }}</span>
        <span>分类: {{ post.categoryName }}</span>
        <span>{{ formatDate(post.createTime) }}</span>
      </div>
      <div class="content">{{ post.content }}</div>
      <div class="actions" style="margin-top: 1.5rem;">
        <button class="btn btn-secondary" @click="toggleLike">
          ❤️ {{ likeCount }}
        </button>
        <button class="btn btn-secondary" @click="toggleCollect">
          ⭐ 收藏
        </button>
        <button 
          class="btn btn-danger" 
          v-if="user && user.id === post.userId" 
          @click="deletePost"
        >
          删除帖子
        </button>
      </div>
    </div>

    <div class="comment-list">
      <h3>评论 ({{ comments.length }})</h3>
      <div v-for="comment in comments" :key="comment.id" class="card comment-item">
        <div class="meta">
          <span>{{ comment.nickname }}</span>
          <span>{{ formatDate(comment.createTime) }}</span>
        </div>
        <div class="content">{{ comment.content }}</div>
      </div>
    </div>

    <div v-if="user" class="comment-form card">
      <h3>发表评论</h3>
      <form @submit.prevent="submitComment">
        <div class="form-group">
          <textarea v-model="commentContent" placeholder="请输入评论内容" required></textarea>
        </div>
        <button type="submit" class="btn btn-primary">发表评论</button>
      </form>
    </div>

    <div v-if="!user" class="card" style="text-align: center;">
      <p>请先<a href="/login">登录</a>后发表评论</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { postApi, commentApi, likeApi, collectApi } from '../api'
import { getUser } from '../store'

const route = useRoute()
const router = useRouter()
const post = ref(null)
const comments = ref([])
const commentContent = ref('')
const likeCount = ref(0)

const user = getUser()

const loadPost = async () => {
  const postId = parseInt(route.params.id)
  const res = await postApi.getDetail(postId)
  if (res.data.code === 200) {
    post.value = res.data.data
    likeCount.value = post.value.likeCount || 0
  }
}

const loadComments = async () => {
  const postId = parseInt(route.params.id)
  const res = await commentApi.getList(postId)
  if (res.data.code === 200) {
    comments.value = res.data.data
  }
}

const submitComment = async () => {
  if (!user) return
  
  const postId = parseInt(route.params.id)
  const res = await commentApi.add({ 
    userId: user.id, 
    postId, 
    content: commentContent.value 
  })
  
  if (res.data.code === 200) {
    commentContent.value = ''
    loadComments()
    if (post.value) {
      post.value.commentCount = (post.value.commentCount || 0) + 1
    }
  }
}

const toggleLike = async () => {
  if (!user) {
    router.push('/login')
    return
  }
  const postId = parseInt(route.params.id)
  await likeApi.toggle({ userId: user.id, postId })
  likeCount.value += likeCount.value > 0 ? -1 : 1
}

const toggleCollect = async () => {
  if (!user) {
    router.push('/login')
    return
  }
  const postId = parseInt(route.params.id)
  await collectApi.toggle({ userId: user.id, postId })
}

const deletePost = async () => {
  if (!user) return
  
  const postId = parseInt(route.params.id)
  const res = await postApi.delete({ postId, userId: user.id })
  
  if (res.data.code === 200) {
    router.push('/')
  }
}

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadPost()
  loadComments()
})
</script>

<style scoped>
.post-detail {
  max-width: 900px;
  margin: 0 auto;
  padding: 1rem;
}

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

.card {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 1.5rem;
}

.card h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 1rem;
}

.meta {
  display: flex;
  gap: 1rem;
  font-size: 0.9rem;
  color: #6b7280;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.content {
  font-size: 1rem;
  line-height: 1.8;
  color: #374151;
  white-space: pre-wrap;
  margin-bottom: 1rem;
}

.actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.btn {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
}

.btn-secondary:hover {
  background: #e5e7eb;
}

.btn-primary {
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
}

.btn-danger {
  background: #fee2e2;
  color: #dc2626;
}

.btn-danger:hover {
  background: #fecaca;
}

.comment-list {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.comment-list h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 1rem;
}

.comment-item {
  margin-bottom: 1rem;
  padding: 1rem;
}

.comment-item:last-child {
  margin-bottom: 0;
}

.comment-form {
  padding: 1.5rem;
}

.comment-form h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 1rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  font-size: 0.95rem;
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
}

.form-group textarea:focus {
  outline: none;
  border-color: #8b5cf6;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.comment-form .btn {
  width: 100%;
}

@media (max-width: 640px) {
  .post-detail {
    padding: 0.75rem;
  }
  
  .meta {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
}
</style>

