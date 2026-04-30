package com.example.catforum.mapper;

import com.example.catforum.entity.PostImage;
import org.apache.ibatis.annotations.*;

/**
 * 帖子图片数据访问接口
 * 提供帖子图片表的CRUD操作
 */
@Mapper
public interface PostImageMapper {

    /**
     * 插入帖子图片
     * @param postImage 帖子图片实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO post_image (post_id, image_url) VALUES (#{postId}, #{imageUrl})")
    int insert(PostImage postImage);

    /**
     * 根据帖子ID查询所有图片
     * @param postId 帖子ID
     * @return 图片URL列表
     */
    @Select("SELECT image_url FROM post_image WHERE post_id = #{postId}")
    String[] findByPostId(@Param("postId") Integer postId);

    /**
     * 根据帖子ID删除所有图片
     * @param postId 帖子ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM post_image WHERE post_id = #{postId}")
    int deleteByPostId(@Param("postId") Integer postId);
}