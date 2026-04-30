package com.example.catforum.entity;

import lombok.Data;

/**
 * 帖子图片实体类
 * 对应数据库中的post_image表
 */
@Data
public class PostImage {
    private Integer id;       // 图片ID
    private Integer postId;  // 帖子ID
    private String imageUrl; // 图片地址
}