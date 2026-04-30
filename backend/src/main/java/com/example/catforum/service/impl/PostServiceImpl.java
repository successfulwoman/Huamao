package com.example.catforum.service.impl;

import com.example.catforum.entity.Post;
import com.example.catforum.entity.PostImage;
import com.example.catforum.mapper.PostImageMapper;
import com.example.catforum.mapper.PostMapper;
import com.example.catforum.service.PostService;
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

    /**
     * 发布帖子
     * 使用事务确保帖子和图片同时插入
     * @param post 帖子实体
     * @param imageUrls 图片URL数组
     * @return 帖子ID（成功）或 -1（失败）
     */
    @Override
    @Transactional
    public int publish(Post post, String[] imageUrls) {
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
            // 默认值（收藏功能未实现）
            post.setCollectCount(0);
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
            // 默认值（收藏功能未实现）
            post.setCollectCount(0);
            post.setLiked(false);
            post.setCollected(false);
        }
        return posts;
    }
}