package com.example.catforum.entity;

import lombok.Data;

import java.util.Date;

/**
 * 评论实体类
 * 对应数据库中的comment表
 */
@Data
public class Comment {
    private Integer id;       // 评论ID
    private Integer postId;   // 帖子ID
    private Integer userId;   // 评论人ID
    private String content;   // 评论内容
    private Date createTime;  // 评论时间
    
    // 扩展字段
    private String nickname;  // 评论人昵称
    private String avatar;    // 评论人头像
}