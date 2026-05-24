package com.example.catforum.mapper;

import com.example.catforum.entity.Like;
import com.example.catforum.entity.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 点赞数据访问接口
 * 提供点赞表的CRUD操作
 */
@Mapper
public interface LikeMapper {

    @Insert("INSERT INTO `like` (user_id, post_id) VALUES (#{userId}, #{postId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Like like);

    @Delete("DELETE FROM `like` WHERE user_id = #{userId} AND post_id = #{postId}")
    int delete(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Select("SELECT * FROM `like` WHERE user_id = #{userId} AND post_id = #{postId}")
    Like findByUserAndPost(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Select("SELECT COUNT(*) FROM `like` WHERE post_id = #{postId}")
    int countByPost(@Param("postId") Integer postId);

    @Select("SELECT p.*, u.username as nickname, u.avatar, cat.name as category_name " +
            "FROM `like` l " +
            "INNER JOIN post p ON l.post_id = p.id " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "LEFT JOIN category cat ON p.category_id = cat.id " +
            "WHERE l.user_id = #{userId} " +
            "ORDER BY l.id DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Post> findPostsByUser(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
}