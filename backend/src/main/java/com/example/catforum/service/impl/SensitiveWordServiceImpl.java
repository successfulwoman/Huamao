package com.example.catforum.service.impl;

import com.example.catforum.entity.SensitiveWord;
import com.example.catforum.mapper.SensitiveWordMapper;
import com.example.catforum.service.SensitiveWordService;
import com.example.catforum.util.SensitiveWordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensitiveWordServiceImpl implements SensitiveWordService {

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    @Autowired
    private SensitiveWordUtil sensitiveWordUtil;

    @Override
    public int addWord(String word) {
        // 检查是否已存在
        if (sensitiveWordMapper.findByWord(word) != null) {
            return -1; // 已存在
        }
        SensitiveWord sensitiveWord = new SensitiveWord();
        sensitiveWord.setWord(word);
        int result = sensitiveWordMapper.insert(sensitiveWord);
        if (result > 0) {
            // 添加到内存缓存
            sensitiveWordUtil.addWordToCache(word);
        }
        return result;
    }

    @Override
    public List<SensitiveWord> getAllWords() {
        return sensitiveWordMapper.findAll();
    }

    @Override
    public List<String> getWordList() {
        return sensitiveWordMapper.findAllWords();
    }

    @Override
    public int deleteById(Integer id) {
        // 先获取敏感词内容，用于从缓存中移除
        SensitiveWord word = sensitiveWordMapper.findById(id);
        int result = sensitiveWordMapper.deleteById(id);
        if (result > 0 && word != null) {
            // 从内存缓存中移除
            sensitiveWordUtil.removeWordFromCache(word.getWord());
        }
        return result;
    }

    @Override
    public int deleteByWord(String word) {
        int result = sensitiveWordMapper.deleteByWord(word);
        if (result > 0) {
            // 从内存缓存中移除
            sensitiveWordUtil.removeWordFromCache(word);
        }
        return result;
    }

    @Override
    public int count() {
        return sensitiveWordMapper.count();
    }

    @Override
    public String filterContent(String content) {
        // 使用内存缓存的敏感词工具进行过滤
        return sensitiveWordUtil.filter(content);
    }

    @Override
    public boolean containsSensitive(String content) {
        // 使用内存缓存的敏感词工具检查
        return sensitiveWordUtil.containsSensitive(content);
    }

    /**
     * 批量添加敏感词
     */
    public int batchAddWords(List<String> words) {
        int successCount = 0;
        for (String word : words) {
            if (word != null && !word.trim().isEmpty()) {
                int result = addWord(word.trim());
                if (result > 0) {
                    successCount++;
                }
            }
        }
        return successCount;
    }

    /**
     * 刷新敏感词缓存（用于手动同步）
     */
    public void refreshCache() {
        List<String> words = sensitiveWordMapper.findAllWords();
        sensitiveWordUtil.initSensitiveWordCache(words);
    }
}
