package com.aox.infrastructure.sms.controller;

import com.aox.common.core.domain.R;
import com.aox.infrastructure.sms.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 短信服务控制器
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Tag(name = "短信服务", description = "短信发送和验证接口")
@RestController
@RequestMapping("/api/v1/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @Operation(summary = "发送验证码", description = "发送短信验证码到指定手机号")
    @Parameter(name = "phone", description = "手机号", example = "13800138000")
    @PostMapping("/code/send")
    public R<String> sendCode(@RequestParam String phone) {
        smsService.sendCode(phone);
        return R.ok("验证码发送成功");
    }

    @Operation(summary = "验证验证码", description = "验证短信验证码是否正确")
    @PostMapping("/code/verify")
    public R<Boolean> verifyCode(@RequestParam String phone, @RequestParam String code) {
        boolean result = smsService.verifyCode(phone, code);
        return R.ok(result);
    }

    @Operation(summary = "发送通知短信", description = "发送通知短信到指定手机号")
    @PostMapping("/notice/send")
    public R<String> sendNotice(@RequestParam String phone, @RequestParam String content) {
        smsService.sendNotice(phone, content);
        return R.ok("通知短信发送成功");
    }
}
