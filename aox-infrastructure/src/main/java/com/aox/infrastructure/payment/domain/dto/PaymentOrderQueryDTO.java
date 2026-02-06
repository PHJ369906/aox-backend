package com.aox.infrastructure.payment.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付订单查询DTO（后台管理）
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Data
public class PaymentOrderQueryDTO {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 订单号（模糊查询）
     */
    private String orderNo;

    /**
     * 用户手机号（模糊查询）
     */
    private String phone;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 支付类型：wechat / alipay
     */
    private String paymentType;

    /**
     * 支付状态：0待支付 1已支付 2已退款 3已关闭
     */
    private Integer status;

    /**
     * 开始时间（订单创建时间范围）
     */
    private LocalDateTime startTime;

    /**
     * 结束时间（订单创建时间范围）
     */
    private LocalDateTime endTime;
}
