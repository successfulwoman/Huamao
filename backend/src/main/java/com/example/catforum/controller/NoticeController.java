package com.example.catforum.controller;

import com.example.catforum.entity.Notice;
import com.example.catforum.service.NoticeService;
import com.example.catforum.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告控制器
 * 处理公告发布和查询相关请求
 */
@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 获取最新公告
     */
    @GetMapping("/latest")
    public Result getLatestNotice() {
        Notice notice = noticeService.getLatestNotice();
        if (notice != null) {
            return Result.success(notice);
        }
        return Result.success(null);
    }

    /**
     * 获取所有公告（普通用户用）
     */
    @GetMapping("/list")
    public Result getNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        return Result.success(notices);
    }

    /**
     * 获取所有公告（管理员用）
     */
    @GetMapping("/admin/list")
    public Result getAllNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        return Result.success(notices);
    }

    /**
     * 发布公告（管理员用）
     */
    @PostMapping("/admin/publish")
    public Result publish(@RequestBody Notice notice) {
        if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            return Result.error("公告标题不能为空");
        }
        if (notice.getContent() == null || notice.getContent().trim().isEmpty()) {
            return Result.error("公告内容不能为空");
        }
        int result = noticeService.publish(notice);
        if (result > 0) {
            return Result.success("公告发布成功", notice.getId());
        }
        return Result.error("公告发布失败");
    }

    /**
     * 更新公告（管理员用）
     */
    @PostMapping("/admin/update")
    public Result update(@RequestBody Notice notice) {
        if (notice.getId() == null) {
            return Result.error("公告ID不能为空");
        }
        int result = noticeService.update(notice);
        if (result > 0) {
            return Result.success("公告更新成功");
        }
        return Result.error("公告更新失败");
    }

    /**
     * 删除公告（管理员用）
     */
    @DeleteMapping("/admin/{id}")
    public Result delete(@PathVariable Integer id) {
        int result = noticeService.delete(id);
        if (result > 0) {
            return Result.success("公告删除成功");
        }
        return Result.error("公告删除失败");
    }

    /**
     * 获取公告详情（管理员用）
     */
    @GetMapping("/admin/detail")
    public Result getDetail(@RequestParam Integer id) {
        Notice notice = noticeService.getNoticeById(id);
        if (notice != null) {
            return Result.success(notice);
        }
        return Result.error("公告不存在");
    }

    /**
     * 统计公告数量（管理员用）
     */
    @GetMapping("/admin/count")
    public Result count() {
        int count = noticeService.count();
        return Result.success(count);
    }

    /**
     * 设置公告置顶（管理员用）
     */
    @PostMapping("/admin/top/{id}")
    public Result setTop(@PathVariable Integer id) {
        int result = noticeService.setTop(id);
        if (result > 0) {
            return Result.success("公告置顶成功");
        }
        return Result.error("公告置顶失败");
    }

    /**
     * 取消公告置顶（管理员用）
     */
    @PostMapping("/admin/cancelTop/{id}")
    public Result cancelTop(@PathVariable Integer id) {
        int result = noticeService.cancelTop(id);
        if (result > 0) {
            return Result.success("取消置顶成功");
        }
        return Result.error("取消置顶失败");
    }
}
