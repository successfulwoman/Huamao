package com.example.catforum.service;

import com.example.catforum.entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 * 提供评论相关功能
 */
public interface CommentService {

    /**
     * 添加评论
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param content 评论内容
     * @return 评论对象（成功）或 null（失败）
     */
    Comment addComment(Integer postId, Integer userId, String content);

    /**
     * 获取帖子评论列表（带分页）
     * @param postId 帖子ID
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 评论列表
     */
    List<Comment> getCommentList(Integer postId, int page, int pageSize);

    /**
     * 获取帖子评论数
     * @param postId 帖子ID
     * @return 评论数量
     */
    int getCommentCount(Integer postId);

    /**
     * 获取所有评论（管理员用）
     * @return 评论列表
     */
    List<Comment> getAllComments();

    /**
     * 删除评论
     * @param id 评论ID
     * @return 是否删除成功
     */
    boolean deleteComment(Integer id);

    /**
     * 统计所有评论数
     * @return 评论总数
     */
    int getAllCommentCount();
}
