package com.aox.infrastructure.payment.client.impl;

import com.aox.common.exception.BusinessException;
import com.aox.infrastructure.payment.client.PaymentClient;
import com.aox.infrastructure.payment.config.PaymentProperties;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝客户端实现
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Slf4j
public class AlipayClient implements PaymentClient {

    private final PaymentProperties.AlipayConfig config;

    public AlipayClient(PaymentProperties.AlipayConfig config) {
        this.config = config;
    }

    @Override
    public Map<String, String> createOrder(String orderNo, BigDecimal amount, String subject, String body) {
        log.info("支付宝 - 创建订单: orderNo={}, amount={}, subject={}", orderNo, amount, subject);

        try {
            // 注意：以下为开发环境模拟实现，生产环境需集成支付宝 SDK
            // 集成步骤：添加支付宝SDK依赖后，参考以下示例代码
            /*
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl(config.getNotifyUrl());
            request.setReturnUrl(config.getReturnUrl());

            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", orderNo);
            bizContent.put("total_amount", amount);
            bizContent.put("subject", subject);
            bizContent.put("body", body);
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
            request.setBizContent(bizContent.toString());

            AlipayClient alipayClient = new DefaultAlipayClient(
                config.getGatewayUrl(),
                config.getAppId(),
                config.getPrivateKey(),
                "json",
                "UTF-8",
                config.getPublicKey(),
                "RSA2"
            );

            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);

            Map<String, String> result = new HashMap<>();
            result.put("form", response.getBody());
            return result;
            */

            // 开发环境模拟返回
            Map<String, String> result = new HashMap<>();
            result.put("form", "<form>支付宝支付表单</form>");

            log.warn("【开发模式】支付订单未实际创建,请配置支付宝SDK");
            return result;

        } catch (Exception e) {
            log.error("支付宝创建订单失败: orderNo={}", orderNo, e);
            throw new BusinessException("创建支付订单失败: " + e.getMessage());
        }
    }

    @Override
    public String queryOrder(String orderNo) {
        log.info("支付宝 - 查询订单: orderNo={}", orderNo);
        log.warn("【开发模式】订单查询功能未实现，返回模拟状态");
        // 开发环境返回模拟状态，生产环境需集成支付宝SDK
        return "WAIT_BUYER_PAY";
    }

    @Override
    public boolean refund(String orderNo, BigDecimal refundAmount, String refundReason) {
        log.info("支付宝 - 退款: orderNo={}, refundAmount={}, reason={}",
                orderNo, refundAmount, refundReason);
        log.warn("【开发模式】退款功能未实现，返回模拟成功");
        // 开发环境返回模拟成功，生产环境需集成支付宝SDK
        return true;
    }

    @Override
    public boolean closeOrder(String orderNo) {
        log.info("支付宝 - 关闭订单: orderNo={}", orderNo);
        log.warn("【开发模式】关闭订单功能未实现，返回模拟成功");
        // 开发环境返回模拟成功，生产环境需集成支付宝SDK
        return true;
    }

    @Override
    public boolean verifySign(Map<String, String> params) {
        log.info("支付宝 - 验证回调签名");

        try {
            // 注意：生产环境需实现真实的签名验证
            // 支付宝签名验证步骤：
            // 1. 获取回调参数中的签名 sign 和签名类型 sign_type
            // 2. 将除 sign 和 sign_type 外的所有参数按字典序排列
            // 3. 拼接参数：key1=value1&key2=value2
            // 4. 使用支付宝公钥验证签名

            /*
            String sign = params.get("sign");
            String signType = params.get("sign_type");

            if (sign == null || signType == null) {
                return false;
            }

            // 构建待验签字符串
            StringBuilder sb = new StringBuilder();
            params.entrySet().stream()
                .filter(entry -> !"sign".equals(entry.getKey())
                              && !"sign_type".equals(entry.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append(entry.getKey()).append("=").append(entry.getValue());
                });

            // 使用支付宝公钥验证签名
            AlipaySignature.rsaCheckV1(
                params,
                config.getPublicKey(),
                "UTF-8",
                signType
            );
            */

            log.warn("【开发模式】签名验证未实现，返回 true");
            return true;

        } catch (Exception e) {
            log.error("支付宝签名验证失败", e);
            return false;
        }
    }
}
