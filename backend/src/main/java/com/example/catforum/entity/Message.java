package com.example.catforum.entity;

import lombok.Data;

import java.util.Date;

/**
 * 消息通知实体类
 * 对应数据库中的message表
 */
@Data
public class Message {
    private Integer id;         // 消息ID
    private Integer userId;     // 接收用户ID
    private Integer senderId;   // 发送者ID（评论通知时为评论人ID，公告通知时为0）
    private Integer postId;     // 关联帖子ID（评论通知时使用）
    private Integer noticeId;   // 关联公告ID（公告通知时使用）
    private String type;        // 消息类型：comment(评论), notice(公告)
    private String content;     // 消息内容
    private String senderName;  // 发送者名称
    private String postTitle;   // 帖子标题（评论通知时使用）
    private Integer isRead;     // 是否已读：0-未读，1-已读
    private Date createTime;    // 创建时间
}