package com.aox.miniapp.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 微信登录DTO
 *
 * @author Aox Team
 */
@Data
public class WxLoginDTO {

    @NotBlank(message = "微信code不能为空")
    private String code;

    private String encryptedData;

    private String iv;

    private Object userInfo;
}
