package com.example.catforum.service;

import com.example.catforum.entity.Notice;

import java.util.List;

/**
 * 公告服务接口
 */
public interface NoticeService {

    int publish(Notice notice);

    List<Notice> getAllNotices();

    Notice getLatestNotice();

    Notice getNoticeById(Integer id);

    int update(Notice notice);

    int delete(Integer id);

    int count();

    /**
     * 设置公告置顶
     * @param id 公告ID
     * @return 影响的行数
     */
    int setTop(Integer id);

    /**
     * 取消公告置顶
     * @param id 公告ID
     * @return 影响的行数
     */
    int cancelTop(Integer id);
}
