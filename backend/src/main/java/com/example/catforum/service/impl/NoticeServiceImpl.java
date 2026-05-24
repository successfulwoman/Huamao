package com.example.catforum.service.impl;

import com.example.catforum.entity.Notice;
import com.example.catforum.mapper.NoticeMapper;
import com.example.catforum.service.MessageService;
import com.example.catforum.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private MessageService messageService;

    @Override
    @Transactional
    public int publish(Notice notice) {
        int result = noticeMapper.insert(notice);
        if (result > 0 && notice.getId() != null) {
            try {
                // 发送公告通知给所有用户（不影响公告发布）
                messageService.sendNoticeNotification(notice.getId(), notice.getContent());
            } catch (Exception e) {
                // 通知发送失败不影响公告发布
                System.err.println("发送公告通知失败: " + e.getMessage());
            }
        }
        return result;
    }

    @Override
    public List<Notice> getAllNotices() {
        return noticeMapper.findAll();
    }

    @Override
    public Notice getLatestNotice() {
        return noticeMapper.findLatest();
    }

    @Override
    public Notice getNoticeById(Integer id) {
        return noticeMapper.findById(id);
    }

    @Override
    public int update(Notice notice) {
        return noticeMapper.update(notice);
    }

    @Override
    public int delete(Integer id) {
        return noticeMapper.deleteById(id);
    }

    @Override
    public int count() {
        return noticeMapper.count();
    }

    @Override
    public int setTop(Integer id) {
        return noticeMapper.setTop(id);
    }

    @Override
    public int cancelTop(Integer id) {
        return noticeMapper.cancelTop(id);
    }
}
