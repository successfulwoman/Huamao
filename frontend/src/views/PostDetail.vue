
<template>
  <div class="post-detail">
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

onMounted(() => {
  loadPost()
  loadComments()
})
</script>
