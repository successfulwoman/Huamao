<template>
  <div class="admin-layout">
    <!-- 左侧菜单栏 -->
    <aside class="admin-sidebar">
      <!-- Logo区域 -->
      <div class="sidebar-logo">
        <img src="../assets/icons/Logo.webp" class="logo-icon" alt="Logo" width="40" height="40" />
        <div class="logo-text">花猫社区</div>
        <div class="logo-subtitle">管理后台</div>
      </div>

      <!-- 菜单列表 -->
      <nav class="sidebar-menu">
        <div 
          v-for="menu in menuList" 
          :key="menu.id" 
          class="menu-item"
        >
          <button 
            v-if="!menu.children"
            :class="['menu-btn', { active: activeMenu === menu.id }]"
            @click="activeMenu = menu.id"
          >
            <el-icon class="menu-icon"><component :is="menu.icon" /></el-icon>
            <span class="menu-label">{{ menu.label }}</span>
            <span v-if="menu.id === 'reports' && pendingReports > 0" class="menu-badge">{{ pendingReports }}</span>
          </button>
          
          <!-- 二级菜单 -->
          <div v-else class="sub-menu">
            <button 
              :class="['menu-btn', { active: activeMenu === menu.id }]"
              @click="toggleSubMenu(menu.id)"
            >
              <el-icon class="menu-icon"><component :is="menu.icon" /></el-icon>
              <span class="menu-label">{{ menu.label }}</span>
              <span class="menu-arrow" :class="{ expanded: expandedMenus.includes(menu.id) }">▼</span>
            </button>
            <div v-if="expandedMenus.includes(menu.id)" class="sub-menu-items">
              <button 
                v-for="child in menu.children" 
                :key="child.id"
                :class="['sub-menu-btn', { active: activeMenu === child.id }]"
                @click="activeMenu = child.id"
              >
                {{ child.label }}
              </button>
            </div>
          </div>
        </div>
      </nav>
    </aside>

    <!-- 主内容区域 -->
    <main class="admin-main">
      <!-- 顶部导航栏 -->
      <header class="admin-header">
        <div class="header-left">
          <h1 class="page-title">{{ currentPageTitle }}</h1>
        </div>
        <div class="header-right">
          <span class="admin-info">管理员: {{ admin?.username }}</span>
          <button class="logout-btn" @click="handleLogout">
            <el-icon><ArrowRight /></el-icon>
            退出登录
          </button>
        </div>
      </header>

      <!-- 内容区域 -->
      <div class="content-container">
        <!-- 控制台 -->
        <div v-if="activeMenu === 'dashboard'" class="dashboard-page">
          <!-- 统计卡片 -->
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon-wrapper users-bg">
                <el-icon class="stat-icon"><User /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ stats.users }}</span>
                <span class="stat-label">用户总数</span>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon-wrapper posts-bg">
                <el-icon class="stat-icon"><Document /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ stats.posts }}</span>
                <span class="stat-label">帖子总数</span>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon-wrapper comments-bg">
                <el-icon class="stat-icon"><Comment /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ stats.comments }}</span>
                <span class="stat-label">评论总数</span>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon-wrapper reports-bg">
                <el-icon class="stat-icon"><WarnTriangleFilled /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ stats.pendingReports }}</span>
                <span class="stat-label">待处理举报</span>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon-wrapper notices-bg">
                <el-icon class="stat-icon"><Bell /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ stats.notices }}</span>
                <span class="stat-label">公告总数</span>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon-wrapper sensitive-bg">
                <el-icon class="stat-icon"><Document /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ stats.sensitiveWords }}</span>
                <span class="stat-label">敏感词数量</span>
              </div>
            </div>
          </div>

          <!-- 快捷操作 -->
          <div class="quick-actions">
            <h3 class="section-title">快捷操作</h3>
            <div class="action-grid">
              <button class="action-card" @click="activeMenu = 'users'">
                <el-icon class="action-icon"><User /></el-icon>
                <span class="action-label">用户管理</span>
              </button>
              <button class="action-card" @click="activeMenu = 'posts'">
                <el-icon class="action-icon"><Document /></el-icon>
                <span class="action-label">帖子管理</span>
              </button>
              <button class="action-card" @click="activeMenu = 'reports'">
                <el-icon class="action-icon"><WarnTriangleFilled /></el-icon>
                <span class="action-label">处理举报</span>
              </button>
              <button class="action-card" @click="activeMenu = 'notices'">
                <el-icon class="action-icon"><Bell /></el-icon>
                <span class="action-label">发布公告</span>
              </button>
            </div>
          </div>
        </div>

        <!-- 用户管理 -->
        <div v-if="activeMenu === 'users'" class="users-page">
          <div class="page-header">
            <h2 class="page-title">用户管理</h2>
            <div class="search-box">
              <input v-model="userSearch" placeholder="搜索用户名或昵称..." class="search-input" />
              <el-icon class="search-icon"><Search /></el-icon>
            </div>
          </div>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>用户信息</th>
                  <th>邮箱</th>
                  <th>注册时间</th>
                  <th>角色</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in filteredUsers" :key="user.id">
                  <td>{{ user.id }}</td>
                  <td>
                    <div class="user-info">
                      <img v-lazy="{ src: user.avatar || defaultAvatar, size: 'avatar' }" class="user-avatar" width="40" height="40" />
                      <div class="user-text">
                        <span class="username">{{ user.username }}</span>
                        <span class="nickname">{{ user.profile || '暂无简介' }}</span>
                      </div>
                    </div>
                  </td>
                  <td>{{ user.useremail }}</td>
                  <td>{{ formatTime(user.createTime) }}</td>
                  <td>
                    <span :class="['role-badge', user.role === 1 ? 'admin' : 'normal']">
                      {{ user.role === 1 ? '管理员' : '普通用户' }}
                    </span>
                  </td>
                  <td>
                    <span :class="['status-badge', user.status === 1 ? 'active' : 'disabled']">
                      {{ user.status === 1 ? '正常' : '禁用' }}
                    </span>
                  </td>
                  <td>
                    <div class="action-btns">
                      <button 
                        :class="['action-btn', 'toggle-status', user.status === 1 ? 'danger' : 'success']"
                        @click="toggleUserStatus(user)"
                      >
                        {{ user.status === 1 ? '禁用' : '启用' }}
                      </button>
                      <button 
                        :class="['action-btn', 'toggle-role', user.role === 1 ? 'warning' : 'primary']"
                        @click="toggleUserRole(user)"
                      >
                        {{ user.role === 1 ? '降为普通' : '设为管理员' }}
                      </button>
                      <button class="action-btn delete" @click="deleteUser(user.id)">
                        删除
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 帖子管理 -->
        <div v-if="activeMenu === 'posts'" class="posts-page">
          <div class="page-header">
            <h2 class="page-title">帖子管理</h2>
            <div class="search-box">
              <input v-model="postSearch" placeholder="搜索帖子标题..." class="search-input" />
              <el-icon class="search-icon"><Search /></el-icon>
            </div>
          </div>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>标题</th>
                  <th>作者</th>
                  <th>分类</th>
                  <th>评论/点赞</th>
                  <th>状态</th>
                  <th>置顶/精华</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="post in filteredPosts" :key="post.id">
                  <td>{{ post.id }}</td>
                  <td class="title-cell">{{ post.title }}</td>
                  <td>{{ post.nickname }}</td>
                  <td><span class="category-tag">{{ post.categoryName }}</span></td>
                  <td>{{ post.commentCount }}/{{ post.likeCount }}</td>
                  <td>
                    <span :class="['status-badge', post.status === 1 ? 'active' : 'disabled']">
                      {{ post.status === 1 ? '正常' : '隐藏' }}
                    </span>
                  </td>
                  <td>
                    <span v-if="post.isTop" class="tag top">置顶</span>
                    <span v-if="post.isBest" class="tag best">精华</span>
                    <span v-if="!post.isTop && !post.isBest" class="tag none">-</span>
                  </td>
                  <td>
                    <div class="action-btns">
                      <button 
                        :class="['action-btn', 'toggle-top', post.isTop ? 'active' : 'secondary']"
                        @click="togglePostTop(post)"
                      >
                        {{ post.isTop ? '取消置顶' : '置顶' }}
                      </button>
                      <button 
                        :class="['action-btn', 'toggle-best', post.isBest ? 'active' : 'secondary']"
                        @click="togglePostBest(post)"
                      >
                        {{ post.isBest ? '取消精华' : '加精' }}
                      </button>
                      <button 
                        :class="['action-btn', 'toggle-status', post.status === 1 ? 'danger' : 'success']"
                        @click="togglePostStatus(post)"
                      >
                        {{ post.status === 1 ? '隐藏' : '恢复' }}
                      </button>
                      <button class="action-btn delete" @click="deletePost(post.id)">
                        删除
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 评论管理 -->
        <div v-if="activeMenu === 'comments'" class="comments-page">
          <div class="page-header">
            <h2 class="page-title">评论管理</h2>
            <div class="search-box">
              <input v-model="commentSearch" placeholder="搜索评论内容..." class="search-input" />
              <el-icon class="search-icon"><Search /></el-icon>
            </div>
          </div>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>评论内容</th>
                  <th>所属帖子</th>
                  <th>评论者</th>
                  <th>发布时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="comment in filteredComments" :key="comment.id">
                  <td>{{ comment.id }}</td>
                  <td class="content-cell">{{ comment.content }}</td>
                  <td class="title-cell">{{ comment.postTitle }}</td>
                  <td>{{ comment.nickname }}</td>
                  <td>{{ formatTime(comment.createTime) }}</td>
                  <td>
                    <div class="action-btns">
                      <button class="action-btn delete" @click="deleteComment(comment.id)">
                        删除
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 帖子标签 -->
        <div v-if="activeMenu === 'categories'" class="categories-page">
          <div class="page-header">
            <h2 class="page-title">帖子标签</h2>
            <button class="add-btn" @click="showAddCategoryModal = true">
              <span>+</span> 新增标签
            </button>
          </div>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>标签名称</th>
                  <th>是否用户</th>
                  <th>所属</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="category in categories" :key="category.id">
                  <td>{{ category.id }}</td>
                  <td>{{ category.categoryName }}</td>
                  <td>
                    <span :class="['status-badge', category.isUser === 1 ? 'active' : 'disabled']">
                      {{ category.isUser === 1 ? '是' : '否' }}
                    </span>
                  </td>
                  <td>{{ category.username || '-' }}</td>
                  <td>
                    <div class="action-btns">
                      <button class="action-btn edit" @click="editCategory(category)">编辑</button>
                      <button class="action-btn delete" @click="deleteCategory(category.id)">删除</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 公告管理 -->
        <div v-if="activeMenu === 'notices'" class="notices-page">
          <div class="page-header">
            <h2 class="page-title">公告管理</h2>
            <button class="add-btn" @click="showAddNoticeModal = true">
              <span>+</span> 发布公告
            </button>
          </div>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>标题</th>
                  <th>内容预览</th>
                  <th>发布时间</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="notice in notices" :key="notice.id">
                  <td>{{ notice.id }}</td>
                  <td>{{ notice.title }}</td>
                  <td class="content-cell">{{ notice.content }}</td>
                  <td>{{ formatTime(notice.createTime) }}</td>
                  <td>
                    <span v-if="notice.isTop === 1" class="status-badge top">置顶</span>
                    <span v-else class="status-badge active">正常</span>
                  </td>
                  <td>
                    <div class="action-btns">
                      <button class="action-btn edit" @click="editNotice(notice)">编辑</button>
                      <button 
                        :class="['action-btn', 'toggle-status', notice.isTop === 1 ? 'danger' : 'success']"
                        @click="toggleNoticeTop(notice)"
                      >
                        {{ notice.isTop === 1 ? '取消置顶' : '置顶' }}
                      </button>
                      <button class="action-btn delete" @click="deleteNotice(notice.id)">删除</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 举报管理 -->
        <div v-if="activeMenu === 'reports'" class="reports-page">
          <div class="page-header">
            <h2 class="page-title">举报审核</h2>
            <div class="filter-tabs">
              <button 
                :class="['filter-tab', { active: reportFilter === 'all' }]"
                @click="reportFilter = 'all'"
              >
                全部 ({{ reports.length }})
              </button>
              <button 
                :class="['filter-tab', { active: reportFilter === 'pending' }]"
                @click="reportFilter = 'pending'"
              >
                待处理 ({{ pendingReports }})
              </button>
            </div>
          </div>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>举报类型</th>
                  <th>举报内容</th>
                  <th>被举报人</th>
                  <th>举报人</th>
                  <th>举报时间</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="report in filteredReports" :key="report.id">
                  <td>{{ report.id }}</td>
                  <td><span class="report-type">{{ report.type }}</span></td>
                  <td class="content-cell">{{ report.content }}</td>
                  <td>{{ report.targetUsername }}</td>
                  <td>{{ report.reporterUsername }}</td>
                  <td>{{ formatTime(report.createTime) }}</td>
                  <td>
                    <span :class="['status-badge', getReportStatusClass(report.status)]">
                      {{ getReportStatusText(report.status) }}
                    </span>
                  </td>
                  <td>
                    <div class="action-btns">
                      <button v-if="report.status === 0" class="action-btn success" @click="handleReport(report.id, 1)">
                        处理
                      </button>
                      <button v-if="report.status === 0" class="action-btn danger" @click="handleReport(report.id, 2)">
                        忽略
                      </button>
                      <button class="action-btn delete" @click="deleteReport(report.id)">删除</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 敏感词管理 -->
        <div v-if="activeMenu === 'sensitive'" class="sensitive-page">
          <div class="page-header">
            <h2 class="page-title">敏感词库管理</h2>
            <div class="header-actions">
              <button class="action-btn secondary" @click="showTestSensitiveModal = true">
                <el-icon><Search /></el-icon> 测试过滤
              </button>
              <button class="add-btn" @click="showAddSensitiveModal = true">
                <span>+</span> 添加敏感词
              </button>
            </div>
          </div>
          <div class="sensitive-stats">
            <span class="stats-text">共 {{ sensitiveWords.length }} 个敏感词</span>
            <button v-if="selectedSensitiveWords.length > 0" class="btn-batch-delete" @click="batchDeleteSensitiveWords">
              批量删除 ({{ selectedSensitiveWords.length }})
            </button>
          </div>
          <div class="word-list">
            <div v-for="word in sensitiveWords" :key="word.id" class="word-item">
              <input
                type="checkbox"
                :value="word.id"
                v-model="selectedSensitiveWords"
                class="word-checkbox"
              />
              <span class="word-text">{{ word.word }}</span>
              <span class="word-time">{{ formatTime(word.createTime) }}</span>
              <button class="word-delete" @click="deleteSensitiveWord(word.id)">删除</button>
            </div>
          </div>
        </div>

        <!-- 系统设置 -->
        <div v-if="activeMenu === 'settings'" class="settings-page">
          <div class="page-header">
            <h2 class="page-title">系统设置</h2>
            <button class="action-btn secondary" @click="loadSettings">
              <el-icon><Refresh /></el-icon> 刷新
            </button>
          </div>

          <div class="settings-tabs">
            <div class="tab-nav">
              <button
                v-for="tab in settingTabs"
                :key="tab.id"
                :class="['tab-btn', { active: activeSettingTab === tab.id }]"
                @click="activeSettingTab = tab.id"
              >
                {{ tab.name }}
              </button>
            </div>

            <!-- 基本信息 -->
            <div v-if="activeSettingTab === 'basic'" class="tab-content">
              <div class="settings-form">
                <div class="form-group">
                  <label>网站名称</label>
                  <input v-model="siteSettings.name" class="form-input" placeholder="请输入网站名称" />
                </div>
                <div class="form-group">
                  <label>网站简称</label>
                  <input v-model="siteSettings.shortName" class="form-input" placeholder="请输入网站简称" />
                </div>
                <div class="form-group">
                  <label>网站描述</label>
                  <textarea v-model="siteSettings.description" class="form-textarea" rows="3" placeholder="请输入网站描述"></textarea>
                </div>
                <div class="form-group">
                  <label>网站Logo URL</label>
                  <input v-model="siteSettings.logo" class="form-input" placeholder="请输入Logo图片地址" />
                </div>
                <div class="form-group">
                  <label>ICP备案号</label>
                  <input v-model="siteSettings.icp" class="form-input" placeholder="请输入ICP备案号" />
                </div>
                <div class="form-group">
                  <label>版权信息</label>
                  <input v-model="siteSettings.copyright" class="form-input" placeholder="请输入版权信息" />
                </div>
                <div class="form-group">
                  <label>网站关键词（SEO）</label>
                  <input v-model="siteSettings.keywords" class="form-input" placeholder="多个关键词用逗号分隔" />
                </div>
              </div>
            </div>

            <!-- 功能开关 -->
            <div v-if="activeSettingTab === 'features'" class="tab-content">
              <div class="settings-form">
                <div class="form-group switch-group">
                  <label>允许用户注册</label>
                  <el-switch v-model="siteSettings.allowRegister" active-text="开启" inactive-text="关闭" />
                </div>
                <div class="form-group switch-group">
                  <label>允许发帖</label>
                  <el-switch v-model="siteSettings.allowPost" active-text="开启" inactive-text="关闭" />
                </div>
                <div class="form-group switch-group">
                  <label>允许评论</label>
                  <el-switch v-model="siteSettings.allowComment" active-text="开启" inactive-text="关闭" />
                </div>
                <div class="form-group switch-group">
                  <label>发帖需要审核</label>
                  <el-switch v-model="siteSettings.postNeedAudit" active-text="是" inactive-text="否" />
                </div>
                <div class="form-group switch-group">
                  <label>评论需要审核</label>
                  <el-switch v-model="siteSettings.commentNeedAudit" active-text="是" inactive-text="否" />
                </div>
              </div>
            </div>
          </div>

          <div class="settings-footer">
            <button class="save-btn" @click="saveSettings">
              <el-icon><Check /></el-icon> 保存设置
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- 新增/编辑标签弹窗 -->
    <div v-if="showAddCategoryModal" class="modal-overlay" @click.self="showAddCategoryModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingCategory ? '编辑标签' : '新增标签' }}</h3>
          <button class="modal-close" @click="showAddCategoryModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>标签名称</label>
            <input v-model="categoryForm.name" class="form-input" />
          </div>
          <div class="form-group">
            <label>是否用户</label>
            <select v-model="categoryForm.isUser" class="form-input">
              <option :value="0">否（管理员）</option>
              <option :value="1">是</option>
            </select>
          </div>
          <div class="form-group">
            <label>所属</label>
            <input v-model="categoryForm.username" class="form-input" placeholder="请输入用户名" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-cancel" @click="showAddCategoryModal = false">取消</button>
          <button class="btn btn-primary" @click="saveCategory">保存</button>
        </div>
      </div>
    </div>

    <!-- 发布公告弹窗 -->
    <div v-if="showAddNoticeModal" class="modal-overlay" @click.self="showAddNoticeModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingNotice ? '编辑公告' : '发布公告' }}</h3>
          <button class="modal-close" @click="showAddNoticeModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>公告标题</label>
            <input v-model="noticeForm.title" class="form-input" />
          </div>
          <div class="form-group">
            <label>公告内容</label>
            <textarea v-model="noticeForm.content" class="form-textarea"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-cancel" @click="showAddNoticeModal = false">取消</button>
          <button class="btn btn-primary" @click="saveNotice">保存</button>
        </div>
      </div>
    </div>

    <!-- 添加敏感词弹窗 -->
    <div v-if="showAddSensitiveModal" class="modal-overlay" @click.self="showAddSensitiveModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>添加敏感词</h3>
          <button class="modal-close" @click="showAddSensitiveModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>敏感词（多个用逗号、换行或空格分隔）</label>
            <textarea v-model="sensitiveForm.words" class="form-textarea" rows="6" placeholder="输入敏感词，多个用逗号、换行或空格分隔"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-cancel" @click="showAddSensitiveModal = false">取消</button>
          <button class="btn btn-primary" @click="saveSensitiveWords">添加</button>
        </div>
      </div>
    </div>

    <!-- 测试敏感词过滤弹窗 -->
    <div v-if="showTestSensitiveModal" class="modal-overlay" @click.self="showTestSensitiveModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>测试敏感词过滤</h3>
          <button class="modal-close" @click="showTestSensitiveModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>输入要测试的内容</label>
            <textarea v-model="testSensitiveContent" class="form-textarea" rows="4" placeholder="输入要测试的内容..."></textarea>
          </div>
          <div v-if="testSensitiveResult" class="test-result">
            <div class="result-item">
              <label>原文：</label>
              <p class="result-text">{{ testSensitiveResult.original }}</p>
            </div>
            <div class="result-item">
              <label>过滤后：</label>
              <p class="result-text filtered">{{ testSensitiveResult.filtered }}</p>
            </div>
            <div class="result-item">
              <label>是否包含敏感词：</label>
              <span :class="['status-badge', testSensitiveResult.containsSensitive ? 'warning' : 'success']">
                {{ testSensitiveResult.containsSensitive ? '是' : '否' }}
              </span>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-cancel" @click="showTestSensitiveModal = false">关闭</button>
          <button class="btn btn-primary" @click="testSensitiveFilter">测试</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdmin, clearAdmin } from '../store'
import { adminApi, categoryApi } from '../api'

const router = useRouter()
const admin = ref(getAdmin())

// 当前选中的菜单
const activeMenu = ref('dashboard')

// 展开的二级菜单
const expandedMenus = ref(['content'])

// 搜索关键词
const userSearch = ref('')
const postSearch = ref('')
const commentSearch = ref('')
const reportFilter = ref('all')

// 弹窗状态
const showAddCategoryModal = ref(false)
const showAddNoticeModal = ref(false)
const showAddSensitiveModal = ref(false)
const showTestSensitiveModal = ref(false)

// 敏感词相关
const selectedSensitiveWords = ref([])
const testSensitiveContent = ref('')
const testSensitiveResult = ref(null)

// 表单数据
const editingCategory = ref(null)
const categoryForm = ref({ name: '', isUser: 0, userId: null, username: '' })

const editingNotice = ref(null)
const noticeForm = ref({ title: '', content: '' })

const sensitiveForm = ref({ words: '' })

// 系统设置
const activeSettingTab = ref('basic')
const settingTabs = [
  { id: 'basic', name: '基本信息' },
  { id: 'features', name: '功能开关' }
]
const siteSettings = ref({
  name: '花猫社区论坛网站',
  shortName: '花猫社区',
  description: '一个温馨的社区论坛',
  logo: '',
  icp: '',
  copyright: '© 2024 花猫社区',
  keywords: '论坛,社区,交流',
  allowRegister: true,
  allowPost: true,
  allowComment: true,
  postNeedAudit: false,
  commentNeedAudit: false
})

// 菜单列表
const menuList = ref([
  { id: 'dashboard', icon: 'DataBoard', label: '控制台' },
  { id: 'users', icon: 'User', label: '用户管理' },
  { id: 'categories', icon: 'FolderOpened', label: '帖子标签' },
  { id: 'posts', icon: 'Document', label: '帖子管理' },
  { id: 'comments', icon: 'Comment', label: '评论管理' },
  { id: 'notices', icon: 'Bell', label: '公告管理' },
  { id: 'reports', icon: 'WarnTriangleFilled', label: '举报审核' },
  { id: 'sensitive', icon: 'Document', label: '敏感词管理' },
  { id: 'settings', icon: 'Setting', label: '系统设置' }
])

// 统计数据
const stats = ref({
  users: 156,
  posts: 423,
  comments: 1892,
  pendingReports: 3,
  notices: 12,
  sensitiveWords: 156
})

// 用户数据
const users = ref([])

// 帖子数据
const posts = ref([])

// 评论数据
const comments = ref([])

// 帖子标签数据
const categories = ref([])

// 公告数据（从后端获取）
const notices = ref([])

// 举报数据（从后端获取）
const reports = ref([])

// 模拟敏感词数据
const sensitiveWords = ref([
  { id: 1, word: '脏话1' },
  { id: 2, word: '脏话2' },
  { id: 3, word: '脏话3' },
  { id: 4, word: '广告词1' },
  { id: 5, word: '广告词2' }
])

// 计算属性
const currentPageTitle = computed(() => {
  const titles = {
    dashboard: '控制台',
    users: '用户管理',
    categories: '帖子标签',
    posts: '帖子管理',
    comments: '评论管理',
    notices: '公告管理',
    reports: '举报审核',
    sensitive: '敏感词管理',
    settings: '系统设置'
  }
  return titles[activeMenu.value] || '控制台'
})

const filteredUsers = computed(() => {
  if (!userSearch.value) return users.value
  return users.value.filter(u => 
    u.username.includes(userSearch.value) || 
    (u.profile && u.profile.includes(userSearch.value))
  )
})

const filteredPosts = computed(() => {
  if (!postSearch.value) return posts.value
  return posts.value.filter(p => p.title.includes(postSearch.value))
})

const filteredComments = computed(() => {
  if (!commentSearch.value) return comments.value
  return comments.value.filter(c => c.content.includes(commentSearch.value))
})

const filteredReports = computed(() => {
  if (reportFilter.value === 'all') return reports.value
  return reports.value.filter(r => r.status === 0)
})

const pendingReports = computed(() => {
  return reports.value.filter(r => r.status === 0).length
})

// 方法
const defaultAvatar = 'https://huamaoss.oss-cn-qingdao.aliyuncs.com/Logo.png'

const formatTime = (time) => {
  if (!time) return '-'
  return time
}

const toggleSubMenu = (menuId) => {
  const index = expandedMenus.value.indexOf(menuId)
  if (index > -1) {
    expandedMenus.value.splice(index, 1)
  } else {
    expandedMenus.value.push(menuId)
  }
}

const handleLogout = () => {
  clearAdmin()
  router.push('/admin/login')
}

// 用户管理
const loadUsers = async () => {
  try {
    const res = await adminApi.getUserList()
    users.value = res.data.data || []
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

const toggleUserStatus = async (user) => {
  const newStatus = user.status === 1 ? 0 : 1
  try {
    await adminApi.updateUserStatus(user.id, newStatus)
    user.status = newStatus
  } catch (error) {
    console.error('更新用户状态失败:', error)
  }
}

const toggleUserRole = async (user) => {
  const newRole = user.role === 1 ? 0 : 1
  const actionText = newRole === 1 ? '设为管理员' : '降为普通用户'
  
  try {
    await ElMessageBox.confirm(
      `确定要将用户「${user.username}」${actionText}吗？`,
      '操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.updateUserRole(user.id, newRole)
    user.role = newRole
    ElMessage.success(`已${actionText}`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新用户角色失败:', error)
    }
  }
}

const deleteUser = async (id) => {
  const user = users.value.find(u => u.id === id)
  try {
    await ElMessageBox.confirm(
      `确定要删除用户「${user?.username}」吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.deleteUser(id)
    users.value = users.value.filter(u => u.id !== id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
    }
  }
}

// 帖子管理
const loadPosts = async () => {
  try {
    const res = await adminApi.getPostList()
    posts.value = res.data.data || []
  } catch (error) {
    console.error('加载帖子列表失败:', error)
  }
}

const togglePostTop = async (post) => {
  const newTop = post.isTop === 1 ? 0 : 1
  const actionText = newTop === 1 ? '置顶' : '取消置顶'
  
  try {
    await ElMessageBox.confirm(
      `确定要将帖子「${post.title}」${actionText}吗？`,
      '操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.updatePostTop(post.id, newTop)
    post.isTop = newTop
    ElMessage.success(`${actionText}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新置顶状态失败:', error)
    }
  }
}

const togglePostBest = async (post) => {
  const newBest = post.isBest === 1 ? 0 : 1
  const actionText = newBest === 1 ? '加精' : '取消精华'
  
  try {
    await ElMessageBox.confirm(
      `确定要将帖子「${post.title}」${actionText}吗？`,
      '操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.updatePostBest(post.id, newBest)
    post.isBest = newBest
    ElMessage.success(`${actionText}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新精华状态失败:', error)
    }
  }
}

const togglePostStatus = async (post) => {
  const newStatus = post.status === 1 ? 0 : 1
  const actionText = newStatus === 1 ? '恢复' : '隐藏'
  
  try {
    await ElMessageBox.confirm(
      `确定要${actionText}帖子「${post.title}」吗？`,
      '操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.updatePostStatus(post.id, newStatus)
    post.status = newStatus
    ElMessage.success(`${actionText}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新帖子状态失败:', error)
    }
  }
}

const deletePost = async (id) => {
  const post = posts.value.find(p => p.id === id)
  try {
    await ElMessageBox.confirm(
      `确定要删除帖子「${post?.title}」吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.deletePost(id)
    posts.value = posts.value.filter(p => p.id !== id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除帖子失败:', error)
    }
  }
}

// 评论管理
const loadComments = async () => {
  try {
    const res = await adminApi.getCommentList()
    comments.value = res.data.data || []
  } catch (error) {
    console.error('加载评论列表失败:', error)
  }
}

const deleteComment = async (id) => {
  const comment = comments.value.find(c => c.id === id)
  try {
    await ElMessageBox.confirm(
      `确定要删除该评论吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.deleteComment(id)
    comments.value = comments.value.filter(c => c.id !== id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
    }
  }
}

// 标签管理
const loadCategories = async () => {
  try {
    const res = await categoryApi.getAdminList()
    categories.value = (res.data.data || []).map(item => ({
      id: item.id,
      categoryName: item.name,
      isUser: item.isCustom,
      userId: item.userId,
      username: item.username || (item.isCustom === 0 ? '管理员' : '未知')
    }))
  } catch (error) {
    console.error('加载标签列表失败:', error)
  }
}

const editCategory = (category) => {
  editingCategory.value = category
  categoryForm.value = {
    name: category.categoryName,
    isUser: category.isUser,
    userId: category.userId,
    username: category.username
  }
  showAddCategoryModal.value = true
}

const deleteCategory = async (id) => {
  const category = categories.value.find(c => c.id === id)
  try {
    await ElMessageBox.confirm(
      `确定要删除标签「${category?.categoryName}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await categoryApi.deleteCategory(id)
    categories.value = categories.value.filter(c => c.id !== id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除标签失败:', error)
    }
  }
}

const saveCategory = async () => {
  const data = {
    name: categoryForm.value.name,
    isCustom: categoryForm.value.isUser,
    userId: null,
    username: categoryForm.value.isUser === 0 ? null : categoryForm.value.username
  }

  try {
    if (editingCategory.value) {
      data.id = editingCategory.value.id
      await categoryApi.updateCategory(data)
      editingCategory.value.categoryName = categoryForm.value.name
      editingCategory.value.isUser = categoryForm.value.isUser
      editingCategory.value.username = categoryForm.value.username
    } else {
      const res = await categoryApi.addCategory(data)
      if (res.data.data) {
        const newItem = res.data.data
        categories.value.unshift({
          id: newItem.id,
          categoryName: newItem.name,
          isUser: newItem.isCustom,
          userId: newItem.userId,
          username: newItem.username || (newItem.isCustom === 0 ? '管理员' : '未知')
        })
      }
    }
  } catch (error) {
    console.error('保存标签失败:', error)
  }
  showAddCategoryModal.value = false
  categoryForm.value = { name: '', isUser: 0, userId: null, username: '' }
  editingCategory.value = null
}

// 公告管理
const loadNotices = async () => {
  try {
    const res = await adminApi.getNoticeList()
    notices.value = res.data.data.map(n => ({
      ...n,
      status: 1 // 公告默认都是发布状态
    })) || []
  } catch (error) {
    console.error('加载公告列表失败:', error)
  }
}

const editNotice = (notice) => {
  editingNotice.value = notice
  noticeForm.value = { title: notice.title, content: notice.content }
  showAddNoticeModal.value = true
}

const toggleNoticeTop = async (notice) => {
  const actionText = notice.isTop === 1 ? '取消置顶' : '置顶'
  
  try {
    await ElMessageBox.confirm(
      `确定要对公告「${notice.title}」${actionText}吗？`,
      '操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    if (notice.isTop === 1) {
      await adminApi.cancelNoticeTop(notice.id)
      notice.isTop = 0
      ElMessage.success('取消置顶成功')
    } else {
      await adminApi.setNoticeTop(notice.id)
      notice.isTop = 1
      ElMessage.success('置顶成功')
    }
    loadNotices()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const deleteNotice = async (id) => {
  const notice = notices.value.find(n => n.id === id)
  try {
    await ElMessageBox.confirm(
      `确定要删除公告「${notice?.title}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.deleteNotice(id)
    notices.value = notices.value.filter(n => n.id !== id)
    loadStats()
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除公告失败:', error)
    }
  }
}

const saveNotice = async () => {
  try {
    if (!noticeForm.value.title || !noticeForm.value.content) {
      ElMessage.error('标题和内容不能为空')
      return
    }
    
    if (editingNotice.value) {
      const res = await adminApi.updateNotice({
        id: editingNotice.value.id,
        title: noticeForm.value.title,
        content: noticeForm.value.content
      })
      if (res.data.code === 200) {
        ElMessage.success('公告更新成功')
        editingNotice.value.title = noticeForm.value.title
        editingNotice.value.content = noticeForm.value.content
      } else {
        ElMessage.error(res.data.message || '更新失败')
      }
    } else {
      const res = await adminApi.publishNotice({
        title: noticeForm.value.title,
        content: noticeForm.value.content
      })
      if (res.data.code === 200) {
        ElMessage.success('公告发布成功')
        loadNotices() // 刷新公告列表
        loadStats() // 更新统计数据
      } else {
        ElMessage.error(res.data.message || '发布失败')
      }
    }
  } catch (error) {
    console.error('保存公告失败:', error)
    ElMessage.error('保存失败，请稍后重试')
  }
  showAddNoticeModal.value = false
  noticeForm.value = { title: '', content: '' }
  editingNotice.value = null
}

// 举报管理
const getReportStatusClass = (status) => {
  const classes = { 0: 'warning', 1: 'success', 2: 'disabled' }
  return classes[status] || 'warning'
}

const getReportStatusText = (status) => {
  const texts = { 0: '待处理', 1: '已处理', 2: '已忽略' }
  return texts[status] || '待处理'
}

// 加载举报列表
const loadReports = async () => {
  try {
    const res = await adminApi.getReportList()
    reports.value = res.data.data.map(r => ({
        id: r.id,
        type: r.postId ? '帖子举报' : '评论举报',
        content: r.reason,
        targetUsername: r.targetUsername || '未知',
        reporterUsername: r.reporterName || '未知',
        createTime: formatDateTime(r.createTime),
        status: r.status
      })) || []
  } catch (error) {
    console.error('加载举报列表失败:', error)
  }
}

// 格式化日期时间
const formatDateTime = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`
}

const handleReport = async (id, status) => {
  const report = reports.value.find(r => r.id === id)
  const actionText = status === 1 ? '处理' : '忽略'
  
  try {
    await ElMessageBox.confirm(
      `确定要${actionText}该举报吗？`,
      '操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.handleReport(id, status)
    if (report) {
      report.status = status
    }
    ElMessage.success(status === 1 ? '举报已处理' : '举报已忽略')
  } catch (error) {
    console.error('处理举报失败:', error)
    ElMessage.error('处理举报失败')
  }
}

const deleteReport = async (id) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除该举报记录吗？`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.deleteReport(id)
    reports.value = reports.value.filter(r => r.id !== id)
    ElMessage.success('举报删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除举报失败:', error)
      ElMessage.error('删除举报失败')
    }
  }
}

// 敏感词管理
const loadSensitiveWords = async () => {
  try {
    const res = await adminApi.getSensitiveWordList()
    sensitiveWords.value = res.data.data || []
  } catch (error) {
    console.error('加载敏感词列表失败:', error)
  }
}

const deleteSensitiveWord = async (id) => {
  const word = sensitiveWords.value.find(w => w.id === id)
  try {
    await ElMessageBox.confirm(
      `确定要删除敏感词「${word?.word}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.deleteSensitiveWord(id)
    sensitiveWords.value = sensitiveWords.value.filter(w => w.id !== id)
    selectedSensitiveWords.value = selectedSensitiveWords.value.filter(sid => sid !== id)
    ElMessage.success('敏感词删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除敏感词失败:', error)
    }
  }
}

const batchDeleteSensitiveWords = async () => {
  const count = selectedSensitiveWords.value.length
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${count} 个敏感词吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.batchDeleteSensitiveWords(selectedSensitiveWords.value)
    sensitiveWords.value = sensitiveWords.value.filter(w => !selectedSensitiveWords.value.includes(w.id))
    ElMessage.success(`成功删除 ${count} 个敏感词`)
    selectedSensitiveWords.value = []
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除敏感词失败:', error)
      ElMessage.error('批量删除敏感词失败')
    }
  }
}

const saveSensitiveWords = async () => {
  try {
    // 支持逗号、换行、空格分隔
    const words = sensitiveForm.value.words
      .split(/[,，\n\s]+/)
      .map(w => w.trim())
      .filter(w => w.length > 0)

    if (words.length === 0) {
      ElMessage.warning('请输入至少一个敏感词')
      return
    }

    const res = await adminApi.batchAddSensitiveWords(words)
    ElMessage.success(res.data.data || '添加成功')
    showAddSensitiveModal.value = false
    sensitiveForm.value = { words: '' }
    loadSensitiveWords()
  } catch (error) {
    console.error('添加敏感词失败:', error)
    ElMessage.error('添加敏感词失败')
  }
}

const testSensitiveFilter = async () => {
  try {
    if (!testSensitiveContent.value.trim()) {
      ElMessage.warning('请输入要测试的内容')
      return
    }
    const res = await adminApi.testSensitiveFilter(testSensitiveContent.value)
    testSensitiveResult.value = res.data.data
  } catch (error) {
    console.error('测试敏感词过滤失败:', error)
    ElMessage.error('测试失败')
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const [usersRes, postsRes, commentsRes, noticesRes, sensitiveRes, reportsRes] = await Promise.all([
      adminApi.countUsers(),
      adminApi.countPosts(),
      adminApi.countComments(),
      adminApi.countNotices(),
      adminApi.countSensitiveWords(),
      adminApi.countPendingReports()
    ])
    stats.value = {
      users: usersRes.data.data || 0,
      posts: postsRes.data.data || 0,
      comments: commentsRes.data.data || 0,
      pendingReports: reportsRes.data.data || 0,
      notices: noticesRes.data.data || 0,
      sensitiveWords: sensitiveRes.data.data || 0
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 初始化加载数据
onMounted(() => {
  if (!admin.value) {
    router.push('/admin/login')
  } else {
    loadStats()
    loadUsers()
    loadPosts()
    loadComments()
    loadCategories()
    loadNotices()
    loadReports()
    loadSensitiveWords()
  }
})

watch(activeMenu, (newMenu) => {
  if (newMenu === 'posts') {
    loadPosts()
  } else if (newMenu === 'comments') {
    loadComments()
  } else if (newMenu === 'categories') {
    loadCategories()
  } else if (newMenu === 'notices') {
    loadNotices()
  } else if (newMenu === 'reports') {
    loadReports()
  } else if (newMenu === 'sensitive') {
    loadSensitiveWords()
  } else if (newMenu === 'settings') {
    loadSettings()
  }
})

// 系统设置
const loadSettings = async () => {
  try {
    const res = await adminApi.getAllSettingsAsMap()
    const settings = res.data.data || {}

    siteSettings.value = {
      name: settings['site.name'] || '花猫社区论坛网站',
      shortName: settings['site.shortName'] || '花猫社区',
      description: settings['site.description'] || '一个温馨的社区论坛',
      logo: settings['site.logo'] || '',
      icp: settings['site.icp'] || '',
      copyright: settings['site.copyright'] || '© 2024 花猫社区',
      keywords: settings['site.keywords'] || '论坛,社区,交流',
      allowRegister: settings['site.allowRegister'] === 'true',
      allowPost: settings['site.allowPost'] === 'true',
      allowComment: settings['site.allowComment'] === 'true',
      postNeedAudit: settings['site.postNeedAudit'] === 'true',
      commentNeedAudit: settings['site.commentNeedAudit'] === 'true'
    }
  } catch (error) {
    console.error('加载系统设置失败:', error)
    ElMessage.error('加载系统设置失败')
  }
}

const saveSettings = async () => {
  try {
    const settings = {
      'site.name': siteSettings.value.name,
      'site.shortName': siteSettings.value.shortName,
      'site.description': siteSettings.value.description,
      'site.logo': siteSettings.value.logo,
      'site.icp': siteSettings.value.icp,
      'site.copyright': siteSettings.value.copyright,
      'site.keywords': siteSettings.value.keywords,
      'site.allowRegister': String(siteSettings.value.allowRegister),
      'site.allowPost': String(siteSettings.value.allowPost),
      'site.allowComment': String(siteSettings.value.allowComment),
      'site.postNeedAudit': String(siteSettings.value.postNeedAudit),
      'site.commentNeedAudit': String(siteSettings.value.commentNeedAudit)
    }

    const res = await adminApi.batchUpdateSettings(settings)
    ElMessage.success(res.data.data || '设置保存成功')
  } catch (error) {
    console.error('保存系统设置失败:', error)
    ElMessage.error('保存系统设置失败')
  }
}
</script>

<style scoped>
/* 布局 */
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f3f4f6;
}

/* 侧边栏 */
.admin-sidebar {
  width: 240px;
  background: #1f2937;
  color: white;
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar-logo {
  padding: 2rem 1.5rem;
  text-align: center;
  border-bottom: 1px solid #374151;
}

.logo-icon {
  width: 60px;
  height: 60px;
  display: block;
  margin: 0 auto 0.5rem;
  border-radius: 8px;
}

.logo-text {
  font-size: 1.25rem;
  font-weight: 700;
  display: block;
}

.logo-subtitle {
  font-size: 0.75rem;
  color: #9ca3af;
  margin-top: 0.25rem;
}

.sidebar-menu {
  flex: 1;
  padding: 1rem 0;
}

.menu-item {
  margin-bottom: 0.25rem;
}

.menu-btn {
  width: 100%;
  display: flex;
  align-items: center;
  padding: 0.875rem 1.5rem;
  background: transparent;
  border: none;
  color: #d1d5db;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
  gap: 0.75rem;
}

.menu-btn:hover {
  background: rgba(255, 255, 255, 0.05);
  color: white;
}

.menu-btn.active {
  background: #f59e0b;
  color: white;
}

.menu-icon {
  font-size: 1rem;
}

.menu-label {
  flex: 1;
  text-align: left;
}

.menu-badge {
  background: #ef4444;
  color: white;
  font-size: 0.7rem;
  padding: 0.125rem 0.5rem;
  border-radius: 10px;
}

.menu-arrow {
  font-size: 0.6rem;
  transition: transform 0.2s ease;
}

.menu-arrow.expanded {
  transform: rotate(180deg);
}

.sub-menu-items {
  background: #111827;
}

.sub-menu-btn {
  width: 100%;
  display: block;
  padding: 0.75rem 1.5rem 0.75rem 3.5rem;
  background: transparent;
  border: none;
  color: #9ca3af;
  font-size: 0.85rem;
  cursor: pointer;
  text-align: left;
  transition: all 0.2s ease;
}

.sub-menu-btn:hover {
  background: rgba(255, 255, 255, 0.05);
  color: white;
}

.sub-menu-btn.active {
  color: #f59e0b;
  font-weight: 600;
}

/* 主内容区 */
.admin-main {
  flex: 1;
  margin-left: 240px;
  display: flex;
  flex-direction: column;
}

.admin-header {
  background: white;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e5e7eb;
}

.page-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1f2937;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.admin-info {
  color: #6b7280;
  font-size: 0.9rem;
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: #fee2e2;
  color: #dc2626;
  border: none;
  border-radius: 8px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.logout-btn:hover {
  background: #fecaca;
}

/* 内容容器 */
.content-container {
  flex: 1;
  padding: 1.5rem 2rem;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  padding: 1.25rem;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.stat-icon-wrapper {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.users-bg { background: rgba(139, 92, 246, 0.1); }
.posts-bg { background: rgba(59, 130, 246, 0.1); }
.comments-bg { background: rgba(16, 185, 129, 0.1); }
.reports-bg { background: rgba(239, 68, 68, 0.1); }
.notices-bg { background: rgba(245, 158, 11, 0.1); }
.sensitive-bg { background: rgba(236, 72, 153, 0.1); }

.stat-icon {
  font-size: 1.5rem;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
}

.stat-label {
  font-size: 0.8rem;
  color: #6b7280;
}

/* 快捷操作 */
.quick-actions {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 1rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 1rem;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1rem;
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-card:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
  transform: translateY(-2px);
}

.action-icon {
  font-size: 1.5rem;
}

.action-label {
  font-size: 0.85rem;
  color: #374151;
}

/* 页面通用 */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.search-box {
  position: relative;
}

.search-input {
  padding: 0.5rem 2.5rem 0.5rem 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 0.9rem;
  width: 250px;
  outline: none;
  transition: border-color 0.2s ease;
}

.search-input:focus {
  border-color: #f59e0b;
}

.search-icon {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: #f59e0b;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.add-btn:hover {
  background: #d97706;
}

/* 表格 */
.table-wrapper {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 0.875rem 1rem;
  text-align: left;
  border-bottom: 1px solid #f3f4f6;
}

.data-table th {
  background: #f9fafb;
  font-weight: 600;
  color: #374151;
  font-size: 0.85rem;
}

.data-table tbody tr:hover {
  background: #f9fafb;
}

.data-table td {
  font-size: 0.9rem;
  color: #4b5563;
}

.title-cell,
.content-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.user-text {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: 600;
  color: #1f2937;
}

.nickname {
  font-size: 0.8rem;
  color: #6b7280;
}

/* 标签和徽章 */
.role-badge,
.status-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 500;
}

.role-badge.admin {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

.role-badge.normal {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.status-badge.active {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
}

.status-badge.disabled {
  background: rgba(156, 163, 175, 0.1);
  color: #6b7280;
}

.status-badge.warning {
  background: rgba(245, 158, 11, 0.1);
  color: #d97706;
}

.status-badge.success {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
}

.status-badge.top {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

.category-tag {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
  border-radius: 20px;
  font-size: 0.75rem;
}

.report-type {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
  border-radius: 20px;
  font-size: 0.75rem;
}

.tag {
  font-size: 0.7rem;
  padding: 0.125rem 0.5rem;
  border-radius: 4px;
  margin-right: 0.25rem;
}

.tag.top {
  background: #ef4444;
  color: white;
}

.tag.best {
  background: #f59e0b;
  color: white;
}

.tag.none {
  color: #9ca3af;
}

/* 操作按钮 */
.action-btns {
  display: flex;
  gap: 0.375rem;
}

.action-btn {
  padding: 0.375rem 0.75rem;
  border: none;
  border-radius: 6px;
  font-size: 0.8rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn.primary {
  background: #3b82f6;
  color: white;
}

.action-btn.success {
  background: #10b981;
  color: white;
}

.action-btn.danger {
  background: #ef4444;
  color: white;
}

.action-btn.warning {
  background: #f59e0b;
  color: white;
}

.action-btn.secondary {
  background: #e5e7eb;
  color: #374151;
}

.action-btn.active {
  background: #1f2937;
}

.action-btn.edit {
  background: #f59e0b;
  color: white;
}

.action-btn.delete {
  background: #fee2e2;
  color: #dc2626;
}

.action-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

/* 敏感词管理 */
.sensitive-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding: 0.75rem 1rem;
  background: #f9fafb;
  border-radius: 8px;
}

.stats-text {
  color: #6b7280;
  font-size: 0.9rem;
}

.btn-batch-delete {
  padding: 0.5rem 1rem;
  background: #fee2e2;
  color: #dc2626;
  border: none;
  border-radius: 6px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-batch-delete:hover {
  background: #fecaca;
}

/* 敏感词列表 */
.word-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.word-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

.word-item:hover {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.word-checkbox {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.word-text {
  color: #1f2937;
  font-size: 0.9rem;
  min-width: 60px;
}

.word-time {
  color: #9ca3af;
  font-size: 0.75rem;
  margin-left: auto;
  padding: 0 0.5rem;
}

.word-delete {
  background: #fee2e2;
  color: #dc2626;
  border: none;
  border-radius: 4px;
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.word-delete:hover {
  background: #fecaca;
}

/* 测试敏感词过滤结果 */
.test-result {
  margin-top: 1rem;
  padding: 1rem;
  background: #f9fafb;
  border-radius: 8px;
}

.result-item {
  margin-bottom: 0.75rem;
}

.result-item:last-child {
  margin-bottom: 0;
}

.result-item label {
  font-weight: 600;
  color: #374151;
  font-size: 0.85rem;
}

.result-text {
  margin-top: 0.25rem;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  font-size: 0.9rem;
  color: #1f2937;
  word-break: break-all;
}

.result-text.filtered {
  background: #fef3c7;
  color: #92400e;
}

/* 头部操作按钮 */
.header-actions {
  display: flex;
  gap: 0.75rem;
}

/* 系统设置表单 */
.settings-form {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  max-width: 600px;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.2s ease;
}

.form-input:focus {
  border-color: #f59e0b;
}

.form-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.2s ease;
  min-height: 120px;
  resize: vertical;
}

.form-textarea:focus {
  border-color: #f59e0b;
}

.save-btn {
  padding: 0.75rem 1.5rem;
  background: #f59e0b;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.save-btn:hover {
  background: #d97706;
}

/* 系统设置页面 */
.settings-tabs {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.tab-nav {
  display: flex;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
}

.tab-btn {
  padding: 1rem 1.5rem;
  background: transparent;
  border: none;
  font-size: 0.95rem;
  font-weight: 500;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 2px solid transparent;
}

.tab-btn:hover {
  color: #374151;
  background: #f3f4f6;
}

.tab-btn.active {
  color: #f59e0b;
  border-bottom-color: #f59e0b;
  background: white;
}

.tab-content {
  padding: 1.5rem;
}

.switch-group {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f9fafb;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.switch-group label {
  margin-bottom: 0;
  font-weight: 500;
}

.settings-footer {
  margin-top: 1.5rem;
  padding: 1rem 1.5rem;
  background: #f9fafb;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
}

/* 过滤器标签 */
.filter-tabs {
  display: flex;
  gap: 0.5rem;
}

.filter-tab {
  padding: 0.5rem 1rem;
  background: #f3f4f6;
  border: none;
  border-radius: 8px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-tab.active {
  background: #f59e0b;
  color: white;
}

/* 弹窗 */
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
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1f2937;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #9ca3af;
  cursor: pointer;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid #e5e7eb;
}

.btn {
  padding: 0.5rem 1.25rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.btn-primary {
  background: #f59e0b;
  color: white;
}

.btn-primary:hover {
  background: #d97706;
}

.btn-cancel {
  background: #e5e7eb;
  color: #374151;
}

.btn-cancel:hover {
  background: #d1d5db;
}

/* 响应式 */
@media (max-width: 768px) {
  .admin-sidebar {
    width: 100%;
    height: auto;
    position: relative;
  }

  .admin-main {
    margin-left: 0;
  }

  .sidebar-menu {
    display: flex;
    flex-wrap: wrap;
  }

  .menu-btn {
    width: auto;
    padding: 0.75rem 1rem;
  }

  .content-container {
    padding: 1rem;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .page-header {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .search-input {
    width: 100%;
  }

  .table-wrapper {
    overflow-x: auto;
  }

  .data-table th,
  .data-table td {
    padding: 0.5rem;
    font-size: 0.8rem;
  }

  .action-btns {
    flex-direction: column;
  }
}

/* 二次确认弹窗美化 */
:deep(.el-message-box) {
  border-radius: 16px !important;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

:deep(.el-message-box__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px 24px !important;
  border-bottom: none !important;
}

:deep(.el-message-box__title) {
  color: white !important;
  font-size: 18px !important;
  font-weight: 600 !important;
}

:deep(.el-message-box__close) {
  color: rgba(255, 255, 255, 0.8) !important;
  font-size: 20px !important;
}

:deep(.el-message-box__close:hover) {
  color: white !important;
}

:deep(.el-message-box__body) {
  padding: 24px !important;
  font-size: 15px !important;
  color: #4b5563 !important;
  line-height: 1.6;
}

:deep(.el-message-box__body) p {
  margin: 0 !important;
}

:deep(.el-message-box__btns) {
  padding: 16px 24px !important;
  display: flex !important;
  justify-content: flex-end !important;
  gap: 12px !important;
  border-top: 1px solid #f3f4f6 !important;
}

:deep(.el-message-box__btns .el-button) {
  padding: 10px 24px !important;
  border-radius: 8px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
}

:deep(.el-message-box__btns .el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  color: white !important;
}

:deep(.el-message-box__btns .el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

:deep(.el-message-box__btns .el-button--default) {
  background: #f3f4f6 !important;
  border: 1px solid #e5e7eb !important;
  color: #6b7280 !important;
}

:deep(.el-message-box__btns .el-button--default:hover) {
  background: #e5e7eb !important;
  border-color: #d1d5db !important;
}

/* 警告图标美化 */
:deep(.el-message-box__icon .el-icon-warning) {
  font-size: 48px !important;
  color: #f59e0b !important;
}

/* 弹窗动画 */
:deep(.el-message-box) {
  animation: modalFadeIn 0.3s ease-out;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
</style>