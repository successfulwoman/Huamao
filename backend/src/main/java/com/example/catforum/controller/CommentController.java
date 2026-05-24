package com.example.catforum.controller;

import com.example.catforum.entity.Comment;
import com.example.catforum.service.CommentService;
import com.example.catforum.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 * 处理评论相关请求
 */
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 添加评论
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param content 评论内容
     * @return 评论对象
     */
    @PostMapping("/add")
    public Result addComment(@RequestParam Integer postId, @RequestParam Integer userId, @RequestParam String content) {
        if (content == null || content.trim().isEmpty()) {
            return Result.error("评论内容不能为空");
        }
        Comment comment = commentService.addComment(postId, userId, content);
        if (comment != null) {
            return Result.success("评论成功", comment);
        }
        return Result.error("评论失败");
    }

    /**
     * 获取帖子评论列表
     * @param postId 帖子ID
     * @param page 页码（默认第1页）
     * @return 评论列表
     */
    @GetMapping("/list")
    public Result getCommentList(@RequestParam Integer postId, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;
        List<Comment> comments = commentService.getCommentList(postId, page, pageSize);
        return Result.success(comments);
    }

    /**
     * 获取所有评论（管理员用）
     * @return 评论列表
     */
    @GetMapping("/admin/list")
    public Result getAllComments() {
        List<Comment> comments = commentService.getAllComments();
        return Result.success(comments);
    }

    /**
     * 删除评论（管理员用）
     * @param id 评论ID
     * @return 操作结果
     */
    @DeleteMapping("/admin/{id}")
    public Result deleteComment(@PathVariable Integer id) {
        boolean success = commentService.deleteComment(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 统计所有评论数（管理员用）
     * @return 评论总数
     */
    @GetMapping("/admin/count")
    public Result countAllComments() {
        int count = commentService.getAllCommentCount();
        return Result.success(count);
    }
}
