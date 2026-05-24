package com.example.catforum.mapper;

import com.example.catforum.entity.Report;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 举报数据访问接口
 */
@Mapper
public interface ReportMapper {

    @Insert("INSERT INTO report (report_user_id, post_id, comment_id, reason, status, create_time) " +
            "VALUES (#{reportUserId}, #{postId}, #{commentId}, #{reason}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Report report);

    @Select("SELECT r.*, u.username as reporter_name, p.title as post_title, " +
            "COALESCE(pu.username, cu.username) as target_username " +
            "FROM report r " +
            "LEFT JOIN `user` u ON r.report_user_id = u.id " +
            "LEFT JOIN post p ON r.post_id = p.id " +
            "LEFT JOIN comment c ON r.comment_id = c.id " +
            "LEFT JOIN `user` pu ON p.user_id = pu.id " +
            "LEFT JOIN `user` cu ON c.user_id = cu.id " +
            "ORDER BY r.create_time DESC")
    List<Report> findAll();

    @Select("SELECT r.*, u.username as reporter_name, p.title as post_title, " +
            "COALESCE(pu.username, cu.username) as target_username " +
            "FROM report r " +
            "LEFT JOIN `user` u ON r.report_user_id = u.id " +
            "LEFT JOIN post p ON r.post_id = p.id " +
            "LEFT JOIN comment c ON r.comment_id = c.id " +
            "LEFT JOIN `user` pu ON p.user_id = pu.id " +
            "LEFT JOIN `user` cu ON c.user_id = cu.id " +
            "WHERE r.status = #{status} " +
            "ORDER BY r.create_time DESC")
    List<Report> findByStatus(@Param("status") Integer status);

    @Select("SELECT r.*, u.username as reporter_name, p.title as post_title, " +
            "COALESCE(pu.username, cu.username) as target_username " +
            "FROM report r " +
            "LEFT JOIN `user` u ON r.report_user_id = u.id " +
            "LEFT JOIN post p ON r.post_id = p.id " +
            "LEFT JOIN comment c ON r.comment_id = c.id " +
            "LEFT JOIN `user` pu ON p.user_id = pu.id " +
            "LEFT JOIN `user` cu ON c.user_id = cu.id " +
            "WHERE r.id = #{id}")
    Report findById(@Param("id") Integer id);

    @Update("UPDATE report SET status = #{status}, handle_time = #{handleTime} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status, @Param("handleTime") Date handleTime);

    @Delete("DELETE FROM report WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    @Select("SELECT COUNT(*) FROM report WHERE status = 0")
    int countPending();

    @Select("SELECT COUNT(*) FROM report")
    int count();
}
