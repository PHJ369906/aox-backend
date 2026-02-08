package com.aox.common.security.filter;

import cn.hutool.core.util.StrUtil;
import com.aox.common.core.constant.RedisConstants;
import com.aox.common.security.context.SecurityContextHolder;
import com.aox.common.security.domain.LoginUser;
import com.aox.common.security.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JWT 认证过滤器
 *
 * @author Aox Team
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil,
                                   RedisTemplate<String, Object> redisTemplate) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.redisTemplate = redisTemplate;
    }

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String TOKEN_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        log.debug("JWT过滤器处理请求: {} {}", request.getMethod(), requestUri);

        try {
            // 1. 获取 Token
            String token = getTokenFromRequest(request);

            log.debug("请求URI: {}, Token是否存在: {}", requestUri, StrUtil.isNotBlank(token));

            if (StrUtil.isNotBlank(token)) {
                // 2. 验证 Token
                if (jwtTokenUtil.validateToken(token)) {
                    // 3. 检查 Token 是否在 Redis 中存在（用于支持登出功能）
                    String tokenKey = RedisConstants.LOGIN_TOKEN_KEY + token;
                    Object cachedUserId = redisTemplate.opsForValue().get(tokenKey);

                    if (cachedUserId != null) {
                        // 4. 从 Token 中解析用户信息
                        Long userId = jwtTokenUtil.getUserIdFromToken(token);
                        String username = jwtTokenUtil.getUsernameFromToken(token);
                        String userType = jwtTokenUtil.getTypeFromToken(token);
                        Long tenantId = jwtTokenUtil.getTenantIdFromToken(token);

                        Long cachedUserIdLong = parseUserId(cachedUserId);
                        if (cachedUserIdLong == null || !cachedUserIdLong.equals(userId)) {
                            log.warn("Token与缓存用户ID不一致，忽略本次认证: tokenUserId={}, cachedUserId={}, uri={}",
                                    userId, cachedUserId, requestUri);
                            filterChain.doFilter(request, response);
                            return;
                        }

                        // 5. 构建 LoginUser 对象
                        LoginUser loginUser = buildLoginUser(userId, username, userType, tenantId);

                        // 6. 设置到 SecurityContext
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        org.springframework.security.core.context.SecurityContextHolder.getContext()
                                .setAuthentication(authentication);

                        // 7. 设置到自定义 SecurityContextHolder
                        SecurityContextHolder.setLoginUser(loginUser);

                        log.debug("用户 {} 认证成功", username);
                    }
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            // 清理 ThreadLocal，防止内存泄漏
            SecurityContextHolder.clear();
        }
    }

    /**
     * 从请求中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (StrUtil.isNotBlank(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        // 也支持从 query 参数获取 token（用于某些特殊场景，如文件下载）
        return request.getParameter("token");
    }

    /**
     * 构建 LoginUser 对象
     * 注意：权限和角色将在阶段二由 PermissionService 加载
     */
    private LoginUser buildLoginUser(Long userId, String username, String userType, Long tenantId) {
        // 从 Redis 获取用户的权限和角色信息
        // 在阶段二完成后，这里将调用 PermissionService 获取真实的权限数据
        Set<String> permissions = getUserPermissionsFromCache(userId);
        Set<String> roles = getUserRolesFromCache(userId);

        return LoginUser.builder()
                .userId(userId)
                .username(username)
                .userType(userType)
                .tenantId(tenantId == null ? 0L : tenantId)
                .permissions(permissions)
                .roles(roles)
                .loginTime(System.currentTimeMillis())
                .build();
    }

    /**
     * 将缓存中的 userId 转为 Long
     */
    private Long parseUserId(Object cachedUserId) {
        if (cachedUserId instanceof Number number) {
            return number.longValue();
        }
        if (cachedUserId instanceof String value) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    /**
     * 从缓存获取用户权限
     * 阶段二将实现真正的权限查询
     */
    @SuppressWarnings("unchecked")
    private Set<String> getUserPermissionsFromCache(Long userId) {
        String permissionKey = RedisConstants.USER_PERMISSIONS_KEY + userId;
        Object cached = redisTemplate.opsForValue().get(permissionKey);
        if (cached instanceof List) {
            return new HashSet<>((List<String>) cached);
        }
        // 默认返回空权限集，阶段二将实现完整的权限加载
        return new HashSet<>();
    }

    /**
     * 从缓存获取用户角色
     * 阶段二将实现真正的角色查询
     */
    @SuppressWarnings("unchecked")
    private Set<String> getUserRolesFromCache(Long userId) {
        String roleKey = RedisConstants.USER_ROLES_KEY + userId;
        Object cached = redisTemplate.opsForValue().get(roleKey);
        if (cached instanceof List) {
            return new HashSet<>((List<String>) cached);
        }
        // 默认返回空角色集，阶段二将实现完整的角色加载
        return new HashSet<>();
    }
}
