package com.aox.infrastructure.payment.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付统计数据VO
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Data
@Builder
public class PaymentStatisticsVO {

    /**
     * 订单总数
     */
    private Long totalOrders;

    /**
     * 今日订单数
     */
    private Long todayOrders;

    /**
     * 订单总金额（元）
     */
    private BigDecimal totalAmount;

    /**
     * 今日订单金额（元）
     */
    private BigDecimal todayAmount;

    /**
     * 成功订单数（已支付）
     */
    private Long successOrders;

    /**
     * 待支付订单数
     */
    private Long pendingOrders;

    /**
     * 已退款订单数
     */
    private Long refundOrders;

    /**
     * 微信支付订单数
     */
    private Long wechatOrders;

    /**
     * 支付宝订单数
     */
    private Long alipayOrders;

    /**
     * 支付成功率（%）
     */
    private Double successRate;
}
