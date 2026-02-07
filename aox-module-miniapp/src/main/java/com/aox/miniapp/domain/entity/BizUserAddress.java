package com.aox.miniapp.domain.entity;

import com.aox.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 小程序用户地址
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_user_address")
public class BizUserAddress extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long addressId;

    private Long userId;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private Integer isDefault;

    private Long tenantId;
}
