package com.example.catforum.service;

import com.example.catforum.entity.User;

/**
 * 用户服务接口
 * 提供用户注册、登录功能
 */
public interface UserService {
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户实体（登录成功）或null（登录失败）
     */
    User login(String username, String password);

    int updateAvatar(Integer id, String avatar);

    int register(User user);
}