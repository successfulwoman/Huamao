package com.example.catforum.service.impl;

import com.example.catforum.entity.Post;
import com.example.catforum.entity.PostImage;
import com.example.catforum.mapper.PostImageMapper;
import com.example.catforum.mapper.PostMapper;
import com.example.catforum.service.CollectService;
import com.example.catforum.service.PostService;
import com.example.catforum.service.SensitiveWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 帖子服务实现类
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostImageMapper postImageMapper;

    @Autowired
    private CollectService collectService;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    /**
     * 发布帖子
     * 使用事务确保帖子和图片同时插入
     * 自动过滤敏感词
     * @param post 帖子实体
     * @param imageUrls 图片URL数组
     * @return 帖子ID（成功）或 -1（失败）
     */
    @Override
    @Transactional
    public int publish(Post post, String[] imageUrls) {
        // 过滤标题和内容中的敏感词
        if (post.getTitle() != null) {
            post.setTitle(sensitiveWordService.filterContent(post.getTitle()));
        }
        if (post.getContent() != null) {
            post.setContent(sensitiveWordService.filterContent(post.getContent()));
        }

        // 1. 插入帖子基本信息
        int result = postMapper.insert(post);
        if (result <= 0) {
            return -1;
        }

        // 2. 插入帖子图片（如果有多张图片）
        if (imageUrls != null && imageUrls.length > 0) {
            for (String imageUrl : imageUrls) {
                PostImage postImage = new PostImage();
                postImage.setPostId(post.getId());
                postImage.setImageUrl(imageUrl);
                postImageMapper.insert(postImage);
            }
        }

        return post.getId();
    }

    /**
     * 获取帖子列表（带分页）
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 帖子列表（包含图片URL）
     */
    @Override
    public List<Post> getPostList(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Post> posts = postMapper.findAll(offset, pageSize);
        // 为每个帖子加载图片URL并设置前端需要的字段
        for (Post post : posts) {
            String[] imageUrls = postImageMapper.findByPostId(post.getId());
            post.setImageUrls(imageUrls);
            // 设置前端需要的字段别名（前端使用likeCount/commentCount）
            post.setLikeCount(post.getLikeNum());
            post.setCommentCount(post.getCommentNum());
            // 获取收藏数
            post.setCollectCount(collectService.getCollectCount(post.getId()));
            post.setLiked(false);
            post.setCollected(false);
        }
        return posts;
    }

    /**
     * 根据分类获取帖子列表（带分页）
     * @param categoryId 分类ID
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 帖子列表（包含图片URL）
     */
    @Override
    public List<Post> getPostListByCategory(Integer categoryId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Post> posts = postMapper.findByCategory(categoryId, offset, pageSize);
        // 为每个帖子加载图片URL并设置前端需要的字段
        for (Post post : posts) {
            String[] imageUrls = postImageMapper.findByPostId(post.getId());
            post.setImageUrls(imageUrls);
            // 设置前端需要的字段别名（前端使用likeCount/commentCount）
            post.setLikeCount(post.getLikeNum());
            post.setCommentCount(post.getCommentNum());
            // 获取收藏数
            post.setCollectCount(collectService.getCollectCount(post.getId()));
            post.setLiked(false);
            post.setCollected(false);
        }
        return posts;
    }

    /**
     * 根据用户ID获取帖子列表（带分页）
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 帖子列表（包含图片URL）
     */
    @Override
    public List<Post> getPostListByUser(Integer userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Post> posts = postMapper.findByUser(userId, offset, pageSize);
        // 为每个帖子加载图片URL并设置前端需要的字段
        for (Post post : posts) {
            String[] imageUrls = postImageMapper.findByPostId(post.getId());
            post.setImageUrls(imageUrls);
            // 设置前端需要的字段别名
            post.setLikeCount(post.getLikeNum());
            post.setCommentCount(post.getCommentNum());
            // 获取收藏数
            post.setCollectCount(collectService.getCollectCount(post.getId()));
        }
        return posts;
    }

    /**
     * 根据帖子ID获取帖子详情
     * @param postId 帖子ID
     * @return 帖子详情（包含图片URL）
     */
    @Override
    public Post getPostById(Integer postId) {
        Post post = postMapper.findById(postId);
        if (post != null) {
            // 加载图片URL
            String[] imageUrls = postImageMapper.findByPostId(postId);
            post.setImageUrls(imageUrls);
            // 设置前端需要的字段别名
            post.setLikeCount(post.getLikeNum());
            post.setCommentCount(post.getCommentNum());
            // 获取收藏数
            post.setCollectCount(collectService.getCollectCount(post.getId()));
        }
        return post;
    }

    /**
     * 获取所有帖子（不分页，管理员管理用）
     * @return 帖子列表
     */
    @Override
    public List<Post> getAllPostsForAdmin() {
        List<Post> posts = postMapper.findAllForAdmin();
        for (Post post : posts) {
            String[] imageUrls = postImageMapper.findByPostId(post.getId());
            post.setImageUrls(imageUrls);
            post.setLikeCount(post.getLikeNum());
            post.setCommentCount(post.getCommentNum());
        }
        return posts;
    }

    /**
     * 更新帖子状态（管理员隐藏/显示帖子）
     * @param id 帖子ID
     * @param status 状态
     * @return 影响的行数
     */
    @Override
    public int updatePostStatus(Integer id, Integer status) {
        return postMapper.updateStatus(id, status);
    }

    /**
     * 设置帖子置顶
     * @param id 帖子ID
     * @param isTop 是否置顶
     * @return 影响的行数
     */
    @Override
    public int updatePostTop(Integer id, Integer isTop) {
        return postMapper.updateTop(id, isTop);
    }

    /**
     * 设置帖子精华
     * @param id 帖子ID
     * @param isBest 是否精华
     * @return 影响的行数
     */
    @Override
    public int updatePostBest(Integer id, Integer isBest) {
        return postMapper.updateBest(id, isBest);
    }

    /**
     * 删除帖子（管理员用）
     * @param id 帖子ID
     * @return 影响的行数
     */
    @Override
    public int deletePost(Integer id) {
        // 删除帖子相关的图片记录
        postImageMapper.deleteByPostId(id);
        // 删除帖子
        return postMapper.deleteById(id);
    }

    /**
     * 用户删除自己的帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 影响的行数
     */
    @Override
    public int deletePostByUser(Integer userId, Integer postId) {
        Post post = postMapper.findById(postId);
        if (post != null && userId.equals(post.getUserId())) {
            postImageMapper.deleteByPostId(postId);
            return postMapper.deleteById(postId);
        }
        return 0;
    }

    /**
     * 增加帖子点赞数
     * @param postId 帖子ID
     * @return 是否成功
     */
    public boolean incrementLikeNum(Integer postId) {
        return postMapper.incrementLikeNum(postId) > 0;
    }

    /**
     * 减少帖子点赞数
     * @param postId 帖子ID
     * @return 是否成功
     */
    public boolean decrementLikeNum(Integer postId) {
        return postMapper.decrementLikeNum(postId) > 0;
    }

    /**
     * 增加帖子评论数
     * @param postId 帖子ID
     * @return 是否成功
     */
    public boolean incrementCommentNum(Integer postId) {
        return postMapper.incrementCommentNum(postId) > 0;
    }

    /**
     * 减少帖子评论数
     * @param postId 帖子ID
     * @return 是否成功
     */
    public boolean decrementCommentNum(Integer postId) {
        return postMapper.decrementCommentNum(postId) > 0;
    }

    /**
     * 统计帖子总数
     * @return 帖子数量
     */
    @Override
    public int countPosts() {
        return postMapper.count();
    }

    /**
     * 查询隐藏的帖子
     * @return 帖子列表
     */
    @Override
    public List<Post> getHiddenPosts() {
        List<Post> posts = postMapper.findHiddenPosts();
        for (Post post : posts) {
            String[] imageUrls = postImageMapper.findByPostId(post.getId());
            post.setImageUrls(imageUrls);
            post.setLikeCount(post.getLikeNum());
            post.setCommentCount(post.getCommentNum());
        }
        return posts;
    }

    /**
     * 查询置顶帖子
     * @return 帖子列表
     */
    @Override
    public List<Post> getTopPosts() {
        List<Post> posts = postMapper.findTopPosts();
        for (Post post : posts) {
            String[] imageUrls = postImageMapper.findByPostId(post.getId());
            post.setImageUrls(imageUrls);
            post.setLikeCount(post.getLikeNum());
            post.setCommentCount(post.getCommentNum());
            post.setCollectCount(collectService.getCollectCount(post.getId()));
            post.setLiked(false);
            post.setCollected(false);
        }
        return posts;
    }

    /**
     * 搜索帖子（支持标题、内容、用户昵称模糊搜索）
     * @param keyword 搜索关键词
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 帖子列表
     */
    @Override
    public List<Post> searchPosts(String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Post> posts = postMapper.searchPosts(keyword, offset, pageSize);
        for (Post post : posts) {
            String[] imageUrls = postImageMapper.findByPostId(post.getId());
            post.setImageUrls(imageUrls);
            post.setLikeCount(post.getLikeNum());
            post.setCommentCount(post.getCommentNum());
            post.setCollectCount(collectService.getCollectCount(post.getId()));
            post.setLiked(false);
            post.setCollected(false);
        }
        return posts;
    }
}
