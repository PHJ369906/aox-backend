package com.aox.miniapp.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小程序用户查询DTO（后台管理）
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Data
public class MiniappUserQueryDTO {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 手机号（模糊查询）
     */
    private String phone;

    /**
     * 昵称（模糊查询）
     */
    private String nickname;

    /**
     * 用户状态：0-正常，1-禁用
     */
    private Integer status;

    /**
     * 开始时间（注册时间范围）
     */
    private LocalDateTime startTime;

    /**
     * 结束时间（注册时间范围）
     */
    private LocalDateTime endTime;
}
