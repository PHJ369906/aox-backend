package com.aox.infrastructure.payment.client;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付客户端抽象接口
 *
 * @author Aox Team
 * @since 2026-01-31
 */
public interface PaymentClient {

    /**
     * 创建支付订单
     *
     * @param orderNo 订单号
     * @param amount  金额
     * @param subject 订单标题
     * @param body    订单描述
     * @return 支付参数
     */
    Map<String, String> createOrder(String orderNo, BigDecimal amount, String subject, String body);

    /**
     * 查询订单状态
     *
     * @param orderNo 订单号
     * @return 订单状态
     */
    String queryOrder(String orderNo);

    /**
     * 退款
     *
     * @param orderNo      订单号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return 是否退款成功
     */
    boolean refund(String orderNo, BigDecimal refundAmount, String refundReason);

    /**
     * 关闭订单
     *
     * @param orderNo 订单号
     * @return 是否关闭成功
     */
    boolean closeOrder(String orderNo);

    /**
     * 验证回调签名
     *
     * @param params 回调参数
     * @return 签名是否有效
     */
    boolean verifySign(Map<String, String> params);
}
