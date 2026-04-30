
# 花猫论坛网站

一个适合大学生课程设计/毕业设计的轻量化论坛平台。

## 项目技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- MyBatis
- MySQL 8.0

### 前端
- Vue 3
- Vue Router 4
- Axios
- Vite

## 项目结构

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
│   │   ├── mapper/        # MyBatis XML配置
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

## 功能模块


### 用户模块(主要功能)
- 用户注册
- 用户登录
- 个人信息查看与修改

### 管理员模块(暂且不实现)
- 管理员登录
- 用户列表管理
- 帖子列表管理

### 帖子模块(主要功能)
- 发布帖子
- 帖子列表展示
- 分类筛选
- 帖子详情
- 删除帖子

### 评论模块(次要模块)
- 发布评论
- 评论列表

### 点赞模块(次要模块)
- 点赞/取消点赞
- 点赞数量统计

### 收藏模块(次要模块)
- 收藏/取消收藏
- 我的收藏列表

### 消息模块(次要模块)
- 我的消息
- 标记已读

## 启动步骤

### 1. 数据库配置
- 创建数据库 `huamaodb`
- 执行 `backend/src/main/resources/schema.sql` 初始化表结构和初始数据
- username: `root`
- password: `root`

### 2. 后端启动
- 修改 `backend/src/main/resources/application.yml` 中的数据库连接配置
- 使用 IDEA 运行 `HuamaoApplication.java`
- 服务默认运行在 `http://localhost:8081`

### 3. 前端启动
- 进入 `frontend` 目录
- 执行 `npm install` 安装依赖
- 执行 `npm run dev` 启动开发服务器
- 访问 `http://localhost:5173`

## 管理员账号(暂且不实现)

- 用户名: `admin`
- 密码: `123456`

## API 接口

基础路径: `http://localhost:8081/api`

详细接口文档请参考项目设计文档。
