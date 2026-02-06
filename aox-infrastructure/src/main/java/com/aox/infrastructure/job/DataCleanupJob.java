package com.aox.infrastructure.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 定时任务 - 数据清理
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataCleanupJob {

    private final StringRedisTemplate redisTemplate;

    /**
     * 清理过期数据任务
     *
     * 执行频率：每天凌晨2点执行
     * Cron表达式：0 0 2 * * ?
     */
    // @XxlJob("cleanExpiredDataJob")
    public void cleanExpiredData() {
        log.info("================ 清理过期数据任务开始 ================");
        LocalDateTime startTime = LocalDateTime.now();

        try {
            int totalCleaned = 0;

            // 1. 清理过期的短信验证码（Redis）
            int smsCleaned = cleanExpiredSmsCode();
            totalCleaned += smsCleaned;
            log.info("清理过期短信验证码: {} 条", smsCleaned);

            // 2. 清理过期的登录失败记录（Redis）
            int loginFailCleaned = cleanExpiredLoginFail();
            totalCleaned += loginFailCleaned;
            log.info("清理过期登录失败记录: {} 条", loginFailCleaned);

            // 3. 清理过期的临时Token（Redis）
            int tokenCleaned = cleanExpiredToken();
            totalCleaned += tokenCleaned;
            log.info("清理过期临时Token: {} 条", tokenCleaned);

            LocalDateTime endTime = LocalDateTime.now();
            log.info("================ 清理过期数据任务完成 ================");
            log.info("总计清理: {} 条数据, 耗时: {} 毫秒",
                     totalCleaned,
                     java.time.Duration.between(startTime, endTime).toMillis());

        } catch (Exception e) {
            log.error("清理过期数据失败", e);
            throw e;
        }
    }

    /**
     * 清理过期的短信验证码
     * Redis Key模式: sms:code:*
     */
    private int cleanExpiredSmsCode() {
        try {
            // Redis的TTL机制会自动清理过期key
            // 这里只是做一次扫描统计，实际清理由Redis自动完成
            Set<String> keys = redisTemplate.keys("sms:code:*");
            if (keys == null || keys.isEmpty()) {
                return 0;
            }

            int cleaned = 0;
            for (String key : keys) {
                Long ttl = redisTemplate.getExpire(key);
                if (ttl != null && ttl <= 0) {
                    redisTemplate.delete(key);
                    cleaned++;
                }
            }
            return cleaned;

        } catch (Exception e) {
            log.error("清理过期短信验证码失败", e);
            return 0;
        }
    }

    /**
     * 清理过期的登录失败记录
     * Redis Key模式: login:fail:*
     */
    private int cleanExpiredLoginFail() {
        try {
            Set<String> keys = redisTemplate.keys("login:fail:*");
            if (keys == null || keys.isEmpty()) {
                return 0;
            }

            int cleaned = 0;
            for (String key : keys) {
                Long ttl = redisTemplate.getExpire(key);
                if (ttl != null && ttl <= 0) {
                    redisTemplate.delete(key);
                    cleaned++;
                }
            }
            return cleaned;

        } catch (Exception e) {
            log.error("清理过期登录失败记录失败", e);
            return 0;
        }
    }

    /**
     * 清理过期的临时Token
     * Redis Key模式: temp:token:*
     */
    private int cleanExpiredToken() {
        try {
            Set<String> keys = redisTemplate.keys("temp:token:*");
            if (keys == null || keys.isEmpty()) {
                return 0;
            }

            int cleaned = 0;
            for (String key : keys) {
                Long ttl = redisTemplate.getExpire(key);
                if (ttl != null && ttl <= 0) {
                    redisTemplate.delete(key);
                    cleaned++;
                }
            }
            return cleaned;

        } catch (Exception e) {
            log.error("清理过期临时Token失败", e);
            return 0;
        }
    }

    /**
     * 清理旧日志文件
     *
     * 执行频率：每周日凌晨3点执行
     * Cron表达式：0 0 3 ? * SUN
     */
    // @XxlJob("cleanOldLogsJob")
    public void cleanOldLogs() {
        log.info("================ 清理旧日志任务开始 ================");

        try {
            // 清理30天前的日志文件
            int days = 30;
            String logPath = "./logs";

            log.info("清理 {} 天前的日志文件，日志路径: {}", days, logPath);

            // 实际实现需要遍历日志目录，删除过期文件
            // 这里只是示例框架

            log.info("================ 清理旧日志任务完成 ================");

        } catch (Exception e) {
            log.error("清理旧日志失败", e);
            throw e;
        }
    }
}
