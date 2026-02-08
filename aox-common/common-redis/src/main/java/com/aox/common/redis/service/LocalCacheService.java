package com.aox.common.redis.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 本地缓存服务（Caffeine）
 * 用于高频访问数据的本地缓存，减少 Redis 访问压力
 *
 * @author Aox Team
 */
@Slf4j
@Service
public class LocalCacheService {

    /**
     * 用户权限缓存
     * 容量: 1000, 过期时间: 5分钟
     */
    private Cache<String, Object> permissionCache;

    /**
     * 用户信息缓存
     * 容量: 500, 过期时间: 10分钟
     */
    private Cache<String, Object> userCache;

    /**
     * 通用短期缓存
     * 容量: 2000, 过期时间: 1分钟
     */
    private Cache<String, Object> shortTermCache;

    /**
     * 系统配置缓存
     * 容量: 200, 过期时间: 30分钟
     */
    private Cache<String, Object> configCache;

    @PostConstruct
    public void init() {
        // 权限缓存：高频访问，短过期
        permissionCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats()
                .build();

        // 用户缓存
        userCache = Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats()
                .build();

        // 短期缓存
        shortTermCache = Caffeine.newBuilder()
                .maximumSize(2000)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .recordStats()
                .build();

        // 配置缓存：低频变更，长过期
        configCache = Caffeine.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats()
                .build();

        log.info("LocalCacheService 初始化完成");
    }

    // ==================== 权限缓存 ====================

    /**
     * 获取用户权限（优先本地缓存）
     */
    @SuppressWarnings("unchecked")
    public <T> T getPermission(String key, Function<String, T> loader) {
        return (T) permissionCache.get(key, loader::apply);
    }

    /**
     * 设置用户权限缓存
     */
    public void setPermission(String key, Object value) {
        permissionCache.put(key, value);
    }

    /**
     * 删除用户权限缓存
     */
    public void removePermission(String key) {
        permissionCache.invalidate(key);
    }

    /**
     * 清空所有权限缓存
     */
    public void clearPermissionCache() {
        permissionCache.invalidateAll();
        log.info("权限缓存已清空");
    }

    // ==================== 用户缓存 ====================

    @SuppressWarnings("unchecked")
    public <T> T getUser(String key, Function<String, T> loader) {
        return (T) userCache.get(key, loader::apply);
    }

    public void setUser(String key, Object value) {
        userCache.put(key, value);
    }

    public void removeUser(String key) {
        userCache.invalidate(key);
    }

    public void clearUserCache() {
        userCache.invalidateAll();
    }

    // ==================== 短期缓存 ====================

    @SuppressWarnings("unchecked")
    public <T> T getShortTerm(String key, Function<String, T> loader) {
        return (T) shortTermCache.get(key, loader::apply);
    }

    public void setShortTerm(String key, Object value) {
        shortTermCache.put(key, value);
    }

    public void removeShortTerm(String key) {
        shortTermCache.invalidate(key);
    }

    // ==================== 配置缓存 ====================

    @SuppressWarnings("unchecked")
    public <T> T getConfig(String key, Function<String, T> loader) {
        return (T) configCache.get(key, loader::apply);
    }

    public void setConfig(String key, Object value) {
        configCache.put(key, value);
    }

    public void removeConfig(String key) {
        configCache.invalidate(key);
    }

    public void clearConfigCache() {
        configCache.invalidateAll();
    }

    // ==================== 统计信息 ====================

    /**
     * 获取缓存统计信息
     */
    public String getCacheStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 本地缓存统计 ===\n");
        sb.append("权限缓存: ").append(permissionCache.stats()).append("\n");
        sb.append("用户缓存: ").append(userCache.stats()).append("\n");
        sb.append("短期缓存: ").append(shortTermCache.stats()).append("\n");
        sb.append("配置缓存: ").append(configCache.stats()).append("\n");
        return sb.toString();
    }

    /**
     * 清空所有本地缓存
     */
    public void clearAll() {
        permissionCache.invalidateAll();
        userCache.invalidateAll();
        shortTermCache.invalidateAll();
        configCache.invalidateAll();
        log.info("所有本地缓存已清空");
    }
}
