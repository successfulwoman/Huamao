package com.example.catforum.service;

import com.example.catforum.entity.User;

import java.util.List;

/**
 * 用户服务接口
 * 提供用户注册、登录功能及管理员管理功能
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

    User getInfo(Integer id);

    int update(User user);

    /**
     * 管理员登录
     * @param username 用户名
     * @param password 密码
     * @return 用户实体（登录成功）或null（登录失败）
     */
    User adminLogin(String username, String password);

    /**
     * 获取所有用户列表（管理员管理）
     * @return 用户列表
     */
    List<User> getAllUsers();

    /**
     * 更新用户状态（管理员禁用/启用用户）
     * @param id 用户ID
     * @param status 状态
     * @return 影响的行数
     */
    int updateUserStatus(Integer id, Integer status);

    /**
     * 更新用户角色（管理员设置权限）
     * @param id 用户ID
     * @param role 角色
     * @return 影响的行数
     */
    int updateUserRole(Integer id, Integer role);

    /**
     * 删除用户（管理员删除）
     * @param id 用户ID
     * @return 影响的行数
     */
    int deleteUser(Integer id);

    /**
     * 统计用户总数
     * @return 用户数量
     */
    int countUsers();
}