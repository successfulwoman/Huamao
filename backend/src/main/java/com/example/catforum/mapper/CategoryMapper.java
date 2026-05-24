package com.example.catforum.mapper;

import com.example.catforum.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 分类数据访问接口
 * 提供分类表的查询和插入操作
 */
@Mapper
public interface CategoryMapper {

    /**
     * 查询所有分类（包括系统分类和用户自定义标签）
     * @return 分类列表
     */
    @Select("SELECT * FROM category")
    List<Category> findAll();

    /**
     * 查询所有分类（包含用户名，用于管理员后台）
     * @return 分类列表（含用户名）
     */
    @Select("SELECT c.id, c.name, c.is_custom AS isCustom, c.user_id AS userId, u.username " +
            "FROM category c LEFT JOIN user u ON c.user_id = u.id ORDER BY c.id DESC")
    List<Category> findAllWithUser();

    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类实体
     */
    @Select("SELECT * FROM category WHERE id = #{id}")
    Category findById(Integer id);

    /**
     * 查询系统分类（is_custom = 0）
     * @return 系统分类列表
     */
    @Select("SELECT * FROM category WHERE is_custom = 0")
    List<Category> findSystemCategories();

    /**
     * 查询指定用户的自定义标签
     * @param userId 用户ID
     * @return 用户自定义标签列表
     */
    @Select("SELECT * FROM category WHERE is_custom = 1 AND user_id = #{userId}")
    List<Category> findCustomByUserId(Integer userId);

    /**
     * 插入新分类/标签
     * @param category 分类实体（插入后会自动填充id字段）
     */
    @Insert("INSERT INTO category(name, is_custom, user_id) VALUES(#{name}, #{isCustom}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Category category);

    /**
     * 更新分类
     * @param category 分类实体
     * @return 影响的行数
     */
    @Update("UPDATE category SET name = #{name}, is_custom = #{isCustom}, user_id = #{userId} WHERE id = #{id}")
    int update(Category category);

    /**
     * 删除分类
     * @param id 分类ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM category WHERE id = #{id}")
    int deleteById(Integer id);
}