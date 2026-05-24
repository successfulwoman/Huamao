package com.example.catforum.mapper;

import com.example.catforum.entity.Notice;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 公告数据访问接口
 */
@Mapper
public interface NoticeMapper {

    @Insert("INSERT INTO notice (title, content, create_time) VALUES (#{title}, #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notice notice);

    @Select("SELECT * FROM notice ORDER BY is_top DESC, top_time DESC, create_time DESC")
    List<Notice> findAll();

    @Select("SELECT * FROM notice ORDER BY is_top DESC, top_time DESC, create_time DESC LIMIT 1")
    Notice findLatest();

    @Select("SELECT * FROM notice WHERE id = #{id}")
    Notice findById(@Param("id") Integer id);

    @Update("UPDATE notice SET title = #{title}, content = #{content} WHERE id = #{id}")
    int update(Notice notice);

    @Delete("DELETE FROM notice WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    @Select("SELECT COUNT(*) FROM notice")
    int count();

    /**
     * 设置公告置顶
     */
    @Update("UPDATE notice SET is_top = 1, top_time = NOW() WHERE id = #{id}")
    int setTop(@Param("id") Integer id);

    /**
     * 取消公告置顶
     */
    @Update("UPDATE notice SET is_top = 0, top_time = NULL WHERE id = #{id}")
    int cancelTop(@Param("id") Integer id);
}
