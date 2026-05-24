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

    /**
     * 根据用户ID获取帖子列表（带分页）
     * @param userId 用户ID
     * @param page 页码（默认第1页）
     * @return 帖子列表
     */
    @GetMapping("/user")
    public Result getPostListByUser(@RequestParam Integer userId, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;
        List<Post> posts = postService.getPostListByUser(userId, page, pageSize);
        return Result.success(posts);
    }

    /**
     * 获取帖子详情
     * @param postId 帖子ID
     * @return 帖子详情
     */
    @GetMapping("/detail")
    public Result getPostDetail(@RequestParam Integer postId) {
        Post post = postService.getPostById(postId);
        if (post != null) {
            return Result.success(post);
        }
        return Result.error("帖子不存在");
    }

    // ==================== 管理员管理接口 ====================

    /**
     * 获取所有帖子（不分页，管理员管理用）
     * @return 帖子列表
     */
    @GetMapping("/admin/list")
    public Result getAllPostsForAdmin() {
        List<Post> posts = postService.getAllPostsForAdmin();
        return Result.success(posts);
    }

    /**
     * 更新帖子状态（管理员隐藏/显示帖子）
     * @param id 帖子ID
     * @param status 状态
     * @return 操作结果
     */
    @PostMapping("/admin/status")
    public Result updatePostStatus(@RequestParam Integer id, @RequestParam Integer status) {
        int result = postService.updatePostStatus(id, status);
        if (result > 0) {
            return Result.success(status == 1 ? "帖子已显示" : "帖子已隐藏");
        }
        return Result.error("操作失败");
    }

    /**
     * 设置帖子置顶
     * @param id 帖子ID
     * @param isTop 是否置顶
     * @return 操作结果
     */
    @PostMapping("/admin/top")
    public Result updatePostTop(@RequestParam Integer id, @RequestParam Integer isTop) {
        int result = postService.updatePostTop(id, isTop);
        if (result > 0) {
            return Result.success(isTop == 1 ? "帖子已置顶" : "帖子已取消置顶");
        }
        return Result.error("操作失败");
    }

    /**
     * 设置帖子精华
     * @param id 帖子ID
     * @param isBest 是否精华
     * @return 操作结果
     */
    @PostMapping("/admin/best")
    public Result updatePostBest(@RequestParam Integer id, @RequestParam Integer isBest) {
        int result = postService.updatePostBest(id, isBest);
        if (result > 0) {
            return Result.success(isBest == 1 ? "帖子已设为精华" : "帖子已取消精华");
        }
        return Result.error("操作失败");
    }

    /**
     * 统计帖子总数
     * @return 帖子数量
     */
    @GetMapping("/admin/count")
    public Result countPosts() {
        int count = postService.countPosts();
        return Result.success(count);
    }

    /**
     * 查询隐藏的帖子
     * @return 帖子列表
     */
    @GetMapping("/admin/hidden")
    public Result getHiddenPosts() {
        List<Post> posts = postService.getHiddenPosts();
        return Result.success(posts);
    }

    /**
     * 查询置顶帖子
     * @return 帖子列表
     */
    @GetMapping("/admin/top")
    public Result getTopPosts() {
        List<Post> posts = postService.getTopPosts();
        return Result.success(posts);
    }

    /**
     * 用户删除自己的帖子
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public Result deletePostByUser(@RequestParam Integer postId, @RequestParam Integer userId) {
        int result = postService.deletePostByUser(postId, userId);
        if (result == 1) {
            return Result.success("帖子删除成功");
        } else if (result == 0) {
            return Result.error("帖子不存在");
        } else {
            return Result.error("无权限删除此帖子");
        }
    }

    /**
     * 删除帖子（管理员删除）
     * @param id 帖子ID
     * @return 操作结果
     */
    @DeleteMapping("/admin/{id}")
    public Result deletePost(@PathVariable Integer id) {
        int result = postService.deletePost(id);
        if (result > 0) {
            return Result.success("帖子删除成功");
        }
        return Result.error("帖子删除失败");
    }

    /**
     * 搜索帖子（支持标题、内容、用户昵称模糊搜索）
     * @param keyword 搜索关键词
     * @param page 页码（默认第1页）
     * @return 帖子列表
     */
    @GetMapping("/search")
    public Result searchPosts(@RequestParam String keyword, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;
        List<Post> posts = postService.searchPosts(keyword, page, pageSize);
        return Result.success(posts);
    }
}