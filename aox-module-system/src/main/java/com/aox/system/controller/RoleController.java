package com.aox.system.controller;

import com.aox.common.core.domain.PageResult;
import com.aox.common.core.domain.R;
import com.aox.common.log.annotation.Log;
import com.aox.common.security.annotation.RequirePermission;
import com.aox.system.domain.SysRole;
import com.aox.system.domain.request.RoleCreateRequest;
import com.aox.system.domain.request.RoleQueryRequest;
import com.aox.system.domain.request.RoleUpdateRequest;
import com.aox.system.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author Aox Team
 */
@RestController
@RequestMapping("/api/v1/system/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色增删改查")
public class RoleController {

    private final RoleService roleService;

    /**
     * 分页查询角色列表
     */
    @GetMapping
    @Operation(summary = "分页查询角色列表")
    @Log(module = "角色管理", operation = "查询角色列表", saveResponseData = false)
    @RequirePermission("system:role:list")
    public R<PageResult<SysRole>> list(RoleQueryRequest request) {
        PageResult<SysRole> pageResult = roleService.listRoles(request);
        return R.ok(pageResult);
    }

    /**
     * 根据ID查询角色
     */
    @GetMapping("/{roleId}")
    @Operation(summary = "根据ID查询角色")
    @RequirePermission("system:role:list")
    public R<SysRole> getById(@PathVariable Long roleId) {
        SysRole role = roleService.getRoleById(roleId);
        return R.ok(role);
    }

    /**
     * 新增角色
     */
    @PostMapping
    @Operation(summary = "新增角色")
    @Log(module = "角色管理", operation = "新增角色")
    @RequirePermission("system:role:add")
    public R<Void> add(@Valid @RequestBody RoleCreateRequest request) {
        roleService.createRole(request);
        return R.ok();
    }

    /**
     * 更新角色
     */
    @PutMapping("/{roleId}")
    @Operation(summary = "更新角色")
    @Log(module = "角色管理", operation = "更新角色")
    @RequirePermission("system:role:edit")
    public R<Void> update(@PathVariable Long roleId, @Valid @RequestBody RoleUpdateRequest request) {
        roleService.updateRole(roleId, request);
        return R.ok();
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色")
    @Log(module = "角色管理", operation = "删除角色")
    @RequirePermission("system:role:delete")
    public R<Void> delete(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return R.ok();
    }

    /**
     * 分配权限
     */
    @PutMapping("/{roleId}/permissions")
    @Operation(summary = "分配权限")
    @Log(module = "角色管理", operation = "分配权限")
    @RequirePermission("system:role:edit")
    public R<Void> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return R.ok();
    }

    /**
     * 获取角色已分配的权限ID列表
     */
    @GetMapping("/{roleId}/permissions")
    @Operation(summary = "获取角色已分配的权限ID列表")
    @RequirePermission("system:role:list")
    public R<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        List<Long> permissionIds = roleService.getRolePermissions(roleId);
        return R.ok(permissionIds);
    }
}
