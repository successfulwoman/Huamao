package com.example.catforum.mapper;

import com.example.catforum.entity.Collect;
import com.example.catforum.entity.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 收藏数据访问接口
 * 提供收藏表的CRUD操作
 */
@Mapper
public interface CollectMapper {

    @Insert("INSERT INTO collect (user_id, post_id) VALUES (#{userId}, #{postId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Collect collect);

    @Delete("DELETE FROM collect WHERE user_id = #{userId} AND post_id = #{postId}")
    int delete(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Select("SELECT * FROM collect WHERE user_id = #{userId} AND post_id = #{postId}")
    Collect findByUserAndPost(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Select("SELECT COUNT(*) FROM collect WHERE post_id = #{postId}")
    int countByPost(@Param("postId") Integer postId);

    @Select("SELECT p.*, u.username as nickname, u.avatar, cat.name as category_name " +
            "FROM collect c " +
            "INNER JOIN post p ON c.post_id = p.id " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "LEFT JOIN category cat ON p.category_id = cat.id " +
            "WHERE c.user_id = #{userId} " +
            "ORDER BY c.id DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Post> findPostsByUser(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
}