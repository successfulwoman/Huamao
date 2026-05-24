package com.example.catforum.service.impl;

import com.example.catforum.entity.Comment;
import com.example.catforum.entity.Post;
import com.example.catforum.entity.User;
import com.example.catforum.mapper.CommentMapper;
import com.example.catforum.mapper.PostMapper;
import com.example.catforum.mapper.UserMapper;
import com.example.catforum.service.CommentService;
import com.example.catforum.service.MessageService;
import com.example.catforum.service.SensitiveWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    /**
     * 添加评论
     * 自动过滤敏感词
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param content 评论内容
     * @return 评论对象（成功）或 null（失败）
     */
    @Override
    @Transactional
    public Comment addComment(Integer postId, Integer userId, String content) {
        // 过滤评论内容中的敏感词
        String filteredContent = sensitiveWordService.filterContent(content);

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(filteredContent);
        int result = commentMapper.insert(comment);
        if (result > 0) {
            postMapper.incrementCommentNum(postId);

            // 获取帖子信息和评论人信息，发送通知
            Post post = postMapper.findById(postId);
            User sender = userMapper.findById(userId);

            if (post != null && sender != null && post.getUserId() != null) {
                messageService.sendCommentNotification(
                    postId,
                    post.getTitle(),
                    post.getUserId(),
                    userId,
                    sender.getUsername(),
                    filteredContent
                );
            }

            // 查询评论详情（包括用户昵称和头像）
            List<Comment> comments = commentMapper.findByPost(postId, 0, 1);
            if (!comments.isEmpty()) {
                return comments.get(0);
            }
            return comment;
        }
        return null;
    }

    /**
     * 获取帖子评论列表（带分页）
     * @param postId 帖子ID
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 评论列表
     */
    @Override
    public List<Comment> getCommentList(Integer postId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return commentMapper.findByPost(postId, offset, pageSize);
    }

    /**
     * 获取帖子评论数
     * @param postId 帖子ID
     * @return 评论数量
     */
    @Override
    public int getCommentCount(Integer postId) {
        return commentMapper.countByPost(postId);
    }

    /**
     * 获取所有评论（管理员用）
     * @return 评论列表
     */
    @Override
    public List<Comment> getAllComments() {
        return commentMapper.findAll();
    }

    /**
     * 删除评论
     * @param id 评论ID
     * @return 是否删除成功
     */
    @Override
    @Transactional
    public boolean deleteComment(Integer id) {
        Comment comment = commentMapper.findById(id);
        if (comment != null) {
            int result = commentMapper.deleteById(id);
            if (result > 0) {
                // 更新帖子评论数
                postMapper.decrementCommentNum(comment.getPostId());
                return true;
            }
        }
        return false;
    }

    /**
     * 统计所有评论数
     * @return 评论总数
     */
    @Override
    public int getAllCommentCount() {
        return commentMapper.countAll();
    }
}
