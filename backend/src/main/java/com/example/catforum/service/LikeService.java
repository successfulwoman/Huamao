package com.example.catforum.service;

import com.example.catforum.entity.Post;

import java.util.List;

/**
 * 点赞服务接口
 * 提供点赞相关功能
 */
public interface LikeService {

    /**
     * 切换点赞状态
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return true-已点赞，false-已取消点赞
     */
    boolean toggleLike(Integer userId, Integer postId);

    /**
     * 检查用户是否点赞了帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return true-已点赞，false-未点赞
     */
    boolean isLiked(Integer userId, Integer postId);

    /**
     * 获取帖子点赞数
     * @param postId 帖子ID
     * @return 点赞数量
     */
    int getLikeCount(Integer postId);

    /**
     * 获取用户点赞的帖子列表
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 帖子列表
     */
    List<Post> getLikedPosts(Integer userId, int page, int pageSize);
}