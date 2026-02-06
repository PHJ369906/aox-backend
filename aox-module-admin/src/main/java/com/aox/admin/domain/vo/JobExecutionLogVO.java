package com.aox.admin.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 任务执行日志VO
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobExecutionLogVO {

    /**
     * 日志ID
     */
    private Long logId;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executionTime;

    /**
     * 执行时长（毫秒）
     */
    private Long executionDuration;

    /**
     * 执行状态：0-失败，1-成功
     */
    private Integer status;

    /**
     * 执行结果
     */
    private String result;

    /**
     * 错误信息
     */
    private String errorMessage;
}
