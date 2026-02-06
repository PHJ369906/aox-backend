package com.aox.infrastructure.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付配置属性
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Data
@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

    /**
     * 微信支付配置
     */
    private WechatPayConfig wechat = new WechatPayConfig();

    /**
     * 支付宝配置
     */
    private AlipayConfig alipay = new AlipayConfig();

    /**
     * 微信支付配置
     */
    @Data
    public static class WechatPayConfig {
        /**
         * 应用ID
         */
        private String appId;

        /**
         * 商户号
         */
        private String mchId;

        /**
         * API密钥
         */
        private String apiKey;

        /**
         * API证书路径
         */
        private String certPath;

        /**
         * 支付回调地址
         */
        private String notifyUrl;

        /**
         * 退款回调地址
         */
        private String refundNotifyUrl;
    }

    /**
     * 支付宝配置
     */
    @Data
    public static class AlipayConfig {
        /**
         * 应用ID
         */
        private String appId;

        /**
         * 应用私钥
         */
        private String privateKey;

        /**
         * 支付宝公钥
         */
        private String publicKey;

        /**
         * 网关地址
         */
        private String gatewayUrl = "https://openapi.alipay.com/gateway.do";

        /**
         * 支付回调地址
         */
        private String notifyUrl;

        /**
         * 前端回跳地址
         */
        private String returnUrl;
    }
}
