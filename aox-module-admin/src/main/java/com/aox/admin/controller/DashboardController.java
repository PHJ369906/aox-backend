package com.aox.admin.controller;

import com.aox.admin.domain.vo.DashboardStatisticsVO;
import com.aox.admin.domain.vo.PaymentMethodVO;
import com.aox.admin.domain.vo.PaymentTrendVO;
import com.aox.admin.domain.vo.UserTrendVO;
import com.aox.admin.service.DashboardService;
import com.aox.common.core.domain.R;
import com.aox.common.security.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Dashboard统计控制器
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Tag(name = "Dashboard统计", description = "数据看板统计接口")
@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "获取综合统计数据", description = "获取用户、订单、金额、登录等综合统计")
    @GetMapping("/statistics")
    @RequirePermission("dashboard:view")
    public R<DashboardStatisticsVO> getStatistics() {
        DashboardStatisticsVO statistics = dashboardService.getStatistics();
        return R.ok(statistics);
    }

    @Operation(summary = "获取用户趋势数据", description = "获取最近30天用户新增和活跃趋势")
    @GetMapping("/trends/user")
    @RequirePermission("dashboard:view")
    public R<List<UserTrendVO>> getUserTrend() {
        List<UserTrendVO> trend = dashboardService.getUserTrend();
        return R.ok(trend);
    }

    @Operation(summary = "获取支付趋势数据", description = "获取最近30天订单数和金额趋势")
    @GetMapping("/trends/payment")
    @RequirePermission("dashboard:view")
    public R<List<PaymentTrendVO>> getPaymentTrend() {
        List<PaymentTrendVO> trend = dashboardService.getPaymentTrend();
        return R.ok(trend);
    }

    @Operation(summary = "获取支付方式分布", description = "获取微信支付和支付宝的订单分布")
    @GetMapping("/payment-methods")
    @RequirePermission("dashboard:view")
    public R<List<PaymentMethodVO>> getPaymentMethods() {
        List<PaymentMethodVO> methods = dashboardService.getPaymentMethods();
        return R.ok(methods);
    }
}
