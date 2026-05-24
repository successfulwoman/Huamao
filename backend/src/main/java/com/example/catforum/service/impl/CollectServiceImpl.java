package com.example.catforum.service.impl;

import com.example.catforum.entity.Collect;
import com.example.catforum.entity.Post;
import com.example.catforum.mapper.CollectMapper;
import com.example.catforum.mapper.PostImageMapper;
import com.example.catforum.mapper.PostMapper;
import com.example.catforum.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收藏服务实现类
 */
@Service
public class CollectServiceImpl implements CollectService {

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostImageMapper postImageMapper;

    /**
     * 切换收藏状态
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return true-已收藏，false-已取消收藏
     */
    @Override
    public boolean toggleCollect(Integer userId, Integer postId) {
        Collect existing = collectMapper.findByUserAndPost(userId, postId);
        if (existing != null) {
            collectMapper.delete(userId, postId);
            postMapper.decrementCollectNum(postId);
            return false;
        } else {
            Collect collect = new Collect();
            collect.setUserId(userId);
            collect.setPostId(postId);
            collectMapper.insert(collect);
            postMapper.incrementCollectNum(postId);
            return true;
        }
    }

    /**
     * 检查用户是否收藏了帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return true-已收藏，false-未收藏
     */
    @Override
    public boolean isCollected(Integer userId, Integer postId) {
        return collectMapper.findByUserAndPost(userId, postId) != null;
    }

    /**
     * 获取帖子收藏数
     * @param postId 帖子ID
     * @return 收藏数量
     */
    @Override
    public int getCollectCount(Integer postId) {
        return collectMapper.countByPost(postId);
    }

    /**
     * 获取用户收藏的帖子列表
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 帖子列表
     */
    @Override
    public List<Post> getCollectedPosts(Integer userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Post> posts = collectMapper.findPostsByUser(userId, offset, pageSize);
        for (Post post : posts) {
            String[] imageUrls = postImageMapper.findByPostId(post.getId());
            post.setImageUrls(imageUrls);
            post.setLikeCount(post.getLikeNum());
            post.setCommentCount(post.getCommentNum());
        }
        return posts;
    }
}