package com.aox.infrastructure.payment.controller;

import com.aox.common.core.domain.R;
import com.aox.common.security.annotation.RequirePermission;
import com.aox.infrastructure.payment.domain.dto.PaymentOrderQueryDTO;
import com.aox.infrastructure.payment.domain.vo.PaymentOrderVO;
import com.aox.infrastructure.payment.domain.vo.PaymentStatisticsVO;
import com.aox.infrastructure.payment.service.AdminPaymentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 后台管理-支付订单控制器
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Tag(name = "后台管理-支付订单", description = "支付订单管理接口")
@RestController
@RequestMapping("/api/v1/admin/payment/orders")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final AdminPaymentService adminPaymentService;

    @Operation(summary = "分页查询订单列表", description = "支持订单号、手机号、支付方式、状态、日期范围查询")
    @GetMapping
    @RequirePermission("payment:order:list")
    public R<Page<PaymentOrderVO>> getPaymentOrderList(PaymentOrderQueryDTO query) {
        Page<PaymentOrderVO> page = adminPaymentService.getPaymentOrderList(query);
        return R.ok(page);
    }

    @Operation(summary = "查询订单详情", description = "根据订单ID查询详细信息")
    @GetMapping("/{orderId}")
    @RequirePermission("payment:order:detail")
    public R<PaymentOrderVO> getPaymentOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long orderId
    ) {
        PaymentOrderVO order = adminPaymentService.getPaymentOrderDetail(orderId);
        return R.ok(order);
    }

    @Operation(summary = "查询订单统计数据", description = "获取订单总数、总金额、成功率等统计信息")
    @GetMapping("/statistics")
    @RequirePermission("payment:order:list")
    public R<PaymentStatisticsVO> getPaymentStatistics() {
        PaymentStatisticsVO statistics = adminPaymentService.getPaymentStatistics();
        return R.ok(statistics);
    }

    @Operation(summary = "导出订单", description = "导出订单列表为Excel文件")
    @GetMapping("/export")
    @RequirePermission("payment:order:export")
    public void exportPaymentOrders(PaymentOrderQueryDTO query, HttpServletResponse response) {
        adminPaymentService.exportPaymentOrders(query, response);
    }
}
