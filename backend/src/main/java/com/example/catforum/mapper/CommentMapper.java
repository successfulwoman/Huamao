package com.example.catforum.mapper;

import com.example.catforum.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 评论数据访问接口
 * 提供评论表的CRUD操作
 */
@Mapper
public interface CommentMapper {

    /**
     * 插入评论
     * @param comment 评论实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO comment (post_id, user_id, content, create_time) VALUES (#{postId}, #{userId}, #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    /**
     * 根据帖子ID查询评论列表（带分页）
     * @param postId 帖子ID
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 评论列表
     */
    @Select("SELECT c.id, c.post_id AS postId, c.user_id AS userId, c.content, c.create_time AS createTime, u.username as nickname, u.avatar FROM comment c LEFT JOIN user u ON c.user_id = u.id WHERE c.post_id = #{postId} ORDER BY c.create_time DESC LIMIT #{offset}, #{limit}")
    List<Comment> findByPost(@Param("postId") Integer postId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 根据帖子ID统计评论数
     * @param postId 帖子ID
     * @return 评论数量
     */
    @Select("SELECT COUNT(*) FROM comment WHERE post_id = #{postId}")
    int countByPost(@Param("postId") Integer postId);

    /**
     * 获取所有评论（管理员用）
     * @return 评论列表
     */
    @Select("SELECT c.id, c.post_id AS postId, c.user_id AS userId, c.content, c.create_time AS createTime, u.username as nickname, p.title as postTitle FROM comment c LEFT JOIN user u ON c.user_id = u.id LEFT JOIN post p ON c.post_id = p.id ORDER BY c.create_time DESC")
    List<Comment> findAll();

    /**
     * 根据ID查询评论
     * @param id 评论ID
     * @return 评论对象
     */
    @Select("SELECT id, post_id AS postId, user_id AS userId, content, create_time AS createTime FROM comment WHERE id = #{id}")
    Comment findById(@Param("id") Integer id);

    /**
     * 删除评论
     * @param id 评论ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM comment WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    /**
     * 统计所有评论数
     * @return 评论总数
     */
    @Select("SELECT COUNT(*) FROM comment")
    int countAll();
}