# 花猫社区论坛网站

一个适合大学生课程设计/毕业设计的轻量化论坛平台。

## 📋 项目简介

花猫社区是一个功能完善的论坛网站，包含用户发帖、评论、点赞、收藏等核心功能，同时提供完善的管理员后台，支持用户管理、帖子管理、公告发布、举报处理等。

## 🛠️ 技术栈

### 后端
- **Java 17** - 编程语言
- **Spring Boot 3.2.0** - Web框架
- **MyBatis** - ORM框架
- **MySQL 8.0** - 数据库
- **阿里云OSS** - 对象存储服务（图片存储）

### 前端
- **Vue 3** - 前端框架
- **Vue Router 4** - 路由管理
- **Axios** - HTTP客户端
- **Vite** - 构建工具
- **Element UI** - UI组件库

## 📁 项目结构

```
Huamao/
├── backend/           # 后端SpringBoot项目
│   ├── src/main/java/com/example/catforum/
│   │   ├── controller/    # REST API控制器
│   │   ├── service/       # 业务逻辑层
│   │   ├── mapper/        # MyBatis映射接口
│   │   ├── entity/        # 数据库实体类
│   │   ├── config/        # 配置类
│   │   └── util/          # 工具类
│   ├── src/main/resources/
│   │   ├── application.yml # 应用配置
│   │   └── schema.sql     # 数据库初始化脚本
│   └── pom.xml
├── frontend/          # 前端Vue项目
│   ├── src/
│   │   ├── views/         # 页面组件
│   │   ├── components/    # 公共组件
│   │   ├── api/           # API接口封装
│   │   ├── router/        # 路由配置
│   │   ├── store/         # 状态管理
│   │   ├── App.vue        # 根组件
│   │   ├── main.js        # 入口文件
│   │   └── style.css      # 全局样式
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
└── README.md
```

## ✨ 功能模块

### 👤 用户模块（主要功能）
- ✅ 用户注册
- ✅ 用户登录
- ✅ 个人信息查看与修改
- ✅ 头像上传
- ✅ 个人资料编辑

### 📝 帖子模块（核心功能）
- ✅ 发布帖子（支持图片上传）
- ✅ 帖子列表展示
- ✅ 分类筛选
- ✅ 帖子详情查看
- ✅ 删除帖子
- ✅ 自定义标签（以#为标识）
- ✅ 标签选择与展示（最多显示5个）

### 💬 评论模块
- ✅ 发布评论
- ✅ 评论列表
- ✅ 删除评论

### ❤️ 点赞模块
- ✅ 点赞/取消点赞
- ✅ 点赞数量统计

### 🔖 收藏模块
- ✅ 收藏/取消收藏
- ✅ 我的收藏列表

### 🔐 管理员模块（新增）
- ✅ 管理员登录
- ✅ 用户管理（查看、禁用/启用、设置角色、删除）
- ✅ 帖子管理（查看、隐藏/显示、置顶、设为精华、删除）
- ✅ 公告管理（发布、编辑、删除公告）
- ✅ 举报处理（查看、处理、删除举报）
- ✅ 敏感词管理（添加、删除、批量添加敏感词）
- ✅ 数据统计（用户数、帖子数、公告数、举报数）

### 📢 公告模块（新增）
- ✅ 公告展示（首页最新公告）
- ✅ 公告发布（管理员）
- ✅ 公告编辑（管理员）
- ✅ 公告删除（管理员）

### 🚨 举报模块（新增）
- ✅ 用户提交举报
- ✅ 管理员查看举报
- ✅ 管理员处理举报
- ✅ 举报删除（管理员）

### 🚫 敏感词模块（新增）
- ✅ 敏感词管理（管理员）
- ✅ 敏感词检测
- ✅ 内容自动过滤

## 🚀 启动步骤

### 1. 数据库配置
- 创建数据库 `huamaodb`（使用utf8mb4字符集）
- 执行 `backend/schema.sql` 初始化表结构和初始数据
- 修改 `backend/src/main/resources/application.yml` 中的数据库连接配置

### 2. 后端启动
- 使用 IDEA 打开 backend 目录
- 运行 `HuamaoApplication.java`
- 服务默认运行在 `http://localhost:8081`

### 3. 前端启动
- 进入 `frontend` 目录
- 执行 `npm install` 安装依赖
- 执行 `npm run dev` 启动开发服务器
- 访问 `http://localhost:5173`

## 🔑 账号说明

### 管理员账号
- **用户名**: `admin`
- **密码**: `123456`
- **邮箱**: `liwenlin2022@163.com`

> 💡 管理员账号已在 schema.sql 中初始化，登录后可访问管理后台进行系统管理。

### 普通用户
- 可通过注册页面自行注册

## 📡 API 接口

基础路径: `http://localhost:8081/api`

### 用户接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/user/register` | 用户注册 |
| POST | `/user/login` | 用户登录 |
| POST | `/user/uploadoss` | 上传头像 |
| GET | `/user/info` | 获取用户信息 |
| POST | `/user/update` | 更新用户信息 |

### 管理员接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/user/admin/login` | 管理员登录 |
| GET | `/user/admin/list` | 获取用户列表 |
| POST | `/user/admin/status` | 更新用户状态 |
| POST | `/user/admin/role` | 更新用户角色 |
| DELETE | `/user/admin/{id}` | 删除用户 |
| GET | `/user/admin/count` | 统计用户总数 |

### 帖子接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/post/upload` | 上传帖子图片 |
| POST | `/post/publish` | 发布帖子 |
| GET | `/post/list` | 获取帖子列表 |
| GET | `/post/category` | 按分类获取帖子 |
| GET | `/post/user` | 获取用户帖子 |
| GET | `/post/detail` | 获取帖子详情 |

### 帖子管理接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/post/admin/list` | 获取所有帖子 |
| POST | `/post/admin/status` | 隐藏/显示帖子 |
| POST | `/post/admin/top` | 设置帖子置顶 |
| POST | `/post/admin/best` | 设置帖子精华 |
| DELETE | `/post/admin/{id}` | 删除帖子 |
| GET | `/post/admin/count` | 统计帖子总数 |
| GET | `/post/admin/hidden` | 获取隐藏帖子 |
| GET | `/post/admin/top` | 获取置顶帖子 |

### 公告接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/notice/latest` | 获取最新公告 |
| GET | `/notice/admin/list` | 获取所有公告 |
| POST | `/notice/admin/publish` | 发布公告 |
| POST | `/notice/admin/update` | 更新公告 |
| DELETE | `/notice/admin/{id}` | 删除公告 |

### 举报接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/report/add` | 提交举报 |
| GET | `/report/admin/list` | 获取所有举报 |
| GET | `/report/admin/pending` | 获取待处理举报 |
| POST | `/report/admin/handle` | 处理举报 |
| DELETE | `/report/admin/{id}` | 删除举报 |

### 敏感词接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/sensitive/admin/add` | 添加敏感词 |
| POST | `/sensitive/admin/batch` | 批量添加敏感词 |
| GET | `/sensitive/admin/list` | 获取所有敏感词 |
| DELETE | `/sensitive/admin/{id}` | 删除敏感词 |
| POST | `/sensitive/check` | 检查敏感词 |
| POST | `/sensitive/filter` | 过滤敏感词 |

## 📊 数据库设计

### 主要数据表
- `user` - 用户表（包含角色和状态字段）
- `category` - 分类表（支持用户自定义标签）
- `post` - 帖子表（包含置顶、精华、状态字段）
- `post_image` - 帖子图片表
- `comment` - 评论表
- `like` - 点赞表
- `collect` - 收藏表
- `notice` - 公告表
- `report` - 举报表
- `sensitive_word` - 敏感词表

### 字段说明
- 用户表 `user.role`: 0-普通用户，1-管理员
- 用户表 `user.status`: 0-禁用，1-正常
- 帖子表 `post.is_top`: 0-否，1-是
- 帖子表 `post.is_best`: 0-否，1-是
- 帖子表 `post.status`: 0-隐藏，1-正常
- 举报表 `report.status`: 0-待处理，1-已处理，2-已忽略

## 📝 更新日志

### 2026-05-14
- ✨ 新增管理员功能模块
- ✨ 新增公告管理功能
- ✨ 新增举报处理功能
- ✨ 新增敏感词管理功能
- ✨ 完善用户管理功能（禁用/启用、设置角色）
- ✨ 完善帖子管理功能（置顶、精华、隐藏）
- 📝 更新数据库表结构
- 🔧 优化项目文档

## 📄 License

本项目仅供学习交流使用。
