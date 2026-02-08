package com.aox.system.service.impl;

import com.aox.common.redis.service.RedisService;
import com.aox.system.domain.*;
import com.aox.system.mapper.*;
import com.aox.system.service.MenuService;
import com.aox.system.service.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限管理服务实现
 * 专注于权限关联关系的管理（用户-角色、角色-菜单）
 *
 * 优化说明：
 * 1. 只注入关联关系的Mapper（UserRoleMapper、RoleMenuMapper）
 * 2. 不注入其他业务实体的Mapper（RoleMapper、MenuMapper、PostMapper）
 * 3. 需要其他业务逻辑时，通过调用对应的Service实现
 * 4. 符合单一职责原则：专注于权限关联关系管理
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    // 只注入关联关系的Mapper
    private final UserRoleMapper userRoleMapper;
    private final RoleMenuMapper roleMenuMapper;

    // 注入其他Service来处理复杂业务逻辑
    private final MenuService menuService;

    // Redis服务用于缓存
    private final RedisService redisService;

    private static final String USER_PERMISSIONS_KEY = "user:permissions:";
    private static final String USER_ROLES_KEY = "user:roles:";

    /**
     * 缓存过期时间：2小时
     */
    private static final Long CACHE_EXPIRE_TIME = 7200L;

    // ==================== 用户角色关联 ====================

    @Override
    public List<SysRole> getUserRoles(Long userId) {
        return userRoleMapper.selectRolesByUserId(userId);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = userRoleMapper.selectList(wrapper);
        return userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        // 删除用户现有的角色
        LambdaQueryWrapper<SysUserRole> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysUserRole::getUserId, userId);
        userRoleMapper.delete(deleteWrapper);

        // 分配新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            roleIds.forEach(roleId -> {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            });
        }

        log.info("为用户 {} 分配角色成功，角色数量: {}", userId, roleIds != null ? roleIds.size() : 0);
        clearUserPermissionCache(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAssignRolesToUsers(List<Long> userIds, List<Long> roleIds) {
        if (userIds == null || userIds.isEmpty()) {
            log.warn("批量分配角色失败：用户ID列表为空");
            return;
        }

        userIds.forEach(userId -> assignRolesToUser(userId, roleIds));
        log.info("批量为 {} 个用户分配角色成功", userIds.size());
    }

    // ==================== 角色菜单关联 ====================

    @Override
    public List<SysMenu> getRoleMenus(Long roleId) {
        return roleMenuMapper.selectMenusByRoleId(roleId);
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);
        return roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenusToRole(Long roleId, List<Long> menuIds) {
        List<Long> affectedUserIds = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, roleId)
        ).stream().map(SysUserRole::getUserId).distinct().collect(Collectors.toList());

        // 删除角色现有的菜单权限
        LambdaQueryWrapper<SysRoleMenu> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysRoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(deleteWrapper);

        // 分配新菜单权限
        if (menuIds != null && !menuIds.isEmpty()) {
            menuIds.forEach(menuId -> {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            });
        }

        log.info("为角色 {} 分配菜单权限成功，菜单数量: {}", roleId, menuIds != null ? menuIds.size() : 0);
        affectedUserIds.forEach(this::clearUserPermissionCache);
    }

    @Override
    public List<SysMenu> getUserMenus(Long userId) {
        return userRoleMapper.selectMenusByUserId(userId);
    }

    @Override
    public List<SysMenu> getUserMenuTree(Long userId) {
        List<SysMenu> allMenus = getUserMenus(userId);
        List<SysMenu> menuList = allMenus.stream()
                .filter(menu -> menu.getMenuType() == 1 || menu.getMenuType() == 2)
                .filter(menu -> menu.getStatus() == null || menu.getStatus() == 0)
                .filter(menu -> menu.getVisible() == null || menu.getVisible() == 1)
                .collect(Collectors.toList());

        return menuService.buildMenuTree(menuList, 0L);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        List<SysMenu> menus = getUserMenus(userId);
        return menus.stream()
                .filter(menu -> menu.getPermission() != null && !menu.getPermission().isEmpty())
                .map(SysMenu::getPermission)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasPermission(Long userId, String permission) {
        if (userId == null || permission == null || permission.isEmpty()) {
            return false;
        }

        Set<String> userPermissions = getPermissionCodesByUserId(userId);
        return userPermissions.contains(permission);
    }

    @Override
    public Set<String> getPermissionCodesByUserId(Long userId) {
        // 先从缓存获取
        String cacheKey = USER_PERMISSIONS_KEY + userId;
        Object cached = redisService.get(cacheKey);
        if (cached instanceof List) {
            return new HashSet<>((List<String>) cached);
        }

        // 从数据库查询（使用getUserPermissions方法）
        List<String> permissions = getUserPermissions(userId);
        Set<String> permissionSet = new HashSet<>(permissions);

        // 缓存结果
        redisService.set(cacheKey, new ArrayList<>(permissionSet), CACHE_EXPIRE_TIME);

        return permissionSet;
    }

    @Override
    public Set<String> getRoleCodesByUserId(Long userId) {
        // 先从缓存获取
        String cacheKey = USER_ROLES_KEY + userId;
        Object cached = redisService.get(cacheKey);
        if (cached instanceof List) {
            return new HashSet<>((List<String>) cached);
        }

        // 从数据库查询
        List<SysRole> roles = getUserRoles(userId);
        Set<String> roleSet = roles.stream()
                .map(SysRole::getRoleCode)
                .collect(Collectors.toSet());

        // 缓存结果
        redisService.set(cacheKey, new ArrayList<>(roleSet), CACHE_EXPIRE_TIME);

        return roleSet;
    }

    @Override
    public void cacheUserPermissions(Long userId) {
        // 缓存权限
        Set<String> permissions = getPermissionCodesByUserId(userId);
        redisService.set(USER_PERMISSIONS_KEY + userId, new ArrayList<>(permissions), CACHE_EXPIRE_TIME);

        // 缓存角色
        Set<String> roles = getRoleCodesByUserId(userId);
        redisService.set(USER_ROLES_KEY + userId, new ArrayList<>(roles), CACHE_EXPIRE_TIME);

        log.debug("已缓存用户 {} 的权限和角色", userId);
    }

    @Override
    public void clearUserPermissionCache(Long userId) {
        redisService.del(USER_PERMISSIONS_KEY + userId);
        redisService.del(USER_ROLES_KEY + userId);
        log.debug("已清除用户 {} 的权限缓存", userId);
    }
}
