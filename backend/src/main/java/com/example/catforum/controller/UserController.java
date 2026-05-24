package com.example.catforum.controller;

import com.example.catforum.entity.User;
import com.example.catforum.service.UserService;
import com.example.catforum.util.Result;
import com.example.catforum.util.UploadOss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 用户控制器
 * 处理用户登录相关请求及管理员管理功能
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
     * 管理员登录
     * @param user 用户对象（包含username和password）
     * @return 登录结果
     */
    @PostMapping("/admin/login")
    public Result adminLogin(@RequestBody User user) {
        User result = userService.adminLogin(user.getUsername(), user.getPassword());
        if (result != null) {
            return Result.success("登录成功", result);
        }
        return Result.error("管理员账号或密码错误");
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

    @GetMapping("/info")
    public Result getInfo(@RequestParam Integer userId) {
        User user = userService.getInfo(userId);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    @PostMapping("/update")
    public Result update(@RequestBody User user) {
        if (user.getId() == null) {
            return Result.error("用户ID不能为空");
        }
        int result = userService.update(user);
        if (result > 0) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    // ==================== 管理员管理接口 ====================

    /**
     * 获取所有用户列表（管理员管理）
     * @return 用户列表
     */
    @GetMapping("/admin/list")
    public Result getAllUsers() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }

    /**
     * 更新用户状态（管理员禁用/启用用户）
     * @param id 用户ID
     * @param status 状态
     * @return 操作结果
     */
    @PostMapping("/admin/status")
    public Result updateUserStatus(@RequestParam Integer id, @RequestParam Integer status) {
        int result = userService.updateUserStatus(id, status);
        if (result > 0) {
            return Result.success(status == 1 ? "用户已启用" : "用户已禁用");
        }
        return Result.error("操作失败");
    }

    /**
     * 更新用户角色（管理员设置权限）
     * @param id 用户ID
     * @param role 角色
     * @return 操作结果
     */
    @PostMapping("/admin/role")
    public Result updateUserRole(@RequestParam Integer id, @RequestParam Integer role) {
        int result = userService.updateUserRole(id, role);
        if (result > 0) {
            return Result.success(role == 1 ? "已设为管理员" : "已设为普通用户");
        }
        return Result.error("操作失败");
    }

    /**
     * 删除用户（管理员删除）
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/admin/{id}")
    public Result deleteUser(@PathVariable Integer id) {
        int result = userService.deleteUser(id);
        if (result > 0) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 统计用户总数
     * @return 用户数量
     */
    @GetMapping("/admin/count")
    public Result countUsers() {
        int count = userService.countUsers();
        return Result.success(count);
    }
}