<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="register-container::before"></div>
    
    <!-- 左侧Logo展示区域 -->
    <div class="register-left">
      <div class="logo-wrapper">
        <img :src="LOGO_URL" alt="花猫社区" class="logo" />
        <h1 class="brand-title">花猫社区</h1>
        <p class="brand-desc">分享生活，交流思想</p>
      </div>
      <!-- 装饰元素 -->
      <div class="decorative-elements">
        <div class="circle circle-1"></div>
        <div class="circle circle-2"></div>
        <div class="circle circle-3"></div>
      </div>
    </div>
    
    <!-- 右侧注册表单区域 -->
    <div class="register-right">
      <div class="register-wrapper">
        <!-- 注册卡片 -->
        <div class="register-card">
          <h2 class="register-title">创建账号</h2>
          <p class="register-subtitle">加入花猫社区，开启精彩之旅</p>
          
          <!-- 错误提示 -->
          <div v-if="error" class="alert alert-error">{{ error }}</div>
          <div v-if="success" class="alert alert-success">{{ success }}</div>
          
          <!-- 注册表单 -->
          <form @submit.prevent="handleSubmit" class="register-form">
            <!-- 用户名输入 -->
            <div class="form-group">
              <label>用户名</label>
              <div class="input-wrapper">
                <el-icon class="input-icon"><User /></el-icon>
                <input 
                  v-model="username" 
                  type="text" 
                  placeholder="请输入用户名" 
                  @blur="validateUsername"
                />
              </div>
              <div v-if="usernameError" class="error-message">{{ usernameError }}</div>
            </div>
            
            <!-- 邮箱输入 -->
            <div class="form-group">
              <label>邮箱 <span class="optional">(可选填)</span></label>
              <div class="input-wrapper">
                <el-icon class="input-icon"><Message /></el-icon>
                <input 
                  v-model="email" 
                  type="email" 
                  placeholder="请输入邮箱地址" 
                />
              </div>
            </div>
            
            <!-- 密码输入 -->
            <div class="form-group">
              <label>密码</label>
              <div class="input-wrapper">
                <el-icon class="input-icon"><Lock /></el-icon>
                <input 
                  v-model="password" 
                  :type="showPwd ? 'text' : 'password'" 
                  placeholder="请输入密码（至少6位）" 
                  @blur="validatePassword"
                />
                <button type="button" class="toggle-pwd" @click="showPwd = !showPwd">
                  <el-icon v-if="showPwd"><View /></el-icon>
                  <el-icon v-else><Hide /></el-icon>
                </button>
              </div>
              <div v-if="passwordError" class="error-message">{{ passwordError }}</div>
            </div>
            
            <!-- 确认密码 -->
            <div class="form-group">
              <label>确认密码</label>
              <div class="input-wrapper">
                <el-icon class="input-icon"><Key /></el-icon>
                <input 
                  v-model="confirmPassword" 
                  :type="showConfirmPwd ? 'text' : 'password'" 
                  placeholder="请再次输入密码（至少6位）" 
                  @blur="validateConfirmPassword"
                />
                <button type="button" class="toggle-pwd" @click="showConfirmPwd = !showConfirmPwd">
                  <el-icon v-if="showConfirmPwd"><View /></el-icon>
                  <el-icon v-else><Hide /></el-icon>
                </button>
              </div>
              <div v-if="confirmPasswordError" class="error-message">{{ confirmPasswordError }}</div>
            </div>
            
            <!-- 注册按钮 -->
            <button type="submit" class="btn-register">
              <span>注 册</span>
              <el-icon class="btn-icon"><ArrowRight /></el-icon>
            </button>
          </form>
          
          <!-- 分隔线 -->
          <div class="divider">
            <span>或</span>
          </div>
          
          <!-- 已有账号 -->
          <div class="login-link">
            <span>已有账号？</span>
            <a href="/login" class="link">立即登录</a>
          </div>
          
          <!-- 游客登录 -->
          <button class="btn-guest" @click="goGuest">
            <el-icon><User /></el-icon>
            游客浏览
          </button>
        </div>
        
        <!-- 版权信息 -->
        <p class="copyright">© 2024 花猫论坛 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 注册页面组件
 * 功能：用户通过用户名、邮箱、密码进行注册
 * 所有验证逻辑由后端处理，前端仅负责数据收集和提交
 */
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api'
import { setUser } from '../store'
import { Icons } from '../assets/icons'
import { User, Message, Lock, Key, View, Hide, ArrowRight } from '@element-plus/icons-vue'

// 使用公共 Logo URL
const LOGO_URL = Icons.logo

const router = useRouter()

// 状态管理
const username = ref('')        // 用户名
const email = ref('')           // 邮箱
const password = ref('')        // 密码
const confirmPassword = ref('') // 确认密码
const showPwd = ref(false)      // 密码显示/隐藏
const showConfirmPwd = ref(false) // 确认密码显示/隐藏
const error = ref('')           // 错误提示信息
const success = ref('')         // 成功提示信息
const usernameError = ref('')   // 用户名错误提示
const passwordError = ref('')   // 密码错误提示
const confirmPasswordError = ref('') // 确认密码错误提示

// 验证用户名
const validateUsername = () => {
  if (!username.value || username.value.trim() === '') {
    usernameError.value = '用户名不能为空'
    return false
  }
  usernameError.value = ''
  return true
}

// 验证密码
const validatePassword = () => {
  if (!password.value) {
    passwordError.value = '密码不能为空'
    return false
  }
  if (password.value.length < 6) {
    passwordError.value = '密码必须输入6位及以上'
    return false
  }
  passwordError.value = ''
  return true
}

// 验证确认密码
const validateConfirmPassword = () => {
  if (!confirmPassword.value) {
    confirmPasswordError.value = '确认密码不能为空'
    return false
  }
  if (confirmPassword.value.length < 6) {
    confirmPasswordError.value = '确认密码必须输入6位及以上'
    return false
  }
  if (confirmPassword.value !== password.value) {
    confirmPasswordError.value = '两次输入的密码不一致'
    return false
  }
  confirmPasswordError.value = ''
  return true
}

/**
 * 处理注册提交
 * 先进行前端验证，验证通过后调用后端API
 */
const handleSubmit = async () => {
  // 清空之前的提示信息
  error.value = ''
  success.value = ''
  
  // 前端验证
  const isUsernameValid = validateUsername()
  const isPasswordValid = validatePassword()
  const isConfirmPasswordValid = validateConfirmPassword()
  
  // 如果任何验证失败，不提交
  if (!isUsernameValid || !isPasswordValid || !isConfirmPasswordValid) {
    return
  }
  
  try {
    // 调用后端注册接口
    const res = await userApi.register({ 
      username: username.value, 
      useremail: email.value || null,
      password: password.value, 
      confirmPassword: confirmPassword.value 
    })
    
    if (res.data.code === 200) {
      // 注册成功，显示提示并跳转到登录页面
      success.value = '注册成功，即将跳转到登录页面...'
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } else {
      // 注册失败，显示后端返回的错误信息
      error.value = res.data.message || '注册失败'
    }
  } catch (err) {
    // 网络错误处理
    error.value = '网络错误，请稍后重试'
  }
}

/**
 * 游客浏览（不验证身份，直接进入首页）
 */
const goGuest = () => {
  setUser(null)
  router.push('/')
}
</script>

<style scoped>
/* 容器 */
.register-container {
  display: flex;
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #faf5ff 0%, #f3e8ff 25%, #e9d5ff 50%, #ddd6fe 75%, #c4b5fd 100%);
  overflow: hidden;
  position: relative;
}

/* 背景装饰 */
.register-container::before {
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
.register-left {
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
.register-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  position: relative;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.7) 0%, rgba(255, 255, 255, 0.5) 100%);
  backdrop-filter: blur(15px);
}

.register-wrapper {
  width: 100%;
  max-width: 450px;
  text-align: center;
}

/* 注册卡片 */
.register-card {
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

.register-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 0.5rem;
}

.register-subtitle {
  color: #6b7280;
  margin-bottom: 2rem;
}

/* 表单 */
.register-form {
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

.form-group label .optional {
  font-weight: 400;
  color: #9ca3af;
  font-size: 0.85rem;
}

.error-message {
  color: #ef4444;
  font-size: 0.85rem;
  margin-top: 0.5rem;
  text-align: left;
  padding-left: 0.5rem;
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
  display: flex;
  align-items: center;
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
  border-color: #8b5cf6;
  background: white;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.toggle-pwd {
  position: absolute;
  right: 1rem;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
}

/* 注册按钮 */
.btn-register {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem;
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  border: none;
  border-radius: 10px;
  color: white;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 0.5rem;
}

.btn-register:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(139, 92, 246, 0.4);
}

.btn-icon {
  font-size: 1.2rem;
  transition: transform 0.3s ease;
}

.btn-register:hover .btn-icon {
  transform: translateX(3px);
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

/* 登录链接 */
.login-link {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  font-size: 0.9rem;
  color: #6b7280;
}

.login-link .link {
  color: #8b5cf6;
  text-decoration: none;
  font-weight: 600;
}

.login-link .link:hover {
  text-decoration: underline;
}

/* 游客登录 */
.btn-guest {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.875rem;
  margin-top: 1rem;
  background: transparent;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  color: #6b7280;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-guest:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
}

.btn-guest .el-icon {
  font-size: 1rem;
}

/* 提示信息 */
.alert {
  padding: 0.75rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  font-size: 0.875rem;
}

.alert-error {
  background: #fee2e2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.alert-success {
  background: #dcfce7;
  color: #16a34a;
  border: 1px solid #bbf7d0;
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
  .register-container {
    flex-direction: column;
  }
  
  .register-left {
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
  
  .register-card {
    padding: 1.5rem;
  }
}
</style>