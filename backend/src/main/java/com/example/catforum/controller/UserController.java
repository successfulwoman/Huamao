package com.example.catforum.controller;

import com.example.catforum.entity.User;
import com.example.catforum.service.UserService;
import com.example.catforum.util.Result;
import com.example.catforum.util.UploadOss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 用户控制器
 * 处理用户登录相关请求
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * @param user 用户对象（包含username和password）
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        int result = userService.register(user);
        if (result == -1) {
            return Result.error("用户名已存在");
        } else if (result > 0) {
            return Result.success("注册成功");
        }
        return Result.error("注册失败");
    }

    /**
     * 用户登录
     * @param user 用户对象（包含username和password）
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        User result = userService.login(user.getUsername(), user.getPassword());
        if (result != null) {
            return Result.success("登录成功", result);
        }
        return Result.error("用户名或密码错误");
    }

    /**
     * 上传用户头像至OSS并更新用户信息
     * 将头像存储到OSS的avatar路径下，并添加Content-Disposition为inline以支持直接预览
     * @param file 头像文件
     * @param userId 用户ID
     * @return 头像URL
     */
    @PostMapping("/uploadoss")
    public Result uploadoss(@RequestParam("file") MultipartFile file, @RequestParam("userId") Integer userId) throws IOException {
        // 使用AVATAR_PATH路径上传头像，确保与帖子图片分开存储
        String avatarUrl = UploadOss.upload(file, UploadOss.AVATAR_PATH);
        if (avatarUrl != null) {
            userService.updateAvatar(userId, avatarUrl);
            return Result.success("头像上传成功", avatarUrl);
        }
        return Result.error("头像上传失败");
    }
}