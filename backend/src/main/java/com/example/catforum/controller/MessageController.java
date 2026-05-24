package com.example.catforum.controller;

import com.example.catforum.entity.Message;
import com.example.catforum.service.MessageService;
import com.example.catforum.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息通知控制器
 * 处理消息通知相关的API请求
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取用户消息列表
     * @param userId 用户ID
     * @param page 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 消息列表
     */
    @GetMapping("/my")
    public Result<List<Message>> getMyMessages(@RequestParam Integer userId,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Message> messages = messageService.getUserMessages(userId, page, pageSize);
        return Result.success(messages);
    }

    /**
     * 获取用户未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    @GetMapping("/unread/count")
    public Result<Map<String, Integer>> getUnreadCount(@RequestParam Integer userId) {
        int count = messageService.getUnreadCount(userId);
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @return 是否成功
     */
    @PostMapping("/read")
    public Result<Boolean> markRead(@RequestParam Integer messageId) {
        boolean success = messageService.markAsRead(messageId);
        return success ? Result.success(true) : Result.error("标记失败");
    }
}