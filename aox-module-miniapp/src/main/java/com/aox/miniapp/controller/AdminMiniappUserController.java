package com.aox.miniapp.controller;

import com.aox.common.core.domain.R;
import com.aox.common.security.annotation.RequirePermission;
import com.aox.miniapp.domain.dto.MiniappUserQueryDTO;
import com.aox.miniapp.domain.vo.MiniappUserVO;
import com.aox.miniapp.domain.vo.UserStatisticsVO;
import com.aox.miniapp.service.AdminMiniappUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理-小程序用户控制器
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Tag(name = "后台管理-小程序用户", description = "小程序用户管理接口")
@RestController
@RequestMapping("/api/v1/admin/miniapp/users")
@RequiredArgsConstructor
public class AdminMiniappUserController {

    private final AdminMiniappUserService adminUserService;

    @Operation(summary = "分页查询用户列表", description = "支持手机号、昵称、状态、日期范围查询")
    @GetMapping
    @RequirePermission("miniapp:user:list")
    public R<Page<MiniappUserVO>> getUserList(MiniappUserQueryDTO query) {
        Page<MiniappUserVO> page = adminUserService.getUserList(query);
        return R.ok(page);
    }

    @Operation(summary = "查询用户详情", description = "根据用户ID查询详细信息")
    @GetMapping("/{userId}")
    @RequirePermission("miniapp:user:detail")
    public R<MiniappUserVO> getUserDetail(
            @Parameter(description = "用户ID") @PathVariable Long userId
    ) {
        MiniappUserVO user = adminUserService.getUserDetail(userId);
        return R.ok(user);
    }

    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    @PutMapping("/{userId}/status")
    @RequirePermission("miniapp:user:update")
    public R<Void> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "状态：0-正常，1-禁用") @RequestParam Integer status
    ) {
        adminUserService.updateUserStatus(userId, status);
        return R.ok();
    }

    @Operation(summary = "查询用户统计数据", description = "获取用户总数、今日新增、活跃用户等统计信息")
    @GetMapping("/statistics")
    @RequirePermission("miniapp:user:list")
    public R<UserStatisticsVO> getUserStatistics() {
        UserStatisticsVO statistics = adminUserService.getUserStatistics();
        return R.ok(statistics);
    }
}
