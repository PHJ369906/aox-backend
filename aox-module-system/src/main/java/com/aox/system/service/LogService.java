package com.aox.system.service;

import com.aox.common.log.domain.SysLog;
import com.aox.common.log.domain.SysLoginLog;
import com.aox.common.log.domain.request.LoginLogQueryRequest;
import com.aox.common.log.domain.request.OperationLogQueryRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 日志服务接口
 *
 * @author Aox Team
 */
public interface LogService {

    // ==================== 操作日志 ====================

    /**
     * 操作日志分页查询
     */
    IPage<SysLog> getOperationLogs(OperationLogQueryRequest request);

    /**
     * 根据ID查询操作日志
     */
    SysLog getOperationLogById(Long logId);

    /**
     * 批量删除操作日志
     */
    void deleteOperationLogs(List<Long> logIds);

    /**
     * 清空操作日志
     */
    void clearOperationLogs();

    /**
     * 导出操作日志
     */
    void exportOperationLogs(HttpServletResponse response, OperationLogQueryRequest request);

    // ==================== 登录日志 ====================

    /**
     * 登录日志分页查询
     */
    IPage<SysLoginLog> getLoginLogs(LoginLogQueryRequest request);

    /**
     * 根据ID查询登录日志
     */
    SysLoginLog getLoginLogById(Long loginLogId);

    /**
     * 批量删除登录日志
     */
    void deleteLoginLogs(List<Long> logIds);

    /**
     * 清空登录日志
     */
    void clearLoginLogs();

    /**
     * 导出登录日志
     */
    void exportLoginLogs(HttpServletResponse response, LoginLogQueryRequest request);

    // ==================== 统计分析 ====================

    /**
     * 操作日志统计
     */
    Map<String, Object> getOperationStatistics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 登录日志统计
     */
    Map<String, Object> getLoginStatistics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 获取操作日志模块列表
     */
    List<String> getOperationModules();

    /**
     * 获取最近登录记录
     */
    List<SysLoginLog> getRecentLoginLogs(Integer limit);
}
