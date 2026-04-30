package com.example.catforum.mapper;

import com.example.catforum.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问接口
 * 提供用户表的CRUD操作
 */
@Mapper
public interface UserMapper {
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户实体
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(@Param("id") Integer id);
    
    /**
     * 插入新用户
     * @param user 用户实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO user (id, username, password, avatar, create_time) " +
            "VALUES (null, #{username}, #{password}, 'https://huamaoss.oss-cn-qingdao.aliyuncs.com/Logo.png', NOW())")
    int insert(User user);

    @org.apache.ibatis.annotations.Update("UPDATE user SET avatar = #{avatar} WHERE id = #{id}")
    int updateAvatar(@Param("id") Integer id, @Param("avatar") String avatar);

}