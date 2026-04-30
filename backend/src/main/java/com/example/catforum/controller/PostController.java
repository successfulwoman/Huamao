package com.example.catforum.controller;

import com.example.catforum.entity.Post;
import com.example.catforum.service.PostService;
import com.example.catforum.util.Result;
import com.example.catforum.util.UploadOss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 帖子控制器
 * 处理帖子发布、图片上传和帖子列表查询相关请求
 */
@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 上传帖子图片
     * @param file 图片文件
     * @return 图片URL（存储在OSS的tagfileimage路径下）
     */
    @PostMapping("/upload")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 将图片上传到OSS的tagfileimage路径下
            String imageUrl = UploadOss.upload(file, UploadOss.POST_IMAGE_PATH);
            if (imageUrl != null) {
                return Result.success("图片上传成功", imageUrl);
            }
            return Result.error("图片上传失败");
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("图片上传异常：" + e.getMessage());
        }
    }

    /**
     * 发布帖子
     * @param post 帖子对象（包含userId, title, content, categoryId, imageUrls）
     * @return 发布结果
     */
    @PostMapping("/publish")
    public Result publish(@RequestBody Post post) {
        // 参数验证
        if (post.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            return Result.error("帖子标题不能为空");
        }
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            return Result.error("帖子内容不能为空");
        }
        if (post.getCategoryId() == null) {
            return Result.error("分类ID不能为空");
        }

        // 发布帖子（从post对象中获取图片URL列表）
        int postId = postService.publish(post, post.getImageUrls());
        if (postId > 0) {
            return Result.success("帖子发布成功", postId);
        }
        return Result.error("帖子发布失败");
    }

    /**
     * 获取帖子列表（带分页）
     * @param page 页码（默认第1页）
     * @return 帖子列表
     */
    @GetMapping("/list")
    public Result getPostList(@RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;
        List<Post> posts = postService.getPostList(page, pageSize);
        return Result.success(posts);
    }

    /**
     * 根据分类获取帖子列表（带分页）
     * @param categoryId 分类ID
     * @param page 页码（默认第1页）
     * @return 帖子列表
     */
    @GetMapping("/category")
    public Result getPostListByCategory(@RequestParam Integer categoryId, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;
        List<Post> posts = postService.getPostListByCategory(categoryId, page, pageSize);
        return Result.success(posts);
    }
}