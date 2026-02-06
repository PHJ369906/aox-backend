package com.aox.system.service;

import com.aox.system.domain.SysMenu;
import com.aox.system.domain.SysRole;

import java.util.List;
import java.util.Set;

/**
 * 权限管理服务接口
 * 专注于权限关联关系的管理（用户-角色、角色-菜单）
 *
 * @author Aox Team
 */
public interface PermissionService {

    // ==================== 用户角色关联 ====================

    /**
     * 获取用户的角色列表
     */
    List<SysRole> getUserRoles(Long userId);

    /**
     * 获取用户的角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 为用户分配角色
     */
    void assignRolesToUser(Long userId, List<Long> roleIds);

    /**
     * 批量为用户分配角色
     */
    void batchAssignRolesToUsers(List<Long> userIds, List<Long> roleIds);

    // ==================== 角色菜单关联 ====================

    /**
     * 获取角色的菜单列表
     */
    List<SysMenu> getRoleMenus(Long roleId);

    /**
     * 获取角色的菜单ID列表
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * 为角色分配菜单权限
     */
    void assignMenusToRole(Long roleId, List<Long> menuIds);

    /**
     * 获取用户的菜单权限（通过角色）
     */
    List<SysMenu> getUserMenus(Long userId);

    /**
     * 获取用户的菜单树（用于动态菜单生成）
     */
    List<SysMenu> getUserMenuTree(Long userId);

    /**
     * 获取用户的权限标识列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 检查用户是否有指定权限
     */
    boolean hasPermission(Long userId, String permission);

    /**
     * 获取用户的权限代码集合（用于权限验证）
     */
    Set<String> getPermissionCodesByUserId(Long userId);

    /**
     * 获取用户的角色代码集合（用于角色验证）
     */
    Set<String> getRoleCodesByUserId(Long userId);

    /**
     * 缓存用户权限信息
     */
    void cacheUserPermissions(Long userId);

    /**
     * 清除用户权限缓存
     */
    void clearUserPermissionCache(Long userId);
}
