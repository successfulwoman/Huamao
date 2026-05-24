package com.example.catforum.controller;

import com.example.catforum.entity.SensitiveWord;
import com.example.catforum.service.SensitiveWordService;
import com.example.catforum.service.impl.SensitiveWordServiceImpl;
import com.example.catforum.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 敏感词控制器
 * 处理敏感词管理相关请求
 */
@RestController
@RequestMapping("/api/sensitive")
public class SensitiveWordController {

    @Autowired
    private SensitiveWordService sensitiveWordService;

    /**
     * 添加敏感词（管理员用）
     */
    @PostMapping("/admin/add")
    public Result addWord(@RequestParam String word) {
        if (word == null || word.trim().isEmpty()) {
            return Result.error("敏感词不能为空");
        }
        int result = sensitiveWordService.addWord(word.trim());
        if (result == -1) {
            return Result.error("敏感词已存在");
        } else if (result > 0) {
            return Result.success("敏感词添加成功");
        }
        return Result.error("敏感词添加失败");
    }

    /**
     * 获取所有敏感词（管理员用）
     */
    @GetMapping("/admin/list")
    public Result getAllWords() {
        List<SensitiveWord> words = sensitiveWordService.getAllWords();
        return Result.success(words);
    }

    /**
     * 删除敏感词（管理员用）
     */
    @DeleteMapping("/admin/{id}")
    public Result deleteById(@PathVariable Integer id) {
        int result = sensitiveWordService.deleteById(id);
        if (result > 0) {
            return Result.success("敏感词删除成功");
        }
        return Result.error("敏感词删除失败");
    }

    /**
     * 批量添加敏感词（管理员用）
     */
    @PostMapping("/admin/batch")
    public Result batchAdd(@RequestBody List<String> words) {
        int successCount = 0;
        int failCount = 0;
        for (String word : words) {
            if (word != null && !word.trim().isEmpty()) {
                int result = sensitiveWordService.addWord(word.trim());
                if (result > 0) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
        }
        return Result.success("批量添加完成，成功：" + successCount + "，失败：" + failCount);
    }

    /**
     * 批量删除敏感词（管理员用）
     */
    @PostMapping("/admin/batchDelete")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        int successCount = 0;
        int failCount = 0;
        for (Integer id : ids) {
            int result = sensitiveWordService.deleteById(id);
            if (result > 0) {
                successCount++;
            } else {
                failCount++;
            }
        }
        return Result.success("批量删除完成，成功：" + successCount + "，失败：" + failCount);
    }

    /**
     * 获取敏感词总数（管理员用）
     */
    @GetMapping("/admin/count")
    public Result getCount() {
        int count = sensitiveWordService.count();
        return Result.success(count);
    }

    /**
     * 测试内容过滤（管理员用）
     */
    @PostMapping("/admin/testFilter")
    public Result testFilter(@RequestBody Map<String, String> params) {
        String content = params.get("content");
        if (content == null || content.isEmpty()) {
            return Result.error("内容不能为空");
        }
        String filtered = sensitiveWordService.filterContent(content);
        boolean contains = sensitiveWordService.containsSensitive(content);
        return Result.success(Map.of(
            "original", content,
            "filtered", filtered,
            "containsSensitive", contains
        ));
    }

    /**
     * 刷新敏感词缓存（管理员用）
     * 用于手动同步数据库和内存缓存
     */
    @PostMapping("/admin/refreshCache")
    public Result refreshCache() {
        if (sensitiveWordService instanceof SensitiveWordServiceImpl) {
            ((SensitiveWordServiceImpl) sensitiveWordService).refreshCache();
            return Result.success("敏感词缓存刷新成功");
        }
        return Result.error("缓存刷新失败");
    }

    /**
     * 检查内容是否包含敏感词（公开接口）
     */
    @PostMapping("/check")
    public Result checkContent(@RequestBody Map<String, String> params) {
        String content = params.get("content");
        if (content == null || content.isEmpty()) {
            return Result.success(Map.of("contains", false));
        }
        boolean contains = sensitiveWordService.containsSensitive(content);
        return Result.success(Map.of("contains", contains));
    }

    /**
     * 过滤内容中的敏感词（公开接口）
     */
    @PostMapping("/filter")
    public Result filterContent(@RequestBody Map<String, String> params) {
        String content = params.get("content");
        if (content == null || content.isEmpty()) {
            return Result.success(Map.of("filtered", ""));
        }
        String filtered = sensitiveWordService.filterContent(content);
        return Result.success(Map.of("filtered", filtered));
    }
}
