package com.aox.admin.service;

import cn.hutool.core.util.StrUtil;
import com.aox.admin.domain.vo.JobExecutionLogVO;
import com.aox.admin.domain.vo.JobStatisticsVO;
import com.aox.admin.domain.vo.ScheduledJobVO;
import com.aox.admin.integration.xxl.XxlJobAdminClient;
import com.aox.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 定时任务监控服务
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledJobService {

    private static final int PAGE_SIZE = 1000;
    private static final int LOG_STATUS_SUCCESS = 1;
    private static final int LOG_STATUS_FAIL = 2;

    private final XxlJobAdminClient xxlJobAdminClient;

    /**
     * 获取任务列表
     *
     * 从XXL-Job Admin API获取数据
     */
    public List<ScheduledJobVO> getJobList() {
        log.info("查询定时任务列表");

        if (!xxlJobAdminClient.isEnabled()) {
            return Collections.emptyList();
        }

        XxlJobAdminClient.XxlJobPageResult result = xxlJobAdminClient.pageJobInfo(0, PAGE_SIZE, null);
        List<Map<String, Object>> jobList = result.data();

        List<ScheduledJobVO> jobs = new ArrayList<>();
        for (Map<String, Object> jobInfo : jobList) {
            Integer jobId = parseInt(jobInfo.get("id"));
            String jobDesc = parseString(jobInfo.get("jobDesc"));
            String jobHandler = parseString(jobInfo.get("executorHandler"));
            String executorParam = parseString(jobInfo.get("executorParam"));
            String cronExpression = parseString(jobInfo.get("scheduleConf"));
            Integer status = parseInt(jobInfo.get("triggerStatus"));
            Long lastTime = parseLong(jobInfo.get("triggerLastTime"));
            Long nextTime = parseLong(jobInfo.get("triggerNextTime"));

            JobLogSummary summary = getJobLogSummary(jobId);

            jobs.add(ScheduledJobVO.builder()
                    .jobName(StrUtil.isNotBlank(jobDesc) ? jobDesc : jobHandler)
                    .jobDescription(jobDesc)
                    .cronExpression(cronExpression)
                    .jobHandler(jobHandler)
                    .executorParam(executorParam)
                    .status(status != null ? status : 0)
                    .lastExecutionTime(toLocalDateTime(lastTime))
                    .nextExecutionTime(toLocalDateTime(nextTime))
                    .executionCount(summary.total)
                    .successCount(summary.success)
                    .failCount(summary.fail)
                    .build());
        }

        return jobs;
    }

    /**
     * 获取任务执行日志
     */
    public List<JobExecutionLogVO> getJobExecutionLogs(String jobName, Integer limit) {
        log.info("查询任务执行日志: jobName={}, limit={}", jobName, limit);

        if (!xxlJobAdminClient.isEnabled()) {
            return Collections.emptyList();
        }

        Integer jobId = resolveJobId(jobName);
        XxlJobAdminClient.XxlJobPageResult result = xxlJobAdminClient.pageJobLogs(
                0,
                limit != null ? limit : 10,
                null,
                jobId,
                null,
                null
        );

        List<JobExecutionLogVO> logs = new ArrayList<>();
        for (Map<String, Object> logItem : result.data()) {
            Long logId = parseLong(logItem.get("id"));
            String logJobDesc = parseString(logItem.get("jobDesc"));
            Long triggerTime = parseLong(logItem.get("triggerTime"));
            Long handleTime = parseLong(logItem.get("handleTime"));
            Integer handleCode = parseInt(logItem.get("handleCode"));
            Integer triggerCode = parseInt(logItem.get("triggerCode"));
            String handleMsg = parseString(logItem.get("handleMsg"));
            String triggerMsg = parseString(logItem.get("triggerMsg"));

            long duration = 0L;
            if (triggerTime != null && handleTime != null && handleTime >= triggerTime) {
                duration = handleTime - triggerTime;
            }

            boolean success = handleCode != null ? handleCode == 200 : triggerCode != null && triggerCode == 200;

            logs.add(JobExecutionLogVO.builder()
                    .logId(logId)
                    .jobName(StrUtil.isNotBlank(logJobDesc) ? logJobDesc : jobName)
                    .executionTime(toLocalDateTime(triggerTime))
                    .executionDuration(duration)
                    .status(success ? 1 : 0)
                    .result(success ? "执行成功" : "执行失败")
                    .errorMessage(success ? null : (StrUtil.isNotBlank(handleMsg) ? handleMsg : triggerMsg))
                    .build());
        }

        return logs;
    }

    /**
     * 手动触发任务
     */
    public void triggerJob(String jobName) {
        log.info("手动触发任务: jobName={}", jobName);

        if (!xxlJobAdminClient.isEnabled()) {
            throw new BusinessException("XXL-Job未启用或未配置调度中心地址");
        }

        Integer jobId = resolveJobId(jobName);
        if (jobId == null) {
            throw new BusinessException("未找到对应任务: " + jobName);
        }
        xxlJobAdminClient.triggerJob(jobId);
    }

    /**
     * 获取任务统计数据
     */
    public JobStatisticsVO getJobStatistics() {
        log.info("查询任务统计数据");

        if (!xxlJobAdminClient.isEnabled()) {
            return JobStatisticsVO.builder()
                    .totalJobs(0L)
                    .runningJobs(0L)
                    .stoppedJobs(0L)
                    .todayExecutions(0L)
                    .todaySuccessCount(0L)
                    .todayFailCount(0L)
                    .successRate(0.0)
                    .build();
        }

        List<ScheduledJobVO> jobList = getJobList();
        long totalJobs = jobList.size();
        long runningJobs = jobList.stream().filter(job -> job.getStatus() != null && job.getStatus() == 1).count();
        long stoppedJobs = totalJobs - runningJobs;

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String filterTime = today + " - " + today;

        long todayExecutions = getLogCount(null, filterTime);
        long todaySuccessCount = getLogCount(LOG_STATUS_SUCCESS, filterTime);
        long todayFailCount = getLogCount(LOG_STATUS_FAIL, filterTime);

        double successRate = todayExecutions > 0
                ? (double) todaySuccessCount / (double) todayExecutions * 100
                : 0.0;

        return JobStatisticsVO.builder()
                .totalJobs(totalJobs)
                .runningJobs(runningJobs)
                .stoppedJobs(stoppedJobs)
                .todayExecutions(todayExecutions)
                .todaySuccessCount(todaySuccessCount)
                .todayFailCount(todayFailCount)
                .successRate(successRate)
                .build();
    }

    private Integer resolveJobId(String jobName) {
        if (StrUtil.isBlank(jobName)) {
            return null;
        }
        XxlJobAdminClient.XxlJobPageResult result = xxlJobAdminClient.pageJobInfo(0, PAGE_SIZE, null);
        return result.data().stream()
                .filter(item -> jobName.equals(parseString(item.get("jobDesc")))
                        || jobName.equals(parseString(item.get("executorHandler")))
                        || jobName.equals(String.valueOf(parseInt(item.get("id")))))
                .map(item -> parseInt(item.get("id")))
                .filter(id -> id != null)
                .findFirst()
                .orElse(null);
    }

    private JobLogSummary getJobLogSummary(Integer jobId) {
        if (jobId == null) {
            return new JobLogSummary(0L, 0L, 0L);
        }
        long total = getLogCountByJob(jobId, null);
        long success = getLogCountByJob(jobId, LOG_STATUS_SUCCESS);
        long fail = getLogCountByJob(jobId, LOG_STATUS_FAIL);
        return new JobLogSummary(total, success, fail);
    }

    private long getLogCountByJob(Integer jobId, Integer logStatus) {
        XxlJobAdminClient.XxlJobPageResult result = xxlJobAdminClient.pageJobLogs(0, 1, null, jobId, logStatus, null);
        return result.total();
    }

    private long getLogCount(Integer logStatus, String filterTime) {
        XxlJobAdminClient.XxlJobPageResult result = xxlJobAdminClient.pageJobLogs(0, 1, null, null, logStatus, filterTime);
        return result.total();
    }

    private LocalDateTime toLocalDateTime(Long epochMillis) {
        if (epochMillis == null || epochMillis <= 0) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
    }

    private String parseString(Object value) {
        return value != null ? String.valueOf(value) : null;
    }

    private Integer parseInt(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static class JobLogSummary {
        private final long total;
        private final long success;
        private final long fail;

        private JobLogSummary(long total, long success, long fail) {
            this.total = total;
            this.success = success;
            this.fail = fail;
        }
    }
}
