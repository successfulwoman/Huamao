package com.example.catforum.service.impl;

import com.example.catforum.entity.Like;
import com.example.catforum.entity.Post;
import com.example.catforum.mapper.LikeMapper;
import com.example.catforum.mapper.PostImageMapper;
import com.example.catforum.mapper.PostMapper;
import com.example.catforum.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 点赞服务实现类
 */
@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostImageMapper postImageMapper;

    /**
     * 切换点赞状态
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return true-已点赞，false-已取消点赞
     */
    @Override
    public boolean toggleLike(Integer userId, Integer postId) {
        Like existing = likeMapper.findByUserAndPost(userId, postId);
        if (existing != null) {
            likeMapper.delete(userId, postId);
            postMapper.decrementLikeNum(postId);
            return false;
        } else {
            Like like = new Like();
            like.setUserId(userId);
            like.setPostId(postId);
            likeMapper.insert(like);
            postMapper.incrementLikeNum(postId);
            return true;
        }
    }

    /**
     * 检查用户是否点赞了帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return true-已点赞，false-未点赞
     */
    @Override
    public boolean isLiked(Integer userId, Integer postId) {
        return likeMapper.findByUserAndPost(userId, postId) != null;
    }

    /**
     * 获取帖子点赞数
     * @param postId 帖子ID
     * @return 点赞数量
     */
    @Override
    public int getLikeCount(Integer postId) {
        return likeMapper.countByPost(postId);
    }

    /**
     * 获取用户点赞的帖子列表
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 帖子列表
     */
    @Override
    public List<Post> getLikedPosts(Integer userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Post> posts = likeMapper.findPostsByUser(userId, offset, pageSize);
        for (Post post : posts) {
            String[] imageUrls = postImageMapper.findByPostId(post.getId());
            post.setImageUrls(imageUrls);
            post.setLikeCount(post.getLikeNum());
            post.setCommentCount(post.getCommentNum());
        }
        return posts;
    }
}