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
    private String avatar;    // 头像URL
    private Date createTime;  // 创建时间
}
