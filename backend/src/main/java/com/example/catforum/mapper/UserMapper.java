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
    @Insert("INSERT INTO user (id, username, password, useremail, profile, avatar, gender, create_time) " +
            "VALUES (null, #{username}, #{password}, #{useremail}, #{profile}, 'https://huamaoss.oss-cn-qingdao.aliyuncs.com/Logo.png', #{gender}, NOW())")
    int insert(User user);

    @org.apache.ibatis.annotations.Update("UPDATE user SET avatar = #{avatar} WHERE id = #{id}")
    int updateAvatar(@Param("id") Integer id, @Param("avatar") String avatar);

    @org.apache.ibatis.annotations.Update("<script>" +
            "UPDATE user SET username = #{username}" +
            "<if test='useremail != null'>, useremail = #{useremail}</if>" +
            "<if test='profile != null'>, profile = #{profile}</if>" +
            "<if test='gender != null'>, gender = #{gender}</if>" +
            " WHERE id = #{id}" +
            "</script>")
    int update(User user);

    /**
     * 根据用户名和角色查询用户（管理员登录）
     * @param username 用户名
     * @param role 角色
     * @return 用户实体
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND role = #{role}")
    User findByUsernameAndRole(@Param("username") String username, @Param("role") Integer role);

    /**
     * 查询所有用户（管理员管理）
     * @return 用户列表
     */
    @Select("SELECT * FROM user WHERE role = 0 ORDER BY create_time DESC")
    java.util.List<User> findAll();

    /**
     * 更新用户状态（管理员禁用/启用用户）
     * @param id 用户ID
     * @param status 状态
     * @return 影响的行数
     */
    @org.apache.ibatis.annotations.Update("UPDATE user SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 更新用户角色（管理员设置权限）
     * @param id 用户ID
     * @param role 角色
     * @return 影响的行数
     */
    @org.apache.ibatis.annotations.Update("UPDATE user SET role = #{role} WHERE id = #{id}")
    int updateRole(@Param("id") Integer id, @Param("role") Integer role);

    /**
     * 删除用户（管理员删除）
     * @param id 用户ID
     * @return 影响的行数
     */
    @org.apache.ibatis.annotations.Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    /**
     * 统计用户总数（不含管理员）
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM user WHERE role = 0")
    int count();

}