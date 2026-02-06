package com.aox.admin.controller;

import com.aox.admin.domain.vo.JobExecutionLogVO;
import com.aox.admin.domain.vo.JobStatisticsVO;
import com.aox.admin.domain.vo.ScheduledJobVO;
import com.aox.admin.service.ScheduledJobService;
import com.aox.common.core.domain.R;
import com.aox.common.security.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务监控控制器
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Tag(name = "定时任务监控", description = "定时任务监控接口")
@RestController
@RequestMapping("/api/v1/admin/scheduled-jobs")
@RequiredArgsConstructor
public class ScheduledJobController {

    private final ScheduledJobService scheduledJobService;

    @Operation(summary = "获取任务列表", description = "查询所有定时任务信息")
    @GetMapping
    @RequirePermission("job:list")
    public R<List<ScheduledJobVO>> getJobList() {
        List<ScheduledJobVO> jobs = scheduledJobService.getJobList();
        return R.ok(jobs);
    }

    @Operation(summary = "获取任务执行日志", description = "查询任务的执行历史记录")
    @GetMapping("/logs")
    @RequirePermission("job:list")
    public R<List<JobExecutionLogVO>> getJobExecutionLogs(
            @Parameter(description = "任务名称") @RequestParam(value = "jobName", required = false) String jobName,
            @Parameter(description = "查询数量") @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ) {
        List<JobExecutionLogVO> logs = scheduledJobService.getJobExecutionLogs(jobName, limit);
        return R.ok(logs);
    }

    @Operation(summary = "手动触发任务", description = "手动执行指定的定时任务")
    @PostMapping("/trigger")
    @RequirePermission("job:trigger")
    public R<Void> triggerJob(
            @Parameter(description = "任务名称") @RequestParam String jobName
    ) {
        scheduledJobService.triggerJob(jobName);
        return R.ok();
    }

    @Operation(summary = "获取任务统计数据", description = "获取任务执行统计信息")
    @GetMapping("/statistics")
    @RequirePermission("job:list")
    public R<JobStatisticsVO> getJobStatistics() {
        JobStatisticsVO statistics = scheduledJobService.getJobStatistics();
        return R.ok(statistics);
    }
}
