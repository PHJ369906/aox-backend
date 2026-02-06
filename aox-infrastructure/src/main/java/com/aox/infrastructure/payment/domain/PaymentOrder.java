package com.aox.infrastructure.payment.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Data
@TableName("sys_payment_order")
public class PaymentOrder {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 业务订单号
     */
    private String orderNo;

    /**
     * 支付流水号
     */
    private String paymentNo;

    /**
     * 支付类型: wechat / alipay
     */
    private String paymentType;

    /**
     * 支付方式: jsapi / h5 / native / app
     */
    private String paymentMethod;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 币种
     */
    private String currency = "CNY";

    /**
     * 订单标题
     */
    private String subject;

    /**
     * 订单描述
     */
    private String body;

    /**
     * 支付状态: 0待支付 1已支付 2已退款 3已关闭
     */
    private Integer status;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 第三方交易号
     */
    private String transactionId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
