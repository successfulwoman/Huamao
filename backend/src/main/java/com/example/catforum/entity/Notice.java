package com.example.catforum.entity;

import lombok.Data;

import java.util.Date;

/**
 * 公告实体类
 * 对应数据库中的notice表
 */
@Data
public class Notice {
    private Integer id;           // 公告ID
    private String title;        // 公告标题
    private String content;      // 公告内容
    private Integer isTop;       // 是否置顶：0-否 1-是
    private Date topTime;        // 置顶时间
    private Date createTime;     // 发布时间
}
