<template>
  <div class="login-container">
    <!-- 左侧Logo展示区域 -->
    <div class="login-left">
      <div class="logo-wrapper">
        <img :src="LOGO_URL" alt="花猫论坛" class="logo" />
        <h1 class="brand-title">花猫论坛</h1>
        <p class="brand-desc">管理后台</p>
      </div>
      <!-- 装饰元素 -->
      <div class="decorative-elements">
        <div class="circle circle-1"></div>
        <div class="circle circle-2"></div>
        <div class="circle circle-3"></div>
      </div>
    </div>
    
    <!-- 右侧登录表单区域 -->
    <div class="login-right">
      <div class="login-wrapper">
        <!-- 登录卡片 -->
        <div class="login-card">
          <div class="admin-badge">
            <span class="badge-icon">🛡️</span>
            <span>管理员登录</span>
          </div>
          <h2 class="login-title">欢迎回来</h2>
          <p class="login-subtitle">登录管理后台，管理论坛内容</p>
          
          <!-- 错误提示 -->
          <div v-if="error" class="alert alert-error">{{ error }}</div>
          
          <!-- 登录表单 -->
          <form @submit.prevent="handleSubmit" class="login-form">
            <!-- 账号输入 -->
            <div class="form-group">
              <label>管理员账号</label>
              <div class="input-wrapper">
                <span class="input-icon">👤</span>
                <input 
                  v-model="account" 
                  type="text" 
                  placeholder="请输入管理员账号" 
                  required 
                />
              </div>
            </div>
            
            <!-- 密码输入 -->
            <div class="form-group">
              <label>密码</label>
              <div class="input-wrapper">
                <span class="input-icon">🔒</span>
                <input 
                  v-model="password" 
                  type="password" 
                  placeholder="请输入密码" 
                  required 
                />
                <button type="button" class="toggle-pwd" @click="showPwd = !showPwd">
                  {{ showPwd ? '🙈' : '👁️' }}
                </button>
              </div>
            </div>
            
            <!-- 记住我 & 忘记密码 -->
            <div class="form-options">
              <label class="checkbox-label">
                <input type="checkbox" v-model="rememberMe" />
                <span>记住我</span>
              </label>
              <a href="#" class="forgot-pwd">忘记密码?</a>
            </div>
            
            <!-- 登录按钮 -->
            <button type="submit" class="btn-login">
              <span>登 录</span>
              <span class="btn-icon">→</span>
            </button>
          </form>
          
          <!-- 返回按钮 -->
          <button class="btn-back" @click="goBack">
            <span>←</span>
            返回首页
          </button>
        </div>
        
        <!-- 版权信息 -->
        <p class="copyright">© 2024 花猫论坛 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '../api'
import { setUser } from '../store'
import { Icons } from '../assets/icons'

// 使用公共 Logo URL
const LOGO_URL = Icons.logo

const router = useRouter()

// 状态
const account = ref('')
const password = ref('')
const rememberMe = ref(false)
const showPwd = ref(false)
const error = ref('')

// 处理登录
const handleSubmit = async () => {
  error.value = ''
  
  // 验证账号
  if (!account.value) {
    error.value = '请输入管理员账号'
    return
  }
  
  // 验证密码
  if (!password.value) {
    error.value = '请输入密码'
    return
  }
  
  // 模拟管理员登录
  if (account.value === 'admin' && password.value === 'admin123') {
    setUser({ 
      id: 0, 
      nickname: '管理员', 
      role: 'admin' 
    })
    router.push('/admin')
  } else {
    error.value = '账号或密码错误（测试账号：admin，密码：admin123）'
  }
}

// 返回首页
const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
/* 容器 */
.login-container {
  display: flex;
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 25%, #fcd34d 50%, #fbbf24 75%, #f59e0b 100%);
  overflow: hidden;
  position: relative;
}

/* 背景装饰 */
.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 20%, rgba(245, 158, 11, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(251, 191, 36, 0.12) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(252, 211, 77, 0.08) 0%, transparent 60%);
  pointer-events: none;
}

/* 左侧区域 */
.login-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.12) 0%, rgba(251, 191, 36, 0.08) 50%, rgba(252, 211, 77, 0.05) 100%);
  backdrop-filter: blur(10px);
}

.logo-wrapper {
  text-align: center;
  z-index: 1;
}

.logo {
  width: 200px;
  height: 200px;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(245, 158, 11, 0.3);
  animation: float 4s ease-in-out infinite;
}

.brand-title {
  margin-top: 2rem;
  font-size: 2.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #d97706, #f59e0b);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-desc {
  margin-top: 0.5rem;
  font-size: 1.1rem;
  color: #92400e;
}

/* 装饰元素 */
.decorative-elements {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.3;
}

.circle-1 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  top: -100px;
  left: -100px;
  animation: circleFloat1 8s ease-in-out infinite;
}

.circle-2 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #fcd34d, #fbbf24);
  bottom: 50px;
  right: 50px;
  animation: circleFloat2 6s ease-in-out infinite;
}

.circle-3 {
  width: 150px;
  height: 150px;
  background: linear-gradient(135deg, #fde68a, #fef3c7);
  top: 50%;
  right: 100px;
  animation: circleFloat3 7s ease-in-out infinite;
}

/* 右侧区域 */
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  position: relative;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.7) 0%, rgba(255, 255, 255, 0.5) 100%);
  backdrop-filter: blur(15px);
}

.login-wrapper {
  width: 100%;
  max-width: 450px;
  text-align: center;
}

/* 管理员徽章 */
.admin-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.15), rgba(251, 191, 36, 0.1));
  border: 1px solid rgba(245, 158, 11, 0.3);
  border-radius: 20px;
  margin-bottom: 1rem;
  font-size: 0.85rem;
  color: #92400e;
}

.badge-icon {
  font-size: 1rem;
}

/* 登录卡片 */
.login-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 24px;
  padding: 2.5rem;
  box-shadow: 
    0 25px 50px -12px rgba(245, 158, 11, 0.2),
    0 0 60px rgba(245, 158, 11, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  animation: slideIn 0.6s ease-out;
}

.login-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 0.5rem;
}

.login-subtitle {
  color: #6b7280;
  margin-bottom: 2rem;
}

/* 表单 */
.login-form {
  text-align: left;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #374151;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 1rem;
  font-size: 1.1rem;
}

.input-wrapper input {
  width: 100%;
  padding: 0.875rem 1rem 0.875rem 2.75rem;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: #f9fafb;
}

.input-wrapper input:focus {
  outline: none;
  border-color: #f59e0b;
  background: white;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
}

.toggle-pwd {
  position: absolute;
  right: 1rem;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
}

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: #6b7280;
  cursor: pointer;
}

.checkbox-label input {
  width: 1rem;
  height: 1rem;
  accent-color: #f59e0b;
}

.forgot-pwd {
  font-size: 0.85rem;
  color: #f59e0b;
  text-decoration: none;
}

.forgot-pwd:hover {
  text-decoration: underline;
}

/* 登录按钮 */
.btn-login {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  border: none;
  border-radius: 10px;
  color: white;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-login:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(245, 158, 11, 0.4);
}

.btn-icon {
  font-size: 1.2rem;
  transition: transform 0.3s ease;
}

.btn-login:hover .btn-icon {
  transform: translateX(3px);
}

/* 返回按钮 */
.btn-back {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-top: 1rem;
  padding: 0.875rem;
  background: transparent;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  color: #6b7280;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-back:hover {
  border-color: #f59e0b;
  color: #f59e0b;
}

/* 错误提示 */
.alert {
  padding: 0.75rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  font-size: 0.85rem;
}

.alert-error {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
  border: 1px solid rgba(239, 68, 68, 0.2);
}

/* 版权信息 */
.copyright {
  margin-top: 2rem;
  font-size: 0.8rem;
  color: #9ca3af;
}

/* 动画 */
@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-15px);
  }
}

@keyframes circleFloat1 {
  0%, 100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(30px, 30px);
  }
}

@keyframes circleFloat2 {
  0%, 100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(-20px, -20px);
  }
}

@keyframes circleFloat3 {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(20px, -20px) scale(1.1);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }
  
  .login-left {
    padding: 2rem;
    min-height: 30vh;
  }
  
  .logo {
    width: 120px;
    height: 120px;
  }
  
  .brand-title {
    font-size: 1.8rem;
  }
  
  .login-card {
    padding: 1.5rem;
  }
}
</style>
