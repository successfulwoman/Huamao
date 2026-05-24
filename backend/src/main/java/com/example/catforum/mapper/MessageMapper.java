package com.example.catforum.mapper;

import com.example.catforum.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 消息通知数据访问接口
 * 提供消息表的CRUD操作
 */
@Mapper
public interface MessageMapper {

    /**
     * 插入消息
     * @param message 消息实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO message (user_id, sender_id, post_id, notice_id, type, content, sender_name, post_title, is_read, create_time) " +
            "VALUES (#{userId}, #{senderId}, #{postId}, #{noticeId}, #{type}, #{content}, #{senderName}, #{postTitle}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Message message);

    /**
     * 根据用户ID查询消息列表（带分页）
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 消息列表
     */
    @Select("SELECT * FROM message WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Message> findByUser(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 根据用户ID统计消息数量
     * @param userId 用户ID
     * @return 消息数量
     */
    @Select("SELECT COUNT(*) FROM message WHERE user_id = #{userId}")
    int countByUser(@Param("userId") Integer userId);

    /**
     * 根据用户ID统计未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    @Select("SELECT COUNT(*) FROM message WHERE user_id = #{userId} AND is_read = 0")
    int countUnread(@Param("userId") Integer userId);

    /**
     * 标记消息为已读
     * @param id 消息ID
     * @return 影响的行数
     */
    @Update("UPDATE message SET is_read = 1 WHERE id = #{id}")
    int markRead(@Param("id") Integer id);

    /**
     * 根据公告ID查询消息列表
     * @param noticeId 公告ID
     * @return 消息列表
     */
    @Select("SELECT * FROM message WHERE notice_id = #{noticeId}")
    List<Message> findByNotice(@Param("noticeId") Integer noticeId);
}