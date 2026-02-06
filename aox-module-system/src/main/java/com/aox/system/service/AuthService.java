package com.aox.system.service;

import com.aox.system.domain.request.LoginRequest;
import com.aox.system.domain.vo.LoginResponse;
import com.aox.system.domain.vo.UserVO;

/**
 * 认证服务接口
 *
 * @author Aox Team
 */
public interface AuthService {

    /**
     * 登录
     */
    LoginResponse login(LoginRequest request, String ip, String userAgent);

    /**
     * 登录（兼容旧方法签名）
     */
    LoginResponse login(LoginRequest request, String ip);

    /**
     * 登出
     */
    void logout(String token);

    /**
     * 获取当前用户信息
     */
    UserVO getCurrentUser(Long userId);
}
