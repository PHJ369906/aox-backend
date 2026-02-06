package com.aox.system.service.impl;

import com.aox.common.log.domain.SysLog;
import com.aox.common.log.domain.SysLoginLog;
import com.aox.common.log.domain.request.LoginLogQueryRequest;
import com.aox.common.log.domain.request.OperationLogQueryRequest;
import com.aox.common.log.mapper.SysLogMapper;
import com.aox.common.log.mapper.SysLoginLogMapper;
import com.aox.system.service.LogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 日志服务实现
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final SysLogMapper sysLogMapper;
    private final SysLoginLogMapper sysLoginLogMapper;

    // ==================== 操作日志 ====================

    @Override
    public IPage<SysLog> getOperationLogs(OperationLogQueryRequest request) {
        Page<SysLog> page = new Page<>(request.getCurrent(), request.getSize());

        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getModule()), SysLog::getModule, request.getModule())
                .like(StringUtils.hasText(request.getOperation()), SysLog::getOperation, request.getOperation())
                .like(StringUtils.hasText(request.getUsername()), SysLog::getUsername, request.getUsername())
                .eq(request.getStatus() != null, SysLog::getStatus, request.getStatus())
                .ge(request.getStartTime() != null, SysLog::getCreateTime, request.getStartTime())
                .le(request.getEndTime() != null, SysLog::getCreateTime, request.getEndTime())
                .orderByDesc(SysLog::getCreateTime);

        return sysLogMapper.selectPage(page, wrapper);
    }

    @Override
    public SysLog getOperationLogById(Long logId) {
        return sysLogMapper.selectById(logId);
    }

    @Override
    public void deleteOperationLogs(List<Long> logIds) {
        sysLogMapper.deleteBatchIds(logIds);
        log.info("批量删除操作日志: {}", logIds);
    }

    @Override
    public void clearOperationLogs() {
        sysLogMapper.delete(new LambdaQueryWrapper<>());
        log.warn("清空所有操作日志");
    }

    @Override
    public void exportOperationLogs(HttpServletResponse response, OperationLogQueryRequest request) {
        log.info("导出操作日志: {}", request);
        // 注意：需要实现Excel导出逻辑
        log.warn("【开发模式】Excel导出功能未实现");
    }

    // ==================== 登录日志 ====================

    @Override
    public IPage<SysLoginLog> getLoginLogs(LoginLogQueryRequest request) {
        Page<SysLoginLog> page = new Page<>(request.getCurrent(), request.getSize());

        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getUsername()), SysLoginLog::getUsername, request.getUsername())
                .eq(request.getLoginType() != null, SysLoginLog::getLoginType, request.getLoginType())
                .eq(request.getStatus() != null, SysLoginLog::getStatus, request.getStatus())
                .like(StringUtils.hasText(request.getIp()), SysLoginLog::getIp, request.getIp())
                .ge(request.getStartTime() != null, SysLoginLog::getLoginTime, request.getStartTime())
                .le(request.getEndTime() != null, SysLoginLog::getLoginTime, request.getEndTime())
                .orderByDesc(SysLoginLog::getLoginTime);

        return sysLoginLogMapper.selectPage(page, wrapper);
    }

    @Override
    public SysLoginLog getLoginLogById(Long loginLogId) {
        return sysLoginLogMapper.selectById(loginLogId);
    }

    @Override
    public void deleteLoginLogs(List<Long> logIds) {
        sysLoginLogMapper.deleteBatchIds(logIds);
        log.info("批量删除登录日志: {}", logIds);
    }

    @Override
    public void clearLoginLogs() {
        sysLoginLogMapper.delete(new LambdaQueryWrapper<>());
        log.warn("清空所有登录日志");
    }

    @Override
    public void exportLoginLogs(HttpServletResponse response, LoginLogQueryRequest request) {
        log.info("导出登录日志: {}", request);
        // 注意：需要实现Excel导出逻辑
        log.warn("【开发模式】Excel导出功能未实现");
    }

    // ==================== 统计分析 ====================

    @Override
    public Map<String, Object> getOperationStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> statistics = new HashMap<>();

        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(startDate != null, SysLog::getCreateTime, startDate)
                .le(endDate != null, SysLog::getCreateTime, endDate);

        Long total = sysLogMapper.selectCount(wrapper);
        statistics.put("total", total);

        return statistics;
    }

    @Override
    public Map<String, Object> getLoginStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> statistics = new HashMap<>();

        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(startDate != null, SysLoginLog::getLoginTime, startDate)
                .le(endDate != null, SysLoginLog::getLoginTime, endDate);

        Long total = sysLoginLogMapper.selectCount(wrapper);
        statistics.put("total", total);

        return statistics;
    }

    @Override
    public List<String> getOperationModules() {
        List<SysLog> logs = sysLogMapper.selectList(new LambdaQueryWrapper<SysLog>().select(SysLog::getModule));
        return logs.stream()
                .map(SysLog::getModule)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<SysLoginLog> getRecentLoginLogs(Integer limit) {
        Page<SysLoginLog> page = new Page<>(1, limit);
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysLoginLog::getLoginTime);

        return sysLoginLogMapper.selectPage(page, wrapper).getRecords();
    }
}
