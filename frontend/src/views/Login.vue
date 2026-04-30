<template>
  <div class="login-container">
    <!-- 左侧Logo展示区域 -->
    <div class="login-left">
      <div class="logo-wrapper">
        <img :src="LOGO_URL" alt="花猫论坛" class="logo" />
        <h1 class="brand-title">花猫论坛</h1>
        <p class="brand-desc">分享生活，交流思想</p>
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
          <h2 class="login-title">欢迎回来</h2>
          <p class="login-subtitle">登录您的账号，开启精彩之旅</p>
          
          <!-- 错误提示 -->
          <div v-if="error" class="alert alert-error">{{ error }}</div>
          
          <!-- 登录表单 -->
          <form @submit.prevent="handleSubmit" class="login-form">
            <!-- 用户名输入 -->
            <div class="form-group">
              <label>用户名</label>
              <div class="input-wrapper">
                <input 
                  v-model="username" 
                  type="text" 
                  placeholder="请输入用户名" 
                />
              </div>
            </div>
            
            <!-- 密码输入 -->
            <div class="form-group">
              <label>密码</label>
              <div class="input-wrapper">
                <input 
                  v-model="password" 
                  :type="showPwd ? 'text' : 'password'" 
                  placeholder="请输入密码" 
                />
                <button type="button" class="toggle-pwd" @click="showPwd = !showPwd">
                  {{ showPwd ? '隐藏' : '显示' }}
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
              登 录
            </button>
          </form>
          
          <!-- 分隔线 -->
          <div class="divider">
            <span>或</span>
          </div>
          
          <!-- 其他登录方式 -->
          <div class="other-login">
            <button class="btn-register" @click="goRegister">
              立即注册
            </button>
            <button class="btn-guest" @click="goGuest">
              游客登录
            </button>
            <button class="btn-admin" @click="goAdmin">
              管理员
            </button>
          </div>
        </div>
        
        <!-- 版权信息 -->
        <p class="copyright">© 2024 花猫论坛 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 登录页面组件
 * 功能：用户通过用户名和密码进行登录
 * 所有验证逻辑由后端处理，前端仅负责数据收集和提交
 */
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api'
import { setUser } from '../store'
import { Icons } from '../assets/icons'

// 使用公共 Logo URL
const LOGO_URL = Icons.logo

const router = useRouter()

// 状态管理
const username = ref('')   // 用户名
const password = ref('')   // 密码
const rememberMe = ref(false) // 记住我
const showPwd = ref(false)    // 密码显示/隐藏
const error = ref('')         // 错误提示信息

/**
 * 处理登录提交
 * 直接调用后端API进行验证，不进行前端验证
 */
const handleSubmit = async () => {
  // 清空之前的错误提示
  error.value = ''

  try {
    // 调用后端登录接口
    const res = await userApi.login({
      username: username.value,
      password: password.value
    })
    
    if (res.data.code === 200) {
      // 登录成功，保存用户信息并跳转到首页
      setUser(res.data.data)
      router.push('/')
    } else {
      // 登录失败，显示后端返回的错误信息
      error.value = res.data.message || '登录失败，请检查账号密码'
    }
  } catch (err) {
    // 网络错误处理
    error.value = '网络错误，请稍后重试'
  }
}

/**
 * 跳转到注册页面
 */
const goRegister = () => {
  router.push('/register')
}

/**
 * 游客登录（不验证身份，直接进入首页）
 */
const goGuest = () => {
  setUser(null)
  router.push('/')
}

/**
 * 跳转到管理员登录页面
 */
const goAdmin = () => {
  router.push('/admin/login')
}
</script>

<style scoped>
/* 错误提示 */
.alert {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  padding: 0.875rem 1rem;
  border-radius: 12px;
  margin-bottom: 1.25rem;
  font-size: 0.875rem;
  line-height: 1.4;
  animation: shake 0.5s ease-in-out;
}

.alert-error {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.08) 0%, rgba(239, 68, 68, 0.05) 100%);
  border: 1px solid rgba(239, 68, 68, 0.2);
  color: #dc2626;
}

.alert-error::before {
  content: '';
  width: 20px;
  height: 20px;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='white' stroke-width='3' stroke-linecap='round' stroke-linejoin='round'%3E%3Cline x1='18' y1='6' x2='6' y2='18'%3E%3C/line%3E%3Cline x1='6' y1='6' x2='18' y2='18'%3E%3C/line%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: center;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-4px); }
  20%, 40%, 60%, 80% { transform: translateX(4px); }
}

/* 容器 */
.login-container {
  display: flex;
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #faf5ff 0%, #f3e8ff 25%, #e9d5ff 50%, #ddd6fe 75%, #c4b5fd 100%);
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
    radial-gradient(circle at 20% 20%, rgba(139, 92, 246, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(168, 85, 247, 0.12) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(192, 132, 252, 0.08) 0%, transparent 60%);
  pointer-events: none;
}

/* 左侧区域 */
.login-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.12) 0%, rgba(168, 85, 247, 0.08) 50%, rgba(192, 132, 252, 0.05) 100%);
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
  box-shadow: 0 20px 60px rgba(139, 92, 246, 0.3);
  animation: float 4s ease-in-out infinite;
}

.brand-title {
  margin-top: 2rem;
  font-size: 2.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-desc {
  margin-top: 0.5rem;
  font-size: 1.1rem;
  color: #6b7280;
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
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  top: -100px;
  left: -100px;
  animation: circleFloat1 8s ease-in-out infinite;
}

.circle-2 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #c084fc, #d8b4fe);
  bottom: 50px;
  right: 50px;
  animation: circleFloat2 6s ease-in-out infinite;
}

.circle-3 {
  width: 150px;
  height: 150px;
  background: linear-gradient(135deg, #e9d5ff, #f3e8ff);
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

/* 登录卡片 */
.login-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 24px;
  padding: 2.5rem;
  box-shadow: 
    0 25px 50px -12px rgba(139, 92, 246, 0.2),
    0 0 60px rgba(139, 92, 246, 0.05),
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
}

.input-wrapper input {
  width: 100%;
  padding: 0.875rem 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: #f9fafb;
}

.input-wrapper input:focus {
  outline: none;
  border-color: #8b5cf6;
  background: white;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.toggle-pwd {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #8b5cf6;
  font-size: 0.85rem;
  cursor: pointer;
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
  accent-color: #8b5cf6;
}

.forgot-pwd {
  font-size: 0.85rem;
  color: #8b5cf6;
  text-decoration: none;
}

.forgot-pwd:hover {
  text-decoration: underline;
}

/* 登录按钮 */
.btn-login {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
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
  box-shadow: 0 8px 25px rgba(139, 92, 246, 0.4);
}

/* 分隔线 */
.divider {
  display: flex;
  align-items: center;
  margin: 1.5rem 0;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e5e7eb;
}

.divider span {
  padding: 0 1rem;
  color: #9ca3af;
  font-size: 0.85rem;
}

/* 其他登录方式 */
.other-login {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.btn-register,
.btn-guest,
.btn-admin {
  flex: 1;
  min-width: calc(33.33% - 0.375rem);
  padding: 0.75rem 0.5rem;
  border-radius: 10px;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-register {
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  border: none;
  color: white;
}

.btn-register:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(139, 92, 246, 0.3);
}

.btn-guest {
  background: transparent;
  border: 1px solid #e5e7eb;
  color: #6b7280;
}

.btn-guest:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
}

.btn-admin {
  background: transparent;
  border: 1px solid #f59e0b;
  color: #f59e0b;
}

.btn-admin:hover {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  border-color: #f59e0b;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(245, 158, 11, 0.3);
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