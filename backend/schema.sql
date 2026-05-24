-- 创建数据库（utf8mb4支持emoji，论坛必备）
CREATE DATABASE IF NOT EXISTS huamaodb DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
USE huamaodb;

-- 1. 用户表（主表，无外键）+ 新增管理员角色/状态字段
CREATE TABLE `user` (
                        id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
                        username VARCHAR(50) NOT NULL COMMENT '用户名',
                        password VARCHAR(50) NOT NULL COMMENT '密码',
                        useremail VARCHAR(50) COMMENT '邮箱',
                        profile VARCHAR(50) DEFAULT '这家伙很懒什么都没写' COMMENT '个人简介',
                        gender VARCHAR(4) DEFAULT '未知' COMMENT '性别：男 女 未知',
                        avatar VARCHAR(255) NULL COMMENT '头像地址（上传图片路径）',
                        create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
                        role TINYINT(1) DEFAULT 0 COMMENT '用户角色：0-普通用户 1-管理员',
                        status TINYINT(1) DEFAULT 1 COMMENT '用户状态：1-正常 0-禁用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 帖子分类表（支持系统分类和用户自定义标签）
CREATE TABLE category (
                          id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
                          name VARCHAR(50) NOT NULL COMMENT '分类名称（自定义标签以#开头）',
                          is_custom TINYINT(1) DEFAULT 0 COMMENT '是否为自定义标签：0-系统分类，1-用户自定义',
                          user_id INT NULL COMMENT '所属用户ID（系统分类为NULL）',
                          FOREIGN KEY (user_id) REFERENCES `user`(id)
                              ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 3. 帖子表（关联用户、分类）+ 新增管理员管理字段
CREATE TABLE post (
                      id INT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
                      title VARCHAR(200) NOT NULL COMMENT '标题',
                      content TEXT NOT NULL COMMENT '内容',
                      user_id INT NOT NULL COMMENT '发布人ID',
                      category_id INT NOT NULL COMMENT '分类ID',
                      like_num INT DEFAULT 0 COMMENT '点赞数',
                      comment_num INT DEFAULT 0 COMMENT '评论数',
                      collect_num INT DEFAULT 0 COMMENT '收藏数',
                      create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
                      is_top TINYINT(1) DEFAULT 0 COMMENT '是否置顶：0-否 1-是',
                      is_best TINYINT(1) DEFAULT 0 COMMENT '是否精华：0-否 1-是',
                      status TINYINT(1) DEFAULT 1 COMMENT '帖子状态：1-正常 0-隐藏',
    -- 外键1：关联用户表
                      FOREIGN KEY (user_id) REFERENCES `user`(id)
                          ON DELETE CASCADE ON UPDATE CASCADE,
    -- 外键2：关联分类表
                      FOREIGN KEY (category_id) REFERENCES category(id)
                          ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- 4. 帖子图片表（关联帖子）
CREATE TABLE post_image (
                            id INT PRIMARY KEY AUTO_INCREMENT COMMENT '图片ID',
                            post_id INT NOT NULL COMMENT '帖子ID',
                            image_url VARCHAR(255) NOT NULL COMMENT '图片地址',
    -- 外键：关联帖子表
                            FOREIGN KEY (post_id) REFERENCES post(id)
                                ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子图片表';

-- 5. 评论表（关联帖子、用户）
CREATE TABLE comment (
                         id INT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
                         post_id INT NOT NULL COMMENT '帖子ID',
                         user_id INT NOT NULL COMMENT '评论人ID',
                         content VARCHAR(500) NOT NULL COMMENT '评论内容',
                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    -- 外键1：关联帖子表
                         FOREIGN KEY (post_id) REFERENCES post(id)
                             ON DELETE CASCADE ON UPDATE CASCADE,
    -- 外键2：关联用户表
                         FOREIGN KEY (user_id) REFERENCES `user`(id)
                             ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 6. 点赞表（关联用户、帖子）
CREATE TABLE `like` (
                        id INT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
                        user_id INT NOT NULL COMMENT '用户ID',
                        post_id INT NOT NULL COMMENT '帖子ID',
                        UNIQUE KEY (user_id, post_id),
    -- 外键1：关联用户表
                        FOREIGN KEY (user_id) REFERENCES `user`(id)
                            ON DELETE CASCADE ON UPDATE CASCADE,
    -- 外键2：关联帖子表
                        FOREIGN KEY (post_id) REFERENCES post(id)
                            ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 7. 收藏表（关联用户、帖子）
CREATE TABLE collect (
                         id INT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
                         user_id INT NOT NULL COMMENT '用户ID',
                         post_id INT NOT NULL COMMENT '帖子ID',
                         UNIQUE KEY (user_id, post_id),
    -- 外键1：关联用户表
                         FOREIGN KEY (user_id) REFERENCES `user`(id)
                             ON DELETE CASCADE ON UPDATE CASCADE,
    -- 外键2：关联帖子表
                         FOREIGN KEY (post_id) REFERENCES post(id)
                             ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 8. 公告表（管理员专用：发布全站公告）
CREATE TABLE notice (
                        id INT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
                        title VARCHAR(100) NOT NULL COMMENT '公告标题',
                        content TEXT NOT NULL COMMENT '公告内容',
                        is_top TINYINT(1) DEFAULT 0 COMMENT '是否置顶：0-否 1-是',
                        top_time DATETIME NULL COMMENT '置顶时间',
                        create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 9. 举报表（用户举报违规内容，管理员处理）
CREATE TABLE report (
                        id INT PRIMARY KEY AUTO_INCREMENT COMMENT '举报ID',
                        report_user_id INT NOT NULL COMMENT '举报用户ID',
                        post_id INT NULL COMMENT '举报的帖子ID',
                        comment_id INT NULL COMMENT '举报的评论ID',
                        reason VARCHAR(200) NOT NULL COMMENT '举报原因',
                        status TINYINT(1) DEFAULT 0 COMMENT '处理状态：0-待处理 1-已处理 2-已忽略',
                        create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
                        handle_time DATETIME NULL COMMENT '处理时间',
                        FOREIGN KEY (report_user_id) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报表';

-- 10. 消息通知表（用户消息通知）
CREATE TABLE message (
                         id INT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
                         user_id INT NOT NULL COMMENT '接收用户ID',
                         sender_id INT NULL COMMENT '发送者ID（评论通知时为评论人ID，公告通知时为0）',
                         post_id INT NULL COMMENT '关联帖子ID（评论通知时使用）',
                         notice_id INT NULL COMMENT '关联公告ID（公告通知时使用）',
                         type VARCHAR(20) NOT NULL COMMENT '消息类型：comment(评论), notice(公告)',
                         content VARCHAR(500) NOT NULL COMMENT '消息内容',
                         sender_name VARCHAR(50) COMMENT '发送者名称',
                         post_title VARCHAR(200) COMMENT '帖子标题（评论通知时使用）',
                         is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE,
                         FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
                         FOREIGN KEY (notice_id) REFERENCES notice(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- 11. 敏感词表（管理员配置，自动过滤违规内容）
CREATE TABLE sensitive_word (
                                id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
                                word VARCHAR(50) NOT NULL UNIQUE COMMENT '敏感词',
                                create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表';

-- 初始化管理员账号（默认账号：admin 密码：123456）
insert into `user` (username, password, useremail, role, status)
values ('admin', '123456', 'liwenlin2022@163.com', 1, 1);