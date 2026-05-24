package com.example.catforum.entity;

import lombok.Data;

import java.util.Date;

/**
 * 举报实体类
 * 对应数据库中的report表
 */
@Data
public class Report {
    private Integer id;                // 举报ID
    private Integer reportUserId;      // 举报用户ID
    private Integer postId;            // 举报的帖子ID
    private Integer commentId;         // 举报的评论ID
    private String reason;             // 举报原因
    private Integer status;            // 处理状态：0-待处理 1-已处理 2-已忽略
    private Date createTime;           // 举报时间
    private Date handleTime;           // 处理时间
    // 扩展字段
    private String reporterName;       // 举报人用户名
    private String postTitle;          // 被举报帖子标题
    private String targetUsername;     // 被举报人用户名（发帖人/评论作者）
}
