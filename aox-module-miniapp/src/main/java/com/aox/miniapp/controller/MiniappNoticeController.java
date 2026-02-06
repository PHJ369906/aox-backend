package com.aox.miniapp.controller;

import com.aox.common.core.domain.R;
import com.aox.system.domain.SysNotice;
import com.aox.system.mapper.NoticeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序-公告控制器
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Tag(name = "小程序-公告", description = "小程序公告查询接口")
@RestController
@RequestMapping("/api/v1/miniapp/notices")
@RequiredArgsConstructor
public class MiniappNoticeController {

    private final NoticeMapper noticeMapper;

    @Operation(summary = "查询已发布公告列表", description = "分页查询已发布的公告，按置顶和发布时间排序")
    @GetMapping
    public R<Page<SysNotice>> getPublishedNotices(
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();

        // 仅查询已发布的公告
        wrapper.eq(SysNotice::getStatus, 1);

        // 按置顶排序（置顶的在前），然后按发布时间降序
        wrapper.orderByDesc(SysNotice::getIsTop)
               .orderByDesc(SysNotice::getTopOrder)
               .orderByDesc(SysNotice::getPublishTime);

        Page<SysNotice> page = new Page<>(pageNum, pageSize);
        Page<SysNotice> result = noticeMapper.selectPage(page, wrapper);

        return R.ok(result);
    }

    @Operation(summary = "查询公告详情", description = "根据ID查询公告详细信息")
    @GetMapping("/{noticeId}")
    public R<SysNotice> getNoticeDetail(
            @Parameter(description = "公告ID") @PathVariable Long noticeId
    ) {
        SysNotice notice = noticeMapper.selectById(noticeId);

        if (notice == null) {
            return R.fail("公告不存在");
        }

        // 仅返回已发布的公告
        if (notice.getStatus() != 1) {
            return R.fail("公告未发布");
        }

        return R.ok(notice);
    }

    @Operation(summary = "上报公告阅读", description = "增加公告阅读次数")
    @PostMapping("/{noticeId}/read")
    public R<Void> reportNoticeRead(
            @Parameter(description = "公告ID") @PathVariable Long noticeId
    ) {
        SysNotice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            return R.fail("公告不存在");
        }
        if (notice.getStatus() != 1) {
            return R.fail("公告未发布");
        }
        noticeMapper.increaseReadCount(noticeId);
        return R.ok();
    }

    @Operation(summary = "查询最新公告", description = "查询最新的N条已发布公告（不分页）")
    @GetMapping("/latest")
    public R<List<SysNotice>> getLatestNotices(
            @Parameter(description = "查询数量") @RequestParam(value = "limit", defaultValue = "5") Integer limit
    ) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();

        // 仅查询已发布的公告
        wrapper.eq(SysNotice::getStatus, 1);

        // 按置顶和发布时间排序，限制数量
        wrapper.orderByDesc(SysNotice::getIsTop)
               .orderByDesc(SysNotice::getTopOrder)
               .orderByDesc(SysNotice::getPublishTime)
               .last("LIMIT " + limit);

        List<SysNotice> notices = noticeMapper.selectList(wrapper);

        return R.ok(notices);
    }
}
