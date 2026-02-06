package com.aox.common.log.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 操作日志查询请求
 *
 * @author Aox Team
 */
@Data
@Schema(description = "操作日志查询请求")
public class OperationLogQueryRequest {

    @Schema(description = "当前页", example = "1")
    private Integer current = 1;

    @Schema(description = "每页数量", example = "20")
    private Integer size = 20;

    @Schema(description = "模块名称")
    private String module;

    @Schema(description = "操作类型")
    private String operation;

    @Schema(description = "操作用户")
    private String username;

    @Schema(description = "操作状态：0-失败，1-成功")
    private Integer status;

    @Schema(description = "开始时间", example = "2026-01-01 00:00:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2026-12-31 23:59:59")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
