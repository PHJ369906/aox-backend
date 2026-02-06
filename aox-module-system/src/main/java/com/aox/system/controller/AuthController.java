package com.aox.system.controller;

import cn.hutool.core.util.StrUtil;
import com.aox.common.core.domain.R;
import com.aox.common.core.enums.ErrorCode;
import com.aox.common.log.annotation.Log;
import com.aox.common.security.context.SecurityContextHolder;
import com.aox.system.domain.request.LoginRequest;
import com.aox.system.domain.vo.LoginResponse;
import com.aox.system.domain.vo.UserVO;
import com.aox.system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author Aox Team
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录、登出、用户信息")
public class AuthController {

    private final AuthService authService;

    /**
     * 登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录")
    @Log(module = "认证管理", operation = "用户登录")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = getIpAddress(httpRequest);
        LoginResponse response = authService.login(request, ip);
        return R.ok(response);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    @Log(module = "认证管理", operation = "用户登出")
    public R<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        authService.logout(token);
        return R.ok();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current-user")
    @Operation(summary = "获取当前用户信息")
    public R<UserVO> getCurrentUser() {
        Long userId = SecurityContextHolder.getUserId();
        if (userId == null) {
            return R.fail(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage());
        }
        UserVO user = authService.getCurrentUser(userId);
        return R.ok(user);
    }

    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        return ip;
    }
}
