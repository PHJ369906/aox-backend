package com.aox.admin.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 任务统计数据VO
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Data
@Builder
public class JobStatisticsVO {

    /**
     * 任务总数
     */
    private Long totalJobs;

    /**
     * 运行中任务数
     */
    private Long runningJobs;

    /**
     * 停止任务数
     */
    private Long stoppedJobs;

    /**
     * 今日执行次数
     */
    private Long todayExecutions;

    /**
     * 今日成功次数
     */
    private Long todaySuccessCount;

    /**
     * 今日失败次数
     */
    private Long todayFailCount;

    /**
     * 成功率（%）
     */
    private Double successRate;
}
