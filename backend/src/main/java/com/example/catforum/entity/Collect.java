package com.example.catforum.entity;

import lombok.Data;

/**
 * 收藏实体类
 * 对应数据库中的collect表
 */
@Data
public class Collect {
    private Integer id;       // 收藏ID
    private Integer userId;   // 用户ID
    private Integer postId;   // 帖子ID
}