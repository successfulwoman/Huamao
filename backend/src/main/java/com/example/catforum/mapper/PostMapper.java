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
    @Select("SELECT p.*, u.username as nickname, u.avatar, u.role, c.name as category_name " +
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
    @Select("SELECT p.*, u.username as nickname, u.avatar, u.role, c.name as category_name " +
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
    @Select("SELECT p.*, u.username as nickname, u.avatar, u.role, c.name as category_name " +
            "FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.category_id = #{categoryId} " +
            "ORDER BY p.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Post> findByCategory(@Param("categoryId") Integer categoryId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 根据用户ID查询帖子列表（带分页）
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 帖子列表
     */
    @Select("SELECT p.*, u.username as nickname, u.avatar, u.role, c.name as category_name " +
            "FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.user_id = #{userId} " +
            "ORDER BY p.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Post> findByUser(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    @Update("UPDATE post SET comment_num = comment_num + 1 WHERE id = #{postId}")
    int incrementCommentNum(@Param("postId") Integer postId);

    @Update("UPDATE post SET comment_num = comment_num - 1 WHERE id = #{postId} AND comment_num > 0")
    int decrementCommentNum(@Param("postId") Integer postId);

    //点赞收藏
    @Update("UPDATE post SET like_num = like_num + 1 WHERE id = #{postId}")
    int incrementLikeNum(@Param("postId") Integer postId);

    @Update("UPDATE post SET like_num = like_num - 1 WHERE id = #{postId} AND like_num > 0")
    int decrementLikeNum(@Param("postId") Integer postId);

    @Update("UPDATE post SET collect_num = collect_num + 1 WHERE id = #{postId}")
    int incrementCollectNum(@Param("postId") Integer postId);

    @Update("UPDATE post SET collect_num = collect_num - 1 WHERE id = #{postId} AND collect_num > 0")
    int decrementCollectNum(@Param("postId") Integer postId);

    // ==================== 管理员管理方法 ====================

    /**
     * 查询所有帖子（不分页，管理员管理用）
     * @return 帖子列表
     */
    @Select("SELECT p.*, u.username as nickname, u.avatar, u.role, c.name as category_name " +
            "FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "ORDER BY p.create_time DESC")
    List<Post> findAllForAdmin();

    /**
     * 更新帖子状态（管理员隐藏/显示帖子）
     * @param id 帖子ID
     * @param status 状态
     * @return 影响的行数
     */
    @Update("UPDATE post SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 设置帖子置顶
     * @param id 帖子ID
     * @param isTop 是否置顶
     * @return 影响的行数
     */
    @Update("UPDATE post SET is_top = #{isTop} WHERE id = #{id}")
    int updateTop(@Param("id") Integer id, @Param("isTop") Integer isTop);

    /**
     * 设置帖子精华
     * @param id 帖子ID
     * @param isBest 是否精华
     * @return 影响的行数
     */
    @Update("UPDATE post SET is_best = #{isBest} WHERE id = #{id}")
    int updateBest(@Param("id") Integer id, @Param("isBest") Integer isBest);

    /**
     * 统计帖子总数
     * @return 帖子数量
     */
    @Select("SELECT COUNT(*) FROM post")
    int count();

    /**
     * 查询待审核/隐藏的帖子
     * @return 帖子列表
     */
    @Select("SELECT p.*, u.username as nickname, u.avatar, u.role, c.name as category_name " +
            "FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.status = 0 " +
            "ORDER BY p.create_time DESC")
    List<Post> findHiddenPosts();

    /**
     * 查询置顶帖子
     * @return 帖子列表
     */
    @Select("SELECT p.*, u.username as nickname, u.avatar, u.role, c.name as category_name " +
            "FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.is_top = 1 AND p.status = 1 " +
            "ORDER BY p.create_time DESC")
    List<Post> findTopPosts();

    /**
     * 检查帖子是否属于指定用户
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 1表示属于该用户，0表示不属于
     */
    @Select("SELECT COUNT(*) FROM post WHERE id = #{postId} AND user_id = #{userId}")
    int checkPostOwner(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 搜索帖子（支持标题、内容、用户昵称模糊搜索）
     * 自动过滤隐藏帖子，按置顶、精华、时间排序
     * @param keyword 搜索关键词
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 帖子列表
     */
    @Select("SELECT p.*, u.username as nickname, u.avatar, u.role, c.name as category_name " +
            "FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.status = 1 " +
            "AND (p.title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR p.content LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.username LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY p.is_top DESC, p.is_best DESC, p.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Post> searchPosts(@Param("keyword") String keyword, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 统计搜索结果数量
     * @param keyword 搜索关键词
     * @return 帖子数量
     */
    @Select("SELECT COUNT(*) FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.status = 1 " +
            "AND (p.title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR p.content LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.username LIKE CONCAT('%', #{keyword}, '%'))")
    int countSearchResults(@Param("keyword") String keyword);
}
