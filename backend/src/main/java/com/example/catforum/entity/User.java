package com.example.catforum.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户实体类
 * 对应数据库中的user表
 */
@Data
public class User {
    private Integer id;       // 用户ID
    private String username;  // 用户名（登录使用）
    private String password;  // 密码
    private String useremail; // 邮箱
    private String avatar;    // 头像URL
    private String profile;   // 个人简介
    private String gender;    // 性别
    private Date createTime;  // 创建时间
    private Integer role;     // 用户角色：0-普通用户 1-管理员
    private Integer status;   // 用户状态：1-正常 0-禁用
}
