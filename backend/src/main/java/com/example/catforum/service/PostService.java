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

    /**
     * 根据用户ID获取帖子列表（带分页）
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 帖子列表
     */
    List<Post> getPostListByUser(Integer userId, int page, int pageSize);

    /**
     * 根据帖子ID获取帖子详情
     * @param postId 帖子ID
     * @return 帖子详情
     */
    Post getPostById(Integer postId);

    // ==================== 管理员管理方法 ====================

    /**
     * 获取所有帖子（不分页，管理员管理用）
     * @return 帖子列表
     */
    List<Post> getAllPostsForAdmin();

    /**
     * 更新帖子状态（管理员隐藏/显示帖子）
     * @param id 帖子ID
     * @param status 状态
     * @return 影响的行数
     */
    int updatePostStatus(Integer id, Integer status);

    /**
     * 设置帖子置顶
     * @param id 帖子ID
     * @param isTop 是否置顶
     * @return 影响的行数
     */
    int updatePostTop(Integer id, Integer isTop);

    /**
     * 设置帖子精华
     * @param id 帖子ID
     * @param isBest 是否精华
     * @return 影响的行数
     */
    int updatePostBest(Integer id, Integer isBest);

    /**
     * 统计帖子总数
     * @return 帖子数量
     */
    int countPosts();

    /**
     * 查询隐藏的帖子
     * @return 帖子列表
     */
    List<Post> getHiddenPosts();

    /**
     * 查询置顶帖子
     * @return 帖子列表
     */
    List<Post> getTopPosts();

    /**
     * 删除帖子（管理员删除）
     * @param id 帖子ID
     * @return 影响的行数
     */
    int deletePost(Integer id);

    /**
     * 用户删除自己的帖子
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 删除结果：1成功，0帖子不存在，-1无权限
     */
    int deletePostByUser(Integer postId, Integer userId);

    /**
     * 搜索帖子（支持标题、内容、用户昵称模糊搜索）
     * @param keyword 搜索关键词
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 帖子列表
     */
    List<Post> searchPosts(String keyword, int page, int pageSize);
}