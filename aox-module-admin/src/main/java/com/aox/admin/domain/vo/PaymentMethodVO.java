package com.aox.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付方式分布VO
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodVO {

    /**
     * 支付方式名称
     */
    private String method;

    /**
     * 订单数
     */
    private Long count;

    /**
     * 占比（%）
     */
    private Double percentage;
}
