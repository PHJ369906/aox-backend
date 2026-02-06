package com.aox.admin.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 定时任务信息VO
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledJobVO {

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务描述
     */
    private String jobDescription;

    /**
     * Cron表达式
     */
    private String cronExpression;

    /**
     * 任务处理器（Bean名称）
     */
    private String jobHandler;

    /**
     * 执行参数
     */
    private String executorParam;

    /**
     * 任务状态：0-停止，1-运行中
     */
    private Integer status;

    /**
     * 最后执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastExecutionTime;

    /**
     * 下次执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextExecutionTime;

    /**
     * 执行次数
     */
    private Long executionCount;

    /**
     * 成功次数
     */
    private Long successCount;

    /**
     * 失败次数
     */
    private Long failCount;
}
