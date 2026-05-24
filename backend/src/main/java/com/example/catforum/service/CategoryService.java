package com.example.catforum.service;

import com.example.catforum.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 * 提供分类查询和自定义标签添加功能
 */
public interface CategoryService {

    /**
     * 获取所有分类（包括系统分类和用户自定义标签）
     * @return 分类列表
     */
    List<Category> getAllCategories();

    /**
     * 获取指定用户的所有分类（系统分类 + 用户自定义标签）
     * @param userId 用户ID
     * @return 分类列表
     */
    List<Category> getCategoriesByUserId(Integer userId);

    /**
     * 添加自定义标签
     * @param name 标签名称（前端已添加#前缀）
     * @param userId 用户ID
     * @return 添加后的分类实体
     */
    Category addCustomTag(String name, Integer userId);

    /**
     * 获取所有分类（包含用户名，管理员用）
     * @return 分类列表（含用户名）
     */
    List<Category> getAllCategoriesWithUser();

    /**
     * 添加分类/标签（管理员用）
     * @param category 分类实体
     * @return 添加后的分类实体
     */
    Category addCategory(Category category);

    /**
     * 更新分类/标签（管理员用）
     * @param category 分类实体
     * @return 是否更新成功
     */
    boolean updateCategory(Category category);

    /**
     * 删除分类（管理员用）
     * @param id 分类ID
     * @return 是否删除成功
     */
    boolean deleteCategory(Integer id);
}