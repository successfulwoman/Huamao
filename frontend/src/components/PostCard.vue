<template>
  <div class="post-card" @click="handleClick">
    <!-- 删除按钮 -->
    <button
      v-if="showDelete"
      class="delete-btn"
      @click.stop="handleDelete"
    >
      <el-icon :size="16"><Delete /></el-icon>
    </button>

    <!-- 用户信息 -->
    <div class="post-header">
      <div class="user-info">
        <img
          v-lazy="{ src: getAvatarUrl(post.avatar, post.nickname), size: 'avatar' }"
          :alt="post.nickname"
          class="user-avatar"
          width="40"
          height="40"
        />
        <div class="user-detail">
          <span class="user-name">{{ post.nickname }}</span>
          <span class="post-time">{{ formatTime(post.createTime) }}</span>
        </div>
      </div>
      <div class="post-category">
        <span class="category-name">{{ post.categoryName }}</span>
      </div>
    </div>

    <!-- 帖子内容 -->
    <div class="post-content">
      <h3 class="post-title">{{ post.title }}</h3>
      <p class="post-text">{{ truncateText(post.content) }}</p>
    </div>

    <!-- 帖子图片 -->
    <div v-if="post.imageUrls && post.imageUrls.length > 0" class="post-images">
      <div :class="getImageGridClass(post.imageUrls.length)">
        <img
                v-for="(img, index) in post.imageUrls.slice(0, 9)"
                :key="index"
                v-lazy="{ src: img, size: 'thumb' }"
                :alt="`帖子图片 ${index + 1}`"
                class="post-image"
                width="200"
                height="200"
              />
        <div v-if="post.imageUrls.length > 9" class="image-more">
          +{{ post.imageUrls.length - 9 }}
        </div>
      </div>
    </div>

    <!-- 神评论 -->
    <div v-if="post.topComment" class="top-comment">
      <div class="comment-info">
        <span class="commenter">{{ post.topComment.nickname }}</span>
        <span class="comment-content">{{ post.topComment.content }}</span>
      </div>
    </div>

    <!-- 互动信息 -->
    <div class="post-footer">
      <div class="interaction-stats">
        <span class="stat-item">
          <el-icon class="stat-icon"><ChatLineSquare /></el-icon>
          <span class="stat-count">{{ formatCount(post.commentCount) }}</span>
        </span>
        <span class="stat-item">
          <el-icon class="stat-icon"><Star /></el-icon>
          <span class="stat-count">{{ formatCount(post.likeCount) }}</span>
        </span>
        <span class="stat-item">
          <el-icon class="stat-icon"><Collection /></el-icon>
          <span class="stat-count">{{ formatCount(post.collectCount) }}</span>
        </span>
      </div>
      <div class="post-actions">
        <button
          class="action-btn"
          :class="{ liked: post.liked }"
          @click.stop="handleLike"
        >
          <el-icon :size="18" v-if="!post.liked"><Star /></el-icon>
          <el-icon :size="18" v-else><StarFilled /></el-icon>
        </button>
        <button
          class="action-btn"
          :class="{ collected: post.collected }"
          @click.stop="handleCollect"
        >
          <el-icon :size="18" v-if="!post.collected"><Collection /></el-icon>
          <el-icon :size="18" v-else><CollectionTag /></el-icon>
        </button>
        <button class="action-btn" @click.stop="handleComment">
          <el-icon :size="18"><ChatLineSquare /></el-icon>
        </button>
        <button class="action-btn" @click.stop="handleShare">
          <el-icon :size="18"><Share /></el-icon>
        </button>
        <button class="action-btn report-btn" @click.stop="openReportModal">
          <el-icon :size="18"><Warning /></el-icon>
        </button>
      </div>
    </div>
  </div>

  <!-- 举报弹窗 - 放在卡片外面避免事件冲突 -->
  <Teleport to="body">
    <div v-if="showReportModal" class="report-modal-overlay" @click="closeReportModal">
      <div class="report-modal" @click.stop>
        <div class="modal-header">
          <div class="modal-icon">
            <el-icon :size="24" color="#f59e0b"><Warning /></el-icon>
          </div>
          <h3>举报帖子</h3>
          <button class="close-btn" @click="closeReportModal">
            <el-icon :size="20"><Close /></el-icon>
          </button>
        </div>
        <div class="modal-body">
          <div class="post-preview">
            <p class="post-title-preview">{{ post.title }}</p>
            <p class="post-content-preview">{{ truncateText(post.content) }}</p>
          </div>
          <div class="reason-section">
            <label class="section-label">举报原因</label>
            <div class="reason-options">
              <div
                v-for="(reason, index) in reportReasons"
                :key="index"
                class="reason-option"
                :class="{ selected: selectedReason === reason.value }"
                @click="selectReason(reason.value)"
              >
                <span class="reason-radio" :class="{ checked: selectedReason === reason.value }">
                  <span v-if="selectedReason === reason.value" class="radio-inner"></span>
                </span>
                <span class="reason-label">{{ reason.label }}</span>
              </div>
            </div>
          </div>
          <div class="custom-reason">
            <label class="section-label">补充说明（选填）</label>
            <textarea
              v-model="customReason"
              placeholder="请输入详细说明..."
              rows="3"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="closeReportModal">取消</button>
          <button class="btn-submit" @click="submitReport">提交举报</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref } from 'vue'
import { Delete, ChatLineSquare, Star, StarFilled, Collection, CollectionTag, Share, Warning, Close } from '@element-plus/icons-vue'
import { getAvatar, getAvatarUrl, formatTime, truncateText, formatCount } from '../utils'
import { getUser } from '../store'
import { reportApi } from '../api'
import { ElMessage } from 'element-plus'

const props = defineProps({
  post: {
    type: Object,
    required: true
  },
  showDelete: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits([
  'click',
  'like',
  'collect',
  'comment',
  'share',
  'delete'
])

// 举报相关状态
const showReportModal = ref(false)
const selectedReason = ref('')
const customReason = ref('')

const reportReasons = [
  { label: '色情低俗', value: 'porn' },
  { label: '暴力血腥', value: 'violence' },
  { label: '违法违规', value: 'illegal' },
  { label: '广告骚扰', value: 'spam' },
  { label: '人身攻击', value: 'attack' },
  { label: '其他', value: 'other' }
]

const handleClick = () => {
  emit('click', props.post)
}

const handleLike = () => {
  emit('like', props.post.id)
}

const handleCollect = () => {
  emit('collect', props.post.id)
}

const handleComment = () => {
  emit('comment', props.post.id)
}

const handleShare = () => {
  emit('share', props.post)
}

const handleDelete = () => {
  emit('delete', props.post.id)
}

const openReportModal = () => {
  showReportModal.value = true
  selectedReason.value = ''
  customReason.value = ''
}

const closeReportModal = () => {
  showReportModal.value = false
  selectedReason.value = ''
  customReason.value = ''
}

const selectReason = (value) => {
  selectedReason.value = value
}

const submitReport = async () => {
  if (!selectedReason.value) {
    ElMessage.warning('请选择举报原因')
    return
  }

  const user = getUser()
  if (!user) {
    ElMessage.warning('请先登录')
    closeReportModal()
    return
  }

  let reasonText = reportReasons.find(r => r.value === selectedReason.value)?.label || selectedReason.value
  if (customReason.value) {
    reasonText += ' - ' + customReason.value
  }

  try {
    const response = await reportApi.addReport({
      reportUserId: user.id,
      postId: props.post.id,
      reason: reasonText
    })

    if (response.data?.code === 200) {
      ElMessage.success('举报提交成功，我们会尽快处理')
      closeReportModal()
    } else {
      ElMessage.error(response.data?.message || '举报提交失败')
    }
  } catch (error) {
    console.error('举报提交失败:', error)
    ElMessage.error('举报提交失败，请稍后重试')
  }
}

const getImageGridClass = (count) => {
  if (count === 1) return 'grid-cols-1'
  if (count === 2) return 'grid-cols-2'
  if (count === 3) return 'grid-cols-2 grid-rows-2'
  if (count === 4) return 'grid-cols-2 grid-rows-2'
  if (count === 5 || count === 6) return 'grid-cols-3'
  if (count === 7 || count === 8) return 'grid-cols-3 grid-rows-3'
  return 'grid-cols-3 grid-rows-3'
}
</script>

<style scoped>
/* ========== Card Container ========== */
.post-card {
  background: var(--glass-bg-enhanced);
  border: var(--glass-border-enhanced);
  border-radius: var(--radius-2xl);
  padding: var(--spacing-6);
  box-shadow: var(--shadow-md);
  transition: transform var(--transition-spring), box-shadow var(--transition-smooth), border-color var(--transition-smooth);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.post-card::before {
  content: '';
  position: absolute;
  inset: 0;
  background: var(--gradient-card);
  opacity: 0;
  transition: opacity var(--transition-smooth);
  z-index: 0;
  pointer-events: none;
}

.post-card:hover::before { opacity: 1; }

.post-card::after {
  content: '';
  position: absolute;
  inset: -1px;
  border-radius: inherit;
  background: var(--gradient-glow);
  opacity: 0;
  z-index: -1;
  transition: opacity var(--transition-smooth);
}

.post-card:hover::after { opacity: 1; }

.post-card:hover {
  transform: translateY(-6px) scale(1.01);
  box-shadow: var(--shadow-card-hover);
  border-color: rgba(139, 92, 246, 0.3);
}

/* ========== Delete Button ========== */
.delete-btn {
  position: absolute;
  top: var(--spacing-3);
  right: var(--spacing-3);
  width: 30px;
  height: 30px;
  border: none;
  border-radius: var(--radius-full);
  background: rgba(239, 68, 68, 0.1);
  color: var(--error-500);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transform: scale(0.8);
  transition: all var(--transition-spring);
  z-index: 10;
  backdrop-filter: blur(4px);
}

.post-card:hover .delete-btn { opacity: 1; transform: scale(1); }

.delete-btn:hover {
  background: var(--error-500);
  color: white;
  transform: scale(1.15);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

/* ========== Header ========== */
.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-4);
  position: relative;
  z-index: 1;
}

.user-info { display: flex; align-items: center; gap: var(--spacing-3); }

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-full);
  object-fit: cover;
  border: 2px solid var(--primary-100);
  box-shadow: 0 0 0 2px var(--primary-200);
  transition: box-shadow var(--transition-fast);
}

.post-card:hover .user-avatar { box-shadow: 0 0 0 3px var(--primary-300); }

.user-detail { display: flex; flex-direction: column; gap: 2px; }

.user-name {
  font-weight: var(--font-weight-semibold);
  color: var(--gray-800);
  font-size: var(--font-size-sm);
  transition: color var(--transition-fast);
}

.post-card:hover .user-name { color: var(--primary-600); }

.post-time { font-size: var(--font-size-xs); color: var(--gray-400); }

.post-category {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  padding: var(--spacing-1) var(--spacing-3);
  background: linear-gradient(135deg, var(--primary-50), var(--primary-100));
  border: 1px solid var(--primary-200);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  color: var(--primary-600);
  transition: all var(--transition-fast);
  position: relative;
  z-index: 1;
}

.post-card:hover .post-category {
  background: linear-gradient(135deg, var(--primary-100), var(--primary-200));
  border-color: var(--primary-300);
}

/* ========== Content ========== */
.post-content { margin-bottom: var(--spacing-4); position: relative; z-index: 1; }

.post-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--gray-900);
  margin-bottom: var(--spacing-2);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color var(--transition-fast);
}

.post-card:hover .post-title { color: var(--primary-700); }

.post-text {
  font-size: var(--font-size-sm);
  color: var(--gray-500);
  line-height: 1.6;
  margin-bottom: var(--spacing-3);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* ========== Images ========== */
.post-images {
  margin-bottom: var(--spacing-4);
  border-radius: var(--radius-xl);
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.post-images > div {
  display: grid;
  gap: 3px;
  aspect-ratio: 1 / 1;
  border-radius: var(--radius-xl);
  overflow: hidden;
}

.grid-cols-1 { grid-template-columns: 1fr; }
.grid-cols-2 { grid-template-columns: 1fr 1fr; }
.grid-cols-3 { grid-template-columns: 1fr 1fr 1fr; }
.grid-rows-2 { grid-template-rows: 1fr 1fr; }
.grid-rows-3 { grid-template-rows: 1fr 1fr 1fr; }

.post-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-smooth), filter var(--transition-smooth);
}

.post-card:hover .post-image { transform: scale(1.06); filter: brightness(1.05); }

.image-more {
  position: absolute;
  bottom: var(--spacing-2);
  right: var(--spacing-2);
  background: rgba(0, 0, 0, 0.65);
  backdrop-filter: blur(8px);
  color: white;
  padding: var(--spacing-1) var(--spacing-3);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  border-radius: var(--radius-full);
  letter-spacing: 0.02em;
}

/* ========== Top Comment ========== */
.top-comment {
  background: linear-gradient(135deg, var(--gray-50), var(--primary-50));
  border-radius: var(--radius-lg);
  padding: var(--spacing-3);
  margin-bottom: var(--spacing-4);
  border-left: 3px solid var(--primary-400);
  position: relative;
  z-index: 1;
  transition: border-color var(--transition-fast), background var(--transition-fast);
}

.post-card:hover .top-comment {
  border-left-color: var(--primary-500);
  background: linear-gradient(135deg, var(--primary-50), var(--primary-100));
}

.comment-info { display: flex; align-items: flex-start; gap: var(--spacing-2); }

.commenter {
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-xs);
  color: var(--primary-600);
  flex-shrink: 0;
}

.commenter::after { content: ':'; margin-left: 1px; }

.comment-content {
  font-size: var(--font-size-xs);
  color: var(--gray-600);
  line-height: 1.5;
  flex: 1;
}

/* ========== Footer ========== */
.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-4);
  border-top: 1px solid var(--gray-100);
  position: relative;
  z-index: 1;
}

.interaction-stats { display: flex; gap: var(--spacing-5); }

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-xs);
  color: var(--gray-400);
  transition: color var(--transition-fast);
}

.stat-item:hover { color: var(--primary-500); }
.stat-icon { font-size: var(--font-size-sm); }
.stat-count { font-weight: var(--font-weight-semibold); }

.post-actions { display: flex; gap: var(--spacing-1); }

.action-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all var(--transition-spring);
  padding: var(--spacing-1);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  color: var(--gray-400);
  position: relative;
}

.action-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: var(--radius-full);
  background: var(--gray-100);
  opacity: 0;
  transform: scale(0.5);
  transition: all var(--transition-spring);
}

.action-btn:hover::before { opacity: 1; transform: scale(1); }
.action-btn:hover { color: var(--primary-500); transform: scale(1.1); }
.action-btn:active { transform: scale(0.9); }

.action-btn.liked {
  color: #ef4444;
  animation: heartPop 0.4s var(--transition-spring);
}

@keyframes heartPop {
  0% { transform: scale(1); }
  30% { transform: scale(1.35); }
  60% { transform: scale(0.9); }
  100% { transform: scale(1); }
}

.action-btn.liked::before {
  background: rgba(239, 68, 68, 0.1);
  opacity: 1;
  transform: scale(1);
}

.action-btn.collected {
  color: #f59e0b;
  animation: collectPop 0.4s var(--transition-spring);
}

@keyframes collectPop {
  0% { transform: scale(1); }
  30% { transform: scale(1.35) rotate(10deg); }
  60% { transform: scale(0.9); }
  100% { transform: scale(1) rotate(0deg); }
}

.action-btn.collected::before {
  background: rgba(245, 158, 11, 0.1);
  opacity: 1;
  transform: scale(1);
}

.report-btn:hover { color: #f59e0b; }
.report-btn:hover::before { background: rgba(245, 158, 11, 0.1); }

/* ========== Report Modal ========== */
.report-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.report-modal {
  background: white;
  border-radius: var(--radius-2xl);
  width: 90%;
  max-width: 480px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: var(--shadow-2xl);
  animation: slideUp 0.35s var(--transition-spring);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(24px) scale(0.96); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  padding: var(--spacing-5);
  border-bottom: 1px solid var(--gray-100);
  position: sticky;
  top: 0;
  background: white;
  z-index: 1;
}

.modal-icon {
  width: 40px;
  height: 40px;
  background: rgba(245, 158, 11, 0.1);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-header h3 {
  flex: 1;
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--gray-800);
}

.close-btn {
  background: transparent;
  border: none;
  color: var(--gray-400);
  cursor: pointer;
  padding: var(--spacing-1);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover { background: var(--gray-100); color: var(--gray-600); }

.modal-body { padding: var(--spacing-5); }

.post-preview {
  background: linear-gradient(135deg, var(--gray-50), var(--primary-50));
  border-radius: var(--radius-xl);
  padding: var(--spacing-4);
  margin-bottom: var(--spacing-5);
  border: 1px solid var(--gray-100);
}

.post-title-preview {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--gray-800);
  margin-bottom: var(--spacing-2);
}

.post-content-preview { font-size: var(--font-size-sm); color: var(--gray-500); margin: 0; }

.reason-section, .custom-reason { margin-bottom: var(--spacing-5); }

.section-label {
  display: block;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--gray-700);
  margin-bottom: var(--spacing-3);
}

.reason-options { display: grid; grid-template-columns: repeat(3, 1fr); gap: var(--spacing-2); }

.reason-option {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-3);
  background: var(--gray-50);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
  border: 2px solid transparent;
}

.reason-option:hover { background: var(--gray-100); }
.reason-option.selected { background: rgba(139, 92, 246, 0.08); border-color: var(--primary-400); }

.reason-radio {
  width: 18px;
  height: 18px;
  border: 2px solid var(--gray-300);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all var(--transition-fast);
}

.reason-option.selected .reason-radio { border-color: var(--primary-500); background: var(--primary-500); }

.radio-inner { width: 6px; height: 6px; background: white; border-radius: 50%; }

.reason-label { font-size: var(--font-size-sm); color: var(--gray-700); }

.custom-reason textarea {
  width: 100%;
  padding: var(--spacing-3);
  border: 1px solid var(--gray-200);
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  resize: none;
  transition: all var(--transition-fast);
  box-sizing: border-box;
  font-family: inherit;
}

.custom-reason textarea:focus {
  outline: none;
  border-color: var(--primary-400);
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.custom-reason textarea::placeholder { color: var(--gray-400); }

.modal-footer {
  display: flex;
  gap: var(--spacing-3);
  padding: var(--spacing-5);
  border-top: 1px solid var(--gray-100);
}

.btn-cancel, .btn-submit {
  flex: 1;
  padding: var(--spacing-3);
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  cursor: pointer;
  transition: all var(--transition-spring);
  border: none;
}

.btn-cancel { background: var(--gray-100); color: var(--gray-600); }
.btn-cancel:hover { background: var(--gray-200); }

.btn-submit {
  background: var(--gradient-primary);
  color: white;
  box-shadow: var(--shadow-md);
}

.btn-submit:hover { box-shadow: var(--glow-button); transform: translateY(-1px); }
.btn-submit:active { transform: scale(0.97); }

/* ========== Responsive ========== */
@media (max-width: 768px) {
  .post-card { padding: var(--spacing-4); border-radius: var(--radius-xl); }
  .post-title { font-size: var(--font-size-base); }
  .user-avatar { width: 38px; height: 38px; }
  .interaction-stats { gap: var(--spacing-3); }
  .post-actions { gap: 0; }
  .action-btn { width: 30px; height: 30px; }
  .reason-options { grid-template-columns: repeat(2, 1fr); }
  .report-modal { border-radius: var(--radius-xl); }
}
</style>
