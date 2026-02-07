package com.aox.miniapp.domain.entity;

import com.aox.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 小程序业务订单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_order")
public class BizOrder extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long orderId;

    private String orderNo;

    private Long userId;

    private String orderTitle;

    private BigDecimal orderAmount;

    /**
     * 订单状态: pay/ship/receive/refund/completed
     */
    private String orderStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    private Long tenantId;
}
