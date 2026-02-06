package com.aox.common.core.utils;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * Servlet 工具类
 * 用于获取 Request、Response、Session、IP 地址等
 *
 * @author Aox Team
 */
public class ServletUtil {

    private ServletUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 获取 ServletRequestAttributes
     */
    private static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 获取 HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        return Optional.ofNullable(getRequestAttributes())
                .map(ServletRequestAttributes::getRequest)
                .orElse(null);
    }

    /**
     * 获取 HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        return Optional.ofNullable(getRequestAttributes())
                .map(ServletRequestAttributes::getResponse)
                .orElse(null);
    }

    /**
     * 获取 HttpSession
     */
    public static HttpSession getSession() {
        return Optional.ofNullable(getRequest())
                .map(request -> request.getSession(false))
                .orElse(null);
    }

    /**
     * 获取客户端 IP 地址
     * 支持代理、负载均衡等场景
     */
    public static String getClientIp() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (isInvalidIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getRemoteAddr();
        }

        // 多个代理的情况，第一个IP为客户端真实IP
        if (StrUtil.isNotBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 判断 IP 是否无效
     */
    private static boolean isInvalidIp(String ip) {
        return StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip);
    }

    /**
     * 获取 User-Agent
     */
    public static String getUserAgent() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getHeader("User-Agent") : null;
    }

    /**
     * 获取请求 URI
     */
    public static String getRequestUri() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getRequestURI() : null;
    }

    /**
     * 获取请求方法（GET、POST 等）
     */
    public static String getRequestMethod() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getMethod() : null;
    }

    /**
     * 判断是否是 Ajax 请求
     */
    public static boolean isAjaxRequest() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return false;
        }
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(header);
    }
}
