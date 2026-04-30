package com.example.catforum.entity;

import lombok.Data;

/**
 * 帖子分类实体类
 * 对应数据库中的category表
 */
@Data
public class Category {
    private Integer id;       // 分类ID
    private String name;     // 分类名称
}