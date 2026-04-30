package com.example.catforum.service;

import com.example.catforum.entity.Post;

import java.util.List;

/**
 * 帖子服务接口
 * 提供帖子发布和查询功能
 */
public interface PostService {

    /**
     * 发布帖子
     * @param post 帖子实体（包含title, content, userId, categoryId）
     * @param imageUrls 图片URL数组
     * @return 帖子ID（成功） 或 -1（失败）
     */
    int publish(Post post, String[] imageUrls);

    /**
     * 获取帖子列表（带分页）
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 帖子列表
     */
    List<Post> getPostList(int page, int pageSize);

    /**
     * 根据分类获取帖子列表（带分页）
     * @param categoryId 分类ID
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 帖子列表
     */
    List<Post> getPostListByCategory(Integer categoryId, int page, int pageSize);
}