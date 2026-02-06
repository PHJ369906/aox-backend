package com.aox.infrastructure.payment.service;

import cn.hutool.core.util.IdUtil;
import com.aox.common.exception.BusinessException;
import com.aox.infrastructure.payment.client.PaymentClient;
import com.aox.infrastructure.payment.client.impl.AlipayClient;
import com.aox.infrastructure.payment.client.impl.WechatPayClient;
import com.aox.infrastructure.payment.config.PaymentProperties;
import com.aox.infrastructure.payment.domain.PaymentOrder;
import com.aox.infrastructure.payment.mapper.PaymentOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 支付服务
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProperties paymentProperties;
    private final PaymentOrderMapper paymentOrderMapper;

    /**
     * 创建支付订单
     *
     * @param paymentType 支付类型: wechat / alipay
     * @param amount      金额
     * @param subject     订单标题
     * @param body        订单描述
     * @param userId      用户ID
     * @return 支付参数
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> createPayment(String paymentType, BigDecimal amount,
                                              String subject, String body, Long userId) {
        // 生成订单号
        String orderNo = IdUtil.getSnowflakeNextIdStr();

        // 创建支付订单记录
        PaymentOrder order = new PaymentOrder();
        order.setOrderNo(orderNo);
        order.setPaymentType(paymentType);
        order.setAmount(amount);
        order.setSubject(subject);
        order.setBody(body);
        order.setUserId(userId);
        order.setStatus(0); // 待支付
        order.setCreateTime(LocalDateTime.now());

        // 保存订单到数据库
        int result = paymentOrderMapper.insert(order);
        if (result <= 0) {
            throw new BusinessException("订单创建失败");
        }
        log.info("支付订单创建成功: orderNo={}, orderId={}", orderNo, order.getOrderId());

        // 调用支付接口
        PaymentClient paymentClient = getPaymentClient(paymentType);
        Map<String, String> payParams = paymentClient.createOrder(orderNo, amount, subject, body);

        payParams.put("orderNo", orderNo);
        return payParams;
    }

    /**
     * 查询订单状态
     *
     * @param orderNo 订单号
     * @return 订单状态
     */
    public String queryOrderStatus(String orderNo) {
        // 从数据库查询订单
        PaymentOrder order = paymentOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在: " + orderNo);
        }

        log.info("查询订单状态: orderNo={}, status={}", orderNo, order.getStatus());

        // 如果订单已支付，直接返回状态
        if (order.getStatus() == 1) {
            return "已支付";
        }

        // 调用支付平台查询最新状态
        PaymentClient paymentClient = getPaymentClient(order.getPaymentType());
        return paymentClient.queryOrder(orderNo);
    }

    /**
     * 处理支付回调
     *
     * @param paymentType   支付类型
     * @param params        回调参数
     * @param transactionId 第三方交易号
     * @param orderNo       订单号
     * @return 是否处理成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean handleCallback(String paymentType, Map<String, String> params,
                                   String transactionId, String orderNo) {
        log.info("处理支付回调: paymentType={}, orderNo={}, transactionId={}",
                 paymentType, orderNo, transactionId);

        // 1. 验证签名（在具体的 Client 中实现）
        PaymentClient paymentClient = getPaymentClient(paymentType);
        boolean signValid = verifyCallback(paymentClient, params);
        if (!signValid) {
            log.error("支付回调签名验证失败: orderNo={}", orderNo);
            throw new BusinessException("签名验证失败");
        }

        // 2. 查询订单
        PaymentOrder order = paymentOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            log.error("订单不存在: orderNo={}", orderNo);
            throw new BusinessException("订单不存在");
        }

        // 3. 检查订单状态，避免重复处理
        if (order.getStatus() == 1) {
            log.warn("订单已支付，忽略重复回调: orderNo={}", orderNo);
            return true;
        }

        // 4. 更新订单状态
        order.setStatus(1); // 已支付
        order.setPayTime(LocalDateTime.now());
        order.setTransactionId(transactionId);
        order.setUpdateTime(LocalDateTime.now());

        int result = paymentOrderMapper.updateById(order);
        if (result <= 0) {
            throw new BusinessException("订单状态更新失败");
        }

        log.info("支付回调处理成功: orderNo={}, transactionId={}", orderNo, transactionId);

        // 5. 通知业务系统（可通过事件发布或MQ实现）
        notifyBusiness(order);

        return true;
    }

    /**
     * 验证回调签名
     */
    private boolean verifyCallback(PaymentClient paymentClient, Map<String, String> params) {
        log.info("验证支付回调签名");
        return paymentClient.verifySign(params);
    }

    /**
     * 通知业务系统
     */
    private void notifyBusiness(PaymentOrder order) {
        // 这里可以通过事件发布或消息队列通知业务系统
        log.info("通知业务系统支付成功: orderNo={}, userId={}",
                 order.getOrderNo(), order.getUserId());

        // TODO: 实现具体的业务通知逻辑
        // 例如：applicationEventPublisher.publishEvent(new PaymentSuccessEvent(order));
        // 或者：rabbitTemplate.convertAndSend("payment.success", order);
    }

    /**
     * 退款
     *
     * @param orderNo      订单号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return 是否退款成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean refund(String orderNo, BigDecimal refundAmount, String refundReason) {
        // 1. 查询订单
        PaymentOrder order = paymentOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在: " + orderNo);
        }

        // 2. 验证订单状态
        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态不正确，无法退款");
        }

        // 3. 验证退款金额
        if (refundAmount.compareTo(order.getAmount()) > 0) {
            throw new BusinessException("退款金额不能大于订单金额");
        }

        log.info("发起退款: orderNo={}, amount={}, reason={}", orderNo, refundAmount, refundReason);

        // 4. 调用支付平台退款接口
        PaymentClient paymentClient = getPaymentClient(order.getPaymentType());
        boolean success = paymentClient.refund(orderNo, refundAmount, refundReason);

        if (success) {
            // 5. 更新订单状态为已退款
            order.setStatus(2); // 已退款
            order.setUpdateTime(LocalDateTime.now());
            paymentOrderMapper.updateById(order);

            log.info("退款成功: orderNo={}", orderNo);
        } else {
            log.error("退款失败: orderNo={}", orderNo);
        }

        return success;
    }

    /**
     * 获取支付客户端
     */
    private PaymentClient getPaymentClient(String paymentType) {
        return switch (paymentType.toLowerCase()) {
            case "wechat" -> new WechatPayClient(paymentProperties.getWechat());
            case "alipay" -> new AlipayClient(paymentProperties.getAlipay());
            default -> throw new BusinessException("不支持的支付类型: " + paymentType);
        };
    }
}
