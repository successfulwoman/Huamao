package com.example.catforum.entity;

import lombok.Data;

/**
 * 点赞实体类
 * 对应数据库中的like表
 */
@Data
public class Like {
    private Integer id;       // 点赞ID
    private Integer userId;   // 用户ID
    private Integer postId;   // 帖子ID
}