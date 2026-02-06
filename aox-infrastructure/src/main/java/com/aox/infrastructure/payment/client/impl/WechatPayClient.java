package com.aox.infrastructure.payment.client.impl;

import cn.hutool.json.JSONUtil;
import com.aox.common.exception.BusinessException;
import com.aox.infrastructure.payment.client.PaymentClient;
import com.aox.infrastructure.payment.config.PaymentProperties;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付客户端实现
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Slf4j
public class WechatPayClient implements PaymentClient {

    private final PaymentProperties.WechatPayConfig config;

    public WechatPayClient(PaymentProperties.WechatPayConfig config) {
        this.config = config;
    }

    @Override
    public Map<String, String> createOrder(String orderNo, BigDecimal amount, String subject, String body) {
        log.info("微信支付 - 创建订单: orderNo={}, amount={}, subject={}", orderNo, amount, subject);

        try {
            // 注意：以下为开发环境模拟实现，生产环境需集成微信支付 SDK
            // 集成步骤：添加微信支付SDK依赖后，参考以下示例代码
            /*
            WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
            request.setOutTradeNo(orderNo);
            request.setTotalFee(amount.multiply(new BigDecimal("100")).intValue());
            request.setBody(subject);
            request.setNotifyUrl(config.getNotifyUrl());
            request.setTradeType("JSAPI"); // JSAPI / NATIVE / APP / H5

            WxPayService wxPayService = new WxPayServiceImpl();
            wxPayService.setConfig(wxPayConfig);

            WxPayUnifiedOrderResult result = wxPayService.unifiedOrder(request);

            Map<String, String> payParams = new HashMap<>();
            payParams.put("appId", config.getAppId());
            payParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
            payParams.put("nonceStr", result.getNonceStr());
            payParams.put("package", "prepay_id=" + result.getPrepayId());
            payParams.put("signType", "MD5");
            payParams.put("paySign", wxPayService.createSign(payParams));

            return payParams;
            */

            // 开发环境模拟返回
            Map<String, String> result = new HashMap<>();
            result.put("prepay_id", "mock_prepay_id_" + orderNo);
            result.put("code_url", "weixin://wxpay/bizpayurl?pr=mock");

            log.warn("【开发模式】支付订单未实际创建,请配置微信支付SDK");
            return result;

        } catch (Exception e) {
            log.error("微信支付创建订单失败: orderNo={}", orderNo, e);
            throw new BusinessException("创建支付订单失败: " + e.getMessage());
        }
    }

    @Override
    public String queryOrder(String orderNo) {
        log.info("微信支付 - 查询订单: orderNo={}", orderNo);
        log.warn("【开发模式】订单查询功能未实现，返回模拟状态");
        // 开发环境返回模拟状态，生产环境需集成微信支付SDK
        return "NOTPAY";
    }

    @Override
    public boolean refund(String orderNo, BigDecimal refundAmount, String refundReason) {
        log.info("微信支付 - 退款: orderNo={}, refundAmount={}, reason={}",
                orderNo, refundAmount, refundReason);
        log.warn("【开发模式】退款功能未实现，返回模拟成功");
        // 开发环境返回模拟成功，生产环境需集成微信支付SDK
        return true;
    }

    @Override
    public boolean closeOrder(String orderNo) {
        log.info("微信支付 - 关闭订单: orderNo={}", orderNo);
        log.warn("【开发模式】关闭订单功能未实现，返回模拟成功");
        // 开发环境返回模拟成功，生产环境需集成微信支付SDK
        return true;
    }

    @Override
    public boolean verifySign(Map<String, String> params) {
        log.info("微信支付 - 验证回调签名");

        try {
            // 注意：生产环境需实现真实的签名验证
            // 微信支付签名验证步骤：
            // 1. 获取回调参数中的签名 sign
            // 2. 将除 sign 外的所有参数按字典序排列
            // 3. 拼接参数：key1=value1&key2=value2&key=API_KEY
            // 4. 使用 MD5 或 SHA256 计算签名
            // 5. 比较计算的签名与回调签名是否一致

            /*
            String sign = params.get("sign");
            if (sign == null) {
                return false;
            }

            // 构建待签名字符串
            StringBuilder sb = new StringBuilder();
            params.entrySet().stream()
                .filter(entry -> !"sign".equals(entry.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> sb.append(entry.getKey())
                                    .append("=")
                                    .append(entry.getValue())
                                    .append("&"));
            sb.append("key=").append(config.getApiKey());

            // 计算签名
            String calculatedSign = DigestUtils.md5Hex(sb.toString()).toUpperCase();
            return calculatedSign.equals(sign);
            */

            log.warn("【开发模式】签名验证未实现，返回 true");
            return true;

        } catch (Exception e) {
            log.error("微信支付签名验证失败", e);
            return false;
        }
    }
}
