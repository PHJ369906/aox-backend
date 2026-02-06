package com.aox.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 支付趋势数据VO
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTrendVO {

    /**
     * 日期（格式：MM-DD）
     */
    private String date;

    /**
     * 订单数
     */
    private Long orderCount;

    /**
     * 订单金额
     */
    private BigDecimal amount;
}
