package com.aox.infrastructure.payment.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 支付订单导出VO
 *
 * @author Aox Team
 * @since 2026-02-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderExportVO {

    @ExcelProperty("订单ID")
    private Long orderId;

    @ExcelProperty("订单号")
    private String orderNo;

    @ExcelProperty("用户昵称")
    private String userNickname;

    @ExcelProperty("用户手机号")
    private String userPhone;

    @ExcelProperty("订单标题")
    private String subject;

    @ExcelProperty("金额")
    private BigDecimal amount;

    @ExcelProperty("支付方式")
    private String paymentType;

    @ExcelProperty("订单状态")
    private String status;

    @ExcelProperty("支付渠道")
    private String paymentMethod;

    @ExcelProperty("第三方交易号")
    private String transactionId;

    @ExcelProperty("创建时间")
    private String createTime;

    @ExcelProperty("支付时间")
    private String payTime;
}
