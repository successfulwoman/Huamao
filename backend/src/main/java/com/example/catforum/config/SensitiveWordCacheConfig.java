package com.example.catforum.config;

import com.example.catforum.mapper.SensitiveWordMapper;
import com.example.catforum.util.SensitiveWordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 敏感词缓存初始化配置
 * 应用启动时自动加载所有敏感词到内存
 */
@Component
public class SensitiveWordCacheConfig implements ApplicationRunner {

    @Autowired
    private SensitiveWordUtil sensitiveWordUtil;

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    @Override
    public void run(ApplicationArguments args) {
        // 应用启动时加载所有敏感词到缓存
        List<String> words = sensitiveWordMapper.findAllWords();
        sensitiveWordUtil.initSensitiveWordCache(words);
        System.out.println("敏感词缓存初始化完成，共加载 " + words.size() + " 个敏感词");
    }
}
