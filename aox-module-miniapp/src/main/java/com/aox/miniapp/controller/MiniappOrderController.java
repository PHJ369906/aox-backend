package com.aox.miniapp.controller;

import com.aox.common.core.domain.R;
import com.aox.miniapp.domain.vo.MiniappOrderStatsVO;
import com.aox.miniapp.domain.vo.MiniappOrderVO;
import com.aox.miniapp.service.MiniappOrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序订单控制器
 */
@Tag(name = "小程序-订单", description = "小程序订单相关接口")
@RestController
@RequestMapping("/api/v1/miniapp/orders")
@RequiredArgsConstructor
public class MiniappOrderController {

    private final MiniappOrderService orderService;

    @Operation(summary = "分页查询订单", description = "支持按状态筛选订单")
    @GetMapping
    public R<Page<MiniappOrderVO>> getOrderPage(
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            @Parameter(description = "订单状态: all/pay/ship/receive/refund") @RequestParam(value = "status", required = false) String status
    ) {
        return R.ok(orderService.getOrderPage(pageNum, pageSize, status));
    }

    @Operation(summary = "查询订单统计", description = "查询用户各状态订单数")
    @GetMapping("/stats")
    public R<MiniappOrderStatsVO> getOrderStats() {
        return R.ok(orderService.getOrderStats());
    }
}
