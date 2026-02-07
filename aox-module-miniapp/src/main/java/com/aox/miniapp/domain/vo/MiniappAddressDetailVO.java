package com.aox.miniapp.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 小程序地址详情
 */
@Data
@Builder
public class MiniappAddressDetailVO {

    private Long id;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private Boolean isDefault;
}
