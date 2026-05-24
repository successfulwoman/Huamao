package com.example.catforum.entity;

import lombok.Data;

import java.util.Date;

/**
 * 敏感词实体类
 * 对应数据库中的sensitive_word表
 */
@Data
public class SensitiveWord {
    private Integer id;           // ID
    private String word;          // 敏感词
    private Date createTime;      // 添加时间
}
