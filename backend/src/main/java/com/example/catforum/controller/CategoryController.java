package com.example.catforum.controller;

import com.example.catforum.entity.Category;
import com.example.catforum.service.CategoryService;
import com.example.catforum.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 分类控制器
 * 处理分类查询和自定义标签相关请求
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类列表（包括系统分类和所有用户自定义标签）
     * @return 分类列表
     */
    @GetMapping("/list")
    public Result getCategoryList() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 获取指定用户的分类列表（系统分类 + 用户自定义标签）
     * @param userId 用户ID
     * @return 分类列表
     */
    @GetMapping("/user/{userId}")
    public Result getCategoriesByUserId(@PathVariable Integer userId) {
        List<Category> categories = categoryService.getCategoriesByUserId(userId);
        return Result.success(categories);
    }

    /**
     * 添加自定义标签
     * @param requestBody 包含tagName（标签名称）和userId（用户ID）
     * @return 添加后的标签信息
     */
    @PostMapping("/custom")
    public Result addCustomTag(@RequestBody Map<String, Object> requestBody) {
        String tagName = (String) requestBody.get("tagName");
        Integer userId = (Integer) requestBody.get("userId");
        
        if (tagName == null || tagName.trim().isEmpty()) {
            return Result.error(400, "标签名称不能为空");
        }
        
        if (userId == null) {
            return Result.error(400, "用户ID不能为空");
        }
        
        String finalTagName = tagName.startsWith("#") ? tagName : "#" + tagName;
        
        Category category = categoryService.addCustomTag(finalTagName, userId);
        return Result.success("自定义标签添加成功", category);
    }

    /**
     * 获取所有分类（管理员用，包含用户名）
     * @return 分类列表
     */
    @GetMapping("/admin/list")
    public Result getAllCategoriesAdmin() {
        List<Category> categories = categoryService.getAllCategoriesWithUser();
        return Result.success(categories);
    }

    /**
     * 添加分类/标签（管理员用）
     * @param category 分类实体
     * @return 添加结果
     */
    @PostMapping("/admin/add")
    public Result addCategory(@RequestBody Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return Result.error(400, "标签名称不能为空");
        }
        Category result = categoryService.addCategory(category);
        return Result.success("添加成功", result);
    }

    /**
     * 更新分类/标签（管理员用）
     * @param category 分类实体
     * @return 更新结果
     */
    @PutMapping("/admin/update")
    public Result updateCategory(@RequestBody Category category) {
        if (category.getId() == null) {
            return Result.error(400, "标签ID不能为空");
        }
        boolean success = categoryService.updateCategory(category);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除分类（管理员用）
     * @param id 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/admin/{id}")
    public Result deleteCategory(@PathVariable Integer id) {
        boolean success = categoryService.deleteCategory(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}