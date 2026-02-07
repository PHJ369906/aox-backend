package com.aox.miniapp.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增地址请求
 */
@Data
public class CreateAddressDTO {

    @NotBlank(message = "收件人不能为空")
    @Size(max = 30, message = "收件人长度不能超过30")
    private String receiverName;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String receiverPhone;

    @NotBlank(message = "省份不能为空")
    @Size(max = 50, message = "省份长度不能超过50")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Size(max = 50, message = "城市长度不能超过50")
    private String city;

    @NotBlank(message = "区县不能为空")
    @Size(max = 50, message = "区县长度不能超过50")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址长度不能超过200")
    private String detailAddress;

    private Boolean isDefault;
}
