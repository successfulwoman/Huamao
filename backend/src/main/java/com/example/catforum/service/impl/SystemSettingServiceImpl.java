package com.example.catforum.service.impl;

import com.example.catforum.entity.SystemSetting;
import com.example.catforum.mapper.SystemSettingMapper;
import com.example.catforum.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统设置服务实现类
 */
@Service
public class SystemSettingServiceImpl implements SystemSettingService {

    @Autowired
    private SystemSettingMapper systemSettingMapper;

    // 默认设置值
    private static final Map<String, String> DEFAULT_SETTINGS = new HashMap<>();
    private static final Map<String, String> DEFAULT_DESCRIPTIONS = new HashMap<>();

    static {
        DEFAULT_SETTINGS.put("site.name", "花猫社区论坛网站");
        DEFAULT_DESCRIPTIONS.put("site.name", "网站名称");

        DEFAULT_SETTINGS.put("site.shortName", "花猫社区");
        DEFAULT_DESCRIPTIONS.put("site.shortName", "网站简称");

        DEFAULT_SETTINGS.put("site.description", "一个温馨的社区论坛");
        DEFAULT_DESCRIPTIONS.put("site.description", "网站描述");

        DEFAULT_SETTINGS.put("site.logo", "");
        DEFAULT_DESCRIPTIONS.put("site.logo", "网站Logo URL");

        DEFAULT_SETTINGS.put("site.icp", "");
        DEFAULT_DESCRIPTIONS.put("site.icp", "ICP备案号");

        DEFAULT_SETTINGS.put("site.copyright", "© 2024 花猫社区");
        DEFAULT_DESCRIPTIONS.put("site.copyright", "版权信息");

        DEFAULT_SETTINGS.put("site.keywords", "论坛,社区,交流");
        DEFAULT_DESCRIPTIONS.put("site.keywords", "网站关键词（SEO用）");

        DEFAULT_SETTINGS.put("site.allowRegister", "true");
        DEFAULT_DESCRIPTIONS.put("site.allowRegister", "是否允许注册");

        DEFAULT_SETTINGS.put("site.allowPost", "true");
        DEFAULT_DESCRIPTIONS.put("site.allowPost", "是否允许发帖");

        DEFAULT_SETTINGS.put("site.allowComment", "true");
        DEFAULT_DESCRIPTIONS.put("site.allowComment", "是否允许评论");

        DEFAULT_SETTINGS.put("site.postNeedAudit", "false");
        DEFAULT_DESCRIPTIONS.put("site.postNeedAudit", "发帖是否需要审核");

        DEFAULT_SETTINGS.put("site.commentNeedAudit", "false");
        DEFAULT_DESCRIPTIONS.put("site.commentNeedAudit", "评论是否需要审核");
    }

    @Override
    public String getValue(String key) {
        SystemSetting setting = systemSettingMapper.findByKey(key);
        if (setting != null) {
            return setting.getSettingValue();
        }
        return DEFAULT_SETTINGS.get(key);
    }

    @Override
    public String getValue(String key, String defaultValue) {
        String value = getValue(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public List<SystemSetting> getAllSettings() {
        List<SystemSetting> settings = systemSettingMapper.findAll();

        // 补充缺失的默认设置
        Map<String, SystemSetting> existingMap = new HashMap<>();
        for (SystemSetting setting : settings) {
            existingMap.put(setting.getSettingKey(), setting);
        }

        for (Map.Entry<String, String> entry : DEFAULT_SETTINGS.entrySet()) {
            if (!existingMap.containsKey(entry.getKey())) {
                SystemSetting defaultSetting = new SystemSetting();
                defaultSetting.setSettingKey(entry.getKey());
                defaultSetting.setSettingValue(entry.getValue());
                defaultSetting.setDescription(DEFAULT_DESCRIPTIONS.get(entry.getKey()));
                settings.add(defaultSetting);
            }
        }

        return settings;
    }

    @Override
    public Map<String, String> getAllSettingsAsMap() {
        Map<String, String> result = new HashMap<>(DEFAULT_SETTINGS);
        List<SystemSetting> settings = systemSettingMapper.findAll();
        for (SystemSetting setting : settings) {
            result.put(setting.getSettingKey(), setting.getSettingValue());
        }
        return result;
    }

    @Override
    public boolean updateSetting(String key, String value) {
        SystemSetting existing = systemSettingMapper.findByKey(key);
        if (existing != null) {
            return systemSettingMapper.updateValue(key, value) > 0;
        } else {
            SystemSetting setting = new SystemSetting();
            setting.setSettingKey(key);
            setting.setSettingValue(value);
            setting.setDescription(DEFAULT_DESCRIPTIONS.getOrDefault(key, ""));
            return systemSettingMapper.insert(setting) > 0;
        }
    }

    @Override
    public int batchUpdateSettings(Map<String, String> settings) {
        int successCount = 0;
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            if (updateSetting(entry.getKey(), entry.getValue())) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public void initDefaultSettings() {
        for (Map.Entry<String, String> entry : DEFAULT_SETTINGS.entrySet()) {
            SystemSetting existing = systemSettingMapper.findByKey(entry.getKey());
            if (existing == null) {
                SystemSetting setting = new SystemSetting();
                setting.setSettingKey(entry.getKey());
                setting.setSettingValue(entry.getValue());
                setting.setDescription(DEFAULT_DESCRIPTIONS.get(entry.getKey()));
                systemSettingMapper.insert(setting);
            }
        }
    }
}
