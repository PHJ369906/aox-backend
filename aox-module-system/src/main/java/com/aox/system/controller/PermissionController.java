package com.aox.system.controller;

import com.aox.common.core.domain.R;
import com.aox.common.log.annotation.Log;
import com.aox.common.security.annotation.RequirePermission;
import com.aox.system.domain.*;
import com.aox.system.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 *
 * @author Aox Team
 */
@RestController
@RequestMapping("/api/v1/system/permission")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "用户角色、角色菜单权限管理")
public class PermissionController {

    private final PermissionService permissionService;

    // ==================== 用户角色管理 ====================

    /**
     * 获取用户的角色列表
     */
    @GetMapping("/user/{userId}/roles")
    @Operation(summary = "获取用户的角色列表")
    @RequirePermission("system:user:list")
    public R<List<SysRole>> getUserRoles(@PathVariable("userId") Long userId) {
        List<SysRole> roles = permissionService.getUserRoles(userId);
        return R.ok(roles);
    }

    /**
     * 获取用户的角色ID列表
     */
    @GetMapping("/user/{userId}/role-ids")
    @Operation(summary = "获取用户的角色ID列表")
    @RequirePermission("system:user:list")
    public R<List<Long>> getUserRoleIds(@PathVariable("userId") Long userId) {
        List<Long> roleIds = permissionService.getUserRoleIds(userId);
        return R.ok(roleIds);
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/user/{userId}/roles")
    @Operation(summary = "为用户分配角色")
    @Log(module = "权限管理", operation = "分配用户角色")
    @RequirePermission("system:user:edit")
    public R<Void> assignRolesToUser(@PathVariable("userId") Long userId, @RequestBody List<Long> roleIds) {
        permissionService.assignRolesToUser(userId, roleIds);
        return R.ok();
    }

    /**
     * 批量为用户分配角色
     */
    @PostMapping("/users/roles/batch")
    @Operation(summary = "批量为用户分配角色")
    @Log(module = "权限管理", operation = "批量分配用户角色")
    @RequirePermission("system:user:edit")
    public R<Void> batchAssignRolesToUsers(@RequestBody UserRoleAssignRequest request) {
        permissionService.batchAssignRolesToUsers(request.getUserIds(), request.getRoleIds());
        return R.ok();
    }

    // ==================== 角色菜单管理 ====================

    /**
     * 获取角色的菜单列表
     */
    @GetMapping("/role/{roleId}/menus")
    @Operation(summary = "获取角色的菜单列表")
    @RequirePermission("system:role:list")
    public R<List<SysMenu>> getRoleMenus(@PathVariable("roleId") Long roleId) {
        List<SysMenu> menus = permissionService.getRoleMenus(roleId);
        return R.ok(menus);
    }

    /**
     * 获取角色的菜单ID列表
     */
    @GetMapping("/role/{roleId}/menu-ids")
    @Operation(summary = "获取角色的菜单ID列表")
    @RequirePermission("system:role:list")
    public R<List<Long>> getRoleMenuIds(@PathVariable("roleId") Long roleId) {
        List<Long> menuIds = permissionService.getRoleMenuIds(roleId);
        return R.ok(menuIds);
    }

    /**
     * 为角色分配菜单权限
     */
    @PostMapping("/role/{roleId}/menus")
    @Operation(summary = "为角色分配菜单权限")
    @Log(module = "权限管理", operation = "分配角色菜单")
    @RequirePermission("system:role:edit")
    public R<Void> assignMenusToRole(@PathVariable("roleId") Long roleId, @RequestBody List<Long> menuIds) {
        permissionService.assignMenusToRole(roleId, menuIds);
        return R.ok();
    }

    // ==================== 用户菜单权限 ====================

    /**
     * 获取用户的菜单权限（通过角色）
     */
    @GetMapping("/user/{userId}/menus")
    @Operation(summary = "获取用户的菜单权限")
    @RequirePermission("system:user:list")
    public R<List<SysMenu>> getUserMenus(@PathVariable("userId") Long userId) {
        List<SysMenu> menus = permissionService.getUserMenus(userId);
        return R.ok(menus);
    }

    /**
     * 获取用户的菜单树（用于动态菜单生成）
     */
    @GetMapping("/user/{userId}/menu-tree")
    @Operation(summary = "获取用户的菜单树")
    @RequirePermission("system:user:list")
    public R<List<SysMenu>> getUserMenuTree(@PathVariable("userId") Long userId) {
        List<SysMenu> menuTree = permissionService.getUserMenuTree(userId);
        return R.ok(menuTree);
    }

    /**
     * 获取用户的权限标识列表
     */
    @GetMapping("/user/{userId}/permissions")
    @Operation(summary = "获取用户的权限标识列表")
    @RequirePermission("system:user:list")
    public R<List<String>> getUserPermissions(@PathVariable("userId") Long userId) {
        List<String> permissions = permissionService.getUserPermissions(userId);
        return R.ok(permissions);
    }

    /**
     * 检查用户是否有指定权限
     */
    @GetMapping("/user/{userId}/has-permission")
    @Operation(summary = "检查用户是否有指定权限")
    @RequirePermission("system:user:list")
    public R<Boolean> hasPermission(@PathVariable("userId") Long userId, @RequestParam String permission) {
        boolean hasPermission = permissionService.hasPermission(userId, permission);
        return R.ok(hasPermission);
    }

    /**
     * 用户角色分配请求对象
     */
    @Data
    public static class UserRoleAssignRequest {
        private List<Long> userIds;
        private List<Long> roleIds;
    }
}
