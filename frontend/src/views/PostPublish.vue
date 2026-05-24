<template>
  <div class="publish-container">
    <div class="publish-card">
      <!-- 头部 -->
      <div class="card-header">
        <button class="back-btn" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
        </button>
        <h1 class="card-title">发布帖子</h1>
        <div class="header-placeholder"></div>
      </div>

      <!-- 提示信息 -->
      <div v-if="error" class="alert alert-error">
        <el-icon class="alert-icon"><Close /></el-icon>
        {{ error }}
      </div>
      <div v-if="success" class="alert alert-success">
        <el-icon class="alert-icon"><SuccessFilled /></el-icon>
        {{ success }}
      </div>

      <!-- 发布表单 -->
      <form @submit.prevent="handleSubmit" class="publish-form">
        <!-- 标题输入 -->
        <div class="form-group">
          <label class="form-label">
            <el-icon class="label-icon"><Document /></el-icon>
            帖子标题
          </label>
          <input 
            v-model="title" 
            type="text" 
            class="form-input"
            placeholder="请输入一个有吸引力的标题..."
            maxlength="100"
            required 
          />
          <div class="input-hint">
            <span>{{ title.length }}/100</span>
          </div>
        </div>

        <!-- 分类选择 -->
        <div class="form-group">
          <div class="label-row">
            <label class="form-label">
              <el-icon class="label-icon"><PriceTag /></el-icon>
              选择分类
            </label>
            <button type="button" class="custom-tag-btn" @click="showCustomTagModal = true">
              <el-icon><Plus /></el-icon>
              <span>自定义标签</span>
            </button>
          </div>
          <div class="category-grid">
            <button
              v-for="category in categories"
              :key="category.id"
              type="button"
              :class="['category-btn', { active: categoryId === category.id, custom: category.isCustom }]"
              @click="categoryId = category.id"
            >
              <span class="category-name">{{ category.name }}</span>
            </button>
          </div>
        </div>

        <!-- 内容输入 -->
        <div class="form-group">
          <label class="form-label">
            <el-icon class="label-icon"><Edit /></el-icon>
            帖子内容
          </label>
          <textarea 
            v-model="content" 
            class="form-textarea"
            placeholder="分享你的想法、经验或故事..."
            maxlength="5000"
            required
          ></textarea>
          <div class="input-hint">
            <span>{{ content.length }}/5000</span>
          </div>
        </div>

        <!-- 图片上传 -->
        <div class="form-group">
          <label class="form-label">
            <el-icon class="label-icon"><Picture /></el-icon>
            添加图片（可选）
          </label>
          <div class="image-upload-area">
            <div v-if="imageUrls.length > 0" class="image-preview-grid">
              <div v-for="(img, index) in imageUrls" :key="index" class="image-preview">
                <img :src="img" alt="预览图片" />
                <button type="button" class="remove-btn" @click="removeImage(index)">
                  <el-icon><Close /></el-icon>
                </button>
              </div>
            </div>
            <div v-if="imageUrls.length < 9" class="upload-btn" @click="triggerUpload">
              <el-icon class="upload-icon"><Plus /></el-icon>
              <span class="upload-text">添加图片</span>
              <span class="upload-hint">最多9张</span>
            </div>
            <input 
              ref="fileInput"
              type="file" 
              accept="image/*" 
              multiple 
              hidden 
              @change="handleFileChange"
            />
          </div>
        </div>

        <!-- 自定义标签弹窗 -->
        <div v-if="showCustomTagModal" class="modal-overlay" @click.self="showCustomTagModal = false">
          <div class="modal-content">
            <div class="modal-header">
              <h3>添加自定义标签</h3>
              <button class="modal-close" @click="showCustomTagModal = false">
                <el-icon><Close /></el-icon>
              </button>
            </div>
            <div class="modal-body">
              <label class="modal-label">标签名称</label>
              <div class="input-wrapper">
                <span class="tag-prefix">#</span>
                <input 
                  v-model="customTagName" 
                  type="text" 
                  class="custom-tag-input"
                  placeholder="请输入标签名称"
                  maxlength="20"
                  @keyup.enter="handleAddCustomTag"
                />
              </div>
              <p class="modal-hint">自定义标签将以 # 开头标识</p>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" @click="showCustomTagModal = false">取消</button>
              <button class="btn btn-primary" @click="handleAddCustomTag">添加标签</button>
            </div>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <button type="button" class="btn btn-secondary" @click="goBack">
            取消
          </button>
          <button type="submit" class="btn btn-primary" :disabled="isSubmitting || isUploading">
            <el-icon v-if="isSubmitting || isUploading" class="loading-icon"><Loading /></el-icon>
            <span>{{ isUploading ? '上传图片中...' : isSubmitting ? '发布中...' : '发布帖子' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { postApi, categoryApi } from '../api'
import { getUser } from '../store'
// 引入 Element Plus 图标组件
import { ArrowLeft, Document, PriceTag, Edit, Picture, Plus, Close, Loading, SuccessFilled } from '@element-plus/icons-vue'

const router = useRouter()
// 帖子标题
const title = ref('')
// 帖子内容
const content = ref('')
// 选中的分类ID
const categoryId = ref(null)
// 分类列表
const categories = ref([])
// 用户选择的图片文件列表（用于上传）
const imageFiles = ref([])
// 图片预览URL列表（用于本地预览显示）
const imageUrls = ref([])
// 错误提示信息
const error = ref('')
// 成功提示信息
const success = ref('')
// 是否正在提交帖子
const isSubmitting = ref(false)
// 是否正在上传图片
const isUploading = ref(false)
// 文件输入框的DOM引用
const fileInput = ref(null)
// 自定义标签相关
const showCustomTagModal = ref(false)
const customTagName = ref('')

// 获取当前登录用户信息
const user = computed(() => getUser())

// 如果用户未登录，重定向到登录页面
if (!user.value) {
  router.push('/login')
}

/**
 * 加载分类列表
 * 从后端获取所有可用的帖子分类
 */
const loadCategories = async () => {
  try {
    const res = await postApi.getCategories()
    if (res.data.code === 200) {
      categories.value = res.data.data
    }
  } catch (err) {
    console.error('加载分类失败:', err)
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 触发文件选择对话框
const triggerUpload = () => {
  fileInput.value?.click()
}

/**
 * 处理文件选择变化
 * 将用户选择的图片文件转换为本地预览URL
 * @param {Event} event - 文件输入框的change事件
 */
const handleFileChange = (event) => {
  const files = event.target.files
  if (!files) return

  // 遍历用户选择的文件，最多支持9张图片
  for (let i = 0; i < files.length; i++) {
    if (imageFiles.value.length >= 9) break
    const file = files[i]
    // 只处理图片类型的文件
    if (file.type.startsWith('image/')) {
      // 保存原始文件对象（用于上传）
      imageFiles.value.push(file)
      // 读取文件并生成Base64预览URL
      const reader = new FileReader()
      reader.onload = (e) => {
        imageUrls.value.push(e.target.result)
      }
      reader.readAsDataURL(file)
    }
  }
  // 清空input值，允许重复选择同一张图片
  event.target.value = ''
}

/**
 * 移除指定索引的图片
 * 同时从文件列表和预览URL列表中移除
 * @param {number} index - 要移除的图片索引
 */
const removeImage = (index) => {
  imageFiles.value.splice(index, 1)
  imageUrls.value.splice(index, 1)
}

/**
 * 上传所有选中的图片到服务器
 * 逐个上传图片并收集返回的URL列表
 * @returns {Promise<string[]>} 返回上传成功后的图片URL数组
 * @throws {Error} 如果上传失败则抛出错误
 */
const uploadImages = async () => {
  // 如果没有图片，直接返回空数组
  if (imageFiles.value.length === 0) {
    return []
  }

  isUploading.value = true
  const uploadedUrls = []

  try {
    // 遍历所有图片文件，逐个上传
    for (const file of imageFiles.value) {
      const res = await postApi.uploadImage(file)
      // 如果后端返回成功，将图片URL添加到结果数组
      if (res.data.code === 200) {
        uploadedUrls.push(res.data.data)
      }
    }
    return uploadedUrls
  } catch (err) {
    console.error('图片上传失败:', err)
    throw new Error('图片上传失败')
  } finally {
    // 无论成功或失败，都要结束上传状态
    isUploading.value = false
  }
}

/**
 * 处理帖子提交
 * 执行表单验证，依次上传图片，然后提交帖子数据
 */
const handleSubmit = async () => {
  error.value = ''
  success.value = ''

  // 检查用户是否登录
  if (!user.value) {
    error.value = '请先登录'
    router.push('/login')
    return
  }

  // 表单验证：检查标题是否为空
  if (!title.value.trim()) {
    error.value = '请输入帖子标题'
    return
  }

  // 表单验证：检查是否选择了分类
  if (!categoryId.value) {
    error.value = '请选择分类'
    return
  }

  // 表单验证：检查内容是否为空
  if (!content.value.trim()) {
    error.value = '请输入帖子内容'
    return
  }

  isSubmitting.value = true

  try {
    // 先上传所有图片，获取图片URL列表
    let finalImageUrls = []
    if (imageFiles.value.length > 0) {
      finalImageUrls = await uploadImages()
    }

    // 提交帖子数据到后端
    const res = await postApi.publish({
      userId: user.value.id,
      title: title.value.trim(),
      content: content.value.trim(),
      categoryId: parseInt(categoryId.value),
      imageUrls: finalImageUrls
    })

    // 根据后端返回的code判断是否成功
    if (res.data.code === 200) {
      success.value = '发布成功！正在跳转...'
      // 1.5秒后跳转到首页
      setTimeout(() => {
        router.push('/')
      }, 1500)
    } else {
      error.value = res.data.message || '发布失败，请重试'
    }
  } catch (err) {
    error.value = err.message || '网络错误，请检查网络连接'
  } finally {
    isSubmitting.value = false
  }
}

/**
 * 处理添加自定义标签
 * 用户自定义添加的标签最前面必须添加"#"作为标识
 */
const handleAddCustomTag = async () => {
  const tagName = customTagName.value.trim()
  if (!tagName) {
    error.value = '请输入标签名称'
    return
  }
  
  if (!user.value) {
    error.value = '请先登录'
    return
  }
  
  try {
    // 调用后端API添加自定义标签
    const res = await categoryApi.addCustomTag(tagName, user.value.id)
    if (res.data.code === 200) {
      const newTag = res.data.data
      categories.value.push({
        id: newTag.id,
        name: newTag.name,
        isCustom: newTag.isCustom === 1
      })
      categoryId.value = newTag.id
      showCustomTagModal.value = false
      customTagName.value = ''
      success.value = '自定义标签添加成功！'
      
      // 3秒后清除成功提示
      setTimeout(() => {
        success.value = ''
      }, 3000)
    } else {
      error.value = res.data.message || '添加标签失败'
    }
  } catch (err) {
    error.value = '添加标签失败，请重试'
    console.error('添加自定义标签失败:', err)
  }
}

// 组件挂载时加载分类列表
onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.publish-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.publish-card {
  background: white;
  border-radius: 24px;
  padding: 2rem;
  box-shadow: 0 4px 24px rgba(139, 92, 246, 0.1);
}

/* 头部 */
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f0f0f0;
}

.back-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 12px;
  background: #f3f4f6;
  font-size: 1.2rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-btn:hover {
  background: #e5e7eb;
  transform: translateX(-2px);
}

.card-title {
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-placeholder {
  width: 40px;
}

/* 提示框 */
.alert {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  border-radius: 12px;
  margin-bottom: 1.5rem;
  font-size: 0.9rem;
}

.alert-error {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
  border: 1px solid rgba(239, 68, 68, 0.2);
}

.alert-success {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
  border: 1px solid rgba(16, 185, 129, 0.2);
}

.alert-icon {
  font-size: 1.1rem;
  display: flex;
  align-items: center;
}

/* 表单 */
.publish-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.custom-tag-btn {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.3);
}

.custom-tag-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.4);
}

.custom-tag-btn:active {
  transform: translateY(0);
}

.form-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  color: #374151;
  font-size: 0.95rem;
}

.label-icon {
  font-size: 1.1rem;
  display: flex;
  align-items: center;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 1rem;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 1rem;
  font-family: inherit;
  transition: all 0.3s ease;
  background: #f9fafb;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #8b5cf6;
  background: white;
  box-shadow: 0 0 0 4px rgba(139, 92, 246, 0.1);
}

.form-textarea {
  min-height: 200px;
  resize: vertical;
  line-height: 1.6;
}

.input-hint {
  text-align: right;
  font-size: 0.8rem;
  color: #9ca3af;
}

/* 分类选择 */
.category-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.category-btn {
  padding: 0.625rem 1.25rem;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 20px;
  font-size: 0.9rem;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s ease;
}

.category-btn:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
}

.category-btn.active {
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  border-color: transparent;
  color: white;
}

/* 图片上传 */
.image-upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.image-preview-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.image-preview {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 12px;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  font-size: 0.7rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.image-preview:hover .remove-btn {
  opacity: 1;
}

.image-preview:hover .remove-btn {
  opacity: 1;
}

.upload-btn {
  width: 100px;
  height: 100px;
  border: 2px dashed #d1d5db;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-btn:hover {
  border-color: #8b5cf6;
  background: rgba(139, 92, 246, 0.05);
}

.upload-icon {
  font-size: 1.5rem;
  display: flex;
  align-items: center;
}

.loading-icon {
  display: flex;
  align-items: center;
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.upload-text {
  font-size: 0.85rem;
  color: #6b7280;
}

.upload-hint {
  font-size: 0.7rem;
  color: #9ca3af;
}

/* 提交按钮 */
.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.form-actions .btn {
  flex: 1;
  padding: 1rem;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  border: none;
  color: white;
  box-shadow: 0 4px 14px rgba(139, 92, 246, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 92, 246, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  color: #6b7280;
}

.btn-secondary:hover {
  background: #e5e7eb;
  color: #374151;
}

.loading-spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 响应式 */
/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 400px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #1f2937;
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 8px;
  background: #f3f4f6;
  color: #6b7280;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background: #e5e7eb;
  color: #374151;
}

.modal-body {
  padding: 1.5rem;
}

.modal-label {
  display: block;
  font-size: 0.9rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.75rem;
}

.input-wrapper {
  display: flex;
  align-items: center;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.input-wrapper:focus-within {
  border-color: #8b5cf6;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.tag-prefix {
  padding: 0.875rem 1rem;
  background: #f3f4f6;
  color: #8b5cf6;
  font-weight: 600;
  font-size: 1rem;
  border-right: 2px solid #e5e7eb;
}

.custom-tag-input {
  flex: 1;
  padding: 0.875rem 1rem;
  border: none;
  font-size: 1rem;
  outline: none;
  background: transparent;
}

.custom-tag-input::placeholder {
  color: #9ca3af;
}

.modal-hint {
  margin: 0.75rem 0 0;
  font-size: 0.8rem;
  color: #9ca3af;
}

.modal-footer {
  display: flex;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.modal-footer .btn {
  flex: 1;
  padding: 0.75rem;
  font-size: 0.9rem;
}

/* 自定义标签样式 */
.category-btn.custom {
  border-color: #8b5cf6;
  color: #8b5cf6;
  background: rgba(139, 92, 246, 0.05);
}

.category-btn.custom.active {
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  color: white;
}

@media (max-width: 640px) {
  .publish-container {
    padding: 1rem 0.5rem;
  }

  .publish-card {
    padding: 1.5rem;
    border-radius: 16px;
  }

  .card-title {
    font-size: 1.2rem;
  }

  .category-grid {
    gap: 0.5rem;
  }

  .category-btn {
    padding: 0.5rem 1rem;
    font-size: 0.85rem;
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>
