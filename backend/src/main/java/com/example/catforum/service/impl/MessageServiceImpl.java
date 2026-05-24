package com.example.catforum.service.impl;

import com.example.catforum.entity.Message;
import com.example.catforum.entity.User;
import com.example.catforum.mapper.MessageMapper;
import com.example.catforum.mapper.UserMapper;
import com.example.catforum.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 消息通知服务实现类
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void sendCommentNotification(Integer postId, String postTitle, Integer receiverId, Integer senderId, String senderName, String commentContent) {
        // 如果评论人和作者是同一个人，不需要发送通知
        if (senderId.equals(receiverId)) {
            return;
        }

        Message message = new Message();
        message.setUserId(receiverId);
        message.setSenderId(senderId);
        message.setPostId(postId);
        message.setType("comment");
        message.setContent("评论了你的帖子：" + commentContent);
        message.setSenderName(senderName);
        message.setPostTitle(postTitle);
        
        messageMapper.insert(message);
    }

    @Override
    @Transactional
    public void sendNoticeNotification(Integer noticeId, String noticeContent) {
        // 获取所有用户
        List<User> users = userMapper.findAll();
        
        for (User user : users) {
            Message message = new Message();
            message.setUserId(user.getId());
            message.setSenderId(0);
            message.setNoticeId(noticeId);
            message.setType("notice");
            message.setContent(noticeContent);
            message.setSenderName("系统通知");
            message.setPostTitle(null);
            
            messageMapper.insert(message);
        }
    }

    @Override
    public List<Message> getUserMessages(Integer userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return messageMapper.findByUser(userId, offset, pageSize);
    }

    @Override
    public int getUnreadCount(Integer userId) {
        return messageMapper.countUnread(userId);
    }

    @Override
    public boolean markAsRead(Integer messageId) {
        return messageMapper.markRead(messageId) > 0;
    }
}