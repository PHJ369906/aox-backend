package com.aox.common.redis.aspect;

import com.aox.common.exception.BusinessException;
import com.aox.common.redis.annotation.RateLimiter;
import com.aox.common.redis.service.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 限流切面
 *
 * @author Aox Team
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimiterAspect {

    private final RedisService redisService;

    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) {
        String key = buildKey(point, rateLimiter);
        int time = rateLimiter.time();
        int count = rateLimiter.count();

        Long current = redisService.incr(key, 1);
        if (current == 1) {
            // 第一次请求，设置过期时间
            redisService.expire(key, time);
        }

        if (current > count) {
            log.warn("限流触发: key={}, count={}, limit={}", key, current, count);
            throw new BusinessException(429, rateLimiter.message());
        }

        log.debug("限流检查通过: key={}, count={}/{}", key, current, count);
    }

    /**
     * 构建限流 key
     */
    private String buildKey(JoinPoint point, RateLimiter rateLimiter) {
        StringBuilder sb = new StringBuilder(rateLimiter.key());

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        // 添加类名和方法名
        sb.append(method.getDeclaringClass().getName())
          .append(":")
          .append(method.getName());

        // 根据限流类型添加不同的标识
        switch (rateLimiter.limitType()) {
            case IP -> sb.append(":").append(getClientIp());
            case USER -> sb.append(":").append(getUserId());
            default -> {
                // 默认全局限流，不需要额外标识
            }
        }

        return sb.toString();
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }
        HttpServletRequest request = attributes.getRequest();

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取当前用户ID
     */
    private String getUserId() {
        // 从 SecurityContext 获取用户ID，未登录返回 anonymous
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() != null && !"anonymousUser".equals(auth.getPrincipal())) {
                // 通过反射获取 userId，避免循环依赖
                Object principal = auth.getPrincipal();
                Method method = principal.getClass().getMethod("getUserId");
                Object userId = method.invoke(principal);
                return String.valueOf(userId);
            }
        } catch (Exception e) {
            // 未登录或获取失败
            log.debug("获取用户ID失败: {}", e.getMessage());
        }
        return "anonymous";
    }
}
