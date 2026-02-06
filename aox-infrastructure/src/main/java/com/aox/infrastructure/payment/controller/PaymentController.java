package com.aox.infrastructure.payment.controller;

import com.aox.common.core.domain.R;
import com.aox.infrastructure.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付服务控制器
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Slf4j
@Tag(name = "支付服务", description = "支付订单创建、查询、退款接口")
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "创建支付订单", description = "创建微信或支付宝支付订单")
    @PostMapping("/create")
    public R<Map<String, String>> createPayment(
            @RequestParam String paymentType,
            @RequestParam BigDecimal amount,
            @RequestParam String subject,
            @RequestParam(value = "body", required = false) String body,
            @RequestParam Long userId) {

        Map<String, String> result = paymentService.createPayment(
                paymentType, amount, subject, body, userId);
        return R.ok(result);
    }

    @Operation(summary = "查询订单状态", description = "查询支付订单状态")
    @GetMapping("/query/{orderNo}")
    public R<String> queryOrder(@PathVariable String orderNo) {
        String status = paymentService.queryOrderStatus(orderNo);
        return R.ok(status);
    }

    @Operation(summary = "退款", description = "发起订单退款")
    @PostMapping("/refund")
    public R<Boolean> refund(
            @RequestParam String orderNo,
            @RequestParam BigDecimal refundAmount,
            @RequestParam String refundReason) {

        boolean result = paymentService.refund(orderNo, refundAmount, refundReason);
        return R.ok(result);
    }

    @Operation(summary = "支付回调", description = "处理支付平台回调通知")
    @PostMapping("/callback/{paymentType}")
    public String callback(
            @PathVariable String paymentType,
            @RequestParam Map<String, String> params) {

        try {
            // 从回调参数中提取订单号和交易号
            String orderNo = params.get("out_trade_no");  // 支付宝
            if (orderNo == null) {
                orderNo = params.get("out_order_no");  // 微信支付
            }

            String transactionId = params.get("trade_no");  // 支付宝
            if (transactionId == null) {
                transactionId = params.get("transaction_id");  // 微信支付
            }

            boolean success = paymentService.handleCallback(
                paymentType, params, transactionId, orderNo);

            return success ? "SUCCESS" : "FAIL";

        } catch (Exception e) {
            log.error("支付回调处理失败", e);
            return "FAIL";
        }
    }
}
