package com.example.catforum.controller;

import com.example.catforum.entity.Category;
import com.example.catforum.service.CategoryService;
import com.example.catforum.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 * 处理分类查询相关请求
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类列表
     * @return 分类列表
     */
    @GetMapping("/list")
    public Result getCategoryList() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }
}