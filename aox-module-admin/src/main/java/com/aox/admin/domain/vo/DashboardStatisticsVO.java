package com.aox.admin.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Dashboard综合统计数据VO
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Data
@Builder
public class DashboardStatisticsVO {

    /**
     * 用户总数
     */
    private Long totalUsers;

    /**
     * 今日新增用户
     */
    private Long todayNewUsers;

    /**
     * 用户增长率（与昨日对比，%）
     */
    private Double userGrowthRate;

    /**
     * 订单总数
     */
    private Long totalOrders;

    /**
     * 今日订单数
     */
    private Long todayOrders;

    /**
     * 订单增长率（与昨日对比，%）
     */
    private Double orderGrowthRate;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 今日订单金额
     */
    private BigDecimal todayAmount;

    /**
     * 金额增长率（与昨日对比，%）
     */
    private Double amountGrowthRate;

    /**
     * 总登录次数
     */
    private Long totalLogins;

    /**
     * 今日登录次数
     */
    private Long todayLogins;

    /**
     * 活跃用户数（最近7天登录）
     */
    private Long activeUsers;
}
