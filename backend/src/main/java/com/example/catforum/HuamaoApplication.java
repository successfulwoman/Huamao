package com.example.catforum;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 花猫社区论坛网站启动类
 */
@SpringBootApplication
@MapperScan("com.example.catforum.mapper")
public class HuamaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(HuamaoApplication.class, args);
    }
}