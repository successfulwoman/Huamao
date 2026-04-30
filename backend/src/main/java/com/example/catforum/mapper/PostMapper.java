package com.example.catforum.mapper;

import com.example.catforum.entity.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 帖子数据访问接口
 * 提供帖子表的CRUD操作
 */
@Mapper
public interface PostMapper {

    /**
     * 插入新帖子
     * @param post 帖子实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO post (title, content, user_id, category_id, like_num, comment_num, create_time) " +
            "VALUES (#{title}, #{content}, #{userId}, #{categoryId}, 0, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Post post);

    /**
     * 根据ID查询帖子
     * @param id 帖子ID
     * @return 帖子实体
     */
    @Select("SELECT p.*, u.username as nickname, u.avatar, c.name as category_name " +
            "FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.id = #{id}")
    Post findById(@Param("id") Integer id);

    /**
     * 删除帖子
     * @param id 帖子ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM post WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    /**
     * 查询所有帖子列表（带分页）
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 帖子列表
     */
    @Select("SELECT p.*, u.username as nickname, u.avatar, c.name as category_name " +
            "FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "ORDER BY p.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Post> findAll(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 根据分类ID查询帖子列表（带分页）
     * @param categoryId 分类ID
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 帖子列表
     */
    @Select("SELECT p.*, u.username as nickname, u.avatar, c.name as category_name " +
            "FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.category_id = #{categoryId} " +
            "ORDER BY p.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Post> findByCategory(@Param("categoryId") Integer categoryId, @Param("offset") Integer offset, @Param("limit") Integer limit);
}