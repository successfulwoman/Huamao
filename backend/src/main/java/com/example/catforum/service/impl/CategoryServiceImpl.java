package com.example.catforum.service.impl;

import com.example.catforum.entity.Category;
import com.example.catforum.entity.User;
import com.example.catforum.mapper.CategoryMapper;
import com.example.catforum.mapper.UserMapper;
import com.example.catforum.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取所有分类（包括系统分类和用户自定义标签）
     * @return 分类列表
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.findAll();
    }

    /**
     * 获取指定用户的所有分类（系统分类 + 用户自定义标签）
     * @param userId 用户ID
     * @return 分类列表
     */
    @Override
    public List<Category> getCategoriesByUserId(Integer userId) {
        List<Category> categories = new ArrayList<>();
        categories.addAll(categoryMapper.findSystemCategories());
        if (userId != null) {
            categories.addAll(categoryMapper.findCustomByUserId(userId));
        }
        return categories;
    }

    /**
     * 添加自定义标签
     * @param name 标签名称（前端已添加#前缀）
     * @param userId 用户ID
     * @return 添加后的分类实体
     */
    @Override
    public Category addCustomTag(String name, Integer userId) {
        Category category = new Category();
        category.setName(name);
        category.setIsCustom(1);
        category.setUserId(userId);
        categoryMapper.insert(category);
        return category;
    }

    /**
     * 获取所有分类（包含用户名，管理员用）
     * @return 分类列表（含用户名）
     */
    @Override
    public List<Category> getAllCategoriesWithUser() {
        return categoryMapper.findAllWithUser();
    }

    /**
     * 添加分类/标签（管理员用）
     * @param category 分类实体
     * @return 添加后的分类实体
     */
    @Override
    public Category addCategory(Category category) {
        // 如果是用户自定义标签且提供了用户名，根据用户名查询用户ID
        if (category.getIsCustom() != null && category.getIsCustom() == 1 && 
            category.getUserId() == null && category.getUsername() != null && !category.getUsername().isEmpty()) {
            User user = userMapper.findByUsername(category.getUsername());
            if (user != null) {
                category.setUserId(user.getId());
            }
        }
        categoryMapper.insert(category);
        return category;
    }

    /**
     * 更新分类/标签（管理员用）
     * @param category 分类实体
     * @return 是否更新成功
     */
    @Override
    public boolean updateCategory(Category category) {
        // 如果是用户自定义标签且提供了用户名，根据用户名查询用户ID
        if (category.getIsCustom() != null && category.getIsCustom() == 1 && 
            category.getUserId() == null && category.getUsername() != null && !category.getUsername().isEmpty()) {
            User user = userMapper.findByUsername(category.getUsername());
            if (user != null) {
                category.setUserId(user.getId());
            }
        }
        return categoryMapper.update(category) > 0;
    }

    /**
     * 删除分类（管理员用）
     * @param id 分类ID
     * @return 是否删除成功
     */
    @Override
    @Transactional
    public boolean deleteCategory(Integer id) {
        return categoryMapper.deleteById(id) > 0;
    }
}