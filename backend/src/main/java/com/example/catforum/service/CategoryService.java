package com.example.catforum.service;

import com.example.catforum.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 * 提供分类查询功能
 */
public interface CategoryService {

    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<Category> getAllCategories();
}