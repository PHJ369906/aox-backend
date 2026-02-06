package com.aox.common.security.context;

import com.aox.common.security.domain.LoginUser;

/**
 * 安全上下文持有者
 * 用于存储和获取当前线程的登录用户信息
 *
 * @author Aox Team
 */
public class SecurityContextHolder {

    private static final ThreadLocal<LoginUser> CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前登录用户
     *
     * @param loginUser 登录用户
     */
    public static void setLoginUser(LoginUser loginUser) {
        CONTEXT.set(loginUser);
    }

    /**
     * 获取当前登录用户
     *
     * @return 登录用户
     */
    public static LoginUser getLoginUser() {
        return CONTEXT.get();
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUserId() : null;
    }

    /**
     * 获取当前用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUsername() : null;
    }

    /**
     * 获取当前租户ID
     *
     * @return 租户ID
     */
    public static Long getTenantId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getTenantId() : null;
    }

    /**
     * 判断当前用户是否已认证
     *
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        return getLoginUser() != null;
    }

    /**
     * 判断当前用户是否拥有指定权限
     *
     * @param permission 权限标识
     * @return 是否拥有权限
     */
    public static boolean hasPermission(String permission) {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null || loginUser.getPermissions() == null) {
            return false;
        }
        // 超级管理员拥有所有权限
        if (loginUser.getRoles() != null && loginUser.getRoles().contains("ROLE_ADMIN")) {
            return true;
        }
        return loginUser.getPermissions().contains(permission);
    }

    /**
     * 判断当前用户是否拥有指定角色
     *
     * @param role 角色标识
     * @return 是否拥有角色
     */
    public static boolean hasRole(String role) {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null || loginUser.getRoles() == null) {
            return false;
        }
        return loginUser.getRoles().contains(role);
    }

    /**
     * 清除当前登录用户信息
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
