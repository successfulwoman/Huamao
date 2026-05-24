package com.example.catforum.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统设置实体类
 * 存储网站的基本配置信息
 */
@Data
public class SystemSetting {

    private Integer id;

    /**
     * 设置项键名
     */
    private String settingKey;

    /**
     * 设置项值
     */
    private String settingValue;

    /**
     * 设置项描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
