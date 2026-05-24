package com.example.catforum.service;

import com.example.catforum.entity.Post;

import java.util.List;

/**
 * 收藏服务接口
 * 提供收藏相关功能
 */
public interface CollectService {

    /**
     * 切换收藏状态
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return true-已收藏，false-已取消收藏
     */
    boolean toggleCollect(Integer userId, Integer postId);

    /**
     * 检查用户是否收藏了帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return true-已收藏，false-未收藏
     */
    boolean isCollected(Integer userId, Integer postId);

    /**
     * 获取帖子收藏数
     * @param postId 帖子ID
     * @return 收藏数量
     */
    int getCollectCount(Integer postId);

    /**
     * 获取用户收藏的帖子列表
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 帖子列表
     */
    List<Post> getCollectedPosts(Integer userId, int page, int pageSize);
}