package com.aox.miniapp.controller;

import com.aox.common.core.domain.R;
import com.aox.common.redis.annotation.RateLimiter;
import com.aox.miniapp.domain.dto.PasswordLoginDTO;
import com.aox.miniapp.domain.dto.SendSmsCodeDTO;
import com.aox.miniapp.domain.dto.SmsLoginDTO;
import com.aox.miniapp.domain.dto.WxLoginDTO;
import com.aox.miniapp.domain.vo.LoginVO;
import com.aox.miniapp.service.MiniappAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 小程序认证控制器
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Tag(name = "小程序认证", description = "小程序登录、注册接口")
@RestController
@RequestMapping("/api/v1/miniapp/auth")
@RequiredArgsConstructor
public class MiniappAuthController {

    private final MiniappAuthService authService;

    @Operation(summary = "账号密码登录", description = "使用手机号/用户名+密码登录")
    @PostMapping("/login/password")
    @RateLimiter(key = "miniapp:login:", time = 60, count = 5, limitType = RateLimiter.LimitType.IP, message = "登录请求过于频繁，请1分钟后再试")
    public R<LoginVO> passwordLogin(@Valid @RequestBody PasswordLoginDTO dto) {
        LoginVO result = authService.passwordLogin(dto);
        return R.ok(result);
    }

    @Operation(summary = "短信验证码登录", description = "使用手机号+验证码登录")
    @PostMapping("/login/sms")
    @RateLimiter(key = "miniapp:sms:login:", time = 60, count = 5, limitType = RateLimiter.LimitType.IP, message = "登录请求过于频繁，请1分钟后再试")
    public R<LoginVO> smsLogin(@Valid @RequestBody SmsLoginDTO dto) {
        LoginVO result = authService.smsLogin(dto);
        return R.ok(result);
    }

    @Operation(summary = "微信授权登录", description = "使用微信授权登录")
    @PostMapping("/login/wechat")
    public R<LoginVO> wechatLogin(@Valid @RequestBody WxLoginDTO dto) {
        LoginVO result = authService.wechatLogin(dto);
        return R.ok(result);
    }

    @Operation(summary = "发送短信验证码", description = "发送登录/注册验证码")
    @PostMapping("/sms/send")
    @RateLimiter(key = "miniapp:sms:send:", time = 60, count = 3, limitType = RateLimiter.LimitType.IP, message = "验证码发送过于频繁，请1分钟后再试")
    public R<Void> sendSmsCode(@Valid @RequestBody SendSmsCodeDTO dto) {
        authService.sendSmsCode(dto.getPhone());
        return R.ok();
    }
}
