package com.aox.miniapp.controller;

import com.aox.common.core.domain.R;
import com.aox.common.security.context.SecurityContextHolder;
import com.aox.miniapp.domain.dto.BindPhoneDTO;
import com.aox.miniapp.domain.dto.UpdateUserInfoDTO;
import com.aox.miniapp.domain.vo.UserInfoVO;
import com.aox.miniapp.service.MiniappUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 小程序用户控制器
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Tag(name = "小程序用户", description = "用户信息管理接口")
@RestController
@RequestMapping("/api/v1/miniapp/user")
@RequiredArgsConstructor
public class MiniappUserController {

    private final MiniappUserService userService;

    @Operation(summary = "获取用户信息", description = "获取当前登录用户信息")
    @GetMapping("/info")
    public R<UserInfoVO> getUserInfo() {
        Long userId = SecurityContextHolder.getUserId();
        UserInfoVO userInfo = userService.getUserInfo(userId);
        return R.ok(userInfo);
    }

    @Operation(summary = "更新用户信息", description = "更新昵称、头像、性别等信息")
    @PostMapping("/update")
    public R<Void> updateUserInfo(@Valid @RequestBody UpdateUserInfoDTO dto) {
        Long userId = SecurityContextHolder.getUserId();
        userService.updateUserInfo(userId, dto);
        return R.ok();
    }

    @Operation(summary = "绑定手机号", description = "绑定或更换手机号")
    @PostMapping("/bind-phone")
    public R<Void> bindPhone(@Valid @RequestBody BindPhoneDTO dto) {
        Long userId = SecurityContextHolder.getUserId();
        userService.bindPhone(userId, dto.getPhone(), dto.getCode());
        return R.ok();
    }
}
