package com.aox.miniapp.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 用户统计数据VO
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Data
@Builder
public class UserStatisticsVO {

    /**
     * 用户总数
     */
    private Long totalUsers;

    /**
     * 今日新增用户数
     */
    private Long todayNewUsers;

    /**
     * 活跃用户数（最近7天登录）
     */
    private Long activeUsers;

    /**
     * 禁用用户数
     */
    private Long disabledUsers;

    /**
     * 微信注册用户数
     */
    private Long wechatUsers;

    /**
     * 手机号注册用户数
     */
    private Long phoneUsers;
}
