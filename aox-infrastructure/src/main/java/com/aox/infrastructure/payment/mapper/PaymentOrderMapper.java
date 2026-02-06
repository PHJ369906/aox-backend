package com.aox.infrastructure.payment.mapper;

import com.aox.infrastructure.payment.domain.PaymentOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 支付订单Mapper
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 支付订单
     */
    PaymentOrder selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据第三方交易号查询订单
     *
     * @param transactionId 第三方交易号
     * @return 支付订单
     */
    PaymentOrder selectByTransactionId(@Param("transactionId") String transactionId);

    /**
     * 更新订单状态
     *
     * @param orderId 订单ID
     * @param status  新状态
     * @return 影响行数
     */
    int updateStatus(@Param("orderId") Long orderId, @Param("status") Integer status);
}
