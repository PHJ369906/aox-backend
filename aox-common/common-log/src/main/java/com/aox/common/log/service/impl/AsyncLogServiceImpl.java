package com.aox.common.log.service.impl;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.aox.common.log.domain.SysLog;
import com.aox.common.log.domain.SysLoginLog;
import com.aox.common.log.mapper.SysLogMapper;
import com.aox.common.log.mapper.SysLoginLogMapper;
import com.aox.common.log.service.AsyncLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 异步日志服务实现
 *
 * @author Aox Team
 */
@Service
public class AsyncLogServiceImpl implements AsyncLogService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AsyncLogServiceImpl.class);
    private static final int ERROR_MSG_MAX_LEN = 500;

    private final SysLogMapper sysLogMapper;
    private final SysLoginLogMapper sysLoginLogMapper;

    public AsyncLogServiceImpl(SysLogMapper sysLogMapper, SysLoginLogMapper sysLoginLogMapper) {
        this.sysLogMapper = sysLogMapper;
        this.sysLoginLogMapper = sysLoginLogMapper;
    }

    @Override
    @Async("logAsyncExecutor")
    public void saveLog(String module, String operation, String method, String requestUri,
                        String requestMethod, String requestParams, String responseResult,
                        Integer status, String errorMsg, String ip, String userAgent,
                        Long userId, String username, Long executionTime) {
        try {
            SysLog sysLog = new SysLog();
            sysLog.setModule(module);
            sysLog.setOperation(operation);
            sysLog.setMethod(method);
            sysLog.setRequestUri(requestUri);
            sysLog.setRequestMethod(requestMethod);
            sysLog.setRequestParams(requestParams);
            sysLog.setResponseResult(responseResult);
            sysLog.setStatus(status);
            sysLog.setErrorMsg(errorMsg);
            sysLog.setIp(ip);
            sysLog.setUserAgent(userAgent);
            sysLog.setUserId(userId);
            sysLog.setUsername(username);
            sysLog.setExecutionTime(executionTime);
            sysLog.setCreateTime(LocalDateTime.now());

            sysLogMapper.insert(sysLog);
            log.debug("操作日志保存成功: module={}, operation={}", module, operation);
        } catch (Exception e) {
            log.error("操作日志保存失败: {}", e.getMessage(), e);
        }
    }

    @Override
    @Async("logAsyncExecutor")
    public void saveLog(SysLog sysLog) {
        try {
            if (sysLog.getCreateTime() == null) {
                sysLog.setCreateTime(LocalDateTime.now());
            }
            sysLogMapper.insert(sysLog);
            log.debug("操作日志保存成功: module={}, operation={}", sysLog.getModule(), sysLog.getOperation());
        } catch (Exception e) {
            log.error("操作日志保存失败: {}", e.getMessage(), e);
        }
    }

    @Override
    @Async("logAsyncExecutor")
    public void saveLoginLog(String username, Integer loginType, Integer status,
                             String ip, String userAgent, String errorMsg) {
        try {
            SysLoginLog loginLog = new SysLoginLog();
            loginLog.setUsername(username);
            loginLog.setLoginType(loginType);
            loginLog.setStatus(status);
            loginLog.setIp(ip);
            loginLog.setErrorMsg(truncate(errorMsg, ERROR_MSG_MAX_LEN));
            loginLog.setLoginTime(LocalDateTime.now());

            // 解析 UserAgent
            if (userAgent != null) {
                UserAgent ua = UserAgentUtil.parse(userAgent);
                if (ua != null) {
                    loginLog.setBrowser(ua.getBrowser() != null ? ua.getBrowser().getName() : null);
                    loginLog.setOs(ua.getOs() != null ? ua.getOs().getName() : null);
                    loginLog.setDevice(ua.getPlatform() != null ? ua.getPlatform().getName() : null);
                }
            }

            sysLoginLogMapper.insert(loginLog);
            log.debug("登录日志保存成功: username={}, status={}", username, status);
        } catch (Exception e) {
            log.error("登录日志保存失败: {}", e.getMessage(), e);
        }
    }

    @Override
    @Async("logAsyncExecutor")
    public void saveLoginLog(SysLoginLog loginLog) {
        try {
            if (loginLog.getLoginTime() == null) {
                loginLog.setLoginTime(LocalDateTime.now());
            }
            loginLog.setErrorMsg(truncate(loginLog.getErrorMsg(), ERROR_MSG_MAX_LEN));
            sysLoginLogMapper.insert(loginLog);
            log.debug("登录日志保存成功: username={}, status={}", loginLog.getUsername(), loginLog.getStatus());
        } catch (Exception e) {
            log.error("登录日志保存失败: {}", e.getMessage(), e);
        }
    }

    private static String truncate(String value, int maxLen) {
        if (value == null || maxLen <= 0) {
            return value;
        }
        return value.length() <= maxLen ? value : value.substring(0, maxLen);
    }
}
