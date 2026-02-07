package com.aox.miniapp.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 小程序地址列表项
 */
@Data
@Builder
public class MiniappAddressVO {

    private Long id;

    private String name;

    private String phone;

    private String region;

    private String detail;

    private Boolean isDefault;
}
