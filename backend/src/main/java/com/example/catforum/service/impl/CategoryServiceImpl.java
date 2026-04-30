package com.example.catforum.service.impl;

import com.example.catforum.entity.Category;
import com.example.catforum.mapper.CategoryMapper;
import com.example.catforum.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 获取所有分类
     * @return 分类列表
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.findAll();
    }
}