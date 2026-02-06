package com.aox.system.controller;

import com.aox.common.core.domain.R;
import com.aox.common.log.annotation.Log;
import com.aox.system.domain.SysNotice;
import com.aox.system.service.NoticeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统公告管理控制器
 *
 * @author Aox Team
 */
@RestController
@RequestMapping("/api/v1/system/notices")
@RequiredArgsConstructor
@Tag(name = "系统公告", description = "系统公告管理")
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 分页查询公告列表
     */
    @GetMapping
    @Operation(summary = "分页查询公告列表")
    public R<IPage<SysNotice>> getNoticeList(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "noticeTitle", required = false) String noticeTitle,
            @RequestParam(value = "noticeType", required = false) Integer noticeType,
            @RequestParam(value = "status", required = false) Integer status) {

        IPage<SysNotice> page = noticeService.getNoticeList(current, size, noticeTitle, noticeType, status);
        return R.ok(page);
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/{noticeId}")
    @Operation(summary = "获取公告详情")
    public R<SysNotice> getNoticeDetail(@PathVariable Long noticeId) {
        SysNotice notice = noticeService.getNoticeById(noticeId);
        return R.ok(notice);
    }

    /**
     * 创建公告
     */
    @PostMapping
    @Operation(summary = "创建公告")
    @Log(module = "系统公告", operation = "创建公告")
    public R<Void> createNotice(@RequestBody SysNotice notice) {
        noticeService.createNotice(notice);
        return R.ok();
    }

    /**
     * 更新公告
     */
    @PutMapping("/{noticeId}")
    @Operation(summary = "更新公告")
    @Log(module = "系统公告", operation = "更新公告")
    public R<Void> updateNotice(@PathVariable Long noticeId, @RequestBody SysNotice notice) {
        notice.setNoticeId(noticeId);
        noticeService.updateNotice(notice);
        return R.ok();
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{noticeId}")
    @Operation(summary = "删除公告")
    @Log(module = "系统公告", operation = "删除公告")
    public R<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return R.ok();
    }

    /**
     * 批量删除公告
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除公告")
    @Log(module = "系统公告", operation = "批量删除公告")
    public R<Void> batchDeleteNotices(@RequestBody List<Long> noticeIds) {
        noticeService.batchDeleteNotices(noticeIds);
        return R.ok();
    }

    /**
     * 发布公告
     */
    @PostMapping("/{noticeId}/publish")
    @Operation(summary = "发布公告")
    @Log(module = "系统公告", operation = "发布公告")
    public R<Void> publishNotice(@PathVariable Long noticeId) {
        noticeService.publishNotice(noticeId);
        return R.ok();
    }

    /**
     * 撤回公告
     */
    @PostMapping("/{noticeId}/revoke")
    @Operation(summary = "撤回公告")
    @Log(module = "系统公告", operation = "撤回公告")
    public R<Void> revokeNotice(@PathVariable Long noticeId) {
        noticeService.revokeNotice(noticeId);
        return R.ok();
    }

    /**
     * 置顶公告
     */
    @PostMapping("/{noticeId}/top")
    @Operation(summary = "置顶公告")
    @Log(module = "系统公告", operation = "置顶公告")
    public R<Void> topNotice(@PathVariable Long noticeId, @RequestBody TopNoticeRequest request) {
        noticeService.topNotice(noticeId, request.getTopOrder());
        return R.ok();
    }

    /**
     * 取消置顶
     */
    @PostMapping("/{noticeId}/cancel-top")
    @Operation(summary = "取消置顶")
    @Log(module = "系统公告", operation = "取消置顶")
    public R<Void> cancelTop(@PathVariable Long noticeId) {
        noticeService.cancelTop(noticeId);
        return R.ok();
    }

    /**
     * 获取用户未读公告列表
     */
    @GetMapping("/unread")
    @Operation(summary = "获取未读公告列表")
    public R<List<SysNotice>> getUnreadNotices() {
        List<SysNotice> notices = noticeService.getUnreadNotices();
        return R.ok(notices);
    }

    /**
     * 标记公告为已读
     */
    @PostMapping("/{noticeId}/read")
    @Operation(summary = "标记公告为已读")
    public R<Void> markAsRead(@PathVariable Long noticeId) {
        noticeService.markAsRead(noticeId);
        return R.ok();
    }

    /**
     * 获取未读公告数量
     */
    @GetMapping("/unread/count")
    @Operation(summary = "获取未读公告数量")
    public R<Integer> getUnreadCount() {
        Integer count = noticeService.getUnreadCount();
        return R.ok(count);
    }

    // ==================== 内部类 ====================

    @Data
    public static class TopNoticeRequest {
        private Integer topOrder;
    }
}
