package com.aox.miniapp.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 账号密码登录DTO
 *
 * @author Aox Team
 */
@Data
public class PasswordLoginDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
