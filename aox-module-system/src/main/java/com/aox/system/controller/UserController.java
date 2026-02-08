package com.aox.system.controller;

import com.aox.common.core.domain.PageResult;
import com.aox.common.core.domain.R;
import com.aox.common.log.annotation.Log;
import com.aox.common.security.annotation.RequirePermission;
import com.aox.system.domain.request.UserCreateRequest;
import com.aox.system.domain.request.UserQueryRequest;
import com.aox.system.domain.request.UserUpdateRequest;
import com.aox.system.domain.vo.UserVO;
import com.aox.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 *
 * @author Aox Team
 */
@RestController
@RequestMapping("/api/v1/system/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户增删改查")
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping
    @Operation(summary = "分页查询用户列表")
    @Log(module = "用户管理", operation = "查询用户列表", saveResponseData = false)
    @RequirePermission("system:user:list")
    public R<PageResult<UserVO>> list(UserQueryRequest request) {
        PageResult<UserVO> pageResult = userService.listUsers(request);
        return R.ok(pageResult);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{userId}")
    @Operation(summary = "根据ID查询用户")
    @RequirePermission("system:user:list")
    public R<UserVO> getById(@PathVariable Long userId) {
        UserVO vo = userService.getUserById(userId);
        return R.ok(vo);
    }

    /**
     * 新增用户
     */
    @PostMapping
    @Operation(summary = "新增用户")
    @Log(module = "用户管理", operation = "新增用户")
    @RequirePermission("system:user:add")
    public R<Void> add(@Valid @RequestBody UserCreateRequest request) {
        userService.createUser(request);
        return R.ok();
    }

    /**
     * 更新用户
     */
    @PutMapping("/{userId}")
    @Operation(summary = "更新用户")
    @Log(module = "用户管理", operation = "更新用户")
    @RequirePermission("system:user:edit")
    public R<Void> update(@PathVariable Long userId, @Valid @RequestBody UserUpdateRequest request) {
        userService.updateUser(userId, request);
        return R.ok();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户")
    @Log(module = "用户管理", operation = "删除用户")
    @RequirePermission("system:user:delete")
    public R<Void> delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return R.ok();
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{userId}/status")
    @Operation(summary = "更新用户状态")
    @Log(module = "用户管理", operation = "更新用户状态")
    @RequirePermission("system:user:edit")
    public R<Void> updateStatus(@PathVariable Long userId, @RequestParam Integer status) {
        userService.updateUserStatus(userId, status);
        return R.ok();
    }

    /**
     * 重置密码
     */
    @PostMapping("/{userId}/reset-password")
    @Operation(summary = "重置密码")
    @Log(module = "用户管理", operation = "重置密码")
    @RequirePermission("system:user:edit")
    public R<Void> resetPassword(@PathVariable Long userId, @RequestParam String newPassword) {
        userService.resetPassword(userId, newPassword);
        return R.ok();
    }
}
