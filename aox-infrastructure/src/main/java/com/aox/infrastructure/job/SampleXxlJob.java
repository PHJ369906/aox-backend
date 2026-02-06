package com.aox.infrastructure.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * XXL-Job 示例任务
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Slf4j
@Component
public class SampleXxlJob {

    /**
     * 简单任务示例
     *
     * 配置说明：
     * 1. 在 XXL-Job 管理后台添加执行器
     * 2. 添加任务，JobHandler 填写方法名：sampleTask
     * 3. 配置 Cron 表达式
     */
    // @XxlJob("sampleTask")
    public void sampleTask() {
        log.info("XXL-Job 示例任务执行开始");

        try {
            // 业务逻辑
            log.info("执行业务逻辑...");

        } catch (Exception e) {
            log.error("任务执行失败", e);
        }

        log.info("XXL-Job 示例任务执行完成");
    }

    /**
     * 清理过期数据任务
     */
    // @XxlJob("cleanExpiredDataJob")
    public void cleanExpiredData() {
        log.info("清理过期数据任务开始");

        try {
            // 清理过期的验证码
            // 清理过期的日志
            // 清理过期的临时文件

            log.info("过期数据清理完成");
        } catch (Exception e) {
            log.error("清理过期数据失败", e);
        }
    }

    /**
     * 数据统计任务
     */
    // @XxlJob("statisticsJob")
    public void statistics() {
        log.info("数据统计任务开始");

        try {
            // 统计用户数据
            // 统计订单数据
            // 生成报表

            log.info("数据统计完成");
        } catch (Exception e) {
            log.error("数据统计失败", e);
        }
    }

    /**
     * 定时备份任务
     */
    // @XxlJob("backupJob")
    public void backup() {
        log.info("数据备份任务开始");

        try {
            // 备份数据库
            // 备份文件

            log.info("数据备份完成");
        } catch (Exception e) {
            log.error("数据备份失败", e);
        }
    }
}
