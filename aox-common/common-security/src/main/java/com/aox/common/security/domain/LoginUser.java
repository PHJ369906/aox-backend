package com.aox.common.security.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 登录用户信息
 *
 * @author Aox Team
 */
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 用户状态 0正常 1禁用
     */
    private Integer status;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 用户类型 (admin/miniapp)
     */
    private String userType;

    /**
     * 权限列表
     */
    private Set<String> permissions;

    /**
     * 角色列表
     */
    private Set<String> roles;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    public LoginUser() {
    }

    public LoginUser(Long userId, String username, String password, String nickname, String avatar,
                     Integer status, Long deptId, Long tenantId, String userType,
                     Set<String> permissions, Set<String> roles, String loginIp,
                     Long loginTime, Long expireTime) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.avatar = avatar;
        this.status = status;
        this.deptId = deptId;
        this.tenantId = tenantId;
        this.userType = userType;
        this.permissions = permissions;
        this.roles = roles;
        this.loginIp = loginIp;
        this.loginTime = loginTime;
        this.expireTime = expireTime;
    }

    public static LoginUserBuilder builder() {
        return new LoginUserBuilder();
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public String getLoginIp() { return loginIp; }
    public void setLoginIp(String loginIp) { this.loginIp = loginIp; }

    public Long getLoginTime() { return loginTime; }
    public void setLoginTime(Long loginTime) { this.loginTime = loginTime; }

    public Long getExpireTime() { return expireTime; }
    public void setExpireTime(Long expireTime) { this.expireTime = expireTime; }

    // Builder class
    public static class LoginUserBuilder {
        private Long userId;
        private String username;
        private String password;
        private String nickname;
        private String avatar;
        private Integer status;
        private Long deptId;
        private Long tenantId;
        private String userType;
        private Set<String> permissions;
        private Set<String> roles;
        private String loginIp;
        private Long loginTime;
        private Long expireTime;

        public LoginUserBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public LoginUserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public LoginUserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public LoginUserBuilder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public LoginUserBuilder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public LoginUserBuilder status(Integer status) {
            this.status = status;
            return this;
        }

        public LoginUserBuilder deptId(Long deptId) {
            this.deptId = deptId;
            return this;
        }

        public LoginUserBuilder tenantId(Long tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public LoginUserBuilder userType(String userType) {
            this.userType = userType;
            return this;
        }

        public LoginUserBuilder permissions(Set<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public LoginUserBuilder roles(Set<String> roles) {
            this.roles = roles;
            return this;
        }

        public LoginUserBuilder loginIp(String loginIp) {
            this.loginIp = loginIp;
            return this;
        }

        public LoginUserBuilder loginTime(Long loginTime) {
            this.loginTime = loginTime;
            return this;
        }

        public LoginUserBuilder expireTime(Long expireTime) {
            this.expireTime = expireTime;
            return this;
        }

        public LoginUser build() {
            return new LoginUser(userId, username, password, nickname, avatar, status, deptId,
                    tenantId, userType, permissions, roles, loginIp, loginTime, expireTime);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将权限和角色合并为 GrantedAuthority
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status != null && this.status == 0;
    }
}
