package com.example.catforum.controller;

import com.example.catforum.entity.Post;
import com.example.catforum.service.CollectService;
import com.example.catforum.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收藏控制器
 * 处理收藏相关请求
 */
@RestController
@RequestMapping("/api/collect")
public class CollectController {

    @Autowired
    private CollectService collectService;

    /**
     * 切换收藏状态
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 收藏状态
     */
    @PostMapping("/toggle")
    public Result toggleCollect(@RequestParam Integer userId, @RequestParam Integer postId) {
        boolean collected = collectService.toggleCollect(userId, postId);
        Map<String, Object> result = new HashMap<>();
        result.put("collected", collected);
        result.put("count", collectService.getCollectCount(postId));
        return Result.success(result);
    }

    /**
     * 获取用户收藏的帖子列表
     * @param userId 用户ID
     * @param page 页码（默认第1页）
     * @return 帖子列表
     */
    @GetMapping("/my")
    public Result getMyCollects(@RequestParam Integer userId, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;
        List<Post> posts = collectService.getCollectedPosts(userId, page, pageSize);
        return Result.success(posts);
    }
}