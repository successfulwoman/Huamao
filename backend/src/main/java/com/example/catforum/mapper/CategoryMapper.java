package com.example.catforum.mapper;

import com.example.catforum.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类数据访问接口
 * 提供分类表的查询操作
 */
@Mapper
public interface CategoryMapper {

    /**
     * 查询所有分类
     * @return 分类列表
     */
    @Select("SELECT * FROM category")
    List<Category> findAll();

    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类实体
     */
    @Select("SELECT * FROM category WHERE id = #{id}")
    Category findById(Integer id);
}