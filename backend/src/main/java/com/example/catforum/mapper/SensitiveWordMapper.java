package com.example.catforum.mapper;

import com.example.catforum.entity.SensitiveWord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 敏感词数据访问接口
 */
@Mapper
public interface SensitiveWordMapper {

    @Insert("INSERT INTO sensitive_word (word, create_time) VALUES (#{word}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SensitiveWord sensitiveWord);

    @Select("SELECT * FROM sensitive_word ORDER BY create_time DESC")
    List<SensitiveWord> findAll();

    @Select("SELECT word FROM sensitive_word")
    List<String> findAllWords();

    @Select("SELECT * FROM sensitive_word WHERE id = #{id}")
    SensitiveWord findById(@Param("id") Integer id);

    @Select("SELECT * FROM sensitive_word WHERE word = #{word}")
    SensitiveWord findByWord(@Param("word") String word);

    @Delete("DELETE FROM sensitive_word WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    @Delete("DELETE FROM sensitive_word WHERE word = #{word}")
    int deleteByWord(@Param("word") String word);

    @Select("SELECT COUNT(*) FROM sensitive_word")
    int count();
}
