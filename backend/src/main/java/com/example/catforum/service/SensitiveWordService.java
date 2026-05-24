package com.example.catforum.service;

import com.example.catforum.entity.SensitiveWord;

import java.util.List;

/**
 * 敏感词服务接口
 */
public interface SensitiveWordService {

    int addWord(String word);

    List<SensitiveWord> getAllWords();

    List<String> getWordList();

    int deleteById(Integer id);

    int deleteByWord(String word);

    int count();

    String filterContent(String content);

    boolean containsSensitive(String content);
}
