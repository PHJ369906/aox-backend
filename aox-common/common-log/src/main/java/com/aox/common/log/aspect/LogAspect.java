package com.aox.common.log.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aox.common.log.annotation.Log;
import com.aox.common.log.service.AsyncLogService;
import com.aox.common.security.domain.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 操作日志切面
 *
 * @author Aox Team
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    private final AsyncLogService asyncLogService;

    public LogAspect(AsyncLogService asyncLogService) {
        this.asyncLogService = asyncLogService;
    }

    @Pointcut("@annotation(com.aox.common.log.annotation.Log)")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();

        // 获取 HttpServletRequest
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 获取注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);

        // 获取操作信息
        String module = logAnnotation.module();
        String operation = logAnnotation.operation();
        String methodName = point.getTarget().getClass().getName() + "." + signature.getName();
        String requestUri = request != null ? request.getRequestURI() : "";
        String requestMethod = request != null ? request.getMethod() : "";
        String ip = request != null ? getIpAddress(request) : "";
        String userAgent = request != null ? request.getHeader("User-Agent") : "";

        // 获取请求参数
        String requestParams = "";
        if (logAnnotation.saveRequestData()) {
            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                try {
                    requestParams = JSONUtil.toJsonStr(args);
                    // 限制长度
                    if (requestParams.length() > 2000) {
                        requestParams = requestParams.substring(0, 2000) + "...";
                    }
                } catch (Exception e) {
                    requestParams = "参数序列化失败";
                }
            }
        }

        log.info("==> 操作日志: 模块={}, 操作={}, 方法={}, URI={}, IP={}",
                module, operation, methodName, requestUri, ip);
        log.debug("==> 请求参数: {}", requestParams);

        Object result = null;
        String errorMsg = null;
        int status = 0;
        String responseResult = "";

        try {
            // 执行方法
            result = point.proceed();
            status = 0; // 成功
        } catch (Exception e) {
            status = 1; // 失败
            errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.length() > 500) {
                errorMsg = errorMsg.substring(0, 500);
            }
            log.error("==> 操作失败: {}", errorMsg);
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - beginTime;

            // 获取响应结果
            if (logAnnotation.saveResponseData() && result != null) {
                try {
                    responseResult = JSONUtil.toJsonStr(result);
                    if (responseResult.length() > 2000) {
                        responseResult = responseResult.substring(0, 2000) + "...";
                    }
                } catch (Exception e) {
                    responseResult = "响应序列化失败";
                }
            }

            log.info("<== 操作完成: 耗时={}ms, 状态={}", executionTime, status == 0 ? "成功" : "失败");

            // 异步保存日志到数据库
            // 从 SecurityContext 获取用户信息（如果可用）
            Long userId = null;
            String username = null;
            try {
                // 尝试从 SecurityContextHolder 获取用户信息
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null) {
                    Object principal = authentication.getPrincipal();
                    if (principal instanceof LoginUser loginUser) {
                        userId = loginUser.getUserId();
                        username = loginUser.getUsername();
                    }
                }
            } catch (Exception e) {
                // 忽略获取用户信息失败的情况
            }

            asyncLogService.saveLog(module, operation, methodName, requestUri, requestMethod,
                    requestParams, responseResult, status, errorMsg, ip, userAgent,
                    userId, username, executionTime);
        }

        return result;
    }

    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理的情况，第一个IP为客户端真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        return ip;
    }
}
