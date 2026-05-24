<template>
  <div class="home-container">
    <!-- 顶部 Banner -->
    <div class="banner-section">
      <div class="banner-content">
        <div class="banner-bg"></div>
        <div class="banner-text">
          <h1 class="banner-title">花猫社区</h1>
          <p class="banner-subtitle">分享生活，交流思想</p>
        </div>
      </div>
    </div>

    <!-- 分类标签 -->
    <div class="category-section">
      <div class="category-list">
        <button
          class="category-item"
          :class="{ active: selectedCategory === null }"
          @click="selectCategory(null)"
        >
          <el-icon class="category-icon"><Grid /></el-icon>
          全部
        </button>
        <!-- 显示选中的标签（最多5个） -->
        <button
          v-for="category in displayCategories"
          :key="category.id"
          class="category-item"
          :class="{ active: selectedCategory === category.id }"
          @click="selectCategory(category.id)"
        >
          <el-icon class="category-icon"><component :is="getCategoryIcon(category.name)" /></el-icon>
          {{ category.name }}
        </button>
        <!-- 省略号按钮 -->
        <button
          v-if="hiddenCategories.length > 0"
          class="category-item ellipsis-btn"
          @click="handleEllipsisClick"
        >
          <el-icon class="category-icon"><More /></el-icon>
        </button>
      </div>

      <!-- 标签选择下拉菜单 -->
      <div v-if="showCategoryDropdown" class="category-dropdown" @click.stop>
        <div class="dropdown-header">
          <span>选择要展示的标签（最多5个）</span>
          <button class="dropdown-close" @click="showCategoryDropdown = false">
            <el-icon><Close /></el-icon>
          </button>
        </div>
        <div class="dropdown-content">
          <label
            v-for="category in categories"
            :key="category.id"
            class="category-checkbox"
          >
            <input
              type="checkbox"
              :checked="selectedCategoryIds.includes(category.id)"
              @change="toggleCategorySelection(category.id)"
              :disabled="selectedCategoryIds.length >= 5 && !selectedCategoryIds.includes(category.id)"
            />
            <span>{{ category.name }}</span>
          </label>
        </div>
      </div>
    </div>

    <!-- 帖子瀑布流 -->
    <div class="post-section">
      <div class="post-grid">
        <PostCard 
          v-for="post in posts" 
          :key="post.id" 
          :post="post"
          @click="(post) => openPostDetail(post)"
          @like="toggleLike"
          @collect="toggleCollect"
          @comment="goDetail"
          @share="handleShare"
        />
      </div>

      <!-- 暂无帖子 -->
      <div v-if="posts.length === 0" class="empty-state">
        <el-icon class="empty-icon" :size="60"><Bell /></el-icon>
        <p class="empty-text">暂无帖子，快来发布第一篇吧！</p>
      </div>

      <!-- 加载更多 -->
      <div v-if="posts.length > 0" class="load-more">
        <button class="load-more-btn" @click="loadMore">
          <span>加载更多</span>
          <el-icon class="load-icon"><ArrowDown /></el-icon>
        </button>
      </div>
    </div>

    <!-- 帖子详情弹窗 -->
    <PostDetail 
      :visible="showDetail" 
      :post="currentPost" 
      @close="closePostDetail" 
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { categoryApi, postApi, likeApi, collectApi } from '../api'
import { getUser, setCategories, getCategories } from '../store'
import PostDetail from '../components/PostDetail.vue'
import PostCard from '../components/PostCard.vue'
import { getCategoryIcon } from '../utils'
import { More, Close } from '@element-plus/icons-vue'

const router = useRouter()
const posts = ref([])
const categories = ref([])
const selectedCategory = ref(null)
const page = ref(1)
const hasMore = ref(true)

// 帖子详情弹窗
const showDetail = ref(false)
const currentPost = ref(null)

// 标签选择相关
const showCategoryDropdown = ref(false)
// 用户选中的标签ID列表（最多5个），从localStorage读取或默认取前5个
const storedCategoryIds = localStorage.getItem('selectedCategoryIds')
const initialCategoryIds = storedCategoryIds ? (Array.isArray(JSON.parse(storedCategoryIds)) ? JSON.parse(storedCategoryIds) : []) : []
const selectedCategoryIds = ref(initialCategoryIds)

// 显示的标签（最多5个）
const displayCategories = computed(() => {
  if (selectedCategoryIds.value.length === 0) {
    // 如果没有选择，默认显示前5个
    return categories.value.slice(0, 5)
  }
  return categories.value.filter(cat => selectedCategoryIds.value.includes(cat.id))
})

// 隐藏的标签（超出5个的部分）
const hiddenCategories = computed(() => {
  if (selectedCategoryIds.value.length === 0) {
    return categories.value.slice(5)
  }
  return categories.value.filter(cat => !selectedCategoryIds.value.includes(cat.id))
})

// 切换标签选择
const toggleCategorySelection = (categoryId) => {
  const index = selectedCategoryIds.value.indexOf(categoryId)
  if (index > -1) {
    // 取消选择
    selectedCategoryIds.value.splice(index, 1)
  } else if (selectedCategoryIds.value.length < 5) {
    // 添加选择（最多5个）
    selectedCategoryIds.value.push(categoryId)
  }
  // 保存到localStorage
  localStorage.setItem('selectedCategoryIds', JSON.stringify(selectedCategoryIds.value))
}

// 点击省略号按钮时切换弹窗显示
const handleEllipsisClick = () => {
  showCategoryDropdown.value = !showCategoryDropdown.value
}

// 点击页面其他地方关闭弹窗
const closeDropdown = (e) => {
  const target = e.target
  if (!target.closest('.category-section') && !target.closest('.category-dropdown')) {
    showCategoryDropdown.value = false
  }
}

// 点击下拉菜单内部不关闭
const stopDropdownClose = (e) => {
  e.stopPropagation()
}

// 处理下拉菜单滚动
const handleDropdownScroll = (e) => {
  // 不需要特殊处理，让浏览器默认行为生效
}

const loadCategories = async () => {
  const res = await categoryApi.getList()
  if (res.data.code === 200) {
    categories.value = res.data.data
    setCategories(res.data.data)
  }
}

// 假数据
const mockPosts = [
  {
    id: 1001,
    title: '今天终于把代码写完了！',
    content: '连续加班一周，终于把这个功能写完了！感觉整个人都虚脱了，但是看到成果还是很有成就感的。有没有人来夸夸我？😂',
    nickname: '码农小王',
    createTime: new Date(Date.now() - 1000 * 60 * 30).toISOString(),
    categoryName: '日常',
    imageUrls: ['https://neeko-copilot.bytedance.net/api/text_to_image?prompt=programmer%20coding%20at%20computer%20desk%20with%20coffee%20late%20night%20work%20environment&image_size=landscape_16_9'],
    commentCount: 128,
    likeCount: 567,
    collectCount: 89,
    liked: false,
    collected: false,
    topComment: { nickname: '吃瓜群众', content: '太强了！我还在改bug呢😭' }
  },
  {
    id: 1002,
    title: '分享今天做的美食',
    content: '周末在家没事做，研究了一下红烧肉的做法，第一次做居然成功了！超级下饭，配米饭能吃三大碗！🍖',
    nickname: '美食达人',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 2).toISOString(),
    categoryName: '美食',
    imageUrls: [
      'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=delicious%20chinese%20braised%20pork%20belly%20hong%20shao%20rou%20on%20white%20plate%20food%20photography&image_size=landscape_4_3',
      'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=cooking%20chinese%20braised%20pork%20in%20wok%20kitchen%20scene&image_size=landscape_4_3',
      'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=steaming%20rice%20bowl%20with%20braised%20pork%20asian%20food&image_size=landscape_4_3'
    ],
    commentCount: 256,
    likeCount: 1234,
    collectCount: 234,
    liked: true,
    collected: false,
    topComment: { nickname: '吃货一枚', content: '看得我口水都流下来了！求教程！' }
  },
  {
    id: 1003,
    title: '周末去了一趟海边',
    content: '好久没看海了，这次去海边吹吹风，整个人都放松了。大海真的能治愈一切烦恼～🌊',
    nickname: '旅行爱好者',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 5).toISOString(),
    categoryName: '旅行',
    imageUrls: [
      'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=beautiful%20beach%20sunset%20ocean%20waves%20travel%20photography&image_size=landscape_16_9',
      'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=footprints%20on%20beach%20sand%20seaside%20vacation&image_size=landscape_4_3'
    ],
    commentCount: 89,
    likeCount: 789,
    collectCount: 156,
    liked: false,
    collected: true,
    topComment: null
  },
  {
    id: 1004,
    title: '新入手的机械键盘',
    content: '纠结了好久终于入手了这款键盘，手感真的太棒了！打字都变成一种享受～⌨️',
    nickname: '数码控',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 8).toISOString(),
    categoryName: '科技',
    imageUrls: ['https://neeko-copilot.bytedance.net/api/text_to_image?prompt=mechanical%20keyboard%20RGB%20lighting%20gaming%20setup%20desk&image_size=landscape_16_9'],
    commentCount: 67,
    likeCount: 345,
    collectCount: 45,
    liked: false,
    collected: false,
    topComment: { nickname: '外设党', content: '同款！用了两年了还是爱不释手' }
  },
  {
    id: 1005,
    title: '猫咪又在搞破坏了',
    content: '我家猫今天又把花瓶打碎了，但是看到它无辜的眼神，根本舍不得骂它啊！🐱',
    nickname: '猫主子的铲屎官',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 12).toISOString(),
    categoryName: '日常',
    imageUrls: [
      'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=cute%20cat%20knocking%20over%20vase%20playful%20kitten%20mischief&image_size=landscape_4_3',
      'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=cat%20looking%20innocent%20after%20breaking%20something%20adorable&image_size=landscape_4_3'
    ],
    commentCount: 178,
    likeCount: 987,
    collectCount: 123,
    liked: true,
    collected: false,
    topComment: { nickname: '同样养猫', content: '太真实了！我家的已经打碎三个了' }
  },
  {
    id: 1006,
    title: '推荐一部超好看的电影',
    content: '最近看了《流浪地球3》，特效真的炸裂！国产科幻电影越来越厉害了！推荐大家去看！🎬',
    nickname: '电影迷',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(),
    categoryName: '娱乐',
    imageUrls: ['https://neeko-copilot.bytedance.net/api/text_to_image?prompt=sci-fi%20movie%20poster%20space%20earth%20futuristic%20cinema&image_size=portrait_4_3'],
    commentCount: 345,
    likeCount: 2345,
    collectCount: 456,
    liked: false,
    collected: false,
    topComment: { nickname: '影评人', content: '同意！特别是最后那段太震撼了' }
  },
  {
    id: 1007,
    title: '学习打卡第30天',
    content: '坚持学习Python第30天了！从零基础到现在能写简单的爬虫，感觉收获满满～📚',
    nickname: '努力的小白',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 36).toISOString(),
    categoryName: '学习',
    imageUrls: ['https://neeko-copilot.bytedance.net/api/text_to_image?prompt=python%20programming%20code%20on%20screen%20learning%20programming&image_size=landscape_16_9'],
    commentCount: 56,
    likeCount: 234,
    collectCount: 67,
    liked: false,
    collected: false,
    topComment: null
  },
  {
    id: 1008,
    title: '终于拿到驾照了！',
    content: '考了三次科目二终于过了！拿到驾照的那一刻真的太激动了！以后可以自己开车出去玩了～🚗',
    nickname: '新手司机',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 48).toISOString(),
    categoryName: '日常',
    imageUrls: [],
    commentCount: 89,
    likeCount: 456,
    collectCount: 23,
    liked: true,
    collected: false,
    topComment: { nickname: '正在考', content: '恭喜恭喜！我还在科二挣扎😭' }
  }
]

const loadPosts = async (reset = false) => {
  if (reset) {
    page.value = 1
    hasMore.value = true
  }
  
  let res
  if (selectedCategory.value === null) {
    res = await postApi.getList(page.value)
  } else {
    res = await postApi.getByCategory(selectedCategory.value, page.value)
  }
  
  if (res.data.code === 200) {
    const newPosts = res.data.data
    if (reset) {
      posts.value = newPosts
    } else {
      posts.value = [...posts.value, ...newPosts]
    }
    if (newPosts.length < 10) {
      hasMore.value = false
    }
  }
}

const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  loadPosts(true)
}

const openPostDetail = (post) => {
  currentPost.value = post
  showDetail.value = true
}

const closePostDetail = () => {
  showDetail.value = false
  currentPost.value = null
}

const toggleLike = async (postId) => {
  const user = getUser()
  if (!user) {
    router.push('/login')
    return
  }
  await likeApi.toggle(user.id, postId)
  await loadPosts(true)
}

const toggleCollect = async (postId) => {
  const user = getUser()
  if (!user) {
    router.push('/login')
    return
  }
  await collectApi.toggle(user.id, postId)
  await loadPosts(true)
}

const handleShare = (post) => {
  if (navigator.share) {
    navigator.share({
      title: post.title,
      text: post.content,
      url: window.location.origin + `/post/detail/${post.id}`
    })
  } else {
    // 复制链接到剪贴板
    const url = window.location.origin + `/post/detail/${post.id}`
    navigator.clipboard.writeText(url)
    alert('链接已复制到剪贴板')
  }
}

const goDetail = (postId) => {
  router.push(`/post/detail/${postId}`)
}

const loadMore = () => {
  if (hasMore.value) {
    page.value++
    loadPosts()
  }
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



onMounted(() => {
  loadCategories()
  loadPosts()
  document.addEventListener('click', closeDropdown)
})

onUnmounted(() => {
  document.removeEventListener('click', closeDropdown)
})

watch(selectedCategory, () => {
  loadPosts(true)
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  position: relative;
  background: url('../assets/icons/Home1.png') no-repeat center center fixed;
  background-size: cover;
}

.home-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.2);
  z-index: 0;
}

/* Banner 区域 */
.banner-section {
  padding: 1.5rem 0;
  margin-bottom: 0.5rem;
}

.banner-content {
  position: relative;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.banner-bg {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  height: 100%;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.15) 0%, rgba(168, 85, 247, 0.1) 100%);
  border-radius: 24px;
  backdrop-filter: blur(20px);
}

.banner-text {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 2.5rem 0;
}

.banner-title {
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.banner-subtitle {
  font-size: 1.1rem;
  color: #6b7280;
  margin-top: 0.5rem;
}

/* 分类区域 */
.category-section {
  max-width: 1200px;
  margin: -0.5rem auto 1rem;
  padding: 0 1rem;
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
}

.category-list {
  display: flex;
  gap: 0.6rem;
  overflow-x: auto;
  padding-bottom: 0.5rem;
  scrollbar-width: none;
  flex-wrap: wrap;
  justify-content: center;
  max-width: 90%;
  position: relative;
}

.category-list::-webkit-scrollbar {
  display: none;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.25rem;
  background: white;
  border: none;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.category-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.15);
}

.category-item.active {
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  color: white;
  box-shadow: 0 4px 16px rgba(139, 92, 246, 0.3);
}

.category-icon {
  font-size: 16px;
}

/* 省略号按钮 */
.ellipsis-btn {
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
}

.ellipsis-btn:hover {
  background: rgba(139, 92, 246, 0.2);
}

/* 标签选择下拉菜单 */
.category-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 0.5rem;
  min-width: 280px;
  max-width: 360px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  z-index: 9999;
  overflow: hidden;
}

.dropdown-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
  border-bottom: 1px solid #f3f4f6;
  font-size: 0.9rem;
  font-weight: 600;
  color: #374151;
}

.dropdown-close {
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 6px;
  background: #f3f4f6;
  color: #6b7280;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.dropdown-close:hover {
  background: #e5e7eb;
  color: #374151;
}

.dropdown-content {
  padding: 0.5rem;
  max-height: 200px;
  overflow-y: auto;
}

.category-checkbox {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.625rem 0.75rem;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.category-checkbox:hover {
  background: #f9fafb;
}

.category-checkbox input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #8b5cf6;
}

.category-checkbox input[type="checkbox"]:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.category-checkbox span {
  font-size: 0.9rem;
  color: #374151;
}

/* 帖子区域 */
.post-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem 2rem;
  position: relative;
  z-index: 1;
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.5rem;
}

/* 帖子卡片 */
.post-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;
}

.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(139, 92, 246, 0.15);
}

/* 帖子头部 */
.post-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.25rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f3e8ff;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: 0.9rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.post-time {
  font-size: 0.75rem;
  color: #9ca3af;
  margin: 0;
}

.more-btn {
  background: none;
  border: none;
  font-size: 1.25rem;
  color: #9ca3af;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.more-btn:hover {
  background: #f3f4f6;
  color: #6b7280;
}

/* 帖子内容 */
.post-content {
  padding: 0 1.25rem;
}

.post-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 0.5rem;
  line-height: 1.4;
}

.post-text {
  font-size: 0.9rem;
  color: #4b5563;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 帖子图片 */
.post-images {
  padding: 1rem 1.25rem;
}

.post-images .grid-cols-1 {
  display: grid;
  grid-template-columns: 1fr;
  gap: 4px;
}

.post-images .grid-cols-2 {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 4px;
}

.post-images .grid-cols-2.grid-rows-2 {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 4px;
}

.post-images .grid-cols-2.grid-rows-2 img:first-child {
  grid-row: span 2;
}

.post-images .grid-cols-3 {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 4px;
}

.post-image {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  border-radius: 10px;
  background: #f3f4f6;
}

/* 神评论 */
.top-comment {
  margin: 0 1.25rem;
  padding: 0.75rem 1rem;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.08) 0%, rgba(168, 85, 247, 0.05) 100%);
  border-radius: 12px;
  border: 1px solid rgba(139, 92, 246, 0.1);
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  margin-bottom: 0.25rem;
}

.comment-badge {
  font-size: 0.85rem;
}

.comment-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #8b5cf6;
}

.comment-content {
  font-size: 0.85rem;
  color: #4b5563;
  margin: 0;
  line-height: 1.5;
}

.comment-author {
  font-weight: 600;
  color: #8b5cf6;
}

/* 互动栏 */
.post-actions {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 1rem 1.25rem;
  border-top: 1px solid #f3f4f6;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  background: none;
  border: none;
  font-size: 0.85rem;
  color: #6b7280;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.action-btn:hover {
  background: #f3f4f6;
  color: #8b5cf6;
}

.action-icon {
  font-size: 1.1rem;
}

.action-count {
  font-weight: 500;
}

.like-btn.liked {
  color: #ec4899;
}

.collect-btn.collected {
  color: #f59e0b;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
}

.empty-icon {
  margin-bottom: 1rem;
}

.empty-text {
  font-size: 1rem;
  color: #9ca3af;
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 2rem 0;
}

.load-more-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0 auto;
  padding: 0.875rem 2rem;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  font-size: 0.95rem;
  font-weight: 500;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s ease;
}

.load-more-btn:hover {
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  border-color: #8b5cf6;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(139, 92, 246, 0.3);
}

.load-icon {
  transition: transform 0.3s ease;
}

.load-more-btn:hover .load-icon {
  transform: translateY(2px);
}

/* 响应式 */
@media (max-width: 768px) {
  .banner-title {
    font-size: 1.8rem;
  }
  
  .banner-subtitle {
    font-size: 0.95rem;
  }
  
  .post-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .category-item {
    padding: 0.625rem 1rem;
    font-size: 0.85rem;
  }
}
</style>
