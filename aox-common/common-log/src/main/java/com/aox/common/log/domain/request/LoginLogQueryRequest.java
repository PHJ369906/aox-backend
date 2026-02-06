package com.aox.common.log.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 登录日志查询请求
 *
 * @author Aox Team
 */
@Data
@Schema(description = "登录日志查询请求")
public class LoginLogQueryRequest {

    @Schema(description = "当前页", example = "1")
    private Integer current = 1;

    @Schema(description = "每页数量", example = "20")
    private Integer size = 20;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "登录类型：0-账号密码，1-手机验证码")
    private Integer loginType;

    @Schema(description = "登录状态：0-失败，1-成功")
    private Integer status;

    @Schema(description = "登录IP")
    private String ip;

    @Schema(description = "开始时间", example = "2026-01-01 00:00:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2026-12-31 23:59:59")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
