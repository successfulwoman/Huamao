package com.example.catforum.controller;

import com.example.catforum.entity.SystemSetting;
import com.example.catforum.service.SystemSettingService;
import com.example.catforum.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统设置控制器
 * 处理系统设置相关请求
 */
@RestController
@RequestMapping("/api/settings")
public class SystemSettingController {

    @Autowired
    private SystemSettingService systemSettingService;

    /**
     * 获取所有系统设置（管理员用）
     * @return 设置项列表
     */
    @GetMapping("/admin/all")
    public Result getAllSettings() {
        List<SystemSetting> settings = systemSettingService.getAllSettings();
        return Result.success(settings);
    }

    /**
     * 获取所有系统设置（以Map形式返回，管理员用）
     * @return 设置项Map
     */
    @GetMapping("/admin/map")
    public Result getAllSettingsAsMap() {
        Map<String, String> settings = systemSettingService.getAllSettingsAsMap();
        return Result.success(settings);
    }

    /**
     * 获取单个设置项（管理员用）
     * @param key 设置项键名
     * @return 设置值
     */
    @GetMapping("/admin/get")
    public Result getSetting(@RequestParam String key) {
        String value = systemSettingService.getValue(key);
        return Result.success(value);
    }

    /**
     * 更新单个设置项（管理员用）
     * @param params 参数（key, value）
     * @return 是否成功
     */
    @PostMapping("/admin/update")
    public Result updateSetting(@RequestBody Map<String, String> params) {
        String key = params.get("key");
        String value = params.get("value");

        if (key == null || key.isEmpty()) {
            return Result.error("设置项键名不能为空");
        }

        boolean success = systemSettingService.updateSetting(key, value);
        if (success) {
            return Result.success("设置更新成功");
        }
        return Result.error("设置更新失败");
    }

    /**
     * 批量更新设置项（管理员用）
     * @param settings 设置项Map
     * @return 更新结果
     */
    @PostMapping("/admin/batchUpdate")
    public Result batchUpdateSettings(@RequestBody Map<String, String> settings) {
        if (settings == null || settings.isEmpty()) {
            return Result.error("设置项不能为空");
        }

        int successCount = systemSettingService.batchUpdateSettings(settings);
        return Result.success("成功更新 " + successCount + " 项设置");
    }

    /**
     * 初始化默认设置（管理员用）
     * @return 是否成功
     */
    @PostMapping("/admin/init")
    public Result initDefaultSettings() {
        systemSettingService.initDefaultSettings();
        return Result.success("默认设置初始化完成");
    }

    /**
     * 获取网站基本信息（公开接口）
     * @return 网站基本信息
     */
    @GetMapping("/siteInfo")
    public Result getSiteInfo() {
        Map<String, String> allSettings = systemSettingService.getAllSettingsAsMap();

        Map<String, Object> siteInfo = new HashMap<>();
        siteInfo.put("name", allSettings.getOrDefault("site.name", "花猫社区论坛网站"));
        siteInfo.put("shortName", allSettings.getOrDefault("site.shortName", "花猫社区"));
        siteInfo.put("description", allSettings.getOrDefault("site.description", "一个温馨的社区论坛"));
        siteInfo.put("logo", allSettings.getOrDefault("site.logo", ""));
        siteInfo.put("icp", allSettings.getOrDefault("site.icp", ""));
        siteInfo.put("copyright", allSettings.getOrDefault("site.copyright", "© 2024 花猫社区"));
        siteInfo.put("keywords", allSettings.getOrDefault("site.keywords", "论坛,社区,交流"));

        return Result.success(siteInfo);
    }

    /**
     * 获取网站功能开关状态（公开接口）
     * @return 功能开关状态
     */
    @GetMapping("/siteStatus")
    public Result getSiteStatus() {
        Map<String, String> allSettings = systemSettingService.getAllSettingsAsMap();

        Map<String, Object> siteStatus = new HashMap<>();
        siteStatus.put("allowRegister", "true".equals(allSettings.get("site.allowRegister")));
        siteStatus.put("allowPost", "true".equals(allSettings.get("site.allowPost")));
        siteStatus.put("allowComment", "true".equals(allSettings.get("site.allowComment")));
        siteStatus.put("postNeedAudit", "true".equals(allSettings.get("site.postNeedAudit")));
        siteStatus.put("commentNeedAudit", "true".equals(allSettings.get("site.commentNeedAudit")));

        return Result.success(siteStatus);
    }
}
