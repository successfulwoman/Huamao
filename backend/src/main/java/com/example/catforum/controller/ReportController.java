package com.example.catforum.controller;

import com.example.catforum.entity.Report;
import com.example.catforum.service.ReportService;
import com.example.catforum.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 举报控制器
 * 处理举报相关请求
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 用户提交举报
     */
    @PostMapping("/add")
    public Result addReport(@RequestBody Report report) {
        if (report.getReportUserId() == null) {
            return Result.error("举报人ID不能为空");
        }
        if (report.getPostId() == null && report.getCommentId() == null) {
            return Result.error("必须选择举报的帖子或评论");
        }
        if (report.getReason() == null || report.getReason().trim().isEmpty()) {
            return Result.error("举报原因不能为空");
        }
        int result = reportService.addReport(report);
        if (result > 0) {
            return Result.success("举报提交成功");
        }
        return Result.error("举报提交失败");
    }

    /**
     * 获取所有举报（管理员用）
     */
    @GetMapping("/admin/list")
    public Result getAllReports() {
        List<Report> reports = reportService.getAllReports();
        return Result.success(reports);
    }

    /**
     * 获取待处理举报（管理员用）
     */
    @GetMapping("/admin/pending")
    public Result getPendingReports() {
        List<Report> reports = reportService.getPendingReports();
        return Result.success(reports);
    }

    /**
     * 处理举报（管理员用）
     * @param id 举报ID
     * @param status 处理状态：1-已处理 2-已忽略
     */
    @PostMapping("/admin/handle")
    public Result handleReport(@RequestParam Integer id, @RequestParam Integer status) {
        if (status != 1 && status != 2) {
            return Result.error("状态值不正确");
        }
        int result = reportService.handleReport(id, status);
        if (result > 0) {
            return Result.success(status == 1 ? "举报已处理" : "举报已忽略");
        }
        return Result.error("操作失败");
    }

    /**
     * 删除举报（管理员用）
     */
    @DeleteMapping("/admin/{id}")
    public Result deleteReport(@PathVariable Integer id) {
        int result = reportService.deleteReport(id);
        if (result > 0) {
            return Result.success("举报删除成功");
        }
        return Result.error("举报删除失败");
    }

    /**
     * 获取举报详情（管理员用）
     */
    @GetMapping("/admin/detail")
    public Result getDetail(@RequestParam Integer id) {
        Report report = reportService.getReportById(id);
        if (report != null) {
            return Result.success(report);
        }
        return Result.error("举报不存在");
    }

    /**
     * 统计待处理举报数量（管理员用）
     */
    @GetMapping("/admin/pending/count")
    public Result countPending() {
        int count = reportService.countPending();
        return Result.success(count);
    }

    /**
     * 统计总举报数量（管理员用）
     */
    @GetMapping("/admin/count")
    public Result count() {
        int count = reportService.count();
        return Result.success(count);
    }
}
