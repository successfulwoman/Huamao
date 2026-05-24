package com.example.catforum.service.impl;

import com.example.catforum.entity.Report;
import com.example.catforum.mapper.ReportMapper;
import com.example.catforum.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public int addReport(Report report) {
        return reportMapper.insert(report);
    }

    @Override
    public List<Report> getAllReports() {
        return reportMapper.findAll();
    }

    @Override
    public List<Report> getPendingReports() {
        return reportMapper.findByStatus(0);
    }

    @Override
    public Report getReportById(Integer id) {
        return reportMapper.findById(id);
    }

    @Override
    public int handleReport(Integer id, Integer status) {
        return reportMapper.updateStatus(id, status, new Date());
    }

    @Override
    public int deleteReport(Integer id) {
        return reportMapper.deleteById(id);
    }

    @Override
    public int countPending() {
        return reportMapper.countPending();
    }

    @Override
    public int count() {
        return reportMapper.count();
    }
}
