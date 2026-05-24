package com.example.catforum.service;

import com.example.catforum.entity.Message;

import java.util.List;

/**
 * 消息通知服务接口
 * 提供消息通知相关功能
 */
public interface MessageService {

    /**
     * 发送评论通知
     * @param postId 帖子ID
     * @param postTitle 帖子标题
     * @param receiverId 接收用户ID（帖子作者）
     * @param senderId 发送者ID（评论人）
     * @param senderName 发送者名称
     * @param commentContent 评论内容
     */
    void sendCommentNotification(Integer postId, String postTitle, Integer receiverId, Integer senderId, String senderName, String commentContent);

    /**
     * 发送公告通知给所有用户
     * @param noticeId 公告ID
     * @param noticeContent 公告内容
     */
    void sendNoticeNotification(Integer noticeId, String noticeContent);

    /**
     * 获取用户消息列表（带分页）
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 消息列表
     */
    List<Message> getUserMessages(Integer userId, int page, int pageSize);

    /**
     * 获取用户未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int getUnreadCount(Integer userId);

    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @return 是否成功
     */
    boolean markAsRead(Integer messageId);
}