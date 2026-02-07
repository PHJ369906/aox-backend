package com.aox.miniapp.controller;

import com.aox.common.core.domain.R;
import com.aox.miniapp.domain.vo.MiniappMessageVO;
import com.aox.miniapp.service.MiniappMessageService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序消息控制器
 */
@Tag(name = "小程序-消息", description = "小程序消息中心接口")
@RestController
@RequestMapping("/api/v1/miniapp/messages")
@RequiredArgsConstructor
public class MiniappMessageController {

    private final MiniappMessageService messageService;

    @Operation(summary = "分页查询消息", description = "查询当前用户消息列表")
    @GetMapping
    public R<Page<MiniappMessageVO>> getMessagePage(
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        return R.ok(messageService.getMessagePage(pageNum, pageSize));
    }

    @Operation(summary = "标记单条已读", description = "将指定消息标记为已读")
    @PostMapping("/{messageId}/read")
    public R<Void> markAsRead(@Parameter(description = "消息ID") @PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return R.ok();
    }

    @Operation(summary = "全部已读", description = "将当前用户未读消息全部标记为已读")
    @PostMapping("/read-all")
    public R<Void> markAllAsRead() {
        messageService.markAllAsRead();
        return R.ok();
    }

    @Operation(summary = "查询未读数", description = "查询当前用户未读消息数量")
    @GetMapping("/unread-count")
    public R<Long> getUnreadCount() {
        return R.ok(messageService.getUnreadCount());
    }
}
