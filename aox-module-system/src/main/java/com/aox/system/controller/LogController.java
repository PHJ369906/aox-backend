package com.aox.system.controller;

import com.aox.common.core.domain.R;
import com.aox.common.log.domain.SysLog;
import com.aox.common.log.domain.SysLoginLog;
import com.aox.common.log.domain.request.LoginLogQueryRequest;
import com.aox.common.log.domain.request.OperationLogQueryRequest;
import com.aox.system.service.LogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 日志管理控制器
 *
 * @author Aox Team
 */
@RestController
@RequestMapping("/api/v1/system/logs")
@RequiredArgsConstructor
@Tag(name = "日志管理", description = "操作日志、登录日志查询和管理")
public class LogController {

    private final LogService logService;

    // ==================== 操作日志 ====================

    /**
     * 操作日志分页查询
     */
    @GetMapping("/operation")
    @Operation(summary = "操作日志分页查询")
    public R<IPage<SysLog>> getOperationLogs(@Valid OperationLogQueryRequest request) {
        IPage<SysLog> page = logService.getOperationLogs(request);
        return R.ok(page);
    }

    /**
     * 操作日志详情
     */
    @GetMapping("/operation/{logId}")
    @Operation(summary = "操作日志详情")
    public R<SysLog> getOperationLogDetail(@PathVariable Long logId) {
        SysLog log = logService.getOperationLogById(logId);
        return R.ok(log);
    }

    /**
     * 批量删除操作日志
     */
    @DeleteMapping("/operation")
    @Operation(summary = "批量删除操作日志")
    public R<Void> deleteOperationLogs(@RequestBody List<Long> logIds) {
        logService.deleteOperationLogs(logIds);
        return R.ok();
    }

    /**
     * 清空操作日志
     */
    @DeleteMapping("/operation/clear")
    @Operation(summary = "清空操作日志")
    public R<Void> clearOperationLogs() {
        logService.clearOperationLogs();
        return R.ok();
    }

    /**
     * 导出操作日志
     */
    @GetMapping("/operation/export")
    @Operation(summary = "导出操作日志")
    public void exportOperationLogs(HttpServletResponse response, @Valid OperationLogQueryRequest request) {
        logService.exportOperationLogs(response, request);
    }

    // ==================== 登录日志 ====================

    /**
     * 登录日志分页查询
     */
    @GetMapping("/login")
    @Operation(summary = "登录日志分页查询")
    public R<IPage<SysLoginLog>> getLoginLogs(@Valid LoginLogQueryRequest request) {
        IPage<SysLoginLog> page = logService.getLoginLogs(request);
        return R.ok(page);
    }

    /**
     * 登录日志详情
     */
    @GetMapping("/login/{loginLogId}")
    @Operation(summary = "登录日志详情")
    public R<SysLoginLog> getLoginLogDetail(@PathVariable Long loginLogId) {
        SysLoginLog log = logService.getLoginLogById(loginLogId);
        return R.ok(log);
    }

    /**
     * 批量删除登录日志
     */
    @DeleteMapping("/login")
    @Operation(summary = "批量删除登录日志")
    public R<Void> deleteLoginLogs(@RequestBody List<Long> logIds) {
        logService.deleteLoginLogs(logIds);
        return R.ok();
    }

    /**
     * 清空登录日志
     */
    @DeleteMapping("/login/clear")
    @Operation(summary = "清空登录日志")
    public R<Void> clearLoginLogs() {
        logService.clearLoginLogs();
        return R.ok();
    }

    /**
     * 导出登录日志
     */
    @GetMapping("/login/export")
    @Operation(summary = "导出登录日志")
    public void exportLoginLogs(HttpServletResponse response, @Valid LoginLogQueryRequest request) {
        logService.exportLoginLogs(response, request);
    }

    // ==================== 统计分析 ====================

    /**
     * 操作日志统计
     */
    @GetMapping("/operation/statistics")
    @Operation(summary = "操作日志统计")
    public R<Map<String, Object>> getOperationStatistics(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime endDate) {

        Map<String, Object> statistics = logService.getOperationStatistics(startDate, endDate);
        return R.ok(statistics);
    }

    /**
     * 登录日志统计
     */
    @GetMapping("/login/statistics")
    @Operation(summary = "登录日志统计")
    public R<Map<String, Object>> getLoginStatistics(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime endDate) {

        Map<String, Object> statistics = logService.getLoginStatistics(startDate, endDate);
        return R.ok(statistics);
    }

    /**
     * 获取操作日志模块列表
     */
    @GetMapping("/operation/modules")
    @Operation(summary = "获取操作日志模块列表")
    public R<List<String>> getOperationModules() {
        List<String> modules = logService.getOperationModules();
        return R.ok(modules);
    }

    /**
     * 获取最近登录记录
     */
    @GetMapping("/login/recent")
    @Operation(summary = "获取最近登录记录")
    public R<List<SysLoginLog>> getRecentLoginLogs(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<SysLoginLog> logs = logService.getRecentLoginLogs(limit);
        return R.ok(logs);
    }
}
