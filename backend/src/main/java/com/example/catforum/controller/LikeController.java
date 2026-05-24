package com.example.catforum.controller;

import com.example.catforum.entity.Post;
import com.example.catforum.service.LikeService;
import com.example.catforum.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 点赞控制器
 * 处理点赞相关请求
 */
@RestController
@RequestMapping("/api/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * 切换点赞状态
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 点赞状态
     */
    @PostMapping("/toggle")
    public Result toggleLike(@RequestParam Integer userId, @RequestParam Integer postId) {
        boolean liked = likeService.toggleLike(userId, postId);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("count", likeService.getLikeCount(postId));
        return Result.success(result);
    }

    /**
     * 获取帖子点赞数
     * @param postId 帖子ID
     * @return 点赞数量
     */
    @GetMapping("/count")
    public Result getLikeCount(@RequestParam Integer postId) {
        int count = likeService.getLikeCount(postId);
        return Result.success(count);
    }

    /**
     * 获取用户点赞的帖子列表
     * @param userId 用户ID
     * @param page 页码（默认第1页）
     * @return 帖子列表
     */
    @GetMapping("/my")
    public Result getMyLikes(@RequestParam Integer userId, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;
        List<Post> posts = likeService.getLikedPosts(userId, page, pageSize);
        return Result.success(posts);
    }
}