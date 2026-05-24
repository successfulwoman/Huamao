package com.example.catforum.controller;

import com.example.catforum.service.CommentService;
import com.example.catforum.service.PostService;
import com.example.catforum.service.UserService;
import com.example.catforum.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计数据控制器
 * 提供论坛统计数据接口
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    /**
     * 获取论坛统计数据
     * @return 包含用户数、帖子数、评论数的统计信息
     */
    @GetMapping("/overview")
    public Result getOverviewStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("userCount", userService.countUsers());
        stats.put("postCount", postService.countPosts());
        stats.put("commentCount", commentService.getAllCommentCount());
        return Result.success(stats);
    }
}