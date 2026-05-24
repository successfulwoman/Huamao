package com.example.catforum.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 帖子实体类
 * 对应数据库中的post表
 */
@Data
public class Post {
    private Integer id;           // 帖子ID
    private String title;        // 帖子标题
    private String content;      // 帖子内容
    private Integer userId;      // 发布人ID
    private Integer categoryId;  // 分类ID
    private Integer likeNum;     // 点赞数（数据库字段）
    private Integer commentNum;  // 评论数（数据库字段）
    private Integer collectNum;  // 收藏数（数据库字段）
    private Date createTime;     // 创建时间
    // 管理员管理字段
    private Integer isTop;       // 是否置顶：0-否 1-是
    private Integer isBest;      // 是否精华：0-否 1-是
    private Integer status;      // 帖子状态：1-正常 0-隐藏
    // 扩展字段，不对应数据库表
    private String nickname;     // 发布者昵称（从user表关联查询）
    private String avatar;       // 发布者头像
    private String categoryName; // 分类名称
    private Integer role;        // 发布者角色（从user表关联查询）
    private String[] imageUrls; // 帖子图片URL列表（不存数据库，从post_image表查询）
    // 前端需要的字段别名
    private Integer likeCount;   // 点赞数（前端使用）
    private Integer commentCount; // 评论数（前端使用）
    private Integer collectCount; // 收藏数（前端使用）
    private Integer collectCountFront; // 收藏数（前端使用）
    private Boolean liked;       // 是否已点赞
    private Boolean collected;   // 是否已收藏
}