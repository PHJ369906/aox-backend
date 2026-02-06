package com.aox.system.controller;

import com.aox.common.core.domain.R;
import com.aox.common.log.annotation.Log;
import com.aox.system.domain.SysMessage;
import com.aox.system.service.MessageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站内消息管理控制器
 *
 * @author Aox Team
 */
@RestController
@RequestMapping("/api/v1/system/messages")
@RequiredArgsConstructor
@Tag(name = "站内消息", description = "站内消息管理")
public class MessageController {

    private final MessageService messageService;

    /**
     * 分页查询消息列表
     */
    @GetMapping
    @Operation(summary = "分页查询消息列表")
    public R<IPage<SysMessage>> getMessageList(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "messageType", required = false) Integer messageType,
            @RequestParam(value = "isRead", required = false) Integer isRead) {

        IPage<SysMessage> page = messageService.getMessageList(current, size, messageType, isRead);
        return R.ok(page);
    }

    /**
     * 获取消息详情
     */
    @GetMapping("/{messageId}")
    @Operation(summary = "获取消息详情")
    public R<SysMessage> getMessageDetail(@PathVariable Long messageId) {
        SysMessage message = messageService.getMessageById(messageId);
        return R.ok(message);
    }

    /**
     * 发送消息
     */
    @PostMapping
    @Operation(summary = "发送消息")
    @Log(module = "站内消息", operation = "发送消息")
    public R<Void> sendMessage(@RequestBody SysMessage message) {
        messageService.sendMessage(message);
        return R.ok();
    }

    /**
     * 发送系统消息（批量）
     */
    @PostMapping("/system")
    @Operation(summary = "发送系统消息")
    @Log(module = "站内消息", operation = "发送系统消息")
    public R<Void> sendSystemMessage(@RequestBody SendSystemMessageRequest request) {
        messageService.sendSystemMessage(request.getTitle(), request.getContent(), request.getUserIds());
        return R.ok();
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/{messageId}/read")
    @Operation(summary = "标记消息为已读")
    public R<Void> markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return R.ok();
    }

    /**
     * 批量标记为已读
     */
    @PostMapping("/read/batch")
    @Operation(summary = "批量标记为已读")
    public R<Void> batchMarkAsRead(@RequestBody List<Long> messageIds) {
        messageService.batchMarkAsRead(messageIds);
        return R.ok();
    }

    /**
     * 标记所有消息为已读
     */
    @PostMapping("/read/all")
    @Operation(summary = "标记所有消息为已读")
    public R<Void> markAllAsRead() {
        messageService.markAllAsRead();
        return R.ok();
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    @Operation(summary = "删除消息")
    @Log(module = "站内消息", operation = "删除消息")
    public R<Void> deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        return R.ok();
    }

    /**
     * 批量删除消息
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除消息")
    @Log(module = "站内消息", operation = "批量删除消息")
    public R<Void> batchDeleteMessages(@RequestBody List<Long> messageIds) {
        messageService.batchDeleteMessages(messageIds);
        return R.ok();
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/count")
    @Operation(summary = "获取未读消息数量")
    public R<Integer> getUnreadCount() {
        Integer count = messageService.getUnreadCount();
        return R.ok(count);
    }

    // ==================== 内部类 ====================

    @Data
    public static class SendSystemMessageRequest {
        private String title;
        private String content;
        private List<Long> userIds;
    }
}
