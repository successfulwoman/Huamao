package com.example.catforum.config;

import com.example.catforum.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 系统设置初始化配置
 * 应用启动时自动初始化默认设置
 */
@Component
public class SystemSettingInitConfig implements ApplicationRunner {

    @Autowired
    private SystemSettingService systemSettingService;

    @Override
    public void run(ApplicationArguments args) {
        // 应用启动时初始化默认设置
        systemSettingService.initDefaultSettings();
        System.out.println("系统设置初始化完成");
    }
}
