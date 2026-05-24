<template>
  <header class="header">
    <div class="header-container">
      <!-- 左侧 Logo -->
      <div class="header-left" @click="goHome">
        <img :src="LOGO_URL" alt="花猫社区" class="logo" />
        <span class="brand-name">花猫社区</span>
      </div>

      <!-- 中间搜索框 -->
      <div class="header-center">
        <div class="search-wrapper">
          <el-icon class="search-icon"><Search /></el-icon>
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索帖子、用户..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">
            <span>搜索</span>
          </button>
        </div>
      </div>

      <!-- 右侧导航 -->
      <div class="header-right">
        <nav class="nav-links">
          <button class="nav-link" :class="{ active: currentPath === '/' }" @click="goHome">
            <el-icon class="nav-icon"><House /></el-icon>
            <span class="nav-text">首页</span>
          </button>
          <button class="nav-link" :class="{ active: currentPath === '/about' }" @click="goAbout">
            <el-icon class="nav-icon"><Document /></el-icon>
            <span class="nav-text">关于论坛</span>
          </button>
        </nav>

        <!-- 用户操作区 -->
        <div class="user-area">
          <!-- 未登录状态 -->
          <template v-if="!user">
            <button class="login-btn" @click="goLogin">
              <span>登录</span>
            </button>
          </template>

          <!-- 已登录状态 -->
          <template v-else>
            <div class="user-dropdown">
              <button class="user-avatar-btn">
                <img :src="getAvatar(user.avatar, user.nickname)" :alt="user.nickname" class="user-avatar" />
                <span class="user-name">{{ user.nickname }}</span>
                <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
              </button>

              <!-- 下拉菜单 -->
              <div class="dropdown-menu">
                <button class="dropdown-item" @click="goProfile">
                  <el-icon class="dropdown-icon"><User /></el-icon>
                  <span>个人中心</span>
                </button>
                <button class="dropdown-item" @click="goPostPublish">
                  <el-icon class="dropdown-icon"><Edit /></el-icon>
                  <span>发布帖子</span>
                </button>
                <button class="dropdown-item" @click="goCollect">
                  <el-icon class="dropdown-icon"><Collection /></el-icon>
                  <span>我的收藏</span>
                </button>
                <button class="dropdown-item" @click="goMessage">
                  <el-icon class="dropdown-icon"><ChatLineSquare /></el-icon>
                  <span>消息通知</span>
                </button>
                <div class="dropdown-divider"></div>
                <button class="dropdown-item logout-item" @click="logout">
                  <el-icon class="dropdown-icon"><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </button>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getUser, clearUser, store, clearSearchKeyword } from '../store'
import { getAvatar } from '../utils'
import { Icons } from '../assets/icons'

// 使用公共 Logo URL
const LOGO_URL = Icons.logo

const router = useRouter()
const searchKeyword = ref('')

// 监听 store 中的搜索关键词变化，用于清空搜索栏
watch(() => store.searchKeyword, (newVal) => {
  if (newVal === '') {
    searchKeyword.value = ''
  }
})

const user = computed(() => getUser())
const currentPath = computed(() => router.currentRoute.value.path)

const goHome = () => {
  router.push('/')
}

const goAbout = () => {
  router.push('/about')
}

const goLogin = () => {
  router.push('/login')
}

const goProfile = () => {
  router.push('/profile')
  closeDropdown()
}

const goPostPublish = () => {
  router.push('/post/publish')
  closeDropdown()
}

const goCollect = () => {
  router.push('/collect')
  closeDropdown()
}

const goMessage = () => {
  router.push('/message')
  closeDropdown()
}

const logout = () => {
  clearUser()
  router.push('/')
  closeDropdown()
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    store.searchKeyword = searchKeyword.value
    router.push(`/search?keyword=${encodeURIComponent(searchKeyword.value)}`)
    // 搜索后清空搜索栏
    searchKeyword.value = ''
  }
}

const closeDropdown = () => {
  // 关闭下拉菜单
}
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 70px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(139, 92, 246, 0.1);
  z-index: 999;
  box-shadow: 0 2px 20px rgba(139, 92, 246, 0.08);
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 左侧 Logo */
.header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.header-left:hover {
  transform: scale(1.02);
}

.logo {
  width: 45px;
  height: 45px;
  border-radius: 12px;
  object-fit: cover;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.2);
}

.brand-name {
  font-size: 1.3rem;
  font-weight: 700;
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 中间搜索框 */
.header-center {
  flex: 1;
  max-width: 450px;
  margin: 0 3rem;
}

.search-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: #f3f4f6;
  border-radius: 25px;
  padding: 0.5rem 1rem;
  transition: all 0.3s ease;
}

.search-wrapper:focus-within {
  background: white;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1), 0 4px 12px rgba(139, 92, 246, 0.1);
}

.search-icon {
  font-size: 1rem;
  margin-right: 0.75rem;
  color: #9ca3af;
}

.search-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  font-size: 0.95rem;
  color: #374151;
}

.search-input::placeholder {
  color: #9ca3af;
}

.search-btn {
  padding: 0.5rem 1.25rem;
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  border: none;
  border-radius: 20px;
  color: white;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
}

/* 右侧导航 */
.header-right {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.nav-links {
  display: flex;
  gap: 0.25rem;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 1rem;
  background: transparent;
  border: none;
  border-radius: 12px;
  font-size: 0.9rem;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-link:hover {
  background: rgba(139, 92, 246, 0.08);
  color: #8b5cf6;
}

.nav-link.active {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.15) 0%, rgba(168, 85, 247, 0.1) 100%);
  color: #8b5cf6;
  font-weight: 600;
}

.nav-icon {
  font-size: 0.95rem;
}

.nav-text {
  white-space: nowrap;
}

/* 用户操作区 */
.user-area {
  display: flex;
  align-items: center;
}

/* 登录按钮 */
.login-btn {
  padding: 0.625rem 1.5rem;
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  border: none;
  border-radius: 20px;
  color: white;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.25);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 92, 246, 0.35);
}

/* 用户下拉 */
.user-dropdown {
  position: relative;
}

.user-avatar-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.375rem 0.5rem;
  background: transparent;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-avatar-btn:hover {
  background: rgba(139, 92, 246, 0.08);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f3e8ff;
}

.user-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: #374151;
  white-space: nowrap;
}

.dropdown-arrow {
  font-size: 0.65rem;
  color: #9ca3af;
  transition: transform 0.3s ease;
}

.user-avatar-btn:hover .dropdown-arrow {
  transform: rotate(180deg);
}

/* 下拉菜单 */
.dropdown-menu {
  position: absolute;
  top: calc(100% + 0.5rem);
  right: 0;
  min-width: 180px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(139, 92, 246, 0.15);
  padding: 0.5rem;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.3s ease;
  z-index: 1000;
}

.user-dropdown:hover .dropdown-menu,
.user-dropdown:focus-within .dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  width: 100%;
  padding: 0.625rem 0.75rem;
  background: transparent;
  border: none;
  border-radius: 10px;
  font-size: 0.875rem;
  color: #4b5563;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
}

.dropdown-item:hover {
  background: rgba(139, 92, 246, 0.08);
  color: #8b5cf6;
}

.dropdown-icon {
  font-size: 0.9rem;
}

.dropdown-divider {
  height: 1px;
  background: #f3f4f6;
  margin: 0.5rem 0;
}

.logout-item:hover {
  background: rgba(239, 68, 68, 0.08);
  color: #ef4444;
}

/* 响应式 */
@media (max-width: 900px) {
  .header-container {
    padding: 0 1rem;
  }

  .header-center {
    display: none;
  }

  .nav-text {
    display: none;
  }

  .nav-link {
    padding: 0.5rem;
  }

  .user-name {
    display: none;
  }

  .brand-name {
    display: none;
  }
}

@media (max-width: 500px) {
  .header {
    height: 60px;
  }

  .logo {
    width: 38px;
    height: 38px;
  }

  .user-avatar {
    width: 32px;
    height: 32px;
  }

  .nav-links {
    gap: 0.125rem;
  }

  .nav-link {
    padding: 0.375rem;
  }

  .login-btn {
    padding: 0.5rem 1rem;
    font-size: 0.85rem;
  }
}
</style>
