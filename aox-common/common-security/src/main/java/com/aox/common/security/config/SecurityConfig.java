package com.aox.common.security.config;

import com.aox.common.security.filter.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring Security 配置
 *
 * @author Aox Team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                         ObjectMapper objectMapper) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    /**
     * 白名单路径（无需认证）
     */
    private static final String[] WHITE_LIST = {
            // 认证相关
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/captcha",
            // 小程序认证
            "/api/v1/miniapp/auth/**",
            // Swagger/OpenAPI
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/doc.html",
            "/webjars/**",
            // 静态资源
            "/static/**",
            "/favicon.ico",
            // 健康检查
            "/actuator/**",
            "/health"
    };

    /**
     * 安全过滤链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（使用 JWT 不需要）
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用 Session（使用无状态 JWT）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置 CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 配置请求授权
                .authorizeHttpRequests(auth -> auth
                        // 白名单路径允许匿名访问
                        .requestMatchers(WHITE_LIST).permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated())
                // 异常处理
                .exceptionHandling(exception -> exception
                        // 未认证处理
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            Map<String, Object> result = new HashMap<>();
                            result.put("code", 401);
                            result.put("msg", "未登录或token已过期");
                            result.put("timestamp", System.currentTimeMillis());
                            response.getWriter().write(objectMapper.writeValueAsString(result));
                        })
                        // 无权限处理
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            Map<String, Object> result = new HashMap<>();
                            result.put("code", 403);
                            result.put("msg", "无权限访问");
                            result.put("timestamp", System.currentTimeMillis());
                            response.getWriter().write(objectMapper.writeValueAsString(result));
                        }))
                // 添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
