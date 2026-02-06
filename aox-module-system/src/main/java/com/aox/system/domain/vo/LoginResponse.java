package com.aox.system.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 登录响应
 *
 * @author Aox Team
 */
@Data
public class LoginResponse {

    /**
     * Token
     */
    private String token;

    /**
     * 刷新Token
     */
    private String refreshToken;

    /**
     * 用户信息
     */
    private UserVO user;

    /**
     * 权限列表
     */
    private List<String> permissions;

    /**
     * 角色列表
     */
    private List<String> roles;
}
