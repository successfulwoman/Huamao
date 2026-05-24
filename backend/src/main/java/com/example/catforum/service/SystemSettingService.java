package com.example.catforum.service;

import com.example.catforum.entity.SystemSetting;

import java.util.List;
import java.util.Map;

/**
 * 系统设置服务接口
 * 提供系统设置的查询和更新功能
 */
public interface SystemSettingService {

    /**
     * 根据键名获取设置值
     * @param key 键名
     * @return 设置值
     */
    String getValue(String key);

    /**
     * 根据键名获取设置值（带默认值）
     * @param key 键名
     * @param defaultValue 默认值
     * @return 设置值
     */
    String getValue(String key, String defaultValue);

    /**
     * 获取所有设置项
     * @return 设置项列表
     */
    List<SystemSetting> getAllSettings();

    /**
     * 获取所有设置项（以Map形式返回）
     * @return 设置项Map
     */
    Map<String, String> getAllSettingsAsMap();

    /**
     * 更新设置项
     * @param key 键名
     * @param value 值
     * @return 是否成功
     */
    boolean updateSetting(String key, String value);

    /**
     * 批量更新设置项
     * @param settings 设置项Map
     * @return 成功更新的数量
     */
    int batchUpdateSettings(Map<String, String> settings);

    /**
     * 初始化默认设置
     * 如果设置项不存在，则创建默认值
     */
    void initDefaultSettings();
}
