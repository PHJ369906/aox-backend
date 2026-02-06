package com.aox.system.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求
 *
 * @author Aox Team
 */
@Data
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 验证码Key
     */
    private String captchaKey;
}
