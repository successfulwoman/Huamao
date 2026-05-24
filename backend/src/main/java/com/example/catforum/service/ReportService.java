package com.example.catforum.service;

import com.example.catforum.entity.Report;

import java.util.List;

/**
 * 举报服务接口
 */
public interface ReportService {

    int addReport(Report report);

    List<Report> getAllReports();

    List<Report> getPendingReports();

    Report getReportById(Integer id);

    int handleReport(Integer id, Integer status);

    int deleteReport(Integer id);

    int countPending();

    int count();
}
