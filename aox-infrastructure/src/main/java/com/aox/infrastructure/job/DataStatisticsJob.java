package com.aox.infrastructure.job;

import com.aox.system.domain.SysUser;
import com.aox.system.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 定时任务 - 数据统计
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataStatisticsJob {

    private final SysUserMapper userMapper;

    /**
     * 每日数据统计任务
     *
     * 执行频率：每天凌晨1点执行
     * Cron表达式：0 0 1 * * ?
     */
    // @XxlJob("dailyStatisticsJob")
    public void dailyStatistics() {
        log.info("================ 每日数据统计任务开始 ================");
        LocalDateTime startTime = LocalDateTime.now();

        try {
            Map<String, Object> statistics = new HashMap<>();

            // 1. 统计用户数据
            Map<String, Long> userStats = statisticsUserData();
            statistics.put("user", userStats);
            log.info("用户统计: {}", userStats);

            // 2. 统计登录数据
            Map<String, Long> loginStats = statisticsLoginData();
            statistics.put("login", loginStats);
            log.info("登录统计: {}", loginStats);

            // 3. 统计系统数据
            Map<String, Long> systemStats = statisticsSystemData();
            statistics.put("system", systemStats);
            log.info("系统统计: {}", systemStats);

            // 4. 保存统计结果（可以存储到数据库或发送报表）
            saveStatistics(statistics);

            LocalDateTime endTime = LocalDateTime.now();
            log.info("================ 每日数据统计任务完成 ================");
            log.info("统计完成，耗时: {} 毫秒",
                     java.time.Duration.between(startTime, endTime).toMillis());

        } catch (Exception e) {
            log.error("每日数据统计失败", e);
            throw e;
        }
    }

    /**
     * 统计用户数据
     */
    private Map<String, Long> statisticsUserData() {
        Map<String, Long> stats = new HashMap<>();

        try {
            // 总用户数
            QueryWrapper<SysUser> totalWrapper = new QueryWrapper<>();
            totalWrapper.eq("deleted", 0);
            long totalUsers = userMapper.selectCount(totalWrapper);
            stats.put("totalUsers", totalUsers);

            // 正常用户数
            QueryWrapper<SysUser> activeWrapper = new QueryWrapper<>();
            activeWrapper.eq("status", 0).eq("deleted", 0);
            long activeUsers = userMapper.selectCount(activeWrapper);
            stats.put("activeUsers", activeUsers);

            // 禁用用户数
            QueryWrapper<SysUser> disabledWrapper = new QueryWrapper<>();
            disabledWrapper.eq("status", 1).eq("deleted", 0);
            long disabledUsers = userMapper.selectCount(disabledWrapper);
            stats.put("disabledUsers", disabledUsers);

            // 今日新增用户数
            LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            QueryWrapper<SysUser> newWrapper = new QueryWrapper<>();
            newWrapper.ge("create_time", today).eq("deleted", 0);
            long newUsers = userMapper.selectCount(newWrapper);
            stats.put("newUsersToday", newUsers);

        } catch (Exception e) {
            log.error("统计用户数据失败", e);
        }

        return stats;
    }

    /**
     * 统计登录数据
     */
    private Map<String, Long> statisticsLoginData() {
        Map<String, Long> stats = new HashMap<>();

        try {
            // 今日登录用户数
            LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            QueryWrapper<SysUser> todayLoginWrapper = new QueryWrapper<>();
            todayLoginWrapper.ge("last_login_time", today).eq("deleted", 0);
            long todayLoginUsers = userMapper.selectCount(todayLoginWrapper);
            stats.put("loginUsersToday", todayLoginUsers);

            // 近7日登录用户数
            LocalDateTime last7Days = LocalDateTime.now().minusDays(7);
            QueryWrapper<SysUser> last7DaysWrapper = new QueryWrapper<>();
            last7DaysWrapper.ge("last_login_time", last7Days).eq("deleted", 0);
            long last7DaysLoginUsers = userMapper.selectCount(last7DaysWrapper);
            stats.put("loginUsersLast7Days", last7DaysLoginUsers);

            // 近30日登录用户数
            LocalDateTime last30Days = LocalDateTime.now().minusDays(30);
            QueryWrapper<SysUser> last30DaysWrapper = new QueryWrapper<>();
            last30DaysWrapper.ge("last_login_time", last30Days).eq("deleted", 0);
            long last30DaysLoginUsers = userMapper.selectCount(last30DaysWrapper);
            stats.put("loginUsersLast30Days", last30DaysLoginUsers);

        } catch (Exception e) {
            log.error("统计登录数据失败", e);
        }

        return stats;
    }

    /**
     * 统计系统数据
     */
    private Map<String, Long> statisticsSystemData() {
        Map<String, Long> stats = new HashMap<>();

        try {
            // 这里可以添加其他系统统计指标
            // 例如：支付订单数、文件上传数、消息发送数等
            stats.put("placeholder", 0L);

        } catch (Exception e) {
            log.error("统计系统数据失败", e);
        }

        return stats;
    }

    /**
     * 保存统计结果
     */
    private void saveStatistics(Map<String, Object> statistics) {
        try {
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            log.info("统计日期: {}, 统计结果: {}", date, statistics);

            // 可以将统计结果：
            // 1. 保存到数据库统计表
            // 2. 发送邮件报表
            // 3. 推送到监控系统
            // 4. 保存到文件

        } catch (Exception e) {
            log.error("保存统计结果失败", e);
        }
    }

    /**
     * 每周数据汇总任务
     *
     * 执行频率：每周一凌晨2点执行
     * Cron表达式：0 0 2 ? * MON
     */
    // @XxlJob("weeklyStatisticsJob")
    public void weeklyStatistics() {
        log.info("================ 每周数据汇总任务开始 ================");

        try {
            // 汇总过去一周的数据
            log.info("汇总本周数据...");

            // 实现周报逻辑

            log.info("================ 每周数据汇总任务完成 ================");

        } catch (Exception e) {
            log.error("每周数据汇总失败", e);
            throw e;
        }
    }

    /**
     * 每月数据汇总任务
     *
     * 执行频率：每月1号凌晨3点执行
     * Cron表达式：0 0 3 1 * ?
     */
    // @XxlJob("monthlyStatisticsJob")
    public void monthlyStatistics() {
        log.info("================ 每月数据汇总任务开始 ================");

        try {
            // 汇总过去一个月的数据
            log.info("汇总本月数据...");

            // 实现月报逻辑

            log.info("================ 每月数据汇总任务完成 ================");

        } catch (Exception e) {
            log.error("每月数据汇总失败", e);
            throw e;
        }
    }
}
