package com.example.catforum.entity;

import lombok.Data;

/**
 * 帖子分类实体类
 * 对应数据库中的category表
 */
@Data
public class Category {
    private Integer id;       // 分类ID
    private String name;     // 分类名称（自定义标签以#开头）
    private Integer isCustom; // 是否为自定义标签：0-系统分类，1-用户自定义
    private Integer userId;   // 所属用户ID（系统分类为NULL）
    private String username;  // 所属用户名称（用于管理员后台展示）
}