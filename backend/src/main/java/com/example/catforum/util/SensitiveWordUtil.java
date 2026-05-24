package com.example.catforum.util;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 敏感词过滤工具类
 * 使用内存缓存提高性能
 */
@Component
public class SensitiveWordUtil {

    // 敏感词集合（使用HashSet提高查找效率）
    private volatile Set<String> sensitiveWordSet = new HashSet<>();

    // 敏感词替换后的字符串
    private static final String REPLACEMENT = "***";

    /**
     * 初始化/刷新敏感词缓存
     */
    public synchronized void initSensitiveWordCache(List<String> words) {
        Set<String> newSet = new HashSet<>(words);
        this.sensitiveWordSet = newSet;
    }

    /**
     * 获取当前缓存的敏感词数量
     */
    public int getCacheSize() {
        return sensitiveWordSet.size();
    }

    /**
     * 清空敏感词缓存
     */
    public synchronized void clearCache() {
        sensitiveWordSet.clear();
    }

    /**
     * 添加单个敏感词到缓存
     */
    public synchronized void addWordToCache(String word) {
        if (word != null && !word.trim().isEmpty()) {
            sensitiveWordSet.add(word.trim());
        }
    }

    /**
     * 从缓存中移除敏感词
     */
    public synchronized void removeWordFromCache(String word) {
        if (word != null) {
            sensitiveWordSet.remove(word.trim());
        }
    }

    /**
     * 过滤内容中的敏感词，替换为 ***
     * 使用DFA算法实现高效替换
     */
    public String filter(String content) {
        if (content == null || content.isEmpty() || sensitiveWordSet.isEmpty()) {
            return content;
        }

        StringBuilder result = new StringBuilder(content);
        // 按敏感词长度降序排序，优先匹配长词
        List<String> sortedWords = new ArrayList<>(sensitiveWordSet);
        sortedWords.sort((a, b) -> b.length() - a.length());

        for (String word : sortedWords) {
            if (word.isEmpty()) continue;

            // 使用正则表达式进行全局替换（忽略大小写）
            Pattern pattern = Pattern.compile(Pattern.quote(word), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(result);

            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, REPLACEMENT);
            }
            matcher.appendTail(sb);
            result = new StringBuilder(sb.toString());
        }

        return result.toString();
    }

    /**
     * 检查内容是否包含敏感词
     */
    public boolean containsSensitive(String content) {
        if (content == null || content.isEmpty() || sensitiveWordSet.isEmpty()) {
            return false;
        }

        for (String word : sensitiveWordSet) {
            if (word.isEmpty()) continue;
            if (content.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取内容中匹配的所有敏感词
     */
    public Set<String> getMatchedWords(String content) {
        Set<String> matched = new HashSet<>();
        if (content == null || content.isEmpty() || sensitiveWordSet.isEmpty()) {
            return matched;
        }

        String lowerContent = content.toLowerCase();
        for (String word : sensitiveWordSet) {
            if (word.isEmpty()) continue;
            if (lowerContent.contains(word.toLowerCase())) {
                matched.add(word);
            }
        }
        return matched;
    }
}
